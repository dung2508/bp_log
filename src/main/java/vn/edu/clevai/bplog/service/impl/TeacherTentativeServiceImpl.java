package vn.edu.clevai.bplog.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.edu.clevai.bplog.common.TimeSessionCommon;
import vn.edu.clevai.bplog.entity.BpCalShiftStart;
import vn.edu.clevai.bplog.entity.BpClcrRegistration;
import vn.edu.clevai.bplog.entity.BpGGGradeGroup;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.payload.request.teacher.TeacherRegisterDetailRequest;
import vn.edu.clevai.bplog.payload.request.teacher.TeacherRegisterRequest;
import vn.edu.clevai.bplog.payload.response.teacher.GetAvailableResponse;
import vn.edu.clevai.bplog.payload.response.teacher.ProductGradeDay;
import vn.edu.clevai.bplog.payload.response.teacher.ProductGradeDayShiftResponse;
import vn.edu.clevai.bplog.payload.response.teacher.ProductGradeResponse;
import vn.edu.clevai.bplog.payload.response.teacher.ProductGradeShiftDetailResponse;
import vn.edu.clevai.bplog.payload.response.teacher.YourselfResponse;
import vn.edu.clevai.bplog.payload.response.teacher.detail.GradeDetail;
import vn.edu.clevai.bplog.payload.response.teacher.detail.ProductDetail;
import vn.edu.clevai.bplog.payload.response.teacher.detail.ShiftDetail;
import vn.edu.clevai.bplog.repository.BpCalShiftStartRepository;
import vn.edu.clevai.bplog.repository.BpClcrRegistrationRepository;
import vn.edu.clevai.bplog.repository.BpGGGradeGroupRepository;
import vn.edu.clevai.bplog.repository.BpPTProductTypeRepository;
import vn.edu.clevai.bplog.repository.projection.ClassCategoryProjection;
import vn.edu.clevai.bplog.service.TeacherTentativeService;
import vn.edu.clevai.common.api.util.DateUtils;

@Service
@AllArgsConstructor
public class TeacherTentativeServiceImpl implements TeacherTentativeService {

	private final BpGGGradeGroupRepository bpGGGradeGroupRepository;
	private final BpPTProductTypeRepository bpPTProductTypeRepository;
	private final BpCalShiftStartRepository bpCalShiftStartRepository;
	private final BpClcrRegistrationRepository clcrRegistrationRepo;
	private final ModelMapper modelMapper;

	@Override
	public String convertCodeShift(String code) {
		if (!StringUtils.isEmpty(code) && code.length() == 4) {
			String hour = code.substring(0, 2);
			String minute = code.substring(2, 4);
			return String.join(":", hour, minute);
		}
		return null;
	}

	@Override
	public ProductGradeShiftDetailResponse getProductGradeShiftInfo(String code) {
		List<BpGGGradeGroup> bpGGGradeGroupList = bpGGGradeGroupRepository.findAll();
		List<BpPTProductType> bpPTProductTypeList = bpPTProductTypeRepository.findAllByCode(code);
		List<BpCalShiftStart> bpCalShiftStartList = bpCalShiftStartRepository.findAll();

		List<ProductDetail> products = null;
		List<GradeDetail> grades = null;
		List<ShiftDetail> shifts = null;
		if (!CollectionUtils.isEmpty(bpPTProductTypeList)) {
			products = bpPTProductTypeList.stream().map(item -> modelMapper.map(item, ProductDetail.class))
					.collect(Collectors.toList());
		}
		if (!CollectionUtils.isEmpty(bpCalShiftStartList)) {
			shifts = bpCalShiftStartList.stream().map(item -> {
				ShiftDetail shiftDetail = modelMapper.map(item, ShiftDetail.class);
				shiftDetail.setStartAt(convertCodeShift(shiftDetail.getCode()));
				return shiftDetail;
			}).collect(Collectors.toList());
		}
		if (!CollectionUtils.isEmpty(bpGGGradeGroupList)) {
			grades = bpGGGradeGroupList.stream().map(item -> modelMapper.map(item, GradeDetail.class))
					.collect(Collectors.toList());
		}
		return ProductGradeShiftDetailResponse.builder().grades(grades).products(products).shifts(shifts).build();
	}

	@Override
	public YourselfResponse getYourself() {
		return null;
	}

	@Override
	@Transactional
	public void doSave(String myusi, Long start, Long end, TeacherRegisterRequest request) {
		List<TeacherRegisterDetailRequest> listAdd = request.getAdds();
		List<TeacherRegisterDetailRequest> listCancel = request.getCancel();
		String sessionId = TimeSessionCommon.encodeTimeSession(myusi, start, end);
		List<ClassCategoryRegistration> listRegisAdd = buildRegistration(myusi, sessionId, listAdd);
		List<ClassCategoryRegistration> listRegisRemove = buildRegistration(myusi, sessionId, listCancel);

		List<BpClcrRegistration> list = clcrRegistrationRepo.findAllByMyusiAndTimeSessionId(myusi, sessionId);
		List<BpClcrRegistration> listAddNew = new ArrayList<>();
		for (BpClcrRegistration regis : list) {
			ClassCategoryRegistration dbRegis = ClassCategoryRegistration.builder().myusi(myusi).ggCode(regis.getMygg())
					.timeSessionId(sessionId).wsoCode(regis.getMywso()).cashstaCode(regis.getMycasssta())
					.ptCode(regis.getMypt()).build();
			if (listRegisAdd.contains(dbRegis)) {
				regis.setIsDeleted(false);
				regis.setSubmitedAt(DateUtils.now());
				regis.setUpdatedAt(DateUtils.now());
				listAddNew.add(regis);
				listRegisAdd.remove(dbRegis);
			} else if (listRegisRemove.contains(dbRegis)) {
				regis.setIsDeleted(true);
				regis.setUpdatedAt(DateUtils.now());
			}
		}
		List<BpClcrRegistration> clcrCreate = new ArrayList<>();
		for (ClassCategoryRegistration add : listRegisAdd) {
			BpClcrRegistration regis = BpClcrRegistration.builder().code(buildClcrCode(add)).myusi(myusi)
					.timeSessionId(sessionId).mygg(add.getGgCode()).mypt(add.getPtCode()).mywso(add.getWsoCode())
					.mycasssta(add.getCashstaCode()).isDeleted(false).submitedAt(DateUtils.now()).build();
			clcrCreate.add(regis);
		}
		clcrRegistrationRepo.saveAll(clcrCreate);
	}

	@Override
	public GetAvailableResponse getAvailableSlot(String myusi, Long start, Long end) {
		List<ClassCategoryProjection> categoryProjectionList = clcrRegistrationRepo.getAllByMyusiAndTimeSessionId(myusi,
				TimeSessionCommon.encodeTimeSession(myusi, start, end));
		Set<Integer> products = new HashSet<>();
		Set<ProductGradeResponse> productGrades = new HashSet<>();
		Set<ProductGradeDay> productGradeDays = new HashSet<>();
		Set<ProductGradeDayShiftResponse> productGradeDayShifts = new HashSet<>();
		if (CollectionUtils.isEmpty(categoryProjectionList))
			return null;
		else {
			for (ClassCategoryProjection classCategoryProjection : categoryProjectionList) {
				Long id = classCategoryProjection.getId();
				Integer productId = classCategoryProjection.getProductId();
				Integer gradeId = classCategoryProjection.getGradeId();
				Integer dayOfWeek = classCategoryProjection.getDayOfWeek();
				Integer timeSlotId = classCategoryProjection.getTimeSlotId();
				products.add(productId);
				productGrades.add(ProductGradeResponse.builder().productId(productId).gradeId(gradeId).build());
				productGradeDays.add(
						ProductGradeDay.builder().productId(productId).gradeId(gradeId).dayOfWeek(dayOfWeek).build());
				productGradeDayShifts.add(ProductGradeDayShiftResponse.builder().id(id).productId(productId)
						.gradeId(gradeId).dayOfWeek(dayOfWeek).timeOfSlotId(timeSlotId).build());
			}
		}
		return GetAvailableResponse.builder().products(products).productGrades(productGrades)
				.productGradeDays(productGradeDays).productGradeDayShift(productGradeDayShifts).build();
	}

	private String buildClcrCode(ClassCategoryRegistration input) {
		// TODO need to implement here
		return null;
	}

	private List<ClassCategoryRegistration> buildRegistration(String myusi, String sessionId,
			List<TeacherRegisterDetailRequest> inputs) {
		// TODO need to implement here
		List<ClassCategoryRegistration> results = new ArrayList<>();
		return results;
	}

	@Builder
	@Data
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static class ClassCategoryRegistration {
		private String timeSessionId;
		private String myusi;
		private String clcrCode;
		@EqualsAndHashCode.Include
		private String ptCode;

		@EqualsAndHashCode.Include
		private String ggCode;

		@EqualsAndHashCode.Include
		private String wsoCode;

		@EqualsAndHashCode.Include
		private String cashstaCode;
	}
}
