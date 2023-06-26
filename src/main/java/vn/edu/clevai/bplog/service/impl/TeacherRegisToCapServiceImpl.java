package vn.edu.clevai.bplog.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.common.RegisterBpBppCommon;
import vn.edu.clevai.bplog.common.enumtype.*;
import vn.edu.clevai.bplog.dto.bp.BpProcessRegisterDTO;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.payload.request.filter.TeacherRegisterToCapAssigneeFilter;
import vn.edu.clevai.bplog.payload.request.teacher.RegisToCapPublishRequest;
import vn.edu.clevai.bplog.payload.request.teacher.RegisToCapPublishRequest.Assign;
import vn.edu.clevai.bplog.payload.request.teacher.TeachingScheduledQuantityRequest;
import vn.edu.clevai.bplog.payload.response.BPProductGradeClassLevelResponse;
import vn.edu.clevai.bplog.payload.response.ChildResponse;
import vn.edu.clevai.bplog.payload.response.teacher.detail.GradeDetail;
import vn.edu.clevai.bplog.payload.response.teacher.tocap.*;
import vn.edu.clevai.bplog.payload.response.teacher.tocap.QuantityResponse.AssignedQuantity;
import vn.edu.clevai.bplog.payload.response.teacher.tocap.QuantityResponse.RequiredQuantity;
import vn.edu.clevai.bplog.payload.response.teacher.tocap.TeacherApprovedAndReportResponse.RequestApprovedAndScheduledInfo;
import vn.edu.clevai.bplog.repository.*;
import vn.edu.clevai.bplog.repository.bplog.BpBpeBpEventRepository;
import vn.edu.clevai.bplog.repository.bplog.BpBppProcessRepository;
import vn.edu.clevai.bplog.repository.bplog.BpBpsStepRepository;
import vn.edu.clevai.bplog.repository.projection.*;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.utils.Utils;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.api.util.DateUtils;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Slf4j
public class TeacherRegisToCapServiceImpl implements TeacherRegisToCapService {
	@Autowired
	private BpBpsStepRepository bpBpsStepRepository;

	@Autowired
	private PTService ptService;

	@Autowired
	private BpGGGradeGroupService gradeGroupService;

	@Autowired
	private BpDfdlDifficultygradeService difficultyGradeService;

	@Autowired
	private BpUsiUserItemService usiUserItemService;

	@Autowired
	private BpUSIDutyRepository bpUsiDutyRepo;

	@Autowired
	private BpUstUserTypeRepository userTypeRepo;

	@Autowired
	private CustomClagRepository customClagRepo;

	@Autowired
	private CustomBpUSIDutyRepository customUsiDutyRepo;

	@Autowired
	private CalendarPeriodService calendarPeriodService;

	@Autowired
	private BpDfdlDifficultygradeService difficultygradeService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private BpLCPRepository bpLcpRepo;

	@Autowired
	private BpCalendarPeriodRepository capRepo;

	@Autowired
	private AccYearService accyService;

	@Autowired
	private BpUSIDutyService usiDutyService;

	@Autowired
	private BpBppProcessRepository bppProcessRepo;

	@Autowired
	private BpBpeBpEventRepository bpeBpEventRepo;

	@Autowired
	private BpUsiUserItemRepository userRepo;

	private final ModifyService modifyService;

	private final String DATE_PATTERN = "yyyy-MM-dd";

	private static Integer DEFAULT_VALUE = 0;

	private final String DTE = "DTE";
	private final String QO = "QO";
	private final String GTE = "GTE";
	private final String LTE = "LTE";

	public TeacherRegisToCapServiceImpl(@Lazy ModifyService modifyService) {
		this.modifyService = modifyService;
	}

	@Override
	public List<QuantityResponse> gteQuantity(Integer productId, Integer gradeId, Integer classLevelId,
											  String startDate, String endDate, Integer subjectId) {
		List<QuantityResponse> results = new ArrayList<>();
		List<RegistedProjection> listRegisted = listRegistedQuantity(GTE, productId, gradeId, classLevelId, startDate,
				endDate, subjectId);
		List<GteRequiredProjection> listGteRequired = customClagRepo.gteRequiredQuatity(productId, gradeId,
				classLevelId, startDate, endDate, subjectId);
		List<GetAssignedProjection> listGteAssigned = customUsiDutyRepo.gteAssignedQuatity(productId, gradeId,
				classLevelId, startDate, endDate, subjectId);
		List<String> listDate = fromToDateList(startDate, endDate);
		for (String strDay : listDate) {
			QuantityResponse response = QuantityResponse.builder().dateInput(strDay).registeredQuantity(0)
					.requiredQuantity(RequiredQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).build())
					.assignedQuantity(AssignedQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).backup(0)
							.mainConfirmed(0).build())
					.build();
			RegistedProjection registed = listRegisted.stream().filter(k -> k.getDateInWeek().equalsIgnoreCase(strDay))
					.findAny().orElse(null);
			if (Objects.nonNull(registed)) {
				response.setRegisteredQuantity(registed.getTotalTeacher());
			}
			List<GteRequiredProjection> requires = listGteRequired.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(requires)) {
				int main = requires.stream().mapToInt(GteRequiredProjection::getTotalTeacher).sum();
				Integer totalStudent = requires.stream().map(GteRequiredProjection::getTotalStudent)
						.filter(Objects::nonNull).findFirst().orElse(0);
				int mainA = requires.stream().filter(k -> "A".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GteRequiredProjection::getTotalTeacher).sum();
				int mainB = requires.stream().filter(k -> "B".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GteRequiredProjection::getTotalTeacher).sum();
				int mainC = requires.stream().filter(k -> "C".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GteRequiredProjection::getTotalTeacher).sum();
				int mainD = requires.stream().filter(k -> "D".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GteRequiredProjection::getTotalTeacher).sum();
				RequiredQuantity required = RequiredQuantity.builder()
						.totalStudent(totalStudent != 0 ? totalStudent : null).main(main).mainA(mainA).mainB(mainB)
						.mainC(mainC).mainD(mainD).build();
				response.setRequiredQuantity(required);
			}
			List<GetAssignedProjection> listAssigned = listGteAssigned.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(listAssigned)) {
				int totalMain = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(GetAssignedProjection::getTotalTeacher).sum();

				int totalMainA = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition())
								&& "A".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GetAssignedProjection::getTotalTeacher).sum();

				int totalMainB = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition())
								&& "B".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GetAssignedProjection::getTotalTeacher).sum();

				int totalMainC = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition())
								&& "C".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GetAssignedProjection::getTotalTeacher).sum();

				int totalMainD = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition())
								&& "D".equalsIgnoreCase(k.getMydfge()))
						.mapToInt(GetAssignedProjection::getTotalTeacher).sum();

				int totalBackup = listAssigned.stream()
						.filter(k -> PositionAssignEnum.BACKUP.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(GetAssignedProjection::getTotalTeacher).sum();

				AssignedQuantity assigned = AssignedQuantity.builder().main(totalMain).mainConfirmed(totalMain)
						.backup(totalBackup).mainA(totalMainA).mainB(totalMainB).mainC(totalMainC).mainD(totalMainD)
						.build();
				response.setAssignedQuantity(assigned);
			}

			results.add(response);
		}
		return results;
	}

	@Override
	public List<QuantityResponse> dteQuantity(Integer productId, Integer gradeId, Integer classLevelId,
											  String startDate, String endDate, Integer subjectId) {
		List<QuantityResponse> results = new ArrayList<>();
		List<DteAssignedProjection> listDteAssigned = customUsiDutyRepo.dteAssignedQuantity(productId, gradeId,
				classLevelId, startDate, endDate, subjectId);
		List<RegistedProjection> listRegisted = listRegistedQuantity(DTE, productId, gradeId, classLevelId, startDate,
				endDate, subjectId);
		List<DteRequiredProjection> listRequired = customClagRepo.dteRequiredQuatity(productId, gradeId, classLevelId,
				startDate, endDate, subjectId);
		List<String> listDate = fromToDateList(startDate, endDate);
		for (String strDay : listDate) {
			QuantityResponse response = QuantityResponse.builder().dateInput(strDay).registeredQuantity(0)
					.requiredQuantity(RequiredQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).build())
					.assignedQuantity(AssignedQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).backup(0)
							.mainConfirmed(0).build())
					.build();
			RegistedProjection registed = listRegisted.stream().filter(k -> k.getDateInWeek().equalsIgnoreCase(strDay))
					.findAny().orElse(null);
			if (Objects.nonNull(registed)) {
				response.setRegisteredQuantity(registed.getTotalTeacher());
			}
			List<DteAssignedProjection> listAssigned = listDteAssigned.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(listAssigned)) {
				int totalMain = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(DteAssignedProjection::getTotalTeacher).sum();
				int totalBackup = listAssigned.stream()
						.filter(k -> PositionAssignEnum.BACKUP.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(DteAssignedProjection::getTotalTeacher).sum();
				AssignedQuantity assigned = AssignedQuantity.builder().main(totalMain).backup(totalBackup).build();
				response.setAssignedQuantity(assigned);
			}
			DteRequiredProjection dteRequired = listRequired.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).findAny().orElse(null);
			if (Objects.nonNull(dteRequired)) {
				RequiredQuantity required = RequiredQuantity.builder().main(dteRequired.getTotalTeacher()).build();
				response.setRequiredQuantity(required);
			}
			results.add(response);
		}
		return results;
	}

	@Override
	public List<QuantityResponse> qoQuantity(Integer productId, Integer gradeId, Integer classLevelId, String startDate,
											 String endDate, Integer subjectId) {
		List<QuantityResponse> results = new ArrayList<>();
		List<DteAssignedProjection> listDteAssigned = customUsiDutyRepo.qoAssignedQuantity(productId, gradeId,
				classLevelId, startDate, endDate, subjectId);
		List<RegistedProjection> listRegisted = listRegistedQuantity(QO, productId, gradeId, classLevelId, startDate,
				endDate, subjectId);
		List<DteRequiredProjection> listRequired = customClagRepo.qoRequiredQuatity(productId, gradeId, classLevelId,
				startDate, endDate, subjectId);
		List<String> listDate = fromToDateList(startDate, endDate);
		for (String strDay : listDate) {
			QuantityResponse response = QuantityResponse.builder().dateInput(strDay).registeredQuantity(0)
					.requiredQuantity(RequiredQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).build())
					.assignedQuantity(AssignedQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).backup(0)
							.mainConfirmed(0).build())
					.build();
			RegistedProjection registed = listRegisted.stream().filter(k -> k.getDateInWeek().equalsIgnoreCase(strDay))
					.findAny().orElse(null);
			if (Objects.nonNull(registed)) {
				response.setRegisteredQuantity(registed.getTotalTeacher());
			}
			List<DteAssignedProjection> listAssigned = listDteAssigned.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(listAssigned)) {
				int totalMain = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(DteAssignedProjection::getTotalTeacher).sum();
				int totalBackup = listAssigned.stream()
						.filter(k -> PositionAssignEnum.BACKUP.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(DteAssignedProjection::getTotalTeacher).sum();
				AssignedQuantity assigned = AssignedQuantity.builder().main(totalMain).backup(totalBackup).build();
				response.setAssignedQuantity(assigned);
			}
			DteRequiredProjection qoRequired = listRequired.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).findAny().orElse(null);
			if (Objects.nonNull(qoRequired)) {
				RequiredQuantity required = RequiredQuantity.builder().main(qoRequired.getTotalTeacher()).build();
				response.setRequiredQuantity(required);
			}
			results.add(response);
		}
		return results;
	}

	@Override
	public List<QuantityResponse> lteQuantity(Integer productId, Integer gradeId, Integer classLevelId,
											  String startDate, String endDate, Integer subjectId) {
		List<QuantityResponse> results = new ArrayList<>();
		List<DteAssignedProjection> listDteAssigned = customUsiDutyRepo.lteAssignedQuantity(productId, gradeId,
				classLevelId, startDate, endDate, subjectId);
		List<RegistedProjection> listRegisted = listRegistedQuantity(LTE, productId, gradeId, classLevelId, startDate,
				endDate, subjectId);
		List<DteRequiredProjection> listRequired = customClagRepo.lteRequiredQuatity(productId, gradeId, classLevelId,
				startDate, endDate, subjectId);
		List<String> listDate = fromToDateList(startDate, endDate);
		for (String strDay : listDate) {
			QuantityResponse response = QuantityResponse.builder().dateInput(strDay).registeredQuantity(0)
					.requiredQuantity(RequiredQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).build())
					.assignedQuantity(AssignedQuantity.builder().main(0).mainA(0).mainB(0).mainC(0).mainD(0).backup(0)
							.mainConfirmed(0).build())
					.build();
			RegistedProjection registed = listRegisted.stream().filter(k -> k.getDateInWeek().equalsIgnoreCase(strDay))
					.findAny().orElse(null);
			if (Objects.nonNull(registed)) {
				response.setRegisteredQuantity(registed.getTotalTeacher());
			}
			List<DteAssignedProjection> listAssigned = listDteAssigned.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(listAssigned)) {
				int totalMain = listAssigned.stream()
						.filter(k -> PositionAssignEnum.MAIN.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(DteAssignedProjection::getTotalTeacher).sum();
				int totalBackup = listAssigned.stream()
						.filter(k -> PositionAssignEnum.BACKUP.getName().equalsIgnoreCase(k.getPosition()))
						.mapToInt(DteAssignedProjection::getTotalTeacher).sum();
				AssignedQuantity assigned = AssignedQuantity.builder().main(totalMain).backup(totalBackup).build();
				response.setAssignedQuantity(assigned);
			}
			DteRequiredProjection dteRequired = listRequired.stream()
					.filter(k -> k.getFromDay().equalsIgnoreCase(strDay)).findAny().orElse(null);
			if (Objects.nonNull(dteRequired)) {
				RequiredQuantity required = RequiredQuantity.builder().main(dteRequired.getTotalTeacher()).build();
				response.setRequiredQuantity(required);
			}
			results.add(response);
		}
		return results;
	}

	private List<RegistedProjection> listRegistedQuantity(String type, Integer productId, Integer gradeId,
														  Integer classLevelId, String startDate, String endDate, Integer subjectId) {
		if (type.equalsIgnoreCase(DTE)) {
			return customUsiDutyRepo.dteRegistedQuantity(productId, gradeId, classLevelId, startDate, endDate,
					subjectId);
		} else if (type.equalsIgnoreCase(GTE)) {
			return customUsiDutyRepo.gteRegistedQuantity(productId, gradeId, classLevelId, startDate, endDate,
					subjectId);
		} else if (type.equalsIgnoreCase(LTE)) {
			return customUsiDutyRepo.lteRegistedQuantity(productId, gradeId, classLevelId, startDate, endDate,
					subjectId);
		} else if (type.equalsIgnoreCase(QO)) {
			return customUsiDutyRepo.qoRegistedQuantity(productId, gradeId, classLevelId, startDate, endDate,
					subjectId);
		}
		return new ArrayList<>();
	}

	private static List<String> fromToDateList(String startDate, String endDate) {
		List<String> results = new ArrayList<>();
		String pattern = "yyyy-MM-dd";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDate start = LocalDate.parse(startDate, formatter);
		LocalDate end = LocalDate.parse(endDate, formatter);
		LocalDate next;
		if (end.isAfter(start)) {
			next = start;
			results.add(start.format(formatter));
			while ((next = next.plusDays(1)).isBefore(end)) {
				results.add(next.format(formatter));
			}
			results.add(end.format(formatter));
		} else if (start.isAfter(end)) {
			next = end;
			results.add(end.format(formatter));
			while ((next = end.plusDays(1)).isBefore(start)) {
				results.add(next.format(formatter));
			}
			results.add(start.format(formatter));
		} else {
			results.add(start.format(formatter));
		}
		return results;
	}

	@Override
	@Transactional
	public void publish(RegisToCapPublishRequest regisToCap, String userName) {
		List<Assign> listAssign = regisToCap.getAssigns();
		List<Assign> listUpdate = listAssign.stream().filter(k -> Objects.nonNull(k.getId()))
				.collect(Collectors.toList());
		Integer accTypeId = regisToCap.getUserAccountTypeId();
		BpUstUserType ust = userTypeRepo.findById(accTypeId).orElse(null);
		// Write BppProcess -> Declare BppType
		String bpp4Type = RegisterBpBppCommon.step4FindBppTypeByUserType(ust.getCode());
		BpBppProcess bpp4Process = saveBpp(bpp4Type, bpp4Type + "-" + System.currentTimeMillis());
		if (!CollectionUtils.isEmpty(listUpdate)) {
			List<BpUsiDuty> listUDuty = bpUsiDutyRepo
					.findAllById(listUpdate.stream().map(k -> k.getId()).collect(Collectors.toList()));
			for (Assign ass : listUpdate) {
				BpUsiDuty duty = listUDuty.stream().filter(k -> k.getId().equals(ass.getId())).findAny().orElse(null);
				if (Objects.nonNull(duty) && Objects.nonNull(ass.getTeachingCancelReasonId())) {
					duty.setIsDeleted(true);
					duty.setUnallocatedAt(DateUtils.now());
					duty.setUnallocatedMyusi(userName);
					duty.setPublished(false);
					duty.setTeacherCancelReason(ass.getTeacherCancelReason());
					Pair<String, String> pair4 = step4UpdateDateAndBpp(ass, ust.getCode(), duty.getMybpp());
					;
					duty.setUnpublishbps(pair4.getLeft());
					duty.setUnpublishbpe(pair4.getRight());
					BpUsiDuty usiDutyStep5 = bpUsiDutyRepo.findByMypreviouscode(duty.getCode()).orElse(null);
					if (Objects.nonNull(usiDutyStep5)) {
						Pair<String, String> pair5 = step5UpdateDateAndBpp(ass, ust.getCode(), duty.getMybpp());
						usiDutyStep5.setIsDeleted(true);
						usiDutyStep5.setUnallocatedAt(DateUtils.now());
						usiDutyStep5.setUnallocatedMyusi(userName);
						usiDutyStep5.setPublished(false);
						usiDutyStep5.setTeacherCancelReason(ass.getTeacherCancelReason());
						usiDutyStep5.setUnpublishbps(pair5.getLeft());
						usiDutyStep5.setUnpublishbpe(pair5.getRight());
					}
				}
			}
		}

		List<Assign> listNew = listAssign.stream().filter(k -> Objects.isNull(k.getId())).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(listNew)) {
			Integer productId = regisToCap.getProductId();
			Integer classLevelId = regisToCap.getClassLevelId();
			BpDfdlDifficultygrade dfdl = difficultyGradeService.findById(classLevelId);
			Integer gradeId = regisToCap.getGradeId();
			BpProcessRegisterDTO processRegis = extractProcessRegister(ust);
			String bpp5TypeBpp = RegisterBpBppCommon.step5FindBppTypeByUserType(ust.getCode());
			BpBppProcess bpp5Process = saveBpp(bpp5TypeBpp, bpp5TypeBpp + "-" + System.currentTimeMillis());
			for (Assign ass : listNew) {
				try {
					String strTDate = ass.getTeachingDate();
					Date tDate = DateUtils.parse(strTDate, DATE_PATTERN);
					AccYear accYear = accyService.findByTime(tDate);
					String termCode = Utils.getMyTermFromTime(tDate);
					Integer teacherPosition = ass.getTeachingPositionType();
					BpUsiDuty step3 = bpUsiDutyRepo.findFirstByPtGgTeacherIdAccTermAndRegisDate(
							processRegis.getStep3BppEnum().getLikeStatement(), productId, gradeId, ass.getTeacherId(),
							accYear.getCode(), termCode, strTDate).orElse(null);
					if (Objects.nonNull(step3)) {
						CalendarPeriod cap4 = capRepo.step4FindCapByLcpCodeAndCashStaCode(step3.getMyLcp(),
								step3.getMycashsta(), step3.getMypt(), LocalDate.parse(ass.getTeachingDate()))
								.orElse(null);
						CalendarPeriod cap5 = Objects.nonNull(cap4) ? capRepo
								.step5FindCapByLcpParentCapAndLcpCode(cap4.getCode(), step3.getMyLcp()).orElse(null)
								: null;
						PositionAssignEnum positionEnum = PositionAssignEnum.findById(teacherPosition);
						if (Objects.nonNull(cap4) && Objects.nonNull(cap5) && Objects.nonNull(positionEnum)) {
							Integer afterMinute = bpLcpRepo.calculateAfterMinuteByCode(step3.getMyLcp()).orElse(0);
							String step4Code = bppRegisCode(processRegis.getStep4BppEnum().getName(), step3.getMyLcp(),
									step3.getMyUsi());
							BpUsiDuty step4 = BpUsiDuty.builder().code(step4Code).myUsi(step3.getMyUsi())
									.publishedAt(DateUtils.now()).mybpp(bpp4Process.getCode()).myUst(step3.getMyUst())
									.mywso(step3.getMywso()).allocatedAt(DateUtils.now()).allocatedMyusi(userName)
									.myaccyear(step3.getMyaccyear()).myterm(step3.getMyterm()).mypt(step3.getMypt())
									.mygg(step3.getMygg()).mydfdl(dfdl.getCode()).mydfge(step3.getMydfge())
									.myUst(step3.getMyUst()).myLcet(step3.getMyLcet()).myLcp(step3.getMyLcp())
									.myCap(cap4.getCode()).mypreviouscode(step3.getCode()).isApproved(true)
									.position(positionEnum.getName()).mycashsta(step3.getMycashsta())
									.startTime(step3.getStartTime()).approvedAt(step3.getApprovedAt()).published(true)
									.endTime(step3.getEndTime()).mycashstr(step3.getMycashstr()).build();
							if (ust.getCode().equalsIgnoreCase(GTE)) {
								step4.setMydfge(ass.getCategory());
							}
							Pair<String, String> result4 = saveBpsBpe(
									RegisterBpBppCommon.step4FindBpsInsertByUserType(ust.getCode()),
									RegisterBpBppCommon.step4FindBpeInsertByUserType(ust.getCode()), step3.getMyUsi(),
									bpp4Process.getCode());
							// Save step 4
							step4.setMybps(result4.getLeft());
							step4.setPublishbps(result4.getLeft());
							step4.setMybpe(result4.getRight());
							step4.setPublishbpe(result4.getRight());
							bpUsiDutyRepo.save(step4);

							String step5Code = bppRegisCode(processRegis.getStep5BppEnum().getName(), step3.getMyLcp(),
									step3.getMyUsi());
							BpUsiDuty step5 = BpUsiDuty.builder().myUsi(step3.getMyUsi()).myUst(step3.getMyUst())
									.mybpp(bpp5Process.getCode()).publishedAt(DateUtils.now()).code(step5Code)
									.mywso(step3.getMywso()).allocatedAt(DateUtils.now()).allocatedMyusi(userName)
									.myaccyear(step3.getMyaccyear()).myterm(step3.getMyterm()).mypt(step3.getMypt())
									.mygg(step3.getMygg()).mydfdl(step4.getMydfdl()).mydfge(step4.getMydfge())
									.myUst(step3.getMyUst()).myLcet(step3.getMyLcet()).myLcp(step3.getMyLcp())
									.myCap(cap5.getCode()).mypreviouscode(step3.getCode()).isApproved(true)
									.position(positionEnum.getName()).mycashstr(step3.getMycashstr())
									.mypreviouscode(step4Code).mycashsta(step3.getMycashsta())
									.startTime(step3.getStartTime()).endTime(step3.getEndTime()).mydfdl(dfdl.getCode())
									.approvedAt(step3.getApprovedAt()).mycashsta(step3.getMycashsta()).published(true)
									.mycassstr(calculateMyStrsss(step3.getMycashsta(), afterMinute)).build();

							Pair<String, String> result5 = saveBpsBpe(
									RegisterBpBppCommon.step5FindBpsInsertByUserType(ust.getCode()),
									RegisterBpBppCommon.step5FindBpeInsertByUserType(ust.getCode()), step3.getMyUsi(),
									bpp5Process.getCode());
							step5.setMybps(result5.getLeft());
							step5.setPublishbps(result5.getLeft());
							step5.setMybpe(result5.getRight());
							step5.setPublishbpe(result5.getRight());
							bpUsiDutyRepo.save(step5);
						} else {
							throw new RuntimeException("Cant process because cant found cap4, cap5 or teacherPosition");
						}
					} else {
						throw new RuntimeException("Cant process because cant found BpUsiDuty in Step3");
					}
				} catch (Exception e) {
					throw new RuntimeException("Error when execute insert data" + e);
				}
			}
		}

		//modify teacher CIB
		if (!CollectionUtils.isEmpty(listUpdate) && !CollectionUtils.isEmpty(listNew)) {
			CompletableFuture.runAsync(() -> {
				try {
					BpPTProductType pt = ptService.findById(regisToCap.getProductId());
					listUpdate.forEach(cancel -> {
						try {
							String strTDate = cancel.getTeachingDate();
							Date tDate = DateUtils.parse(strTDate, DATE_PATTERN);
							Assign add = listNew.stream().filter(e -> e.getTeachingDate()
									.equals(cancel.getTeachingDate())).findAny().orElse(null);
							if (Objects.isNull(add)) return;
							CalendarPeriod cady = calendarPeriodService
									.getCAPByTime(new Timestamp(tDate.getTime()), CalendarPeriodTypeEnum.DAY.getCode());
							modifyService.bppSyncTE(
									usiUserItemService.findById(cancel.getTeacherId()).getCode(),
									usiUserItemService.findById(add.getTeacherId()).getCode(),
									cady,
									Objects.isNull(pt) ? null : pt.getCode()
							);
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException("ERROR when change and reschedule for Teacher :{}" + DebuggingDTO.build(e));
				}
			});
		}
	}

	private BpProcessRegisterDTO extractProcessRegister(BpUstUserType ust) {
		BpProcessRegisterDTO result = BpProcessRegisterDTO.builder().step3BppEnum(BppRegisterEnum.DTE3_CONFIRM)
				.step4BppEnum(BppRegisterEnum.DTE4_ALLOCATE).step5BppEnum(BppRegisterEnum.DTE5_TRANSFORM).build();
		String ustCode = ust.getCode();
		if (ustCode.equalsIgnoreCase(GTE)) {
			result.setStep3BppEnum(BppRegisterEnum.GTE3_CONFIRM);
			result.setStep4BppEnum(BppRegisterEnum.GTE4_ALLOCATE);
			result.setStep5BppEnum(BppRegisterEnum.GTE5_TRANSFORM);
		} else if (ustCode.equalsIgnoreCase(QO)) {
			result.setStep3BppEnum(BppRegisterEnum.QO3_CONFIRM);
			result.setStep4BppEnum(BppRegisterEnum.QO4_ALLOCATE);
			result.setStep5BppEnum(BppRegisterEnum.QO5_TRANSFORM);
		} else if (ustCode.equalsIgnoreCase(LTE)) {
			result.setStep3BppEnum(BppRegisterEnum.LTE3_CONFIRM);
			result.setStep4BppEnum(BppRegisterEnum.LTE4_ALLOCATE);
			result.setStep5BppEnum(BppRegisterEnum.LTE5_TRANSFORM);
		}
		return result;
	}

	private Pair<String, String> saveBpsBpe(String bpsType, String bpeType, String myUsi, String mybpp) {
		String bpsCode = bpsType + "-" + myUsi + "-" + System.currentTimeMillis();
		BpBpsStep step = saveBps(bpsType, bpsCode, mybpp);
		BpBpeEvent event = saveBpe(bpeType, bpeType + "-" + myUsi + "-" + System.currentTimeMillis(), bpsCode);
		return Pair.of(step.getCode(), event.getCode());
	}

	private Pair<String, String> step4UpdateDateAndBpp(Assign ass, String myust, String mybpp) {
		// Step 4 Need to insert bpsType
		String bps4UpdateType = RegisterBpBppCommon.step4FindBpsUpdateByUserType(myust);
		String bpe4UpdateType = RegisterBpBppCommon.step4FindBpeUpdateByUserType(myust);
		BpUsiUserItem uItem = userRepo.findById(Long.valueOf(ass.getTeacherId())).orElse(null);
		String bps4Code = bps4UpdateType + "-" + uItem.getCode() + "-" + System.currentTimeMillis();
		BpBpsStep process = saveBps(bps4UpdateType, bps4Code, mybpp);
		// Save BpeType for Step4
		BpBpeEvent event = saveBpe(bpe4UpdateType,
				bpe4UpdateType + "-" + uItem.getCode() + "-" + System.currentTimeMillis(), bps4Code);
		return Pair.of(process.getCode(), event.getCode());
	}

	private Pair<String, String> step5UpdateDateAndBpp(Assign ass, String myust, String mybpp) {
		// Step 5 Need to insert bpsType
		String bps5UpdateType = RegisterBpBppCommon.step5FindBpsInsertByUserType(myust);
		String bpe5UpdateType = RegisterBpBppCommon.step5FindBpeInsertByUserType(myust);
		BpUsiUserItem uItem = userRepo.findById(Long.valueOf(ass.getTeacherId())).orElse(null);
		String bps5Code = bps5UpdateType + "-" + uItem.getCode() + "-" + System.currentTimeMillis();
		BpBpsStep process = saveBps(bps5UpdateType, bps5Code, mybpp);
		// Save BpeType for Step5
		BpBpeEvent event = saveBpe(bpe5UpdateType,
				bpe5UpdateType + "-" + uItem.getCode() + "-" + System.currentTimeMillis(), bps5Code);
		return Pair.of(process.getCode(), event.getCode());
	}

	private String bppRegisCode(String bpp, String lcpCode, String myUsi) {
		StringBuilder builder = new StringBuilder(bpp);
		builder.append("-").append(lcpCode).append("-").append(myUsi).append("-").append(System.currentTimeMillis());
		return builder.toString();
	}

	private String calculateMyStrsss(String myCashstr, Integer addMinute) {
		if (!StringUtils.isBlank(myCashstr) && myCashstr.length() == 4) {
			Integer hour = Integer.parseInt(myCashstr.substring(0, 2));
			Integer minute = Integer.parseInt(myCashstr.substring(2, myCashstr.length()));
			Integer addHour = (minute + (Objects.nonNull(addMinute) ? addMinute : 0)) / 60;
			if (addHour > 0) {
				hour = (hour + addHour) % 24;
			}
			Integer nextMinute = (minute + (Objects.nonNull(addMinute) ? addMinute : 0)) % 60;
			String strHour = String.valueOf(hour);
			String strMinute = String.valueOf(nextMinute);
			return ((strHour.length() == 1) ? "0" + strHour : strHour)
					+ ((strMinute.length() == 1) ? "0" + strMinute : strMinute);
		}
		return null;
	}

	private BpBppProcess saveBpp(String bppType, String bppCode) {
		return bppProcessRepo.save(BpBppProcess.builder().bpptype(bppType).name(bppCode).code(bppCode).build());
	}

	private BpBpsStep saveBps(String bpsType, String bpsCode, String mybpp) {
		return bpBpsStepRepository
				.save(BpBpsStep.builder().bpstype(bpsType).name(bpsCode).myprocess(mybpp).code(bpsCode).build());
	}

	private BpBpeEvent saveBpe(String bpeType, String code, String bps) {
		return bpeBpEventRepo.save(BpBpeEvent.builder().code(code).name(code).bpetype(bpeType).mybps(bps).build());
	}

	@SneakyThrows
	@Override
	public BPProductGradeClassLevelResponse getListProductGradeClassLevel(String startDate) {

		List<BpPTProductType> productTypeList = ptService.findProductTeacherScheduleAssign();
		List<BpGGGradeGroup> gradeGroupList = gradeGroupService.findAll();

		List<BpDfdlDifficultygrade> classLevelList = difficultyGradeService.findAll();

		List<GradeDetail> gradeDetails;
		Date date = Objects.isNull(startDate) ? new Date() : DateUtils.parse(startDate, DATE_PATTERN);
		String myTerm = Utils.getMyTermFromTime(date);
		if (Objects.equals(myTerm, TermEnum.ST.getName())) {
			gradeDetails = gradeGroupList.stream()
					.map(gradeGroup -> GradeDetail.builder().id(gradeGroup.getId()).code(gradeGroup.getCode()).name(gradeGroup.getSummerName()).build()).collect(Collectors.toList());
		} else {
			gradeDetails = gradeGroupList.stream()
					.map(gradeGroup -> modelMapper.map(gradeGroup, GradeDetail.class)).collect(Collectors.toList());

		}
		BPProductGradeClassLevelResponse response = BPProductGradeClassLevelResponse.builder().products(productTypeList)
				.grades(gradeDetails).classLevels(classLevelList).build();

		return response;

	}

	@Override
	public GeneralPageResponse<UserAccountResponse> findAllTeacherWithFilter(String name, Pageable pageable) {
		Page<BpUsiUserItem> teList = usiUserItemService.findTE(name, pageable);
		return GeneralPageResponse.toResponse(teList,
				usi -> UserAccountResponse.builder().id(usi.getId()).code(usi.getCode()).username(usi.getUsername())
						.firstName(usi.getFirstname()).lastName(usi.getLastname()).fullName(usi.getFullname()).build());
	}

	@Override
	public BpTeachingScheduleAssigningSlotGroupResponse getAvailableSlots(String bpp) {
		Set<AvailableSlotsProjection> availableSlots = bpUsiDutyRepo.findAvailableSlots(bpp);

		Set<Integer> products = new HashSet<>();
		Set<BpTeachingScheduleAssigningSlotResponse> productGrades = new HashSet<>();
		Set<BpTeachingScheduleAssigningSlotResponse> teachingScheduleAssigningSlotResponses = availableSlots.stream()
				.map(s -> {
					String mypt = s.getMyPt();
					Integer myptId = ptService.findByCode(mypt).getId();

					String mygg = s.getMyGg();
					Integer myggId = gradeGroupService.findByCode(mygg).getId();

					Integer dfdlId = difficultygradeService.findByCode(s.getMyDfdl()).getId();

					products.add(myptId);

					BpTeachingScheduleAssigningSlotResponse productGrade = BpTeachingScheduleAssigningSlotResponse
							.builder().productId(myptId).gradeId(myggId).build();

					productGrades.add(productGrade);

					return BpTeachingScheduleAssigningSlotResponse.builder().productId(myptId).gradeId(myggId)
							.classLevelId(dfdlId).build();
				}).collect(Collectors.toSet());
		BpTeachingScheduleAssigningSlotGroupResponse teachingScheduleAssigningSlotResponse = BpTeachingScheduleAssigningSlotGroupResponse
				.builder().products(products).productGrades(productGrades)
				.productGradeClassLevels(teachingScheduleAssigningSlotResponses).build();
		return teachingScheduleAssigningSlotResponse;
	}

	@Override
	public List<TeachingScheduleAssigningQuantityResponse> dteQuantity(TeachingScheduledQuantityRequest request) {
		Timestamp startDate = DateUtils.startOfDay(Timestamp.valueOf(request.getStartDate().atStartOfDay()));
		Timestamp endDate = DateUtils.endOfDay(Timestamp.valueOf(request.getEndDate().atStartOfDay()));

		String myptCode = Objects.isNull(request.getProductId()) ? null
				: ptService.findById(request.getProductId()).getCode();
		String myggCode = Objects.isNull(request.getGradeId()) ? null
				: gradeGroupService.findById(request.getGradeId()).getCode();
		String dfdlCode = Objects.isNull(request.getClassLevelId()) ? null
				: difficultygradeService.findById(request.getClassLevelId()).getCode();

		List<UsidDTERequiredProjection> dteRequireds = bpUsiDutyRepo.findDTERequired(startDate, endDate,
				BppRegisterEnum.DTE1_SETTING.getName(), request.getMyust(), myptCode, myggCode, dfdlCode);
		List<UsidDTERegisterProjection> dteRegistereds = bpUsiDutyRepo.findDTERegistered(startDate, endDate,
				BppRegisterEnum.DTE3_CONFIRM.getName(), request.getMyust(), myptCode, myggCode, dfdlCode);
		List<UsidDTEAssignProjection> bpUsiDutiesAssign = bpUsiDutyRepo.findDTEAssign(startDate, endDate,
				BppRegisterEnum.DTE4_ALLOCATE.getName(), request.getMyust(), myptCode, myggCode, dfdlCode);

		Map<Integer, List<UsidDTEAssignProjection>> usidDTEAssignMap = bpUsiDutiesAssign.stream()
				.collect(Collectors.groupingBy(o -> {
					String myCap = o.getMyCap();

					String dayOfWeek = calendarPeriodService.findByCode(myCap).getStartTime().toLocalDateTime()
							.toLocalDate().getDayOfWeek().toString().toLowerCase();
					return WSOEnum.findByObjectCode(dayOfWeek).getNumberOfWeek();
				}));

		List<TeachingScheduleAssigningQuantityResponse> responses = LongStream
				.range(0, DAYS.between(request.getStartDate(), request.getEndDate()) + 1)
				.mapToObj(i -> request.getStartDate().plusDays(i)).map(date -> {
					String dayOfWeek = date.getDayOfWeek().toString().toLowerCase();
					Integer wsoMapping = WSOEnum.findByObjectCode(dayOfWeek).getNumberOfWeek();
					Integer requiredQuantity = dteRequireds.stream()
							.filter(usidDTERequiredProjection -> Integer
									.parseInt(usidDTERequiredProjection.getMyWso()) == wsoMapping)
							.findFirst().map(UsidDTERequiredProjection::getRequiredQuantity).orElse(DEFAULT_VALUE);
					Integer registerQuantity = dteRegistereds.stream()
							.filter(usidDTERegisterProjection -> Integer
									.parseInt(usidDTERegisterProjection.getMyWso()) == wsoMapping)
							.findFirst().map(UsidDTERegisterProjection::getRegisteredQuantity).orElse(DEFAULT_VALUE);

					List<UsidDTEAssignProjection> usidDTEAssignProjections = usidDTEAssignMap.get(wsoMapping);
					int mainAssign = Objects.isNull(usidDTEAssignProjections) ? DEFAULT_VALUE
							: getQuantity(usidDTEAssignProjections, PositionAssignEnum.MAIN);
					int backupAssign = Objects.isNull(usidDTEAssignProjections) ? DEFAULT_VALUE
							: getQuantity(usidDTEAssignProjections, PositionAssignEnum.BACKUP);
					return TeachingScheduleAssigningQuantityResponse.builder().date(date)
							.registeredQuantity(requiredQuantity)
							.requiredQuantity(TeacherQuantityResponse.builder().main(registerQuantity)
									.backup(DEFAULT_VALUE).build())
							.assignedQuantity(
									TeacherAssignedQuantityResponse.builder().main(mainAssign).mainConfirmed(mainAssign)
											.mainTemporary(DEFAULT_VALUE).backup(backupAssign).build())
							.build();
				}).collect(Collectors.toList());
		return responses;
	}

	private static Integer getQuantity(List<UsidDTEAssignProjection> usidDTEAssignProjections,
									   PositionAssignEnum main) {
		return usidDTEAssignProjections.stream()
				.filter(usidDTEAssignProjection -> usidDTEAssignProjection.getPosition().equals(main.getName()))
				.findFirst().map(UsidDTEAssignProjection::getQuantity).orElse(DEFAULT_VALUE);
	}

	@SneakyThrows
	@Override
	public GeneralPageResponse<TeacherApprovedAndReportResponse> getAssignee(TeacherRegisterToCapAssigneeFilter filter,
																			 Pageable pageable) {
		String ust = userTypeRepo.findById(filter.getUserAccountTypeId().intValue()).orElseThrow(
				() -> new NotFoundException("Not found ust with id: " + filter.getUserAccountTypeId().intValue()))
				.getCode();
		String myTerm = Utils
				.getMyTermFromTime(DateUtils.parse(filter.getStartDate().toString(), DATE_PATTERN));
		Page<TeacherAssigneeProjection> teacherAssigneePage = bpUsiDutyRepo.getTeacherAssignee(ust,
				CollectionUtils.isEmpty(filter.getTeacherIds()) ? null : filter.getTeacherIds(),
				filter.getProductId() != null ? Collections.singleton(filter.getProductId()) : null,
				filter.getGradeId() != null ? Collections.singleton(filter.getGradeId()) : null, filter.getStartDate(),
				filter.getEndDate(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), myTerm);
		Set<Long> teacherIds = teacherAssigneePage.stream().map(TeacherAssigneeProjection::getUsidId)
				.collect(Collectors.toSet());
		List<TeacherAssigneeProjection> teacherAssigneeDetails = CollectionUtils.isEmpty(teacherIds)
				? Collections.emptyList()
				: bpUsiDutyRepo.getTeacherAssigneeDetails(ust, teacherIds,
				filter.getProductId() != null ? Collections.singleton(filter.getProductId()) : null,
				filter.getGradeId() != null ? Collections.singleton(filter.getGradeId()) : null,
				filter.getStartDate(), filter.getEndDate(), myTerm);
		Map<Long, List<RequestApprovedAndScheduledInfo>> teacherAssigneeDetailsMap = teacherAssigneeDetails.stream()
				.collect(
						Collectors
								.groupingBy(TeacherAssigneeProjection::getUsiId,
										Collectors
												.mapping(
														item -> RequestApprovedAndScheduledInfo.builder()
																.productId(item.getPtId()).gradeId(item.getGgId())
																.subjectId(1L).teachingDate(item.getDate())
																.teachingScheduleAssigneeId(item.getUsidId())
																.classLevelId(item.getDfdlId())
																.teachingPositionType(item.getPosition() == null ? null
																		: item.getPosition().equals("BACKUP") ? 2L : 1L)
																.status(item.getUsidId() != null ? "CONFIRMED" : null)
																.category(item.getDfgeCode()).build(),
														Collectors.toList())));
		return GeneralPageResponse.toResponse(teacherAssigneePage,
				item -> TeacherApprovedAndReportResponse.builder().teacherId(item.getUsiId())
						.teacherUsername(item.getUsiCode()).teacherFullname(item.getUsiFullname())
						.teachingSchedulesInPeriod(
								teacherAssigneeDetailsMap.getOrDefault(item.getUsiId(), Collections.emptyList()))
						.totalScheduleRegisterBasic(item.getSumApproved())
						.totalScheduleRegisterPlus(item.getSumApproved()).totalArrangedIsMain(item.getSumMainAssigned())
						.totalArrangedIsBackup(item.getSumBackupAssigned()).build());
	}

	@Override
	public List<UserTypeResponse> findAll() {
		return userTypeRepo.findAll().stream()
				.map(bpUstUserType -> modelMapper.map(bpUstUserType, UserTypeResponse.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<ChildResponse> getAllMenu() {
		ChildResponse childrenEM = new ChildResponse();
		childrenEM.setLabel("EM");
		childrenEM.setKey("EM");
		childrenEM.setEnable(false);

		ChildResponse childrenTE = new ChildResponse();
		childrenTE.setLabel("TE");
		childrenTE.setKey("TE");
		childrenTE.setEnable(true);

		ChildResponse register4Allocate = new ChildResponse();
		List<ChildResponse> childResponses = new ArrayList<>();
		childResponses.add(childrenTE);
		childResponses.add(childrenEM);
		register4Allocate.setChildren(childResponses);
		register4Allocate.setLabel("Register4-Allocate");
		register4Allocate.setKey("Register4-Allocate");
		register4Allocate.setEnable(true);

		ChildResponse register1Setting = new ChildResponse();
		register1Setting.setLabel("Register1-Setting");
		register1Setting.setKey("Register1-Setting");
		register1Setting.setEnable(false);

		ChildResponse register2Request = new ChildResponse();
		register2Request.setLabel("Register2-Request");
		register2Request.setKey("Register2-Request");
		register2Request.setEnable(false);

		ChildResponse register3Confirm = new ChildResponse();
		register3Confirm.setLabel("Register3-Confirm");
		register3Confirm.setKey("Register3-Confirm");
		register3Confirm.setEnable(false);

		ChildResponse register5Transform = new ChildResponse();
		register5Transform.setLabel("Register5-Transform");
		register5Transform.setKey("Register5-Transform");
		register5Transform.setEnable(false);

		List<ChildResponse> lstRegisterResponse = new ArrayList<>();
		lstRegisterResponse.add(register1Setting);
		lstRegisterResponse.add(register2Request);
		lstRegisterResponse.add(register3Confirm);
		lstRegisterResponse.add(register4Allocate);
		lstRegisterResponse.add(register5Transform);

		ChildResponse register = new ChildResponse();
		register.setChildren(lstRegisterResponse);
		register.setLabel("Register");
		register.setKey("Register");
		register.setEnable(null);

		ChildResponse scheduleMonth = new ChildResponse();
		scheduleMonth.setEnable(true);
		scheduleMonth.setLabel("Month Calendar");
		scheduleMonth.setKey("Month Calendar");

		ChildResponse scheduleWeek = new ChildResponse();
		scheduleWeek.setEnable(false);
		scheduleWeek.setLabel("Week Calendar");
		scheduleWeek.setKey("Week Calendar");

		ChildResponse scheduleShift = new ChildResponse();
		scheduleShift.setEnable(true);
		scheduleShift.setLabel("Shift");
		scheduleShift.setKey("Shift");

		List<ChildResponse> lstScheduleResponse = new ArrayList<>();
		lstScheduleResponse.add(scheduleMonth);
		lstScheduleResponse.add(scheduleWeek);
		lstScheduleResponse.add(scheduleShift);

		ChildResponse schedule = new ChildResponse();
		schedule.setChildren(lstScheduleResponse);
		schedule.setLabel("Schedule");
		schedule.setKey("Schedule");
		schedule.setEnable(null);

		List<ChildResponse> all = new ArrayList<>();
		all.add(register);
		all.add(schedule);

		return all;
	}
}
