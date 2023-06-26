package vn.edu.clevai.bplog.service.clag.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BpeEventTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.BpsStepTypeEnum;
import vn.edu.clevai.bplog.dto.cep200.CEP200DfdlDTO;
import vn.edu.clevai.bplog.dto.cep200.CEP200DfgeDTO;
import vn.edu.clevai.bplog.dto.cep200.CEP200GradeGroupDTO;
import vn.edu.clevai.bplog.dto.cep200.CEP200PTDTO;
import vn.edu.clevai.bplog.dto.cep200.CEP200StudentDTO;
import vn.edu.clevai.bplog.dto.cep200.CEP200WsoDTO;
import vn.edu.clevai.bplog.dto.cep200.ClagInputDTO;
import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.entity.BpPODCLAG;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100ClassLevelResponse;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100LearningScheduleClassResponse;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100StudentClassResponse;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100StudentGradeResponse;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100StudentResponse;
import vn.edu.clevai.bplog.repository.BpPODCLAGRepository;
import vn.edu.clevai.bplog.repository.BpPodProductOfDealRepository;
import vn.edu.clevai.bplog.repository.BpUsiUserItemRepository;
import vn.edu.clevai.bplog.repository.bplog.BpCuiContentUserUlcRepository;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.service.BpBpeEventService;
import vn.edu.clevai.bplog.service.BpBpsStepService;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.PermanentClagMigrationService;
import vn.edu.clevai.bplog.service.clag.CEP100Service;
import vn.edu.clevai.bplog.service.clag.CEP200Service;
import vn.edu.clevai.bplog.service.clag.ClagFacadeService;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.payload.request.Cep100ChangeWsoRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfdlResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpGGResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpWsoResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.ClagResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.SessionGroupStudentResponse;

@Service
public class ClagFacadeServiceImpl implements ClagFacadeService {

	@Autowired
	private CEP100Service cep100Service;

	@Autowired
	private CEP200Service cep200Service;

	@Autowired
	private PermanentClagMigrationService permanentClagMigrationService;

	@Autowired
	private BpClagClassgroupService bpClagService;

	@Autowired
	private BpPODCLAGRepository bpPodClagRepo;

	@Autowired
	private BpPodProductOfDealRepository bpPodRepo;

	@Autowired
	private BpCuiContentUserUlcRepository bpCuiRepo;

	@Autowired
	private BpUniqueLearningComponentRepository bpUlcRepo;

	@Autowired
	private BpUsiUserItemRepository bpUsiRepo;
	
	@Autowired
	private CuiService cuiService;
	
	@Autowired
	private CuiEventService cuiEventService;
	
	@Autowired
	private BpBpsStepService bpsStepService;

	@Autowired
	private BpBpeEventService bpeEventService;
	
	@Override
	@WriteUnitTestLog
	public ClagResponse BPGetCLAG(String podCode, String clagTypeCode) {
		ClagInputDTO dto = buildInputDTO(podCode, clagTypeCode);
		return getClagByInputObject(dto);
	}

	private ClagInputDTO buildInputDTO(String podCode, String clagType) {
		ClagInputDTO result = new ClagInputDTO();
		result.setClagType(clagType);
		CEP200StudentDTO studentDTO = cep200Service.getStudent(podCode);
		if (Objects.nonNull(studentDTO)) {
			CEP100StudentResponse cep100StudentRes = cep100Service.getCEPStudent(studentDTO.getUsername());
			CEP100StudentGradeResponse studentGrade = cep100Service
					.getCEP100StudentGrade(cep100StudentRes.getStudentId());
			SessionGroupStudentResponse sessionGroup = cep100Service
					.getSessionGroupStudent(cep100StudentRes.getStudentId());
			CEP100StudentClassResponse clsResponse = cep100Service.getCEPStudentClass(cep100StudentRes.getStudentId(),
					ClagType.PERMANANT.getCode());
			if (Objects.nonNull(clsResponse)) {
				// Grade group DTO
				CEP200GradeGroupDTO gradeGroupDTO = cep200Service.getCEP200GradeGroup(studentGrade.getGradeId());
				result.setGgDto(gradeGroupDTO);
				List<CEP100LearningScheduleClassResponse> listLearningSchedules = cep100Service
						.getCEP100LearningSchedule(cep100StudentRes.getStudentId());
				// WSO
				result.setWsoDTO(cep200Service.getCEP200Wso(listLearningSchedules));
				// Product Type
				Integer trainingTypeId = cep100Service.getCEP100TrainingTypeId(clsResponse.getClassId());
				result.setPtDto(cep200Service.getProductType(trainingTypeId));
				// DFDL
				result.setClassCodeIndex(cep100Service.getCEP100ClassCodeIndex(clsResponse.getClassId()));
				CEP100ClassLevelResponse clsLevel = cep100Service.getCEP100ClassLevel(clsResponse.getClassId());
				CEP200DfdlDTO dfdl = cep200Service.getCEP200DFDL(clsLevel.getId());
				result.setDfdlDto(dfdl);
				CEP200DfgeDTO dfge = CEP200DfgeDTO.builder()
						.dfgeCode(Cep100TransformUtils.toDfgeCode(sessionGroup.getCategory())).podCode(podCode)
						.usiCode(studentDTO.getMyst()).build();
				result.setDfgeDTO(dfge);
			}
		}
		return result;
	}

	private ClagResponse getClagByInputObject(ClagInputDTO input) {
		ClagResponse response = new ClagResponse();
		response.setClagType(input.getClagType());
		CEP200DfdlDTO dfdlDTO = input.getDfdlDto();
		if (Objects.nonNull(dfdlDTO)) {
			response.setDfdlCode(dfdlDTO.getCode());
			response.setDfdlName(dfdlDTO.getName());
		}
		CEP200WsoDTO wso = input.getWsoDTO();
		if (Objects.nonNull(wso)) {
			response.setWsoCode(wso.getCode());
			response.setWsoId(wso.getId().intValue());
			response.setWsoName(wso.getName());
		}
		CEP200DfgeDTO dfge = input.getDfgeDTO();
		if (Objects.nonNull(dfge)) {
			response.setDfgeCode(dfge.getDfgeCode());
			response.setDfgeName(dfge.getDfgeName());
		}
		CEP200GradeGroupDTO ggDTO = input.getGgDto();
		if (Objects.nonNull(ggDTO)) {
			response.setGgCode(ggDTO.getCode());
			response.setGgName(ggDTO.getName());
			response.setGgId(ggDTO.getId());
		}
		CEP200PTDTO ptDTO = input.getPtDto();
		if ((Objects.nonNull(ptDTO))) {
			response.setPtId(ptDTO.getId());
			response.setPtCode(ptDTO.getCode());
			response.setPtName(response.getPtName());
		}

		response.setClassIndex(input.getClassCodeIndex());

		response.setCode(String.join("_", response.getPtCode(), response.getGgCode(), response.getDfdlCode(),
				response.getWsoCode(), response.getClagType().toUpperCase(), response.getClassIndex().toString()));
		return response;
	}

	@Override
	@WriteUnitTestLog
	public BpGGResponse BPGetGG(String podCode) {
		CEP200StudentDTO studentDTO = cep200Service.getStudent(podCode);
		if (Objects.nonNull(studentDTO)) {
			CEP100StudentResponse cep100StudentRes = cep100Service.getCEPStudent(studentDTO.getUsername());
			CEP100StudentGradeResponse studentGrade = cep100Service
					.getCEP100StudentGrade(cep100StudentRes.getStudentId());
			CEP200GradeGroupDTO dto = cep200Service.getCEP200GradeGroup(studentGrade.getGradeId());
			return BpGGResponse.builder().id(dto.getId()).cep100GradeId(dto.getCep100GradeId()).code(dto.getCode())
					.podCode(podCode).name(dto.getName()).build();
		}
		throw new NotFoundException("BPGetDFGE -> Cant found student of pod [" + podCode + "]");

	}

	@Override
	@WriteUnitTestLog
	public BpWsoResponse BPGetWSO(String podCode) {
		CEP200StudentDTO studentDTO = cep200Service.getStudent(podCode);
		if (Objects.nonNull(studentDTO)) {
			CEP100StudentResponse cep100StudentRes = cep100Service.getCEPStudent(studentDTO.getUsername());
			List<CEP100LearningScheduleClassResponse> listLearningSchedules = cep100Service
					.getCEP100LearningSchedule(cep100StudentRes.getStudentId());
			CEP200WsoDTO dto = cep200Service.getCEP200Wso(listLearningSchedules);
			return BpWsoResponse.builder().id(dto.getId()).podCode(podCode).code(dto.getCode()).name(dto.getName())
					.code(dto.getCode()).monday(dto.getMonday()).tuesday(dto.getTuesday()).wednesday(dto.getWednesday())
					.thursday(dto.getThursday()).friday(dto.getFriday()).saturday(dto.getSaturday())
					.sunday(dto.getSunday()).build();
		}
		throw new NotFoundException("BPGetWSO -> Cant found student of pod [" + podCode + "]");

	}

	@Override
	@WriteUnitTestLog
	public BpDfdlResponse BPGetDFDL(String podCode, String clagTypeCode) {
		CEP200StudentDTO studentDTO = cep200Service.getStudent(podCode);
		if (Objects.nonNull(studentDTO)) {
			CEP100StudentResponse cep100StudentRes = cep100Service.getCEPStudent(studentDTO.getUsername());
			CEP100StudentClassResponse clsResponse = cep100Service.getCEPStudentClass(cep100StudentRes.getStudentId(),
					ClagType.PERMANANT.getCode());
			CEP100ClassLevelResponse clsLevel = cep100Service.getCEP100ClassLevel(clsResponse.getClassId());
			CEP200DfdlDTO dto = cep200Service.getCEP200DFDL(clsLevel.getId());
			return BpDfdlResponse.builder().podCode(podCode).clagTypeCode(clagTypeCode).code(dto.getCode())
					.name(dto.getName()).description(dto.getDescription()).build();
		}
		throw new NotFoundException("BPGetDFDL -> Cant found student of pod [" + podCode + "]");
	}

	@Override
	public void omStudentChangeWso(Cep100ChangeWsoRequest request) throws Exception {
		String newClagCode = "";
		BpClagClassgroup oldClag = bpClagService.findByXClass(request.getOldXclass());
		BpPodProductOfDeal pod = bpPodRepo.findFirstByXdealAndMyst(request.getXDeal(), request.getStudentCode())
				.orElse(null);
		BpUsiUserItem user = bpUsiRepo.findByCode(request.getStudentCode()).orElse(null);
		if (Objects.nonNull(pod) && Objects.nonNull(user)) {
			BpPODCLAG oldPodClag = bpPodClagRepo.findFirstByMypodAndMyclag(pod.getCode(), oldClag.getCode())
					.orElse(null);
			if (Objects.isNull(oldPodClag)) {
				throw new Exception(
						"Cant found pod clag " + request.getOldXclass() + " of student " + request.getStudentCode());
			}
			Timestamp newUnassignedAt = new Timestamp(oldPodClag.getUnAssignedAt().getTime());
			Timestamp endOfDate = DateUtils.endOfWeek(new Date());
			oldPodClag.setUnAssignedAt(DateUtils.endOfWeek(new Date()));

			if (Objects.nonNull(request.getIsAddNewCls()) && request.getIsAddNewCls()) {
				BpClagClassgroupResponse clag = permanentClagMigrationService.migrateClag(request.getNewXClass());
				newClagCode = clag.getCode();
			} else {
				BpClagClassgroup clag = bpClagService.findByXClass(request.getNewXClass());
				newClagCode = clag.getCode();
			}
			DateUtils.startOfDay(DateUtils.addDate(DateUtils.endOfWeek(new Date()), 1));

			bpPodClagRepo.save(BpPODCLAG.builder().code(String.join("-", pod.getCode(), newClagCode))
					.assignedAt(new Timestamp(
							DateUtils.startOfDay(DateUtils.addDate(DateUtils.endOfWeek(new Date()), 1)).getTime()))
					.myclag(newClagCode).memberType("PERM").myUst("ST").mypod(pod.getCode())
					.unAssignedAt(newUnassignedAt).active(true).build());
			// KILL CUI, CUIE
			List<BpUniqueLearningComponent> listRemove = bpUlcRepo.findAllByClagAndCapTime(oldPodClag.getCode(),
					endOfDate);
			if (!CollectionUtils.isEmpty(listRemove)) {
				bpCuiRepo.inactiveCuiByMyusiAndUlcCodeIn(oldClag.getCode(),
						listRemove.stream().map(k -> k.getCode()).collect(Collectors.toList()));
			}

			List<BpUniqueLearningComponent> listAdd = bpUlcRepo.findAllByClagAndCapTime(newClagCode, endOfDate);
			if (!CollectionUtils.isEmpty(listAdd)) {
				for (BpUniqueLearningComponent ulc : listAdd) {
					cuiService.createCui(user.getCode(), null, ulc.getCode());
					bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK4);
					cuiEventService.createCUIJoinEvent(ulc.getCode(), user.getCode(), ulc.getMyLcp().getCode(), null,
							ulc.getMyCap().getCode());
					bpeEventService.createBpeEvent(BpeEventTypeEnum.BPEScheduleCAWK1);
				}

			}
		}
	}

}
