package vn.edu.clevai.bplog.service.clag.impl;

import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.dto.cep200.*;
import vn.edu.clevai.bplog.entity.BpDfdlDifficultygrade;
import vn.edu.clevai.bplog.entity.BpGGGradeGroup;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100LearningScheduleClassResponse;
import vn.edu.clevai.bplog.repository.*;
import vn.edu.clevai.bplog.service.clag.CEP200Service;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.exception.ConflictException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CEP200ServiceImpl implements CEP200Service {

	@Autowired
	private BpUsiUserItemRepository userItemRepo;

	@Autowired
	private BpGGGradeGroupRepository bpGradeGroupRepo;

	@Autowired
	private BpDfdlDifficultygradeRepository dfdlRepo;

	@Autowired
	private BpPTProductTypeRepository ptProductTypeRepo;

	@Autowired
	private BpPodProductOfDealRepository podRepo;

	@Autowired
	private BpWsoWeeklyscheduleoptionRepository wsoRepo;

	@Override
	public CEP200StudentDTO getStudent(String podCode) {
		BpPodProductOfDeal deal = podRepo.findByCode(podCode).orElse(null);
		if (Objects.nonNull(deal)) {
			String myst = deal.getMyst();
			if (!StringUtils.isBlank(myst)) {
				return userItemRepo.findByCode(myst).map(k -> CEP200StudentDTO.builder().studentId(k.getId())
						.username(k.getCode()).myst(myst).build()).orElse(null);
			}
		}
		return null;
	}

	@Override
	public CEP200GradeGroupDTO getCEP200GradeGroup(Integer cep100GradeId) {
		Assert.notNull(cep100GradeId, "cep100GradeId must be not null");
		BpGGGradeGroup bbGG = bpGradeGroupRepo.findByCep100GradeId(cep100GradeId.toString()).orElse(null);
		return CEP200GradeGroupDTO.builder().id(bbGG.getId()).cep100GradeId(bbGG.getCep100GradeId())
				.name(bbGG.getName()).code(bbGG.getCode()).build();
	}

	@Override
	public CEP200WsoDTO getCEP200Wso(List<CEP100LearningScheduleClassResponse> listLearningSchedules) {
		if (CollectionUtils.isEmpty(listLearningSchedules)) {
			throw new ConflictException("List learning schedule is empty");
		}
		return wsoRepo
				.findByCode(generateWsoCode(listLearningSchedules)).map(c -> CEP200WsoDTO.builder()
						.code(c.getCode())
						.id(c.getId()).monday(c.getMonday()).tuesday(c.getTuesday()).wednesday(c.getWednesday())
						.thursday(c.getThursday()).friday(c.getFriday()).saturday(c.getSaturday()).sunday(c.getSunday())
						.build())
				.orElse(null);
	}

	@Override
	public CEP200DfdlDTO getCEP200DFDL(Integer cep100ClassLevelId) {
		String dfdlCode = Cep100TransformUtils.toDfdlCode(cep100ClassLevelId);
		if (!StringUtils.isBlank(dfdlCode)) {
			BpDfdlDifficultygrade dfdl = dfdlRepo.findByCode(dfdlCode).orElse(null);
			if (Objects.nonNull(dfdl)) {
				return CEP200DfdlDTO.builder().code(dfdl.getCode()).name(dfdl.getName())
						.description(dfdl.getDescription()).build();
			}
		}
		return null;
	}

	@Override
	public CEP200PTDTO getProductType(Integer trainingTypeId) {
		String ptCode = Cep100TransformUtils.toPtCode(trainingTypeId);
		if (StringUtils.isBlank(ptCode))
			return null;
		return ptProductTypeRepo.findByCode(ptCode)
				.map(k -> CEP200PTDTO.builder().id(k.getId()).name(k.getName()).code(k.getCode()).build()).orElse(null);
	}

	private String generateWsoCode(List<CEP100LearningScheduleClassResponse> listLearningSchedules) {
		listLearningSchedules.sort(Comparator.comparing(CEP100LearningScheduleClassResponse::getDayOfWeek));
		return listLearningSchedules.stream().map(k -> k.getDayOfWeek().toString()).collect(Collectors.joining(""));
	}
}
