package vn.edu.clevai.bplog.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.annotation.MakeBpProcess;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.*;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.enums.Lcp;
import vn.edu.clevai.bplog.payload.request.bp.ClaGPodRequest;
import vn.edu.clevai.bplog.repository.BpClagClassgroupRepository;
import vn.edu.clevai.bplog.repository.BpPODCLAGRepository;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.repository.projection.CLAGDetailInfoProjection;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.ConflictException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODCLAGResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.BPClassInfoResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.DscSessionGroupResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.XClassInfoResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.XSessionGroupInfoResponse;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BpClagClassgroupServiceImpl extends BaseProxyService implements BpClagClassgroupService {
	private final BpClagClassgroupRepository bpClagClassgroupRepository;

	private final BpGGGradeGroupService bpGGGradeGroupService;

	private final BpDfdlDifficultygradeService bpDfdlDifficultygradeService;

	private final BpWsoWeeklyscheduleoptionService bpWsoWeeklyscheduleoptionService;

	private final BpPodProductOfDealService bpPodProductOfDealService;

	private final BpDfgeDifficultgetService bpDfgeDifficultgetService;

	private final BpPODCLAGRepository bpPODCLAGRepository;

	private final ULCMergeService ulcMergeService;

	@Lazy
	private final CalendarPeriodService calendarPeriodService;

	private final BpUsiUserItemService userItemService;


	private final ModelMapper modelMapper;

	private final LmsService lmsService;

	private final BpULCService bpULCService;

	private final CuiService cuiService;

	private final CuiEventService cuiEventService;

	private final AccYearService accYearService;

	private final BpClagPODService clagPODService;

	private final BpUSIDutyService usiDutyService;

	private final BpLCPService lcpService;

	private final BpTaskInfoService bpTaskInfoService;
	private final BpBpsStepService bpBpsStepService;
	private final BpBpeEventService bpBpeEventService;
	private final BpBppProcessService bpBppProcessService;
	private final LocalValueSaving valueSaving;
	private final ZoomMeetingService zoomMeetingService;

	private final ModifyService modifyService;


	private static final Map<UsiTypeEnum, BpsStepTypeEnum> MAP_USI_TYPE_VS_BPS_STEP = new HashMap<>();

	static {
		MAP_USI_TYPE_VS_BPS_STEP.put(UsiTypeEnum.DTE, BpsStepTypeEnum.ASSIGN_TEMAIN_UDL_CADY);
		MAP_USI_TYPE_VS_BPS_STEP.put(UsiTypeEnum.GTE, BpsStepTypeEnum.ASSIGN_TEMAIN_UGE_CADY);
		MAP_USI_TYPE_VS_BPS_STEP.put(UsiTypeEnum.CTE, BpsStepTypeEnum.ASSIGN_TEMAIN_UCO_CADY);
		MAP_USI_TYPE_VS_BPS_STEP.put(UsiTypeEnum.LTE, BpsStepTypeEnum.ASSIGN_TEMAIN_ULI_CADY);
		MAP_USI_TYPE_VS_BPS_STEP.put(UsiTypeEnum.QO, BpsStepTypeEnum.ASSIGN_EM_TO_SO_QO_UDL);
	}

	private static final int SEVEN_HOURS_IN_SECONDS = 7 * 60 * 60; //  25 200

	public BpClagClassgroupServiceImpl(BpClagClassgroupRepository bpClagClassgroupRepository, BpGGGradeGroupService bpGGGradeGroupService, @Lazy BpDfdlDifficultygradeService bpDfdlDifficultygradeService, BpWsoWeeklyscheduleoptionService bpWsoWeeklyscheduleoptionService, BpPodProductOfDealService bpPodProductOfDealService, BpDfgeDifficultgetService bpDfgeDifficultgetService, BpPODCLAGRepository bpPODCLAGRepository, ULCMergeService ulcMergeService, CalendarPeriodService calendarPeriodService, BpUsiUserItemService userItemService, ModelMapper modelMapper, LmsService lmsService, @Lazy BpULCService bpULCService, @Lazy CuiService cuiService, CuiEventService cuiEventService, AccYearService accYearService, BpClagPODService clagPODService, BpUSIDutyService usiDutyService, BpLCPService lcpService, BpTaskInfoService bpTaskInfoService,
									   BpUniqueLearningComponentRepository bpUniqueLearningComponentRepository, BpBpsStepService bpBpsStepService, BpBpeEventService bpBpeEventService, BpBppProcessService bpBppProcessService, LocalValueSaving valueSaving, @Lazy ZoomMeetingService zoomMeetingService, @Lazy ModifyService modifyService) {
		this.bpClagClassgroupRepository = bpClagClassgroupRepository;
		this.bpGGGradeGroupService = bpGGGradeGroupService;
		this.bpDfdlDifficultygradeService = bpDfdlDifficultygradeService;
		this.bpWsoWeeklyscheduleoptionService = bpWsoWeeklyscheduleoptionService;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.bpDfgeDifficultgetService = bpDfgeDifficultgetService;
		this.bpPODCLAGRepository = bpPODCLAGRepository;
		this.ulcMergeService = ulcMergeService;
		this.calendarPeriodService = calendarPeriodService;
		this.userItemService = userItemService;
		this.modelMapper = modelMapper;
		this.lmsService = lmsService;
		this.bpULCService = bpULCService;
		this.cuiService = cuiService;
		this.cuiEventService = cuiEventService;
		this.accYearService = accYearService;
		this.clagPODService = clagPODService;
		this.usiDutyService = usiDutyService;
		this.lcpService = lcpService;
		this.bpTaskInfoService = bpTaskInfoService;
		this.bpBpsStepService = bpBpsStepService;
		this.bpBpeEventService = bpBpeEventService;
		this.bpBppProcessService = bpBppProcessService;
		this.valueSaving = valueSaving;
		this.zoomMeetingService = zoomMeetingService;
		this.modifyService = modifyService;
	}

	@Override
	public BpClagClassgroup findByCode(String code) {
		return bpClagClassgroupRepository
				.findByCode(code)
				.orElseThrow(() -> new NotFoundException("Could not find BpClagClassgroup using code = " + code));
	}

	@Override
	@WriteUnitTestLog
	public BpClagClassgroupResponse BPSetCLAG(
			String name,
			String ptCode,
			String ggCode,
			String dfdlCode,
			String wsoCode,
			String dfgeCode,
			String clagTypeCode,
			Integer maxtotalstudents,
			String description,
			String xclass,
			Integer classIndex
	) {
		String code = String.join("-", ptCode, ggCode, dfdlCode, wsoCode, clagTypeCode.toUpperCase(), classIndex.toString());

		bpDfdlDifficultygradeService.findByCode(dfdlCode);

		bpGGGradeGroupService.findByCode(ggCode);

		bpWsoWeeklyscheduleoptionService.findByCode(wsoCode);

		bpClagClassgroupRepository
				.findByCode(code)
				.ifPresent(
						c -> {
							throw new ConflictException(
									"BpClagClassgroup (code = " + code + ") already exists."
							);
						}
				);

		BpClagClassgroup bpClagClassgroup = bpClagClassgroupRepository.save(
				BpClagClassgroup
						.builder()
						.code(code)
						.active(true)
						.mydfdl(dfdlCode)
						.mywso(wsoCode)
						.clagtype(clagTypeCode.toUpperCase())
						.mypt(ptCode)
						.mygg(ggCode)
						.mydfge(dfgeCode)
						.maxtotalstudents(maxtotalstudents)
						.description(description)
						.xclass(xclass)
						.build()
		);

		return BpClagClassgroupResponse
				.builder()
				.id(bpClagClassgroup.getId())
				.code(code)
				.name(bpClagClassgroup.getName())
				.mypt(bpClagClassgroup.getMypt())
				.mygg(bpClagClassgroup.getMygg())
				.mydfdl(bpClagClassgroup.getMydfdl())
				.mywso(wsoCode)
				.mydfge(dfgeCode)
				.clagtype(bpClagClassgroup.getClagtype())
				.active(bpClagClassgroup.getActive())
				.maxtotalstudents(bpClagClassgroup.getMaxtotalstudents())
				.description(bpClagClassgroup.getDescription())
				.xclass(bpClagClassgroup.getXclass())
				.xsessiongroup(bpClagClassgroup.getXsessiongroup())
				.xcash(bpClagClassgroup.getXcash())
				.createdAt(bpClagClassgroup.getCreatedAt())
				.updatedAt(bpClagClassgroup.getUpdatedAt())
				.build();
	}

	@Override
	public BpClagClassgroupResponse setCLAGDYN_DFGE(String clagdynCode, String dfgeCode) {
		BpClagClassgroup bpClagClassgroup = findByCode(clagdynCode);

		if (!ClagType.DYNAMIC.getCode().equalsIgnoreCase(bpClagClassgroup.getClagtype())) {
			throw new ConflictException("Could not assign dfge=" + dfgeCode + " to clag=" + clagdynCode + " because it is PERM, not DYN");
		}

		BpDfgeDifficultget bpDfgeDifficultget = bpDfgeDifficultgetService.findByCode(dfgeCode);

		bpClagClassgroup.setMydfge(bpDfgeDifficultget.getCode());

		bpClagClassgroupRepository.save(bpClagClassgroup);

		return BpClagClassgroupResponse
				.builder()
				.id(bpClagClassgroup.getId())
				.code(bpClagClassgroup.getCode())
				.name(bpClagClassgroup.getName())
				.mypt(bpClagClassgroup.getMypt())
				.mygg(bpClagClassgroup.getMygg())
				.mydfdl(bpClagClassgroup.getMydfdl())
				.mywso(bpClagClassgroup.getMywso())
				.mydfge(bpClagClassgroup.getMydfge())
				.clagtype(bpClagClassgroup.getClagtype())
				.active(bpClagClassgroup.getActive())
				.maxtotalstudents(bpClagClassgroup.getMaxtotalstudents())
				.description(bpClagClassgroup.getDescription())
				.xclass(bpClagClassgroup.getXclass())
				.xsessiongroup(bpClagClassgroup.getXsessiongroup())
				.xcash(bpClagClassgroup.getXcash())
				.createdAt(bpClagClassgroup.getCreatedAt())
				.updatedAt(bpClagClassgroup.getUpdatedAt())
				.build();
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setPOD_CLAGPERM")
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_POD_CLAGPERM
	)
	@Transactional
	public BpPODCLAGResponse setPOD_CLAGPERM(String podPod_code, String podMyclagperm, Date assignedAt,
											 Date unassignedAt, String membertype, String modifyType) {
		BpClagClassgroup clag = findByCode(podMyclagperm);

		if (!ClagType.PERMANANT.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}
		if (!ClagType.PERMANANT.getCode().equals(membertype)) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}
		BpPODCLAG previousBpPODCLAG = bpPODCLAGRepository.findLastByPodAndClagType(podPod_code, membertype)
				.orElse(null);
		if (Objects.nonNull(previousBpPODCLAG) && !previousBpPODCLAG.getMyclag().equals(podMyclagperm)) {
			previousBpPODCLAG.setActive(false);
			previousBpPODCLAG.setUnAssignedAt(DateUtils.now());
			assignedAt = DateUtils.now();
		}
		BpPODCLAG bpPodClag = BpPODCLAG.builder()
				.code(String.join("-", podPod_code, podMyclagperm, DateUtils.format(assignedAt, "yyyyMMdd")))
				.mypod(podPod_code).myclag(podMyclagperm).memberType(membertype)
				.assignedAt(new Timestamp(assignedAt.getTime())).unAssignedAt(new Timestamp(unassignedAt.getTime()))
				.active(clag.getActive()).build();
		bpPODCLAGRepository.createOrUpdate(bpPodClag);
		if (Objects.isNull(previousBpPODCLAG)) {
			CompletableFuture.runAsync(() -> {
				try {
					log.info("Start schedule OM for myclag {} and pod {}", bpPodClag.getMyclag(), podPod_code);
					bpULCService.scheduleMPForOM(bpPodClag.getMyclag(), podPod_code);
				} catch (Exception e) {
					log.error("Error when schedule for OM", e);
				}
			});
		}
		if (Objects.nonNull(previousBpPODCLAG) && !previousBpPODCLAG.getMyclag().equals(podMyclagperm)) {
			CompletableFuture.runAsync(() -> {
				try {
					log.info("Start modify change CIB for oldPodClag:{} and newPodClag : {} , modify type : {}",
							previousBpPODCLAG, bpPodClag.getCode(), modifyType);
					modifyService.bppSyncST(previousBpPODCLAG.getCode(), bpPodClag.getCode(), ModifyTypeEnum.CHANGE_CIB.getName());
				} catch (Exception e) {
					log.error("Error when modify change CIB for student with error : {} ", DebuggingDTO.build(e));
				}
			});
		} else {
			CompletableFuture.runAsync(() -> {
				try {
					log.info("Start modify commercial for oldPodClag:{} and newPodClag : {} , modify type : {} ",
							Objects.nonNull(previousBpPODCLAG) ? previousBpPODCLAG.getCode() : null, bpPodClag.getCode(), modifyType);
					modifyService.bppSyncST(Objects.nonNull(previousBpPODCLAG) ? previousBpPODCLAG.getCode() : null
							, bpPodClag.getCode(), modifyType);
				} catch (Exception e) {
					log.error("Error when modify commercial for student with error : {} ", DebuggingDTO.build(e));
				}
			});
		}


		return modelMapper.map(bpPodClag, BpPODCLAGResponse.class);
	}

	@Override
	@WriteUnitTestLog
	@SneakyThrows
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_CLAGDYN_FROM_X
	)
	public BpClagClassgroupResponse getCLAGDYNFromX(String xSession_group_id, String xcash) {
		Timestamp liveAt = new Timestamp(DateUtils.parse(xcash, DateUtils.MEDIUM_PATTERN, DateUtils.UTC_TIME_ZONE).getTime());

		DscSessionGroupResponse dscSessionGroupResponse = lmsService.getDscSessionGroupDetails(xSession_group_id, liveAt);

		/* The start time that we talk to parents*/
		Timestamp startTime = DateUtils.addSecondToTimestamp(dscSessionGroupResponse.getDscLiveAt(), 5 * 60);

		BpClagClassgroupResponse bpClagClassgroupResponse = BpClagClassgroupResponse
				.builder()
				.mypt(Cep100TransformUtils.toPtCode(dscSessionGroupResponse.getProductId()))
				.mygg(Cep100TransformUtils.toGGCode(dscSessionGroupResponse.getGradeId()))
				.mydfdl(Cep100TransformUtils.toDfdlCode(dscSessionGroupResponse.getClassLevelId().intValue()))
				.mywso(DateUtils.getClassDay(startTime).toString())
				.mydfge(dscSessionGroupResponse.getCategory())
				.clagtype(ClagType.DYNAMIC.getCode())
				.active(true)
				.maxtotalstudents(dscSessionGroupResponse.getMaxActiveStudents())
				.xsessiongroup(xSession_group_id)
				.xclass(dscSessionGroupResponse.getClassCode())
				.xcash(xcash)
				.build();

		bpClagClassgroupResponse.setCode(
				String.join("-",
						bpClagClassgroupResponse.getMypt(),
						bpClagClassgroupResponse.getMygg(),
						bpClagClassgroupResponse.getMydfdl(),
						DateUtils.getClassDay(dscSessionGroupResponse.getDscLiveAt()).toString(),
						DateUtils.format(startTime, "yyyyMMdd"),
						bpGGGradeGroupService.findByCode(bpClagClassgroupResponse.getMygg()).getCashStart(),
						bpClagClassgroupResponse.getMydfge(),
						dscSessionGroupResponse.getSessionGroupId().toString()
				)
		);

		return bpClagClassgroupResponse;
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setCLAGDYN")
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_CLAGDYN
	)
	public BpClagClassgroupResponse createOrUpdateDynamicClag(
			String clagClagcode,
			String clagMypt,
			String clagMygg,
			String clagMydfdl,
			String clagMydfge,
			String mywso,
			Integer clagMaxtotalstudent,
			String membertype,
			String xSession_group_id,
			String xcash
	) {
		log.info("createOrUpdateDynamicClag {}", clagClagcode);

		BpClagClassgroup bpClagClassgroup = bpClagClassgroupRepository
				.findByCode(clagClagcode)
				.orElseGet(() -> BpClagClassgroup.builder().code(clagClagcode).build());

		/* Validate input data. */
		if (!ClagType.DYNAMIC.getCode().equalsIgnoreCase(membertype)) {
			throw new BadRequestException("clagtype must be DYN");
		}

		bpWsoWeeklyscheduleoptionService.findByCode(mywso);

		/* Save to db. */
		bpClagClassgroup.setMypt(clagMypt);
		bpClagClassgroup.setClagtype(membertype);
		bpClagClassgroup.setMywso(mywso);
		bpClagClassgroup.setMygg(clagMygg);
		bpClagClassgroup.setActive(true);
		bpClagClassgroup.setMydfdl(clagMydfdl);
		bpClagClassgroup.setMydfge(clagMydfge);
		bpClagClassgroup.setMaxtotalstudents(clagMaxtotalstudent);
		bpClagClassgroup.setXsessiongroup(xSession_group_id);
		bpClagClassgroup.setXcash(xcash);

		return saveAndReturn(clagClagcode, bpClagClassgroup);
	}

	private BpClagClassgroupResponse saveAndReturn(String code, BpClagClassgroup bpClagClassgroup) {
		bpClagClassgroup = bpClagClassgroupRepository.createOrUpdate(bpClagClassgroup);

		return BpClagClassgroupResponse
				.builder()
				.id(bpClagClassgroup.getId())
				.code(code)
				.name(bpClagClassgroup.getName())
				.mypt(bpClagClassgroup.getMypt())
				.mygg(bpClagClassgroup.getMygg())
				.mydfdl(bpClagClassgroup.getMydfdl())
				.mywso(bpClagClassgroup.getMywso())
				.mydfge(bpClagClassgroup.getMydfge())
				.clagtype(bpClagClassgroup.getClagtype())
				.active(bpClagClassgroup.getActive())
				.maxtotalstudents(bpClagClassgroup.getMaxtotalstudents())
				.description(bpClagClassgroup.getDescription())
				.xclass(bpClagClassgroup.getXclass())
				.xsessiongroup(bpClagClassgroup.getXsessiongroup())
				.xcash(bpClagClassgroup.getXcash())
				.createdAt(bpClagClassgroup.getCreatedAt())
				.updatedAt(bpClagClassgroup.getUpdatedAt())
				.build();
	}


	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setCLAGPERM")
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_CLAGPERM
	)
	public BpClagClassgroupResponse createOrUpdatePermanentClag(
			String code,
			String clagMypt,
			String gdgMygg,
			String clagMydfdl,
			String mywso,
			String clagtype,
			String xClass_id,
			Integer maxtotalstudent
	) {
		BpClagClassgroup bpClagClassgroup = bpClagClassgroupRepository
				.findByCode(code)
				.orElseGet(() -> BpClagClassgroup.builder().code(code).build());

		/* Validate input data. */
		if (!ClagType.PERMANANT.getCode().equalsIgnoreCase(clagtype)) {
			throw new BadRequestException("clagtype must be PERM");
		}

		bpWsoWeeklyscheduleoptionService.findByCode(mywso);

		/* Save to db. */
		bpClagClassgroup.setMypt(clagMypt);
		bpClagClassgroup.setClagtype(clagtype);
		bpClagClassgroup.setMywso(mywso);
		bpClagClassgroup.setMygg(gdgMygg);
		bpClagClassgroup.setXclass(xClass_id);
		bpClagClassgroup.setActive(true);
		bpClagClassgroup.setMydfdl(clagMydfdl);
		bpClagClassgroup.setMaxtotalstudents(maxtotalstudent);

		return saveAndReturn(code, bpClagClassgroup);
	}

	@Override
	@WriteUnitTestLog
	public BpPODCLAGResponse getPOD_CLAGDYN(String podCode, String capCode) {
		BpPodProductOfDeal bp = bpPodProductOfDealService.findByCode(podCode);

		CalendarPeriod cap = calendarPeriodService.findByCode(capCode);

		if (!CalendarPeriodTypeEnum.SHIFT.getCode().equals(cap.getCapType())) {
			throw new BadRequestException("Only support cap type CASH!!!");
		}

		Timestamp liveAt = DateUtils.addSecondToTimestamp(cap.getStartTime(), -5 * 60);
		String xcash = DateUtils.format(liveAt, DateUtils.UTC_TIME_ZONE, DateUtils.MEDIUM_PATTERN);

		XSessionGroupInfoResponse response = findXSESSIONGROUP(bp.getXdeal(), xcash);
		BpClagClassgroupResponse clag = getCLAGDYNFromX(response.getCode(), xcash);

		return BpPODCLAGResponse.builder()
				.active(true)
				.memberType(ClagType.DYNAMIC.getCode())
				.myclag(clag.getCode())
				.mypod(bp.getCode())
				.code(bp.getCode() + "-" + clag.getCode())
				.assignedAt(new Timestamp(response.getAssignedAt().getTime()))
				.unAssignedAt(DateUtils.endOfDay(liveAt))
				.build();
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.FIND_XSESSIONGROUP
	)
	public XSessionGroupInfoResponse findXSESSIONGROUP(Long xXdealid, String xcash) {
		return lmsService.findXSESSIONGROUP(xXdealid, xcash);
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_POD_CLAGDYN
	)
	public BpPODCLAGResponse setPOD_CLAGDYN(
			String podPod_code,
			String clagClagcode,
			Date assignedAt,
			Date unassignedAt,
			String membertype
	) {
		BpClagClassgroup clag = findByCode(clagClagcode);

		if (!ClagType.DYNAMIC.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}

		if (!ClagType.DYNAMIC.getCode().equals(membertype)) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}

		BpPODCLAG bpPodClag = BpPODCLAG.builder()
				.code(String.join("-", podPod_code, clagClagcode))
				.mypod(podPod_code)
				.myclag(clagClagcode)
				.memberType(ClagType.DYNAMIC.getCode())
				.active(clag.getActive())
				.assignedAt(new Timestamp(assignedAt.getTime()))
				.unAssignedAt(new Timestamp(unassignedAt.getTime()))
				.build();
		bpPODCLAGRepository.createOrUpdate(bpPodClag);

		return modelMapper.map(bpPodClag, BpPODCLAGResponse.class);
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_XCASH
	)
	public String getXCASH(String xcady, Long xGrade_id) throws ParseException {
		String cashStart = bpGGGradeGroupService.findByCode(bpGGGradeGroupService.getGGFromX(xGrade_id).getCode()).getCashStart();
		return DateUtils.format
				(DateUtils.parse(xcady + " " + cashStart.substring(0, 2) + ":" + cashStart.substring(2, 4) + ":00",
								DateUtils.MEDIUM_PATTERN, DateUtils.LOCAL_TIME_ZONE).getTime() - 5 * 60 * 1000, // 5 Minutes
						DateUtils.UTC_TIME_ZONE, DateUtils.MEDIUM_PATTERN);
	}

	@Override
	@Transactional
	public BpPODCLAGResponse setPOD_CLAGDYN_TE(String podCode, String xsessionggroup, String xcash, boolean published) {
		BpClagClassgroupResponse response1 = getCLAGDYNFromX(xsessionggroup, xcash);
		BpClagClassgroupResponse response2 = createOrUpdateDynamicClag(response1.getCode(), response1.getMypt(), response1.getMygg(), response1.getMydfdl(), response1.getMydfge(), response1.getMywso(), response1.getMaxtotalstudents(), response1.getClagtype(), xsessionggroup, xcash);
		Timestamp assignedAt = DateUtils.now();
		if (published) {
			Timestamp unAssignedAt = DateUtils.addSecondToTimestamp(assignedAt, 60 * 60);
			return setPOD_CLAGDYN(podCode, response2.getCode(), assignedAt, unAssignedAt, response2.getClagtype());
		} else {
			return bpPODCLAGRepository.findFirstByCode(String.join("-", podCode, response2.getCode()))
					.map(p -> setPOD_CLAGDYN(podCode, response2.getCode(), p.getAssignedAt(), assignedAt, response2.getClagtype()))
					.orElseGet(
							() -> setPOD_CLAGDYN(podCode, response2.getCode(), assignedAt, assignedAt, response2.getClagtype())
					);
		}
	}

	@Override
	public List<BpClagClassgroup> findBy(String pt, String gg, String dfdl, CalendarPeriod cady, String clagType) {
		if (ClagType.BACKUP.getCode().equals(clagType)) {
			return bpClagClassgroupRepository.findByClagtypeAndActiveTrueAndMywso(clagType, cady.getNumberAsChild());
		} else {
			return bpClagClassgroupRepository.findBy(pt, gg, dfdl, String.valueOf(DateUtils.getClassDay(cady.getEndTime())), clagType, cady.getStartTime());
		}
	}

	@Override
	public Long getMyPODSHNo(BpPodProductOfDeal pod, CalendarPeriod cash) {
		return bpClagClassgroupRepository.getMyPODSHNo(pod.getCode(), cash.getCode());
	}

	@Override
	public String getPeriodNo(Long podSHNo) {
		return podSHNo >= 3 && ((podSHNo - 3) % 8 == 0) ? "C1" : "C7";
	}

	@Override
	public List<BpClagClassgroup> findClagFromULC(String ulc) {
		return bpClagClassgroupRepository.findByUlc(ulc);
	}

	@Override
	public void resetAssignToClag(BpPODCLAG podclag, String ulc) {
		String usi = bpPodProductOfDealService.findByCode(podclag.getMypod()).getMyst();
		cuiService.unpublishCUICUIE(ulc, usi);
		clagPODService.unpublishedPodClag(podclag);
	}

	@Transactional
	public void bppAssignTEtoCLAG2(String cady, List<String> usts, List<String> pts,
								   List<String> ggs, List<String> dfdls) {
		bpBppProcessService.createBppProcess(BppProcessTypeEnum.BPPASSIGN_TEMAIN, null);
		usts.forEach(ust -> {
			UsiTypeEnum usiType = UsiTypeEnum.optionalOf(ust)
					.orElseThrow(() -> new NotFoundException(String.format("Not found usi type %s", ust)));
			bpBpsStepService.createBpsStep(MAP_USI_TYPE_VS_BPS_STEP.get(usiType));

			// MAIN
			CalendarPeriod cap = calendarPeriodService.findByCode(cady);
			List<CalendarPeriod> cashList = calendarPeriodService.findByParentAndCapType(cady,
					CalendarPeriodTypeEnum.SHIFT.getCode());

			List<String> ptList = CollectionUtils.isEmpty(pts) ?
					bpPodProductOfDealService.getPtFromCadyAndUst(cady, ust) : pts;
			Timestamp startPeriod = DateUtils.addSecondToTimestamp(cap.getStartTime(), SEVEN_HOURS_IN_SECONDS);
			// CTE
			if (Objects.equals(usiType, UsiTypeEnum.CTE)) {
				ClaGPodRequest request = ClaGPodRequest.builder()
						.ust(ust)
						.cashList(cashList)
						.cady(cady)
						.startPeriod(startPeriod)
						.build();
				assignCTE(request, ptList, cap);
			} else {
				List<ClaGPodRequest> requests = buildClaGPodRequest
						(ggs, dfdls, ptList, cashList, startPeriod, cady, ust);
				requests.forEach(request -> {
					try {
						switch (usiType) {
							//DTE
							case DTE:
							case DST:
							case QO:
								assignDTE_DTS(request);
								break;
//							case QO:
//							assignQO(request);
//								break;
							//UGE
							case GTE:
								assignGTEVer2(request, cap);
								break;
							//LTE
							case LTE:
								assignLTEVer2(request);
								break;
							default:
								break;
						}
					} catch (Exception e) {
						log.info("bppAssignTEtoCLAG2 error {}", e.getLocalizedMessage());
					}
				});
			}

			// BACKUP
			bpBppProcessService.createBppProcess(BppProcessTypeEnum.BPP_ASSIGN_TE_BACKUP, null);
			BpUniqueLearningComponent ubw = bpULCService.findUBWFromBP(cady, Lcp.PKAL_1PK_FD1_BW_1DP.getCode(),
					Lcp.PKAL_1PK_FD1_BW_1DP.getMylct()
			);

			Map<String, String> mapUsiUbw = new HashMap<>();
			if (Objects.nonNull(ubw)) {
				bpBpsStepService.createBpsStep(BpsStepTypeEnum.ASSIGN_TEBACKUP_USI_CADY);
				String clagCode = findClagFromULC(ubw.getCode()).get(0).getCode();
				clagPODService.findByCadyAndUstAndClag(cady, ust, clagCode)
						.forEach(podClag -> {
							resetAssignToClag(podClag, ubw.getCode());
						});
				ggs.forEach(gg -> {
					dfdls.forEach(dfdl -> {
						ptList.forEach(pt -> {
							/* cady --> casses. */
							List<String> casses = calendarPeriodService.findByMyGrandParentAndCapType(cady, CalendarPeriodTypeEnum.SESSION.getCode())
									.stream().map(CalendarPeriod::getCode).collect(Collectors.toList());

							if (casses.isEmpty()) {
								return;
							}

							usiDutyService.findRegister5Users(pt, casses,
									ust, gg, dfdl, null, null,
									PositionAssignEnum.findByUstAndUlc(ust, true)
							).forEach(usi -> {
								BpPodProductOfDeal pod = bpPodProductOfDealService.findByUsi(usi, pt);
								BpUsiUserItem userItem = userItemService.findByUsername(usi);
								// Tao pod_Clag

								clagPODService.setPODClag(pod.getCode(), clagCode, ubw.getMyCap().getCode(),
										userItem.getMyust());
								if (!mapUsiUbw.containsKey(pod.getMyst()))
									mapUsiUbw.put(pod.getMyst(), ubw.getCode());
							});
						});
					});
				});
				mapUsiUbw.keySet().forEach(usiCode -> {
					bpBpeEventService.createBpeEvent(BpeEventTypeEnum.CREATE_CUIE_BPPODCLAG_BACKUP);
					cuiEventService.createCUIJoinEvent(ubw.getCode(), usiCode, ubw.getMyLcp().getCode(),
							null, ubw.getMyCap().getCode());
					cuiEventService.setPublishCUIE(ubw.getCode(), usiCode, true);
				});
			}
		});
	}


	private void assignCTE(ClaGPodRequest request, List<String> ptList, CalendarPeriod cap) {
		// CTE
		ptList.forEach(pt -> {
			SortedSet<BpClagClassgroup> clagAll = new TreeSet<>(Comparator.comparingInt(BpClagClassgroup::getId));
			Map<String, List<String>> mapClagUge = new HashMap<>();
			request.getCashList().forEach(cash -> {
				List<BpUniqueLearningComponent> ushList = bpULCService.findUlcByCap(cash.getCode());

				BpLCP lcpDL = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_DL.getCode(), LCLEnum.LCSS.getName());
				BpLCP lcpCo = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_CO.getCode(), LCLEnum.LCSS.getName());
				/* TODO: hardcode regarding to @dungnv's idea. */
				BpLCP lcpDCT = lcpService.findByCode(Lcp.DL_40MI_FD1_DCT_40MI.getCode());

				ushList.forEach(ush -> {
					if (lcpCo != null) {
						bpULCService.findUlcFromParentUlcAndLcp(ush.getCode(), lcpCo.getCode()).forEach(uco -> {
							if (Objects.nonNull(uco)) {
								log.info("Start find clag by uco : " + uco.getCode());
								findClagByUlcAndPt(uco.getCode(), pt).forEach(clag -> {
									if (!clagAll.contains(clag)) {
										clagAll.add(clag);
										mapClagUge.put(clag.getCode(), Collections.singletonList(uco.getCode()));
									} else if (!mapClagUge.get(clag.getCode()).contains(uco.getCode())) {
										List<String> ulcs = new ArrayList<>(mapClagUge.get(clag.getCode()));
										ulcs.add(uco.getCode());
										mapClagUge.put(clag.getCode(), ulcs);
									}
								});
							}
						});
					}

					if (lcpDL == null) {
						return;
					}

					BpUniqueLearningComponent udl = bpULCService
							.findUlcFromParentUlcAndLcp(ush.getCode(), lcpDL.getCode())
							.stream().findFirst().orElse(null);

					if (Objects.nonNull(udl) && lcpDCT != null) {
						log.info("Start find clag by udct from udl : " + udl.getCode());
						bpULCService.findUlcFromParentUlcAndLcp(udl.getCode(), lcpDCT.getCode())
								.forEach(udct -> {
									findClagByUlcAndPt(udct.getCode(), pt).forEach(clag -> {
										if (!clagAll.contains(clag)) {
											clagAll.add(clag);
											mapClagUge.put(clag.getCode(), Collections.singletonList(udct.getCode()));
										}
										if (mapClagUge.containsKey(clag.getCode())) {
											List<String> ulcs = new ArrayList<>(mapClagUge.get(clag.getCode()));
											ulcs.add(udct.getCode());
											mapClagUge.put(clag.getCode(), ulcs);
										}
									});
								});
					}
				});
			});
			if (clagAll.isEmpty()) {
				log.error("Coun't find any clag from for uco and udct");
				return;
			}
			int numberTeMapTOClag = 3;
			Map<String, Integer> config = MapTEToCLagConfigEnum.getConfig();
			if (config.containsKey(pt)) numberTeMapTOClag = config.get(pt);
			int finalNumberTeMapTOClag = numberTeMapTOClag;

			// CTE Teacher
			List<String> cteUsiList = usiDutyService.findUsiFromBp(pt, request.getCady(),
					UsiTypeEnum.CTE.getName(),
					null, null, null, null,
					PositionAssignEnum.findByUstAndUlc(request.getUst(), false));
			List<BpPodProductOfDeal> ctePodList = cteUsiList.stream().map(e -> bpPodProductOfDealService.findByUsi(e, pt)).collect(Collectors.toList());
			if (cteUsiList.isEmpty()) {
				log.error("In , pt {} {}", pt, "Usi list get from bp is empty");
				return;
			}

			List<BpPodProductOfDeal> unchangedPodList = new ArrayList<>();
			clagAll.forEach(clag -> {
				List<BpPodProductOfDeal> pods = bpPodProductOfDealService.findPodByClagCadyAndUst(
						Collections.singletonList(clag.getCode()), Collections.singletonList(request.getUst()), cap);
				if (pods.isEmpty()) return;
				pods.forEach(pod -> {
					if (ctePodList.contains(pod)) {
						if (unchangedPodList.contains(pod)) unchangedPodList.add(pod);
						clagAll.remove(clag);
					}
				});
			});
			ctePodList.removeAll(unchangedPodList);
			Map<String, List<String>> mapUsiUlc = new HashMap<>();
			Queue<BpClagClassgroup> clagQueue = new LinkedList<>(clagAll);
			ctePodList.forEach(ctePod -> {
				if (clagQueue.isEmpty()) return;
				for (int i = 0; i < finalNumberTeMapTOClag; i++) {
					BpClagClassgroup clag = clagQueue.poll();
					if (Objects.isNull(clag)) break;
					mapClagUge.get(clag.getCode()).forEach(ulcCode -> {
						BpUniqueLearningComponent ulc = bpULCService.findByCode(ulcCode);
						clagPODService.findByCadyAndUstAndClag(ulc.getMyCap().getCode(), UsiTypeEnum.CTE.getName(), clag.getCode())
								.forEach(podClag -> {
									resetAssignToClag(podClag, ulcCode);
								});
						clagPODService.setPODClag(ctePod.getCode(), clag.getCode(), ulc.getMyCap().getCode(), UsiTypeEnum.CTE.getName());
						if (!mapUsiUlc.containsKey(ctePod.getMyst())) {
							mapUsiUlc.put(ctePod.getMyst(), Collections.singletonList(ulcCode));
						} else {
							if (!mapUsiUlc.get(ctePod.getMyst()).contains(ulcCode)) {
								List<String> ulcs = new ArrayList<>(mapUsiUlc.get(ctePod.getMyst()));
								ulcs.add(ulcCode);
								mapUsiUlc.put(ctePod.getMyst(), ulcs);
							}
						}
					});
				}
			});
			mapUsiUlc.keySet().forEach(usi -> {
				mapUsiUlc.get(usi).forEach(ulcCode -> {
					BpUniqueLearningComponent ulc = bpULCService.findByCode(ulcCode);
					bpBpeEventService.createBpeEvent(BpeEventTypeEnum.CREATE_CUIE_BPPODCLAG_WITH_UST_EQUAL_CTE);
					cuiEventService.createCUIJoinEvent(ulcCode, usi, ulc.getMyLcp().getCode(), null, ulc.getMyCap().getCode());
					cuiEventService.setPublishCUIE(ulcCode, usi, true);
				});
			});
		});
	}

	private List<ClaGPodRequest> buildClaGPodRequest(List<String> ggs,
													 List<String> dfdls,
													 List<String> ptList,
													 List<CalendarPeriod> cashList,
													 Timestamp startPeriod,
													 String cady,
													 String ust
	) {
		List<ClaGPodRequest> result = new ArrayList<>();
		ggs.forEach(gg -> {
			dfdls.forEach(dfdl -> {
				ptList.forEach(pt -> {
					result.add(
							ClaGPodRequest.builder()
									.cashList(cashList)
									.dfdl(dfdl)
									.gg(gg)
									.pt(pt)
									.startPeriod(startPeriod)
									.cady(cady)
									.ust(ust)
									.build()
					);
				});
			});
		});
		return result;
	}

	private void assignGTEVer2(ClaGPodRequest request, CalendarPeriod cap) throws Exception {
		AccYear ay = accYearService.findByTime(request.getStartPeriod());

		CurriculumProgramPackage crpp = crppService.getCrppByAccYearAndTimeAndPt(
				ay.getCode(),
				DateUtils.subtractSecondsFromTimestamp(request.getStartPeriod(), SEVEN_HOURS_IN_SECONDS),
				request.getPt()
		);

		BpUsiDuty bpUsiDuty = usiDutyService.findCashStart(ay.getCode(), crpp.getMyTerm(), request.getPt(), request.getGg(), request.getDfdl());

		BpLCP lcpSession = lcpService.findLcpSSByPtAndGE(request.getPt());
		BpLCP lcpShift = lcpService.findLcpshByPtAndLct(request.getPt(), lcpSession.getMylctparent()); // TODO

		CalendarPeriod cash = request.getCashList().stream().filter(c -> c.getMyPrd()
						.equals(lcpShift.getMyprd()) && c.getCashStart().equals(bpUsiDuty.getMycashsta())).findAny()
				.orElseThrow(() -> new NotFoundException("Could not find any CAP with cady code = " + request.getCady()));
		CalendarPeriod cass = calendarPeriodService.getCASS(cash.getCode(), lcpSession.getLcperiodno(),
				lcpShift.getMystructure());

		// ULC - Session
		List<BpUniqueLearningComponent> uss = bpULCService.findAllUgesByCapLcpGgDfdl(
				cass.getCode(), lcpSession.getCode(), request.getGg(), request.getDfdl()
		);
		if (uss.isEmpty()) {
			return;
		}

		uss.forEach(u -> {
			bpULCService.getByULC(u.getCode()).forEach(cu -> {
				clagPODService.findByUstAndClag(UsiTypeEnum.GTE.getName(), cu.getMyclag()).forEach(pc -> {
					BpPodProductOfDeal pod = bpPodProductOfDealService.findByCode(pc.getMypod());
					try {
						zoomMeetingService.createAndAssign(pod.getMyst(), u.getCode(), cu.getMyclag());
					} catch (Exception e) {
						log.error("zoomMeetingService.createAndAssign error: {} pod: {} clag: {} ulc: {}",
								e.getLocalizedMessage(), pod.getMyst(), u.getCode(), cu.getMyclag());
						e.printStackTrace();
					}

					bpBpeEventService.createBpeEvent(BpeEventTypeEnum.CREATE_CUIE_BPPODCLAG_WITH_UST_EQUAL_UGE);
					cuiEventService.createCUIJoinEvent(u.getCode(), pod.getMyst(), u.getMyLcp().getCode(),
							null, u.getMyCap().getCode());
//					cuiEventService.setPublishCUIE(u.getCode(), pod.getMyst(), true);
				});
			});
		});
	}

	private void assignDTE_DTS(ClaGPodRequest request) throws Exception {
		AccYear ay = accYearService.findByTime(request.getStartPeriod());

		CurriculumProgramPackage crpp = crppService.getCrppByAccYearAndTimeAndPt(
				ay.getCode(),
				DateUtils.subtractSecondsFromTimestamp(request.getStartPeriod(), SEVEN_HOURS_IN_SECONDS),
				request.getPt()
		);

		BpUsiDuty bpUsiDuty = usiDutyService.findCashStart(ay.getCode(), crpp.getMyTerm(), request.getPt(), request.getGg(), request.getDfdl());

		BpLCP lcpSession = lcpService.findLcpSSByPtAndLct(request.getPt(), LCPLCTLCKEnum.DL_40MI.getCode());
		BpLCP lcpShift = lcpService.findLcpshByPtAndLct(request.getPt(), lcpSession.getMylctparent()); // TODO

		CalendarPeriod cash = request.getCashList().stream().filter(c -> c.getMyPrd()
						.equals(lcpShift.getMyprd()) && c.getCashStart().equals(bpUsiDuty.getMycashsta())).findAny()
				.orElseThrow(() -> new NotFoundException("Could not find any CAP with cady code = " + request.getCady()));
		CalendarPeriod cass = calendarPeriodService.getCASS(cash.getCode(), lcpSession.getLcperiodno(),
				lcpShift.getMystructure());

		// ULC - Session
		BpUniqueLearningComponent udl = bpULCService.findUlcByCapLcpGgDfdlDfge
						(cass.getCode(), lcpSession.getCode(), request.getGg(), request.getDfdl(), null)
				.stream().findAny().orElse(null);
		if (Objects.isNull(udl)) return;


		List<String> usiList = usiDutyService.findUsiFromBp(request.getPt(), cass.getCode(), request.getUst(),
				request.getGg(), request.getDfdl(), null, null,
				PositionAssignEnum.findByUstAndUlc(request.getUst(), false)
		);
		// Find by main ulc
		if (usiList.isEmpty()) {
			// Join ULC
			ULCMerge ulcMerge = ulcMergeService.findByCode(udl.getCode());
			if (Objects.nonNull(ulcMerge)) {
				BpUniqueLearningComponent mainUlc = bpULCService.findByCode(ulcMerge.getMainulc());
				if (Objects.nonNull(mainUlc)) {
					usiList = usiDutyService.findUsiFromBp(mainUlc.getMyPt(), mainUlc.getMyCap().getCode(),
							request.getUst(), request.getGg(), request.getDfdl(), null,
							null,
							PositionAssignEnum.findByUstAndUlc(request.getUst(), false));
				}
			}
		}

		if (usiList.isEmpty()) {
			return;
		}

		// Tim pod dua vao cady, khong phai now //done
		BpPodProductOfDeal pod = bpPodProductOfDealService
				.findByUsiAndDate(usiList.get(0), request.getPt(), request.getStartPeriod().getTime());
		List<String> finalUsiList = usiList;
		findClagByUlcAndPt(udl.getCode(), request.getPt()).forEach(clag -> {
			clagPODService.setPODClag(pod.getCode(), clag.getCode(), udl.getMyCap().getCode(),
					request.getUst());

			bpBpeEventService.createBpeEvent(BpeEventTypeEnum.CREATE_CUIE_BPPODCLAG_WITH_UST_EQUAL_DTE);
			cuiEventService.createCUIJoinEvent(udl.getCode(), finalUsiList.get(0), udl.getMyLcp().getCode(), null,
					udl.getMyCap().getCode());
			cuiEventService.setPublishCUIE(udl.getCode(), finalUsiList.get(0), true);
		});
	}

	@Autowired
	private CurriculumProgramPackageService crppService;

	private void assignLTEVer2(ClaGPodRequest request) throws Exception {
		AccYear ay = accYearService.findByTime(request.getStartPeriod());

		CurriculumProgramPackage crpp = crppService.getCrppByAccYearAndTimeAndPt(
				ay.getCode(),
				DateUtils.subtractSecondsFromTimestamp(request.getStartPeriod(), SEVEN_HOURS_IN_SECONDS),
				request.getPt()
		);

		BpUsiDuty bpUsiDuty = usiDutyService.findCashStart(ay.getCode(), crpp.getMyTerm(), request.getPt(), request.getGg(), request.getDfdl());

		Map<String, String> mapUsiUdl = new HashMap<>();

		BpLCP lcpSession = lcpService.findLcpSSByPtAndLct(request.getPt(), LCPLCTLCKEnum.LI_45MI.getCode());
		BpLCP lcpShift = lcpService.findLcpshByPtAndLct(request.getPt(), lcpSession.getMylctparent()); // TODO

		CalendarPeriod cash = request.getCashList().stream().filter(c -> c.getMyPrd()
						.equals(lcpShift.getMyprd()) && c.getCashStart().equals(bpUsiDuty.getMycashsta())).findAny()
				.orElseThrow(() -> new NotFoundException("Could not find any CAP with cady code = " + request.getCady()));
		CalendarPeriod cass = calendarPeriodService.getCASS(cash.getCode(), lcpSession.getLcperiodno(),
				lcpShift.getMystructure());

		// ULC - Session
		BpUniqueLearningComponent udl = bpULCService.findUlcByCapLcpGgDfdlDfge
						(cass.getCode(), lcpSession.getCode(), request.getGg(), request.getDfdl(), null)
				.stream().findAny().orElse(null);
		if (Objects.isNull(udl)) return;

		List<String> usiList = usiDutyService.findUsiFromBp(request.getPt(), cass.getCode(), request.getUst(),
				request.getGg(), request.getDfdl(), null, lcpSession.getCode(),
				PositionAssignEnum.findByUstAndUlc(request.getUst(), false)
		);

		log.info("In gg {} dfdl {}, pt {} findUsiFromBp usiList {}",
				request.getGg(), request.getDfdl(), request.getPt(), usiList.size());

		if (usiList.isEmpty()) {
			return;
		}

		// Tim pod dua vao cady, khong phai now //done
		BpPodProductOfDeal pod = bpPodProductOfDealService
				.findByUsiAndDate(usiList.get(0), request.getPt(), request.getStartPeriod().getTime());
		findClagByUlcAndPt(udl.getCode(), request.getPt()).forEach(clag -> {
			List<BpPODCLAG> podclags = clagPODService
					.findByCadyAndUstAndClag(udl.getMyCap().getCode(), request.getUst(), clag.getCode());

			if (!CollectionUtils.isEmpty(podclags)) {
				BpPODCLAG claG = podclags.get(0);
				if (!claG.getMypod().equals(pod.getCode())) {
					clagPODService.unpublishedPodClag(claG);
					//unPublishCUi, unPushlishCuie
					cuiService.unpublishCUICUIE(udl.getCode(), pod.getMyst());
					clagPODService.setPODClag(pod.getCode(), clag.getCode(), udl.getMyCap().getCode(),
							request.getUst());
					if (!mapUsiUdl.containsKey(pod.getUsi().getCode())) {
						mapUsiUdl.put(pod.getUsi().getCode(), udl.getCode());
					}
				}
			} else {
				clagPODService.setPODClag(pod.getCode(), clag.getCode(), udl.getMyCap().getCode(),
						request.getUst());
				if (!mapUsiUdl.containsKey(pod.getUsi().getCode())) {
					mapUsiUdl.put(pod.getUsi().getCode(), udl.getCode());
				}
			}
		});

		mapUsiUdl.keySet().
				forEach(usiCode -> {
					BpUniqueLearningComponent udl1 = bpULCService.findByCode(mapUsiUdl.get(usiCode));
					bpBpeEventService.createBpeEvent(BpeEventTypeEnum.CREATE_CUIE_BPPODCLAG_WITH_UST_EQUAL_DTE);
					cuiEventService.createCUIJoinEvent(udl1.getCode(), usiCode, udl1.getMyLcp().getCode(), null,
							udl1.getMyCap().getCode());
					cuiEventService.setPublishCUIE(udl1.getCode(), usiCode, true);
				});
	}


	private void assignQO(ClaGPodRequest request) throws Exception {
		BpUsiDuty bpUsiDuty = usiDutyService.findCashStart("22_23", "MT", // TODO
				request.getPt(), request.getGg(), request.getDfdl());

		BpLCP lcpSC = lcpService.findByParentLctAndLct(LCPLCTLCKEnum.DL_40MI.getCode(), LCPLCTLCKEnum.DSC_SS.getCode());
		BpLCP lcpSession = lcpService.findLcpSSByPtAndLct(request.getPt(), LCPLCTLCKEnum.DL_40MI.getCode());
		BpLCP lcpShift = lcpService.findLcpshByPtAndLct(request.getPt(), lcpSession.getMylctparent()); // TODO

		CalendarPeriod cash = request.getCashList().stream().filter(c -> c.getMyPrd()
						.equals(lcpShift.getMyprd()) && c.getCashStart().equals(bpUsiDuty.getMycashsta())).findAny()
				.orElseThrow(() -> new NotFoundException("Could not find any CAP with cady code = " + request.getCady()));
		CalendarPeriod cass = calendarPeriodService.getCASS(cash.getCode(), lcpSession.getLcperiodno(),
				lcpShift.getMystructure());

		// ULC - Session
		List<BpUniqueLearningComponent> ulcs = bpULCService.findUlcByCapLcpGgDfdlDfge
				(cass.getCode(), lcpSC.getCode(), request.getGg(), request.getDfdl(), null);
		if (ulcs.isEmpty()) {
			return;
		}

		List<String> usiList = usiDutyService.findUsiFromBp(null, cass.getCode(), request.getUst(),
				request.getGg(), request.getDfdl(), null, lcpSC.getCode(),
				PositionAssignEnum.findByUstAndUlc(request.getUst(), false)
		);

		log.info("In gg {} dfdl {}, pt {} findUsiFromBp usiList {}",
				request.getGg(), request.getDfdl(), request.getPt(), usiList.size());

		if (usiList.isEmpty()) {
			return;
		}

		String usi = usiList.get(0);
		// Tim pod dua vao cady
		BpPodProductOfDeal pod = bpPodProductOfDealService
				.findByUsiAndDate(usi, request.getPt(), request.getStartPeriod().getTime());

		ulcs.forEach(ulc -> {
			findClagByUlcAndPt(ulc.getCode(), request.getPt()).forEach(clag -> {
				List<BpPODCLAG> podclags = clagPODService
						.findByCadyAndUstAndClag(ulc.getMyCap().getCode(), request.getUst(), clag.getCode());

				if (!CollectionUtils.isEmpty(podclags)) {
					BpPODCLAG claG = podclags.get(0);
					if (!claG.getMypod().equals(pod.getCode())) {
						clagPODService.unpublishedPodClag(claG);
						//unPublishCUi, unPushlishCuie
						cuiService.unpublishCUICUIE(ulc.getCode(), pod.getMyst());
						clagPODService.setPODClag(pod.getCode(), clag.getCode(), ulc.getMyCap().getCode(),
								request.getUst());
					}
				} else {
					clagPODService.setPODClag(pod.getCode(), clag.getCode(), ulc.getMyCap().getCode(),
							request.getUst());
				}
			});

			bpBpeEventService.createBpeEvent(BpeEventTypeEnum.CREATE_CUIE_BPPODCLAG_WITH_UST_EQUAL_DTE);
			cuiEventService.createCUIJoinEvent(ulc.getCode(), usi, ulc.getMyLcp().getCode(), null,
					ulc.getMyCap().getCode());
			cuiEventService.setPublishCUIE(ulc.getCode(), usi, true);
		});
	}

	@Override
	@WriteBPUnitTestLog(value = BPLogProcessEnum.ASSIGN_TE_TO_CLAG)
	public void bppAssignTeToClagCAWK(Timestamp date, List<String> usts, List<String> pts, List<String> ggs,
									  List<String> dfdls) throws ParseException {
		if (Objects.isNull(usts) || usts.isEmpty()) usts = UsiTypeEnum.getListTeType();
		CalendarPeriod cawk = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.WEEK.getCode());

		if (Objects.isNull(cawk)) {
			throw new NotFoundException("Couldn't find cawk  with date: " + date);
		}

		String taskName = "bppAssignTeToClagCAWK" + cawk.getCode();
		if (BpTaskStatusEnum.PROCESSING.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
			throw new BadRequestException("A task for " + taskName + " is processing!!!");
		}

		bpTaskInfoService.create(taskName, BpTaskStatusEnum.PROCESSING, null);

		try {
			for (CalendarPeriod c : calendarPeriodService.findByParentAndCapType(cawk.getCode(),
					CalendarPeriodTypeEnum.DAY.getCode())) {
				bppAssignTEtoCLAG2(c.getCode(), usts, pts, ggs, dfdls);
			}
		} catch (Exception e) {
			log.error("Got error: {} when {}", e.getLocalizedMessage(), taskName);
			bpTaskInfoService.create(taskName, BpTaskStatusEnum.ERROR, e.getLocalizedMessage());
			throw e;
		}
		bpTaskInfoService.create(taskName, BpTaskStatusEnum.DONE, null);

	}

	@Override
	@WriteBPUnitTestLog(value = BPLogProcessEnum.ASSIGN_TE_TO_CLAG)
	public void bppAssignTeToClagCADY(Timestamp date, List<String> usts, List<String> pts,
									  List<String> ggs, List<String> dfdls) throws
			ParseException {
		if (Objects.isNull(usts) || usts.isEmpty()) usts = UsiTypeEnum.getListTeType();
		CalendarPeriod cady = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.DAY.getCode());
		if (Objects.isNull(cady)) {
			throw new NotFoundException("Couldn't find cady  with date: " + date);
		}

		String taskName = "bppAssignTeToClagCADY" + cady.getCode();
		if (BpTaskStatusEnum.PROCESSING.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
			throw new BadRequestException("A task for " + taskName + " is processing!!!");
		}

		bpTaskInfoService.create(taskName, BpTaskStatusEnum.PROCESSING, null);

		try {
			bppAssignTEtoCLAG2(cady.getCode(), usts, pts, ggs, dfdls);
		} catch (Exception e) {
			log.error("Got error: {} when {}", e.getLocalizedMessage(), taskName);
			bpTaskInfoService.create(taskName, BpTaskStatusEnum.ERROR, e.getLocalizedMessage());
			throw e;
		}
		bpTaskInfoService.create(taskName, BpTaskStatusEnum.DONE, null);
	}

	@Override
	public void bppAssignEmToClagCADY(Timestamp
											  date, List<String> usts, List<String> pts, List<String> ggs, List<String> dfdls) throws
			ParseException {
		if (Objects.isNull(usts) || usts.isEmpty()) usts = UsiTypeEnum.getListEmType();
		CalendarPeriod cady = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.DAY.getCode());
		if (Objects.isNull(cady)) {
			throw new NotFoundException("Couldn't find cady  with date: " + date);
		}

		String taskName = "bppAssignEmToClagCADY" + cady.getCode();
		try {
			bppAssignEMToClag(cady.getCode(), usts, pts, ggs, dfdls);
		} catch (Exception e) {
			log.error("Got error: {} when {}", e.getLocalizedMessage(), taskName);
			bpTaskInfoService.create(taskName, BpTaskStatusEnum.ERROR, e.getLocalizedMessage());
			throw e;
		}
		bpTaskInfoService.create(taskName, BpTaskStatusEnum.DONE, null);
	}

	@Override
	public void bppAssignEmToClagCAWK(Timestamp
											  date, List<String> usts, List<String> pts, List<String> ggs, List<String> dfdls) throws
			ParseException {
		if (Objects.isNull(usts) || usts.isEmpty()) usts = UsiTypeEnum.getListEmType();
		CalendarPeriod cawk = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.WEEK.getCode());
		if (Objects.isNull(cawk)) {
			throw new NotFoundException("Couldn't find cady  with date: " + date);
		}

		String taskName = "bppAssignEmToClagCAWK" + cawk.getCode();
		if (BpTaskStatusEnum.PROCESSING.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
			throw new BadRequestException("A task for " + taskName + " is processing!!!");
		}

		bpTaskInfoService.create(taskName, BpTaskStatusEnum.PROCESSING, null);

		try {
			for (CalendarPeriod c : calendarPeriodService.findByParentAndCapType(cawk.getCode(),
					CalendarPeriodTypeEnum.DAY.getCode())) {
				bppAssignEMToClag(c.getCode(), usts, pts, ggs, dfdls);
			}
		} catch (Exception e) {
			log.error("Got error: {} when {}", e.getLocalizedMessage(), taskName);
			bpTaskInfoService.create(taskName, BpTaskStatusEnum.ERROR, e.getLocalizedMessage());
			throw e;
		}
		bpTaskInfoService.create(taskName, BpTaskStatusEnum.DONE, null);


	}

	private void addUsiMap(String pt, String gg, String dfdl, String ust, String lck,
						   String cady, CalendarPeriod cash, Map<String, String> mapUsi) {
		BpLCP lcpSH = lcpService.findLcpFromPtLck(pt, lck, LCLEnum.LCSH.getName());
		BpLCP lcpSS = lcpService.findLcpFromPtLck(pt, lck, LCLEnum.LCSS.getName());
		List<BpUniqueLearningComponent> uSHList = bpULCService
				.findUlcByCapLcpGgDfdlDfge(cash.getCode(), lcpSH.getCode(),
						gg, dfdl, null);
		uSHList.forEach(ush -> {
			BpUniqueLearningComponent uSS = bpULCService
					.findUlcFromParentUlcAndLcp(ush.getCode(), lcpSS.getCode())
					.stream().findFirst().orElse(null);
			try {
				Map<String, String> x = processUlcULI(uSS, pt, cady, gg, dfdl, ust);
				mapUsi.putAll(x);
			} catch (Exception e) {
				log.error("Error when assign EM to CLAG uli {}, pt {}, cady {}, gg {}, dfdl {}, ust {}. Message: {}",
						uSS, pt, cady, gg, dfdl, ust, e.getMessage()
				);
			}
		});
	}

	@Override
	@MakeBpProcess(process = BppProcessTypeEnum.BPPASSIGN_EM, parentProcess = "")
	public void bppAssignEMToClag(String cady, List<String> usts, List<String> pts, List<String> ggs, List<String> dfdls) {
		List<CalendarPeriod> cashList = calendarPeriodService
				.findByParentAndCapType(cady, CalendarPeriodTypeEnum.SHIFT.getCode());
		usts.forEach(ust -> {
			if (ust.equals(UsiTypeEnum.EM_SO.getName())) {
				List<String> ptList = CollectionUtils.isEmpty(pts) ? bpPodProductOfDealService.getPtFromCadyAndUst(cady, ust) : pts;

				// UDL, ULI
				Map<String, String> mapUsiUdl = new HashMap<>();
				Map<String, String> mapUsiUli = new HashMap<>();
				bpBpsStepService.createBpsStep(BpsStepTypeEnum.ASSIGN_EM_TO_SO_QO_UDL);
				String bpsStepUdl = valueSaving.getBpsCode();
				bpBpsStepService.createBpsStep(BpsStepTypeEnum.ASSIGN_EM_TO_SO_ULI);
				String bpsStepUli = valueSaving.getBpsCode();
				ggs.forEach(gg -> {
					dfdls.forEach(dfdl -> {
						ptList.forEach(pt -> {
							cashList.forEach(cash -> {
								addUsiMap(pt, gg, dfdl, ust, LCPLCTLCKEnum.LCK_LI.getCode(), cady, cash, mapUsiUli);

								addUsiMap(pt, gg, dfdl, ust, LCPLCTLCKEnum.LCK_DL.getCode(), cady, cash, mapUsiUdl);
							});
						});
					});
				});
				valueSaving.setBpsCode(bpsStepUdl, false);
				createCuiWhenAssignPOD(BpeEventTypeEnum.ASSIGN_EM_CREATE_CUIE_UDL_POD, mapUsiUdl);
				valueSaving.setBpsCode(bpsStepUli, false);
				createCuiWhenAssignPOD(BpeEventTypeEnum.ASSIGN_EM_CREATE_CUIE_ULI_POD, mapUsiUli);

				//UGE and UCO
				bpBpsStepService.createBpsStep(BpsStepTypeEnum.ASSIGN_EM_TO_SO_UGE);
				bpBpsStepService.createBpsStep(BpsStepTypeEnum.ASSIGN_EM_TO_SO_UCO);
				ptList.forEach(pt -> {
					List<String> clagUGEs = new ArrayList<>();
					Map<String, String> mapClagUge = new HashMap<>();
					Map<String, String> mapClagUco = new HashMap<>();
					BpLCP lcpUge = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_GE.getCode(), LCLEnum.LCSS.getName());
					BpLCP lcpUco = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_CO.getCode(), LCLEnum.LCSS.getName());

					cashList.forEach(cash -> {
						bpULCService.findUlcByCap(cash.getCode()).forEach(ush -> {
							bpULCService.findUlcFromParentUlcAndLcp(ush.getCode(), lcpUge.getCode()).forEach(uge -> {
								if (Objects.nonNull(uge)) {
									findClagByUlcAndPt(uge.getCode(), pt).forEach(clag -> {
										if (!clagUGEs.contains(clag.getCode())) {
											clagUGEs.add(clag.getCode());
											clagPODService.findByCadyAndUstAndClag(uge.getMyCap().getCode(), ust, clag.getCode())
													.forEach(podClag -> {
														resetAssignToClag(podClag, uge.getCode());
													});
											mapClagUge.put(clag.getCode(), uge.getCode());
										}
									});
								}
							});
							bpULCService.findUlcFromParentUlcAndLcp(ush.getCode(), lcpUco.getCode()).forEach(uco -> {
								findClagByUlcAndPt(uco.getCode(), pt).forEach(clag -> {
									clagPODService.findByCadyAndUstAndClag(uco.getMyCap().getCode(), ust, clag.getCode())
											.forEach(podclag -> resetAssignToClag(podclag, uco.getCode()));
									if (!mapClagUco.containsKey(clag.getCode()))
										mapClagUco.put(clag.getCode(), uco.getCode());
								});
							});
						});
					});

					List<String> usiUgeList = usiDutyService.findEmFromCady(cady, LCETCodeEnum.ASSIGN_EM.getCode(), ust, lcpUge.getCode());
					List<String> usiUcoList = usiDutyService.findEmFromCady(cady, LCETCodeEnum.ASSIGN_EM.getCode(), ust, lcpUco.getCode());
					List<BpPodProductOfDeal> podUgeList = usiUgeList.stream().map(e -> bpPodProductOfDealService.findByUsi(e, pt)).
							collect(Collectors.toList());
					List<BpPodProductOfDeal> podUcoList = usiUcoList.stream().map(e -> bpPodProductOfDealService.findByUsi(e, pt)).
							collect(Collectors.toList());
					if (podUgeList.isEmpty()) {
						log.error("Coun't find  SO by cady : {}, lcet: {},ust: {},lcp: {} ,pt : {}" + cady, LCETCodeEnum.ASSIGN_EM.getCode()
								, ust, lcpUge, pt);
					}
					if (podUcoList.isEmpty()) {
						log.error("Coun't find  SO by cady : {}, lcet: {},ust: {},lcp: {} ,pt : {}" + cady, LCETCodeEnum.ASSIGN_EM.getCode()
								, ust, lcpUco, pt);
					}

					//set podList To clagList
					valueSaving.setPodCase("UGE", false);
					clagPODService.setPodListClagList(podUgeList, clagUGEs, ust, mapClagUge);

					valueSaving.setPodCase("UCO", false);
					clagPODService.setPodListClagList(podUcoList, new ArrayList<>(mapClagUco.keySet()), ust, mapClagUco);

				});
			}

			if (ust.equals(UsiTypeEnum.EM_TO.getName())) {
				List<String> ptList = Objects.isNull(pts) || pts.isEmpty() ? bpPodProductOfDealService.getPtFromCadyAndUst(cady, ust) :
						pts;
				ptList.forEach(pt -> {
					Map<String, String> clagUGEs = new HashMap<>();
					Map<String, String> clagUDLs = new HashMap<>();
					Map<String, String> clagUCOs = new HashMap<>();
					Map<String, String> clagULIs = new HashMap<>();

					cashList.forEach(cash -> {
						List<BpUniqueLearningComponent> ushList = bpULCService.findUlcByCap(cash.getCode());
						ushList.forEach(ush -> {
							// DL, LI
							BpLCP lcpsh = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_LI.getCode(), LCLEnum.LCSH.getName());
							BpUniqueLearningComponent udl = bpULCService
									.findUlcFromParentUlcAndLcp(ush.getCode(), lcpsh.getCode())
									.stream().findFirst().orElse(null);

							// GE
							BpLCP lcpssGe = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_GE.getCode(), LCLEnum.LCSS.getName());
							List<BpUniqueLearningComponent> uges = bpULCService.findUlcFromParentUlcAndLcp(ush.getCode(), lcpssGe.getCode());

							// CO
							BpLCP lcpssCo = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_CO.getCode(), LCLEnum.LCSS.getName());
							List<BpUniqueLearningComponent> ucos = bpULCService.findUlcFromParentUlcAndLcp(ush.getCode(), lcpssCo.getCode());

							// LI
							BpLCP lcpss = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_LI.getCode(), LCLEnum.LCSS.getName());
							List<BpUniqueLearningComponent> ulis = bpULCService.findUlcFromParentUlcAndLcp(ush.getCode(), lcpss.getCode());

							if (Objects.nonNull(udl)) {
								findClagByUlcAndPt(udl.getCode(), pt).forEach(clag -> {
									clagPODService.findByCadyAndUstAndClag(cady, ust, clag.getCode())
											.forEach(clagpod -> resetAssignToClag(clagpod, udl.getCode()));
									if (!clagUDLs.containsKey(clag.getCode())) {
										clagUDLs.put(clag.getCode(), udl.getCode());
									}
								});
							}
							uges.forEach(uge -> {
								if (Objects.nonNull(uge)) {
									findClagByUlcAndPt(uge.getCode(), pt).forEach(clag -> {
										clagPODService.findByCadyAndUstAndClag(cady, ust, clag.getCode())
												.forEach(clagpod -> resetAssignToClag(clagpod, uge.getCode()));
										if (!clagUGEs.containsKey(clag.getCode())) {
											clagUGEs.put(clag.getCode(), uge.getCode());
										}
									});
								}
							});
							ucos.forEach(uco -> {
								if (Objects.nonNull(uco)) {
									findClagByUlcAndPt(uco.getCode(), pt).forEach(clag -> {
										clagPODService.findByCadyAndUstAndClag(uco.getMyCap().getCode(), ust, clag.getCode())
												.forEach(clagpod -> resetAssignToClag(clagpod, uco.getCode()));
										if (!clagUCOs.containsKey(clag.getCode())) {
											clagUCOs.put(clag.getCode(), uco.getCode());
										}
									});
								}
							});
							ulis.forEach(uli -> {
								if (Objects.nonNull(uli)) {
									findClagByUlcAndPt(uli.getCode(), pt).forEach(clag -> {
										clagPODService.findByCadyAndUstAndClag(uli.getMyCap().getCode(), ust, clag.getCode())
												.forEach(clagpod -> resetAssignToClag(clagpod, uli.getCode()));
										if (!clagULIs.containsKey(clag.getCode())) {
											clagULIs.put(clag.getCode(), uli.getCode());
										}
									});
								}
							});
						});
					});
					BpLCP lcpUdl = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_DL.getCode(), LCLEnum.LCSS.getName());
					BpLCP lcpUge = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_GE.getCode(), LCLEnum.LCSS.getName());
					BpLCP lcpUco = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_CO.getCode(), LCLEnum.LCSS.getName());
					BpLCP lcpUli = lcpService.findLcpFromPtLck(pt, LCPLCTLCKEnum.LCK_LI.getCode(), LCLEnum.LCSS.getName());

					List<String> usiUdlList = usiDutyService.findEmFromCady(cady, LCETCodeEnum.ASSIGN_EM.getCode(), ust, lcpUdl.getCode());
					List<String> usiUgeList = usiDutyService.findEmFromCady(cady, LCETCodeEnum.ASSIGN_EM.getCode(), ust, lcpUge.getCode());
					List<String> usiUcoList = usiDutyService.findEmFromCady(cady, LCETCodeEnum.ASSIGN_EM.getCode(), ust, lcpUco.getCode());
					List<String> usiUliList = usiDutyService.findEmFromCady(cady, LCETCodeEnum.ASSIGN_EM.getCode(), ust, lcpUli.getCode());

					List<BpPodProductOfDeal> podUdlList = usiUdlList.stream().map(e -> bpPodProductOfDealService.findByUsi(e, pt)).collect(Collectors.toList());
					List<BpPodProductOfDeal> podUgeList = usiUgeList.stream().map(e -> bpPodProductOfDealService.findByUsi(e, pt)).collect(Collectors.toList());
					List<BpPodProductOfDeal> podUcoList = usiUcoList.stream().map(e -> bpPodProductOfDealService.findByUsi(e, pt)).collect(Collectors.toList());
					List<BpPodProductOfDeal> podUliList = usiUliList.stream().map(e -> bpPodProductOfDealService.findByUsi(e, pt)).collect(Collectors.toList());
					if (podUdlList.isEmpty()) {
						log.error("Find zero TO for udl by cady,lcet,ust,lcp,pt : " + cady + ","
								+ LCETCodeEnum.ASSIGN_EM.getCode() + "," + ust + "," + lcpUdl + ",", pt);
					}
					if (podUgeList.isEmpty()) {
						log.error("Find zero TO for uge by cady,lcet,ust,lcp,pt : " + cady + ","
								+ LCETCodeEnum.ASSIGN_EM.getCode() + "," + ust + "," + lcpUge + ",", pt);
					}
					if (podUcoList.isEmpty()) {
						log.error("Find zero TO for uco by cady,lcet,ust,lcp,pt : " + cady + ","
								+ LCETCodeEnum.ASSIGN_EM.getCode() + "," + ust + "," + lcpUco + ",", pt);
					}
					if (clagUDLs.isEmpty()) {
						log.error("Find zero Clag for udl by cady : {} , pt : {} ", cady, pt);
					}
					if (clagUGEs.isEmpty()) {
						log.error("Find zero Clag for uge by cady : {} , pt : {} ", cady, pt);
					}
					if (clagUCOs.isEmpty()) {
						log.error("Find zero Clag for uco by cady : {} , pt : {} ", cady, pt);
					}
					if (podUliList.isEmpty()) {
						log.error("Find zero Clag for uco by cady : {} , pt : {} ", cady, pt);
					}

					valueSaving.setPodCase("UDL", false);
					clagPODService.setPodListClagList(podUdlList, new ArrayList<>(clagUDLs.keySet()), ust, clagUDLs);

					valueSaving.setPodCase("UGE", false);
					clagPODService.setPodListClagList(podUgeList, new ArrayList<>(clagUGEs.keySet()), ust, clagUGEs);

					valueSaving.setPodCase("UCO", false);
					clagPODService.setPodListClagList(podUcoList, new ArrayList<>(clagUCOs.keySet()), ust, clagUCOs);

					valueSaving.setPodCase("ULI", false);
					clagPODService.setPodListClagList(podUliList, new ArrayList<>(clagULIs.keySet()), ust, clagULIs);
				});
			}
		});
	}

	public void createCuiWhenAssignPOD(BpeEventTypeEnum bpeE, Map<String, String> mapUsiUlc) {
		mapUsiUlc.keySet().forEach(usiCode -> {
			BpUniqueLearningComponent ulc = bpULCService.findByCode(mapUsiUlc.get(usiCode));
			bpBpeEventService.createBpeEvent(bpeE);
			cuiEventService.createCUIJoinEvent(ulc.getCode(), usiCode, ulc.getMyLcp().getCode(), null, ulc.getMyCap().getCode());
			cuiEventService.setPublishCUIE(ulc.getCode(), usiCode, true);
		});
	}

	public Map<String, String> processUlcULI(BpUniqueLearningComponent ulc,
											 String pt,
											 String cady,
											 String gg,
											 String dfdl,
											 String ust) {

		Map<String, String> mapUsiUlc = new HashMap<>();
		String productType = ProductTypeEnum.BC.getName();
		if (Objects.isNull(ulc.getMyJoinUlc())) {
			productType = pt;
		} else {
			BpUniqueLearningComponent joinUdl = bpULCService.findByCode(ulc.getMyJoinUlc());
			if (Objects.nonNull(joinUdl)) {
				String p = lcpService.findPtFromLcpAndLck(joinUdl.getMyLcp().getCode(), LCPLCTLCKEnum.LCK_DL.getCode());
				productType = Objects.isNull(p) ? ProductTypeEnum.BC.getName() : p;
			}
		}
		BpLCP lcp = lcpService.findLcpFromPtLck(productType, LCPLCTLCKEnum.LCK_DL.getCode(), LCLEnum.LCSS.getName());
		List<String> usiList = usiDutyService.findUsiFromBp(
				productType, cady, ust,
				gg, dfdl, null, lcp.getCode(),
				PositionAssignEnum.findByUstAndUlc(ust, false));
		if (usiList.size() != 1) {
			log.error("Find zero or more than one EM by pt {}, cady {}, gg {}, dfdl {}, lcp {} ",
					pt,
					cady,
					gg,
					dfdl,
					lcp
			);
			return null;
		}
		BpPodProductOfDeal pod = bpPodProductOfDealService.findByUsi(usiList.get(0), pt);
		findClagByUlcAndPt(ulc.getCode(), pt).forEach(clag -> {
			List<BpPODCLAG> podclags = clagPODService.findByCadyAndUstAndClag(ulc.getMyCap().getCode(), ust, clag.getCode());
			if (!CollectionUtils.isEmpty(podclags)) {
				BpPODCLAG claG = podclags.get(0);
				if (!claG.getMypod().equals(pod.getCode())) {
					log.info("pod_claG list is not empty and [claG.getMypod().equals(pod.getCode()) = false] -> unpublished pod claG, cui, cuiE");
					clagPODService.unpublishedPodClag(claG);
					cuiService.unpublishCUICUIE(ulc.getCode(), pod.getMyst());

					clagPODService.setPODClag(pod.getCode(), clag.getCode(), ulc.getMyCap().getCode(), ust);
					if (!mapUsiUlc.containsKey(pod.getMyst())) {
						mapUsiUlc.put(pod.getMyst(), ulc.getCode());
					}
				}
			} else {
				clagPODService.setPODClag(pod.getCode(), clag.getCode(), cady, ust);
				if (!mapUsiUlc.containsKey(pod.getMyst())) {
					mapUsiUlc.put(pod.getMyst(), ulc.getCode());
				}
			}
		});

		return mapUsiUlc;
	}

	public List<BpClagClassgroup> findClagByUlcAndType(String ulcCode, String clagType) {
		return bpClagClassgroupRepository.findClagByUlcAndType(ulcCode, clagType);
	}

	@Override
	public List<BpClagClassgroup> findClagByUlcAndPt(String ulcCode, String ptCode) {
		return bpClagClassgroupRepository.findClagByULcAndPt(ulcCode, ptCode);
	}

	@Override
	public List<BpClagClassgroup> findBy(String pt, String gg, String dfdl, String clagType) {
		return bpClagClassgroupRepository.findAllByMyptAndMyggAndMydfdlAndClagtypeAndActiveTrue(pt, gg, dfdl, clagType);
	}

	@Override
	public BpClagClassgroup findByXClass(String xclass) {
		return bpClagClassgroupRepository.findByXCLass(xclass);
	}

	@Override
	@Transactional
	public List<BpClagClassgroup> createClagDynDfge(
			String pt,
			String gg,
			String dfdl,
			CalendarPeriod cap,
			BpLCP lcp,
			List<BpPodProductOfDeal> pods
	) {
		log.info("createClagDynDfge with pt: {} gg: {} dfdl: {} cap: {} lcp: {}", pt, gg, dfdl, cap.getCode(), lcp.getCode());
		List<BpClagClassgroup> clags = new ArrayList<>();

		usiDutyService.findBy(pt, gg, dfdl, cap.getCode(), lcp.getCode(), UsiTypeEnum.GTE.getName())
				.stream()
				.filter(p -> PositionAssignEnum.MAIN.getName().equals(p.getPosition()))
				.collect(Collectors.groupingBy(BpUsiDuty::getMydfge))
				.forEach((k, v) -> {
					if (Objects.nonNull(k)) {
						if (v.size() > 0) {
							clags.addAll(createClagDyn(pt, gg, dfdl, cap, k, v.stream().map(BpUsiDuty::getMyUsi)
									.collect(Collectors.toList())));

						}
					} else {
						log.warn("Got null dfge when createClagDynDfge with pt: {} gg: {} dfdl: {} cap: {} lcp: {}",
								pt, gg, dfdl, cap.getCode(), lcp.getCode());
					}
				});

		clags.add(
				createCladDynForX(pt, gg, dfdl, cap, pods)
		);

		return checkAndSaveAll(clags);
	}

	private List<BpClagClassgroup> checkAndSaveAll(List<BpClagClassgroup> clags) {

		if (CollectionUtils.isEmpty(clags)) {
			return Collections.emptyList();
		}

		Map<String, Integer> existed = bpClagClassgroupRepository.findByCodeIn(clags.stream()
						.map(BpClagClassgroup::getCode)
						.collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(BpClagClassgroup::getCode, BpClagClassgroup::getId));

		clags.forEach(c -> c.setId(existed.get(c.getCode())));

		return bpClagClassgroupRepository.saveAll(clags);
	}

	@Override
	public List<CLAGDetailInfoProjection> findClagDetailFromULC(String ulcCode) {
		return bpClagClassgroupRepository.findClagDetailFromULC(ulcCode);
	}

	private BpClagClassgroup createCladDynForX(
			String pt,
			String gg,
			String dfdl,
			CalendarPeriod cass,
			List<BpPodProductOfDeal> pods
	) {
		CalendarPeriod shiftCap = calendarPeriodService.findByCode(cass.getMyParent());

		String xcash = DateUtils.format(
				DateUtils.subtractSecondsFromTimestamp(shiftCap.getStartTime(), 300),
				DateUtils.UTC_TIME_ZONE,
				"HHmmss"
		);


		String wso = DateUtils.getClassDay(cass.getEndTime()).toString();

		String code = String.join("-",
				pt,
				gg,
				dfdl,
				wso,
				DateUtils.format(cass.getStartTime(), "yyyyMMdd"),
				shiftCap.getCashStart(),
				DfgeEnum.X.getCode(),
				"C200"
		);

		BpClagClassgroup clag = bpClagClassgroupRepository.createOrUpdate(BpClagClassgroup
				.builder()
				.code(code)
				.active(true)
				.mydfdl(dfdl)
				.mywso(wso)
				.clagtype(ClagType.DYNAMIC.getCode())
				.mypt(pt)
				.mygg(gg)
				.mydfge(DfgeEnum.X.getCode())
				.maxtotalstudents(12)
				.xcash(xcash)
				.build());

		pods.forEach(
				pod -> clagPODService.setPODClag(pod.getCode(), code, cass.getCode(), USTEnum.ST.getName())
		);

		return clag;
	}

	private List<BpClagClassgroup> createClagDyn(
			String pt,
			String gg,
			String dfdl,
			CalendarPeriod cap,
			String dfge,
			List<String> listUsi
	) {
		List<BpClagClassgroup> result = new ArrayList<>();

		CalendarPeriod shiftCap = calendarPeriodService.findByCode(cap.getMyParent());

		String cashStart = null;
		String xcash = null;

		if (shiftCap != null && CalendarPeriodTypeEnum.SHIFT.getCode().equals(shiftCap.getCapType())) {
			xcash = DateUtils.format(
					DateUtils.subtractSecondsFromTimestamp(shiftCap.getStartTime(), 300),
					DateUtils.UTC_TIME_ZONE,
					"HHmmss"
			);

			cashStart = shiftCap.getCashStart();
		}

		for (int i = 0; i < listUsi.size(); i++) {
			String wso = DateUtils.getClassDay(cap.getEndTime()).toString();
			String code = String.join("-",
					pt,
					gg,
					dfdl,
					wso,
					DateUtils.format(cap.getStartTime(), "yyyyMMdd"),
					cashStart,
					dfge,
					"C200",
					String.valueOf(i + 1)
			);

			BpClagClassgroup clag = bpClagClassgroupRepository.createOrUpdate(BpClagClassgroup
					.builder()
					.code(code)
					.active(true)
					.mydfdl(dfdl)
					.mywso(wso)
					.clagtype(ClagType.DYNAMIC.getCode())
					.mypt(pt)
					.mygg(gg)
					.mydfge(dfge)
					.maxtotalstudents(12)
					.xcash(xcash)
					.build());
			result.add(clag);

			if (Objects.nonNull(listUsi.get(i))) {
				try {
					BpPodProductOfDeal pod = bpPodProductOfDealService
							.findByUsiAndDate(listUsi.get(i), pt, cap.getStartTime().getTime());
					clagPODService.setPODClag(pod.getCode(), code, cap.getCode(), "GTE");
				} catch (Exception e) {
					log.error("setPODClag error clag-{} usi-{} {}", clag.getCode(), listUsi.get(i), e.getMessage());
				}
			}
		}


		return result;
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_CLAGPERM_FROM_X
	)
	public BpClagClassgroupResponse getCLAGPERMFromX(String xClass_id) {
		BPClassInfoResponse response = lmsService.getClassByCode(xClass_id);
		BpClagClassgroupResponse response1 = BpClagClassgroupResponse.builder()
				.active(true)
				.clagtype(ClagType.PERMANANT.getCode())
				.maxtotalstudents(response.getMaxActiveStudents())
				.mygg(Cep100TransformUtils.toGGCode(response.getGradeId()))
				.mypt(Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(response.getTrainingTypeId()), response.getClassCode()))
				.mydfdl(Cep100TransformUtils.toDfdlCode(Math.toIntExact(response.getClassLevelId())))
				.mywso(bpWsoWeeklyscheduleoptionService.classDayToWSO(response.getClassDays()))
				.xclass(response.getClassCode())
				.name(response.getClassName())
				.build();
		response1.setCode(String.join("-", response1.getMypt(), response1.getMywso(), response1.getMygg(), response1.getMydfdl(), response.getId().toString()));
		return response1;
	}

	public BpClagClassgroupResponse getCLAGPERMFromXClassInfo(XClassInfoResponse xclassInfo) {
		BPClassInfoResponse bpClassInfo = lmsService.getClassByCode(xclassInfo.getClassCode());
		BpClagClassgroupResponse response = BpClagClassgroupResponse.builder()
				.active(true)
				.clagtype(ClagType.PERMANANT.getCode())
				.maxtotalstudents(bpClassInfo.getMaxActiveStudents())
				.mygg(Cep100TransformUtils.toGGCode(bpClassInfo.getGradeId()))
				.mypt(Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(bpClassInfo.getTrainingTypeId()), bpClassInfo.getClassCode()))
				.mydfdl(Cep100TransformUtils.toDfdlCode(Math.toIntExact(bpClassInfo.getClassLevelId())))
				.mywso(bpWsoWeeklyscheduleoptionService.classDayToWSO(bpClassInfo.getClassDays()))
				.xclass(bpClassInfo.getClassCode())
				.name(bpClassInfo.getClassName())
				.assignedAt(xclassInfo.getJoinClassAt())
				.unassignedAt(xclassInfo.getExpireAt())
				.build();
		response.setCode(String.join("-", response.getMypt(), response.getMywso(), response.getMygg(), response.getMydfdl(), bpClassInfo.getId().toString()));
		return response;
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.FIND_XCLASS
	)
	public String findXCLASS(Long xXdealid) {
		return lmsService.getClassCodeByXDeal(xXdealid);
	}

	@Override
	public XClassInfoResponse findXClassInfo(Long xdeal) {
		return lmsService.getXClassInfoByXDeal(xdeal);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getPOD_CLAGPERM")
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_POD_CLAGPERM
	)
	public BpClagClassgroupResponse getPOD_CLAGPERM(String podPod_code) {
		BpPodProductOfDeal bp = bpPodProductOfDealService.findByCode(podPod_code);
		XClassInfoResponse xClassInfo = findXClassInfo(bp.getXdeal());
		return getCLAGPERMFromXClassInfo(xClassInfo);
	}

}
