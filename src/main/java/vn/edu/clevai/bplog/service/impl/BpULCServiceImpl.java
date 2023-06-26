package vn.edu.clevai.bplog.service.impl;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.*;
import vn.edu.clevai.bplog.dto.bp.ValueDto;
import vn.edu.clevai.bplog.dto.cep200.ULCInfoDTO;
import vn.edu.clevai.bplog.dto.redis.BppProcessDTO;
import vn.edu.clevai.bplog.dto.redis.BpsStepDTO;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentType;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.enums.Lcp;
import vn.edu.clevai.bplog.payload.request.bp.BPClagRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPGetContentsRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPScheduleUssRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPUlcRequest;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.payload.response.ULCDetailInfoResponse;
import vn.edu.clevai.bplog.payload.response.ulc.ULCResponse;
import vn.edu.clevai.bplog.queue.RedisMessagePublisher;
import vn.edu.clevai.bplog.repository.*;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.repository.projection.ULCMergeInfoProjection;
import vn.edu.clevai.bplog.repository.projection.UsidDistinctInfoProjection;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.bplog.utils.ComboUlcFunction;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.ConflictException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.api.model.MessageResponseDTO;
import vn.edu.clevai.common.api.slack.SlackService;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.api.util.ListUtils;
import vn.edu.clevai.common.api.util.StrUtils;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCLAGULCResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODCLAGResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpULCResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.BPDscInfoResponse;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum.BPP_TOGGLE_PUBLISHED_USH;
import static vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum.SET_PUBLISHED_USS_117;
import static vn.edu.clevai.bplog.common.enumtype.BpsStepTypeEnum.SET_PUBLISHED_USS_1171;

@Service
@Slf4j
public class BpULCServiceImpl extends BaseProxyService implements BpULCService {

	private static final Integer GET_SESSION_DLG_EXTRA_MINUTES = 45;

	private final BpClagClassgroupService bpClagClassgroupService;

	private final CalendarPeriodService calendarPeriodService;

	private final BpCLAGULCRepository bpCLAGULCRepository;

	private final BpUniqueLearningComponentRepository bpUniqueLearningComponentRepository;

	private final BpDfgeDifficultgetRepository bpDfgeDifficultgetRepository;


	private final BpULCService bpUlcService;

	private final CurriculumProgramPackageService crppService;

	private final BpLCPService bpLCPService;

	private final ModelMapper modelMapper;

	private final BpLctService bpLctService;

	private final CuiService cuiService;

	private final BpGGGradeGroupService ggService;

	private final BpDfdlDifficultygradeService dfdlService;

	private final BpClagClassgroupService clagClassgroupService;

	private final BpClagClassgroupRepository clagRepository;

	private final BpUSIDutyService usiDutyService;

	private final BpPodProductOfDealService bpPodProductOfDealService;

	private final CuiEventService cuiEventService;

	private final LmsService lmsService;

	private final LocalValueSaving valueSaving;
	private final BpBppProcessService bpBppProcessService;
	private final BpBpsStepService bpBpsStepService;
	private final RedisMessagePublisher publisher;
	private final BpPodProductOfDealRepository bpPodProductOfDealRepository;
	private final BpLCPRepository bpLCPRepository;
	private final BpPTProductTypeRepository bpPTProductTypeRepository;
	private final BpGGGradeGroupRepository bpGGGradeGroupRepository;
	private final BpDfdlDifficultygradeRepository bpDfdlDifficultygradeRepository;
	private final CurriculumPeriodService curriculumPeriodService;
	private final CurriculumProgramSheetService curriculumProgramSheetService;
	private final BpUsiUserItemService userItemService;

	private final BpBppProcessService bppProcessService;
	private final BpBpsStepService bpsStepService;
	private final BpBpeEventService bpeEventService;
	private final ULCMergeService ulcMergeService;

	private final RedisLockService redisLockService;
	private final ScheduleWcService scheduleWcService;


	private final BpClagPODService clagPODService;

	private static final String PREFIX_SCHEDULE_SHIFT = "scheduleShifts*";

	private final BpWsoWeeklyscheduleoptionService wsoWeeklyscheduleoptionService;

	private final List<String> LIST_UST_SCHEDULE_ULC = ImmutableList.of(
			USTEnum.ST.getName(),
			USTEnum.TE.getName(),
			USTEnum.SO.getName(),
			USTEnum.TO.getName(),
			USTEnum.CO.getName()
	);

	private final List<String> LIST_EM_UST = ImmutableList.of(
			USTEnum.SO.getName(),
			USTEnum.TO.getName(),
			USTEnum.CO.getName()
	);

	private final List<String> LIST_GE_PT = ImmutableList.of(
			"BC",
			"PM",
			"TP10",
			"PO"
	);

	private final JdbcTemplate jdbcTemplate;

	private final ListUtils listUtils;

	private final BpTaskInfoService bpTaskInfoService;

	private final AccYearService accYearService;

	private final BpUlcToXAtomicService bpUlcToXAtomicService;

	private final SlackService slackService;

	public BpULCServiceImpl(
			@Lazy BpClagClassgroupService bpClagClassgroupService,
			CalendarPeriodService calendarPeriodService,
			BpCLAGULCRepository bpCLAGULCRepository,
			BpUniqueLearningComponentRepository bpUniqueLearningComponentRepository,
			BpDfgeDifficultgetRepository bpDfgeDifficultgetRepository,
			@Lazy BpULCService bpUlcService,
			CurriculumProgramPackageService crppService, BpLCPService bpLCPService,
			ModelMapper modelMapper,
			BpLctService bpLctService,
			@Lazy CuiService cuiService,
			BpGGGradeGroupService ggService,
			BpDfdlDifficultygradeService dfdlService,
			BpClagClassgroupService clagClassgroupService,
			BpClagClassgroupRepository clagRepository,
			BpUSIDutyService usiDutyService,
			BpPodProductOfDealService bpPodProductOfDealService,
			CuiEventService cuiEventService,
			LmsService lmsService,
			LocalValueSaving valueSaving,
			BpBppProcessService bpBppProcessService,
			BpBpsStepService bpBpsStepService,
			RedisMessagePublisher publisher,
			BpPodProductOfDealRepository bpPodProductOfDealRepository,
			BpLCPRepository bpLCPRepository,
			BpPTProductTypeRepository bpPTProductTypeRepository,
			BpGGGradeGroupRepository bpGGGradeGroupRepository,
			BpDfdlDifficultygradeRepository bpDfdlDifficultygradeRepository,
			CurriculumPeriodService curriculumPeriodService,
			CurriculumProgramSheetService curriculumProgramSheetService, BpUsiUserItemService userItemService,
			BpBppProcessService bppProcessService, BpBpsStepService bpsStepService, BpBpeEventService bpeEventService,
			ULCMergeService ulcMergeService, @Lazy BpClagPODService clagPODService,
			@Lazy BpWsoWeeklyscheduleoptionService wsoWeeklyscheduleoptionService, JdbcTemplate jdbcTemplate,
			@Lazy ScheduleWcService scheduleWcService,
			ListUtils listUtils,
			BpTaskInfoService bpTaskInfoService,
			AccYearService accYearService,
			RedisLockService redisLockService,
			BpUlcToXAtomicService bpUlcToXAtomicService, SlackService slackService) {
		this.bpClagClassgroupService = bpClagClassgroupService;
		this.calendarPeriodService = calendarPeriodService;
		this.bpCLAGULCRepository = bpCLAGULCRepository;
		this.bpUniqueLearningComponentRepository = bpUniqueLearningComponentRepository;
		this.bpDfgeDifficultgetRepository = bpDfgeDifficultgetRepository;
		this.bpUlcService = bpUlcService;
		this.crppService = crppService;
		this.bpLCPService = bpLCPService;
		this.modelMapper = modelMapper;
		this.bpLctService = bpLctService;
		this.cuiService = cuiService;
		this.ggService = ggService;
		this.dfdlService = dfdlService;
		this.clagClassgroupService = clagClassgroupService;
		this.clagRepository = clagRepository;
		this.usiDutyService = usiDutyService;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.cuiEventService = cuiEventService;
		this.lmsService = lmsService;
		this.valueSaving = valueSaving;
		this.bpBppProcessService = bpBppProcessService;
		this.bpBpsStepService = bpBpsStepService;
		this.publisher = publisher;
		this.bpPodProductOfDealRepository = bpPodProductOfDealRepository;
		this.bpLCPRepository = bpLCPRepository;
		this.bpPTProductTypeRepository = bpPTProductTypeRepository;
		this.bpGGGradeGroupRepository = bpGGGradeGroupRepository;
		this.bpDfdlDifficultygradeRepository = bpDfdlDifficultygradeRepository;
		this.curriculumPeriodService = curriculumPeriodService;
		this.curriculumProgramSheetService = curriculumProgramSheetService;
		this.userItemService = userItemService;
		this.bppProcessService = bppProcessService;
		this.bpsStepService = bpsStepService;
		this.bpeEventService = bpeEventService;
		this.ulcMergeService = ulcMergeService;
		this.clagPODService = clagPODService;
		this.wsoWeeklyscheduleoptionService = wsoWeeklyscheduleoptionService;
		this.scheduleWcService = scheduleWcService;
		this.redisLockService = redisLockService;
		this.jdbcTemplate = jdbcTemplate;
		this.listUtils = listUtils;
		this.bpTaskInfoService = bpTaskInfoService;
		this.accYearService = accYearService;
		this.bpUlcToXAtomicService = bpUlcToXAtomicService;
		this.slackService = slackService;
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getGESFromX")
	public List<BpULCResponse> getGESFromX(String xdsc) {
		BPDscInfoResponse r = lmsService.getDscInfoByXdsc(xdsc);

		if (!Objects.equals(r.getTrainingTypeId(), TrainingTypeEnum.GET.getId())) {
			return Collections.emptyList();
		}

		String dfdl = Cep100TransformUtils.toDfdlCode(Math.toIntExact(r.getClassLevelId()));

		String gg = Cep100TransformUtils.toGGCode(r.getGradeId());

		String lct = Cep100TransformUtils.trainingTypeIdToLct(r.getTrainingTypeId());

		String lcp = bpLCPService.findShiftLcp(
				Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(r.getTrainingTypeId()), r.getClassCode()),
				lct
		).getCode();

		CalendarPeriod cash = getCapShift(new Timestamp(r.getLiveAt().getTime()), "", gg, "", lct);

		return Collections.singletonList(
				BpULCResponse
						.builder()
						.myCap(cash.getCode())
						.myDfdl(dfdl)
						.myGg(gg)
						.myLct(lct)
						.myLcp(lcp)
						.code(String.join("-", lcp, cash.getCode(), gg, dfdl))
						.xdsc(xdsc)
						.build()
		);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getUSHFromX")
	public List<BpULCResponse> getUSHFromX(String xdsc) {
		List<BPDscInfoResponse> responses = lmsService.getByXdsc(xdsc);
		List<BpULCResponse> responseList = new ArrayList<>();

		assert responses != null;

		for (BPDscInfoResponse d : responses) {
			String dfdl = Cep100TransformUtils.toDfdlCode(Math.toIntExact(d.getClassLevelId()));
			String gg = Cep100TransformUtils.toGGCode(d.getGradeId());
			String lct = Cep100TransformUtils.trainingTypeIdToLct(d.getClassTrainingTypeId());

			String lcp = bpLCPService.findShiftLcp(
					Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(d.getClassTrainingTypeId()), d.getClassCode()),
					lct
			).getCode();

			CalendarPeriod cash = getCapShift(new Timestamp(d.getLiveAt().getTime()), "", gg, "", lct);

			responseList.add(
					BpULCResponse.builder()
							.myCap(cash.getCode())
							.myDfdl(dfdl)
							.myGg(gg)
							.myLct(lct)
							.myLcp(lcp)
							.code(String.join("-", lcp, cash.getCode(), gg, dfdl))
							.xdsc(xdsc)
							.build());
		}
		return responseList;
	}

	@Override
	@WriteUnitTestLog
	public List<BpULCResponse> getUDLFromX(String xdsc) {
		List<BPDscInfoResponse> responses = lmsService.getByXdsc(xdsc);
		List<BpULCResponse> responseList = new ArrayList<>();

		for (BPDscInfoResponse d : responses) {
			String dfdl = Cep100TransformUtils.toDfdlCode(Math.toIntExact(d.getClassLevelId()));
			String gg = Cep100TransformUtils.toGGCode(d.getGradeId());
			String lct = Cep100TransformUtils.trainingTypeIdToLct(d.getClassTrainingTypeId());

			String lcp = bpLCPService.findFirstByMyptAndMylct(
					Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(d.getClassTrainingTypeId()), d.getClassCode()),
					LCPLCTLCKEnum.DL_40MI.getCode()
			).getCode();

			String parentLcp = bpLCPService.findShiftLcp(
					Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(d.getClassTrainingTypeId()), d.getClassCode()),
					lct
			).getCode();

			CalendarPeriod cass = getCapSession(new Timestamp(d.getLiveAt().getTime()), gg, lct,
					"1"); // Hard code session DL = 1

			CalendarPeriod cash = getCapShift(new Timestamp(d.getLiveAt().getTime()), "", gg, "", lct);

			responseList.add(
					BpULCResponse.builder()
							.myCap(cass.getCode())
							.myDfdl(dfdl)
							.myGg(gg)
							.myLct(LCPLCTLCKEnum.DL_40MI.getCode())
							.myLcp(lcp)
							.myJoinUlc(String.join("-", parentLcp, cash.getCode(), gg, dfdl))
							.code(String.join("-", lcp, cass.getCode(), gg, dfdl))
							.xdsc(xdsc)
							.build());
		}
		return responseList;
	}

	@Override
	@WriteUnitTestLog
	public String findXDSC(String xclass) {
		return lmsService.findXDSC(xclass);
	}

	@Override
	@WriteUnitTestLog
	@SneakyThrows
	public String findXDSC(
			String xsessiongroup,
			String xcash
	) {
		Timestamp liveAt = DateUtils.parse(xcash);

		return lmsService.getDscSessionGroupDetails(xsessiongroup, liveAt).getDscCode();
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getCLAGPERM_USH")
	public BpULCResponse getCLAGPERM_USH(String clagCode) {
		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagCode);

		if (!ClagType.PERMANANT.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}

		return getUSHFromX(findXDSC(clag.getXclass()))
				.stream()
				.filter(p -> clag.getMypt().equals(bpLCPService.findByCode(p.getMyLcp()).getMypt()))
				.findAny()
				.orElseThrow(
						() -> new NotFoundException("Could not find any CLAGPERM USH for clagCode = " + clagCode)
				);
	}

	@Override
	public BpULCResponse getCLAGDYN_GES(String clagCode) {
		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagCode);

		if (!ClagType.DYNAMIC.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is only for DYN!");
		}

		if (!clag.getMypt().equals("BC")) {
			throw new BadRequestException("This API is only for BC CLAGDYN");
		}

		return getGESFromX(findXDSC(clag.getXsessiongroup(), clag.getXcash()))
				.stream()
				.findFirst()
				.orElseThrow(
						() -> new NotFoundException("Couldn't find CLAGDYN GES with code: " + clagCode)
				);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getCLAGPERM_UDL")
	public BpULCResponse getCLAGPERM_UDL(String clagCode) {
		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagCode);

		if (!ClagType.PERMANANT.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}

		String mylcp = bpLCPService.findFirstByMyptAndMylct(clag.getMypt(), LCPLCTLCKEnum.DL_40MI.getCode()).getCode();

		return getUDLFromX(findXDSC(clag.getXclass()))
				.stream()
				.filter(u -> mylcp.equals(u.getMyLcp()))
				.findAny()
				.orElseThrow(
						() -> new NotFoundException("Couldn't find CLAGPERM UDL with code: " + clagCode)
				);
	}

	@Override
	public BpUniqueLearningComponent findByCode(String ulcCode) {
		return bpUniqueLearningComponentRepository.findFirstByCode(ulcCode)
				.orElseThrow(
						() -> new NotFoundException("Couldn't find ulc by code: " + ulcCode)
				);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setCLAGPERM_USH")
	public BpCLAGULCResponse setCLAGPERM_USH(String clagCode, String ulcCode) {
		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagCode);

		if (!ClagType.PERMANANT.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}

		return modelMapper.map(bpCLAGULCRepository.save(BpCLAGULC.builder()
				.myclag(clagCode)
				.myulc(ulcCode)
				.code(clagCode + "-" + ulcCode)
				.build()), BpCLAGULCResponse.class);
	}

	//	setCLAGDYN_GES
	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setCLAGDYN_GES")
	public BpCLAGULCResponse setCLAGDYN_GES(String clagCode, String ulcCode) {
		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagCode);

		if (!ClagType.DYNAMIC.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for DYN only!!!");
		}

		return modelMapper.map(bpCLAGULCRepository.save(BpCLAGULC.builder()
				.myclag(clagCode)
				.myulc(ulcCode)
				.code(clagCode + "-" + ulcCode)
				.build()), BpCLAGULCResponse.class);
	}

	@Override
	@Transactional
	@WriteUnitTestLog
	public BpCLAGULCResponse setCLAGPERM_UDL(String clagCode, String ulcCode) {
		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagCode);

		if (!ClagType.PERMANANT.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for CLAGPERM only!!!");
		}

		BpCLAGULC clagulc = bpCLAGULCRepository.findFirstByCode(clagCode + "-" + ulcCode).orElse(null);
		if (clagulc == null) {
			clagulc = bpCLAGULCRepository.save(BpCLAGULC.builder()
					.myclag(clagCode)
					.myulc(ulcCode)
					.code(clagCode + "-" + ulcCode)
					.mybps(valueSaving.getBpsCode())
					.build());
		}
		valueSaving.setBpsCode(clagulc.getMybps(), false);
		return modelMapper.map(clagulc, BpCLAGULCResponse.class);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getUGEFromX")
	public List<BpULCResponse> getUGEFromX(String xdsc) {

		BPDscInfoResponse dscInfo = lmsService.getDscInfoByXdsc(xdsc);
		if (dscInfo == null) {
			throw new NotFoundException("Not found dsc " + xdsc);
		}

		Date startTime;
		Long trainingTypeId;
		LCPLCTLCKEnum lctEnum;
		String sessionNumber = "1";
		switch (TrainingTypeEnum.ofId(dscInfo.getTrainingTypeId())) {
			case GET:
				startTime = new Date(dscInfo.getLiveAt().getTime() + GET_SESSION_DLG_EXTRA_MINUTES * 60 * 1000);
				trainingTypeId = dscInfo.getTrainingTypeId();
				lctEnum = LCPLCTLCKEnum.GE_75MI;
				break;
			case DILIVE:
				startTime = dscInfo.getLiveAt();
				trainingTypeId = TrainingTypeEnum.PLUS.getId();
				sessionNumber = "3"; // Hard code DLG
				lctEnum = LCPLCTLCKEnum.GE_45MI;
				break;
			default:
				startTime = dscInfo.getLiveAt();
				trainingTypeId = dscInfo.getTrainingTypeId();
				lctEnum = LCPLCTLCKEnum.GE_45MI;
				break;
		}

		String dfdl = Cep100TransformUtils.toDfdlCode(Math.toIntExact(dscInfo.getClassLevelId()));
		String gg = Cep100TransformUtils.toGGCode(dscInfo.getGradeId());
		String shiftLct = Cep100TransformUtils.trainingTypeIdToLct(trainingTypeId);
		String lcp = bpLCPService.findFirstByMyptAndMylct(
				Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(trainingTypeId), dscInfo.getClassCode()),
				lctEnum.getCode()
		).getCode();

		String shiftLcp = bpLCPService.findShiftLcp(
				Cep100TransformUtils.toPtCodeVer2(Math.toIntExact(trainingTypeId), dscInfo.getClassCode()),
				shiftLct
		).getCode();

		CalendarPeriod cash = getCapShift(new Timestamp(dscInfo.getLiveAt().getTime()), "", gg, "", shiftLct);

		CalendarPeriod cass = getCapSession(new Timestamp(startTime.getTime()), gg, shiftLct, sessionNumber);

		return bpDfgeDifficultgetRepository.findByCodeIsNot("X") // Hard code
				.stream()
				.map(bpDfgeDifficultget -> BpULCResponse.builder()
						.code(String.join("-", lcp, cass.getCode(), gg, dfdl, bpDfgeDifficultget.getCode()))
						.myJoinUlc(
								String.join("-", shiftLcp, cash.getCode(), gg, dfdl, bpDfgeDifficultget.getCode())
						)
						.myLct(LCPLCTLCKEnum.GE_45MI.getCode())
						.myGg(gg)
						.myCap(cass.getCode())
						.myDfdl(dfdl)
						.myDfge(bpDfgeDifficultget.getCode())
						.myLcp(lcp)
						.xdsc(xdsc)
						.published(true)
						.build())
				.collect(Collectors.toList());

	}

	private CalendarPeriod getCapShift(Timestamp timestamp, String pt, String gg, String dfdl, String lct) {
		CalendarPeriod cady = calendarPeriodService.getCAPByTime
				(timestamp, CalendarPeriodTypeEnum.DAY.getCode());

		if (Objects.isNull(cady)) {
			throw new NotFoundException("Couldn't find cady by timestamp: " + timestamp + " capPeriodType: " +
					CalendarPeriodTypeEnum.DAY.getCode());
		}

		return calendarPeriodService.getCASH(cady.getCode(), pt, gg, dfdl, "", "");
	}

	private CalendarPeriod getCapSession(Timestamp timestamp, String gg, String lct, String sessionNumber) {
		CalendarPeriod cady = calendarPeriodService.getCAPByTime
				(timestamp, CalendarPeriodTypeEnum.DAY.getCode());

		if (Objects.isNull(cady)) {
			throw new NotFoundException("Couldn't find cady by timestamp: " + timestamp + " capPeriodType: " + CalendarPeriodTypeEnum.DAY.getCode());
		}

		CalendarPeriod cash = calendarPeriodService.getCASH(cady.getCode(), gg, lct, "", "", "");

		if (Objects.isNull(cash)) {
			throw new NotFoundException("Couldn't find cash by cadyCode: " + cady.getCode() + " gg: " + gg + " lct: " + lct);
		}
		return calendarPeriodService.getCAP(cash.getCode(), CalendarPeriodTypeEnum.SESSION.getCode(),
				sessionNumber);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getCLAGDYN_UGE")
	public BpULCResponse getCLAGDYN_UGE(String clagdyn) {

		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagdyn);
		if (!ClagType.DYNAMIC.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for DYNAMIC only!!!");
		}

		return bpUlcService.getUGEFromX(findXDSC(clag.getXsessiongroup(), clag.getXcash()))
				.stream()
				.filter(bpULC -> Objects.equals(bpULC.getMyDfge(), clag.getMydfge()))
				.findFirst()
				.orElse(null);

	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setCLAGDYN_UGE")
	public BpCLAGULCResponse setCLAGDYN_UGE(String clagdyn, String uge) {

		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagdyn);
		if (!ClagType.DYNAMIC.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for DYNAMIC only!!!");
		}

		BpUniqueLearningComponent ulc = findByCode(uge);
		BpCLAGULC bpCLAGULC = bpCLAGULCRepository.createOrUpdate(BpCLAGULC.builder()
				.code(clagdyn + "-" + uge)
				.myclag(clagdyn)
				.myulc(ulc.getCode())
				.build());

		return modelMapper.map(bpCLAGULC, BpCLAGULCResponse.class);

	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setCLAGDYN_UGE_GEDLG")
	public BpCLAGULCResponse setCLAGDYN_UGE_GEDLG(String clagdyn, String uge) {
		// create bps
		String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.CALL_SET_CLAGDYN_UGE_GE_DLG);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.CALL_SET_CLAGDYN_UGE_GE_DLG.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());

		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagdyn);
		if (!ClagType.DYNAMIC.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for DYNAMIC only!!!");
		}

		BpUniqueLearningComponent ulc = findByCode(uge);
		BpCLAGULC bpCLAGULC = bpCLAGULCRepository.createOrUpdate(BpCLAGULC.builder()
				.code(clagdyn + "-" + uge)
				.myclag(clagdyn)
				.myulc(ulc.getCode())
				.mybps(bpsCode)
				.build());

		return modelMapper.map(bpCLAGULC, BpCLAGULCResponse.class);

	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setCLAGDYN_UGE_GEGES")
	public BpCLAGULCResponse setCLAGDYN_UGE_GEGES(String clagdyn, String uge) {
		// create bps
		String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.CALL_SET_CLAGDYN_UGE_GE_GES);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.CALL_SET_CLAGDYN_UGE_GE_GES.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());

		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagdyn);
		if (!ClagType.DYNAMIC.getCode().equals(clag.getClagtype())) {
			throw new BadRequestException("This API is for DYNAMIC only!!!");
		}

		BpUniqueLearningComponent ulc = findByCode(uge);
		BpCLAGULC bpCLAGULC = bpCLAGULCRepository.createOrUpdate(BpCLAGULC.builder()
				.code(clagdyn + "-" + uge)
				.myclag(clagdyn)
				.myulc(ulc.getCode())
				.mybps(bpsCode)
				.build());

		return modelMapper.map(bpCLAGULC, BpCLAGULCResponse.class);

	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setULC")
	@Transactional
	public BpULCResponse setULC(
			String myParent,
			String code,
			String name,
			String myJoinUlc,
			String mylct,
			String mygg,
			String mypt,
			String mycap,
			String mydfdl,
			String mydfge,
			String mylcp,
			String xdsc,
			Integer noaschild,
			Boolean published
	) {
		BpUniqueLearningComponent ulc = bpUniqueLearningComponentRepository.findFirstByCode(code)
				.orElseGet(
						() -> BpUniqueLearningComponent
								.builder()
								.code(code)
								.mybps(valueSaving.getBpsCode())
								.build()
				);
		ulc.setMyParent(myParent);
		ulc.setName(name);
		ulc.setMyJoinUlc(myJoinUlc);
		ulc.setUlcNo(noaschild);

		if (mylct != null) {
			ulc.setMyLct(bpLctService.findByCode(mylct));
		}

		ulc.setMyGg(mygg);
		ulc.setMyPt(mypt);

		if (mycap != null)
			ulc.setMyCap(
					calendarPeriodService.findByCode(mycap)
			);

		ulc.setMyDfdl(mydfdl);
		ulc.setMyDfge(mydfge);

		if (mylcp != null) {
			ulc.setMyLcp(bpLCPService.findByCode(mylcp));
		}

		ulc.setXdsc(xdsc);
		ulc.setPublished(published);
		bpUniqueLearningComponentRepository.save(ulc);
		valueSaving.setBpsCode(ulc.getMybps(), false);
		return BpULCResponse
				.builder()
				.id(ulc.getId())
				.myParent(myParent)
				.code(ulc.getCode())
				.name(ulc.getName())
				.myJoinUlc(ulc.getMyJoinUlc())
				.myLct(mylct)
				.myGg(ulc.getMyGg())
				.myCap(mycap)
				.myDfdl(ulc.getMyDfdl())
				.myDfge(ulc.getMyDfge())
				.myLcp(mylcp)
				.xdsc(ulc.getXdsc())
				.description(ulc.getDescription())
				.build();
	}

	@Override
	public void bppScheduleUDLC(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq) {

		String bppCode = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPPSCHEDULE_UDLC);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPPSCHEDULE_UDLC.getCode())
				.build());

		contentsReq.setGcGg(ggService.getGGFromX(contentsReq.getXGg()).getCode());
		contentsReq.setGcDfdl(dfdlService.getDFDLFromX(contentsReq.getXDfdl()).getCode());

		// 101 Schedule_My_CUI__UDLC
		String bppCode2 = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPPSCHEDULE_UDLC02);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode2)
				.code(bppCode2)
				.bpptype(BppProcessTypeEnum.BPPSCHEDULE_UDLC02.getCode())
				.myparent(bppCode)
				.build());

		/// 1011 A_Create_UDLC
		collectUDLCParameters(ulcReq, contentsReq);
		String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.A_CREATE_UDLC);
		valueSaving.setBpsCode(bpsCode, false);
		bpUlcService.createUDLC(ulcReq, contentsReq);
		if (bpsCode.equals(valueSaving.getBpsCode())) {
			publisher.publish(BpsStepDTO.builder()
					.name(bpsCode)
					.code(bpsCode)
					.bpstype(BpsStepTypeEnum.A_CREATE_UDLC.getCode())
					.myprocess(bppCode2)
					.build());
		}
		valueSaving.setBpsCode(null, false);

		// Schedule UDL
		bpUlcService.collectCLAGListDLC(sussReq);
		collectUDLParameters1(ulcReq, contentsReq, sussReq);
		String bppCode3 = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPPSCHEDULE_UDLC01);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode3)
				.code(bppCode3)
				.bpptype(BppProcessTypeEnum.BPPSCHEDULE_UDLC01.getCode())
				.myparent(bppCode)
				.build());

		bpUlcService.collectUDLParameters2(ulcReq, contentsReq, sussReq);
		String bppCode4 = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPPSCHEDULE_UDLC03);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode4)
				.code(bppCode4)
				.bpptype(BppProcessTypeEnum.BPPSCHEDULE_UDLC03.getCode())
				.myparent(bppCode)
				.build());

		bpUlcService.bppScheduleUSS(ulcReq, contentsReq, sussReq, "PERM");

	}

	@Override
	public void scheduleUDLGUDL(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq) {

		String bppCode = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPPSCHEDULE_UDLG);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPPSCHEDULE_UDLG.getCode())
				.build());

		contentsReq.setGcGg(ggService.getGGFromX(contentsReq.getXGg()).getCode());
		contentsReq.setGcDfdl(dfdlService.getDFDLFromX(contentsReq.getXDfdl()).getCode());

		bpUlcService.collectCLAGListDLG(sussReq);

		String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.A_CREATE_UDLG);
		valueSaving.setBpsCode(bpsCode, false);
		bpUlcService.createUDLG(ulcReq, contentsReq, sussReq);
		if (bpsCode.equals(valueSaving.getBpsCode())) {
			publisher.publish(BpsStepDTO.builder()
					.name(bpsCode)
					.code(bpsCode)
					.bpstype(BpsStepTypeEnum.A_CREATE_UDLG.getCode())
					.myprocess(bppCode)
					.build());
		}
		valueSaving.setBpsCode(null, false);

		bpUlcService.collectUDLParameters(ulcReq, contentsReq, sussReq);
		bpUlcService.bppScheduleUSS(ulcReq, contentsReq, sussReq, "PERM");
	}

	@Override
	public void scheduleUDLGUGE(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq) {
		contentsReq.setGcGg(ggService.getGGFromX(contentsReq.getXGg()).getCode());
		contentsReq.setGcDfdl(dfdlService.getDFDLFromX(contentsReq.getXDfdl()).getCode());

		bpUlcService.collectUGEParameters(ulcReq, contentsReq, sussReq);
		bpUlcService.collectUGEParameters2(ulcReq, contentsReq, sussReq);

		bpDfgeDifficultgetRepository.findByCodeIsNot("X").forEach(d -> {
			String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.SUGGEST_CLAG_UGE);
			valueSaving.setBpsCode(bpsCode, false);
			bpUlcService.suggestCLAGUGE(ulcReq, sussReq, d.getCode());
			if (bpsCode.equals(valueSaving.getBpsCode())) {
				publisher.publish(BpsStepDTO.builder()
						.name(bpsCode)
						.code(bpsCode)
						.bpstype(BpsStepTypeEnum.SUGGEST_CLAG_UGE.getCode())
						.build());
			}
			valueSaving.setBpsCode(null, false);
			contentsReq.setGcDfge(d.getCode());
			bpUlcService.bppScheduleUSS(ulcReq, contentsReq, sussReq, "DYN");
		});
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.CREATE_UDLC)
	public void createUDLC(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq) {
		createULC(ulcReq, contentsReq);
	}

	public void createULC(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq) {
		String ulcCode = getULCCode(ulcReq.getUlcLcpcode(), ulcReq.getUlcMycap(),
				contentsReq.getGcGg(), contentsReq.getGcDfdl(), contentsReq.getGcDfge());
		BpULCResponse uDLC = setULC(ulcReq.getUlcMyparentulc(), ulcCode, "SYSTEM CREATE", null, ulcReq.getUlcMylct(),
				contentsReq.getGcGg(), null, ulcReq.getUlcMycap(), contentsReq.getGcDfdl(), contentsReq.getGcDfge(),
				ulcReq.getUlcLcpcode(), ulcReq.getXDsc(), null, ulcReq.getUlcPublished());
		ulcReq.setUlcMyparentulc(uDLC.getCode());
		bpUlcService.setMyJointUdl1(ulcReq, uDLC);
		bpUlcService.setMyJointUdl2(ulcReq, uDLC);
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.SET_MY_JOINT_UDL_1)
	public void setMyJointUdl1(BPUlcRequest ulcReq, BpULCResponse uDLC) {
//		ulcReq.setUlcMyjoinulc(uDLC.getCode());
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.SET_MY_JOINT_UDL_2)
	public void setMyJointUdl2(BPUlcRequest ulcReq, BpULCResponse uDLC) {
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.CREATE_UDLG)
	public void createUDLG(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq) {
		ulcReq.setUlcLcpcode(LCPEnum.PM_1MN_C18_DLG_90MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.DLG_90MI.getCode());
		CalendarPeriod cady = calendarPeriodService.getCAPByTime(ulcReq.getTime(), CalendarPeriodTypeEnum.DAY.getCode());
		CalendarPeriod cash = calendarPeriodService
				.getCASH(cady.getCode(), contentsReq.getGcGg(), ulcReq.getUlcMylct(), "", "", "");
		ulcReq.setUlcMycap(cash.getCode());
		createULC(ulcReq, contentsReq);
	}

	@Override
	public void bppScheduleUDLC1(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq,
								 BPScheduleUssRequest sussReq) {
		contentsReq.setGcGg(ggService.getGGFromX(contentsReq.getXGg()).getCode());
		contentsReq.setGcDfdl(dfdlService.getDFDLFromX(contentsReq.getXDfdl()).getCode());
		bpUlcService.collectUDLCParameters(ulcReq, contentsReq);
		bpUlcService.collectUDLParameters1(ulcReq, contentsReq, sussReq);
	}

	@Override
	@Transactional
	public void setPublishedUSH(String xdsc, boolean published) {
		List<BpUniqueLearningComponent> ush = bpUniqueLearningComponentRepository.findByXdsc(xdsc);
		List<String> ushCodes = new ArrayList<>();
		Map<String, String> mapUshBpp = new HashMap<>();
		ush.forEach(u -> {
			String bppCode = bpBppProcessService.generateBppCode(BPP_TOGGLE_PUBLISHED_USH);

			mapUshBpp.put(u.getCode(), bppCode);
			publisher.publish(BppProcessDTO.builder()
					.bpptype(BPP_TOGGLE_PUBLISHED_USH.getCode())
					.code(bppCode)
					.name(bppCode)
					.myparent(null)
					.build());
			u.setPublished(published);
			ushCodes.add(u.getCode());
		});

		valueSaving.doClean();
		valueSaving.getLocal().set(
				ValueDto.builder().mapUshBpp(mapUshBpp).build()
		);
		List<String> ushChildCodes = setPublishedUSS(ushCodes, published);
		cuiService.setPublishedCUIs(ushChildCodes, published);
	}

	@Override
	@Transactional
	public List<String> setPublishedUSS(List<String> ushCodes, boolean published) {
		List<BpUniqueLearningComponent> ushChilds = bpUniqueLearningComponentRepository.findByMyParentInAndPublishedTrue(ushCodes);
		List<String> ushChildCodes = new ArrayList<>();
		Map<String, String> mapUshBpp = valueSaving.getLocal().get().getMapUshBpp();
		Map<String, String> mapUshChildBpp = new HashMap<>();

		ushChilds.forEach(uc -> {
			String bppCode = bpBppProcessService.generateBppCode(SET_PUBLISHED_USS_117);
			mapUshChildBpp.put(uc.getCode(), bppCode);

			publisher.publish(BppProcessDTO.builder()
					.bpptype(SET_PUBLISHED_USS_117.getCode())
					.code(bppCode)
					.name(bppCode)
					.myparent(mapUshBpp.get(uc.getMyParent()))
					.build());

			String bpsCode = bpBpsStepService.generateBpsCode(SET_PUBLISHED_USS_1171);
			publisher.publish(BpsStepDTO.builder()
					.bpstype(SET_PUBLISHED_USS_1171.getCode())
					.code(bpsCode)
					.name(bpsCode)
					.myprocess(bppCode)
					.build());

			if (published) {
				uc.setPublishbps(bpsCode);
			} else {
				uc.setUnpublishbps(bpsCode);
			}

			uc.setPublished(published);
			ushChildCodes.add(uc.getCode());
		});

		valueSaving.doClean();
		valueSaving.getLocal().set(ValueDto.builder().mapUshBpp(mapUshChildBpp).build());
		return ushChildCodes;
	}

	@SneakyThrows
	@Override
	// TODO ScheduleCAWK: add BP -> X later!!!
	public void SUIScheduleCAWK(Timestamp date) throws ParseException {
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPP_SCHEDULE_CAWK, null);
		CalendarPeriod cawk = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.WEEK.getCode());

		if (Objects.isNull(cawk)) {
			throw new NotFoundException("Couldn't find CAWK  with date: " + date);
		}

		String taskName = "SUIScheduleCAWK" + cawk.getCode();
		if (BpTaskStatusEnum.PROCESSING.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
			throw new BadRequestException("A task for " + taskName + " is processing!!!");
		}

		bpTaskInfoService.create(taskName, BpTaskStatusEnum.PROCESSING, null);

		try {
			// CREATE UWK
			AccYear ay = accYearService.findByTime(cawk.getStartTime());

			List<UsidDistinctInfoProjection> projections = usiDutyService.findDistinctInfo(ay.getCode());

			for (UsidDistinctInfoProjection p : projections) {
				for (BpLCP lcp : bpLCPService.findLCWK(p.getMypt(), LCLEnum.LCWK.getName())) {

					List<BpClagClassgroup> clags = clagClassgroupService.findBy(p.getMypt(),
							p.getMygg(), p.getMydfdl(),
							ClagType.PERMANANT.getCode());

					List<BpPodProductOfDeal> clagPods = bpPodProductOfDealService.findPodCodeByClag(clags.stream()
									.map(BpClagClassgroup::getCode).collect(Collectors.toList()),
							Collections.singletonList(USTEnum.ST.getName()), cawk.getStartTime());

					scheduleULC(null, lcp, cawk, p.getMygg(), p.getMydfdl(),
							clags, p.getMypt(), false, clagPods, null, null, null);
				}
			}

			// CREATE UDY
			List<CalendarPeriod> cadys = calendarPeriodService.findByParentAndCapType(cawk.getCode(), CalendarPeriodTypeEnum.DAY.getCode());
			for (CalendarPeriod c : cadys) {
				// Backup
				createUBW(c);
			}

			// CREATE USH
			for (UsidDistinctInfoProjection p : projections) {
				for (CalendarPeriod cady : cadys) {
					scheduleShifts(p.getMypt(), p.getMygg(), p.getMydfdl(), cady.getEndTime(), null);
				}
			}

			// MERGE UDL
			Set<Pair<String, String>> ggDfdlSet = projections.stream().map(p -> Pair.with(p.getMygg(), p.getMydfdl()))
					.collect(Collectors.toSet());
			for (Pair<String, String> p : ggDfdlSet) {
				for (CalendarPeriod cady : cadys) {
					mergeULC(p.getValue0(), p.getValue1(), cady, true, false);
				}
			}

			// MERGE UGE
			for (Pair<String, String> p : ggDfdlSet) {
				for (CalendarPeriod cady : cadys) {
					mergeULC(p.getValue0(), p.getValue1(), cady, false, true);
				}
			}
		} catch (Exception e) {
			log.error("Got error: {} when {}", e.getLocalizedMessage(), taskName);
			bpTaskInfoService.create(taskName, BpTaskStatusEnum.ERROR, e.getLocalizedMessage());
			throw e;
		}

		bpTaskInfoService.create(taskName, BpTaskStatusEnum.DONE, null);
	}

	public BpULCResponse createUBW(CalendarPeriod c) throws Exception {
		BpULCResponse ulc = bpUlcService.setULC(
				null,
				generateUlcCode(
						Lcp.PKAL_1PK_FD1_BW_1DP.getCode(),
						c.getCode(),
						null,
						null,
						null,
						null,
						null,
						null
				)
				, null, null,
				Lcp.PKAL_1PK_FD1_BW_1DP.getMylct(), null, null, c.getCode(), null, null, Lcp.PKAL_1PK_FD1_BW_1DP.getCode(), null, null, true);

		bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK1);

		for (BpClagClassgroup clag : bpClagClassgroupService.findBy(null, null, null, c,
				ClagType.BACKUP.getCode())) {
			bpUlcService.createOrUpdateClagUlc(clag.getCode(), ulc.getCode());
			bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK2);
		}

		return ulc;
	}


	@Override
	public void scheduleShift(String pt, String gg, String dfdl, Timestamp time, String lcp) {
		String taskName = String.format(
				"scheduleShifts-%s-%s-%s-%s-%s",
				pt, gg, dfdl, time.toString(), lcp
		);

		try {
			if (BpTaskStatusEnum.PROCESSING.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
				throw new BadRequestException("A task for " + taskName + " is processing!!!");
			}

			bpTaskInfoService.create(taskName, BpTaskStatusEnum.PROCESSING, null);

			scheduleShifts(pt, gg, dfdl, time, lcp);

			bpTaskInfoService.create(taskName, BpTaskStatusEnum.DONE, null);
		} catch (Exception e) {
			log.error("Got error: {} when {}", e.getLocalizedMessage(), taskName, e);

			bpTaskInfoService.create(taskName, BpTaskStatusEnum.ERROR, e.getLocalizedMessage());
		}
	}

	@Override
	public List<BpUniqueLearningComponent> findAllUlcFromParentUlc(String ulc) {
		return bpUniqueLearningComponentRepository.findAllByMyParentAndPublishedTrue(ulc);
	}


	public void convertBpToX(String ulcCode) {
		/* Try converting this ulc (as a shift) and its derivations to X. */
		bpUlcToXAtomicService.convert(ulcCode);
	}

	public void convertBpToXMonth(String ulcCode) {
		/* Try converting this ulc (as a month) and its derivations to X. */
		bpUlcToXAtomicService.convertMonth(ulcCode);
	}

	@Override
	public Boolean convertBpToX(ScheduleRequest scheduleRequest) {
		scheduleRequest.setDfdlsIsNull(CollectionUtils.isEmpty(scheduleRequest.getDfdls()));
		scheduleRequest.setGgsIsNull(CollectionUtils.isEmpty(scheduleRequest.getGgs()));
		List<String> ulcList = bpUniqueLearningComponentRepository.findUlcByCapPtGgDfdlShift(scheduleRequest).stream().map(BpUniqueLearningComponent::getCode).collect(Collectors.toList());
		ulcList.forEach(this::convertBpToX);
		List<BpUniqueLearningComponent> ulcNewList = bpUniqueLearningComponentRepository.findUlcByCapPtGgDfdlShift(scheduleRequest);
		for (BpUniqueLearningComponent ulc : ulcNewList) {
			if (ulc.getXdsc() == null) return false;
		}
		return true;
	}

	@Override
	public Boolean convertBpToXMonth(ScheduleRequest scheduleRequest) {
		scheduleRequest.setDfdlsIsNull(CollectionUtils.isEmpty(scheduleRequest.getDfdls()));
		scheduleRequest.setGgsIsNull(CollectionUtils.isEmpty(scheduleRequest.getGgs()));
		List<String> ulcList = bpUniqueLearningComponentRepository.findUlcByCapPtGgDfdlMonth(scheduleRequest).stream().map(BpUniqueLearningComponent::getCode).collect(Collectors.toList());
		ulcList.forEach(this::convertBpToXMonth);
		List<BpUniqueLearningComponent> ulcNewList = bpUniqueLearningComponentRepository.findUlcByCapPtGgDfdlMonth(scheduleRequest);
		for (BpUniqueLearningComponent ulc : ulcNewList) {
			if (ulc.getXdsc() == null) return false;
		}
		return true;
	}

	@Override
	public Boolean convertBpToXWeek(ScheduleRequest request) {
		List<String> lcps = bpLCPService.findWcByPts(request.getPt())
				.stream().map(BpLCP::getCode)
				.collect(Collectors.toList());

		if (lcps.isEmpty()) {
			return true;
		}

		List<String> ulcCodes = bpUlcService.findAllWcUlces(
						lcps,
						request.getCapCode(),
						request.getGgs(),
						request.getDfdls()
				)
				.stream().map(BpUniqueLearningComponent::getCode)
				.collect(Collectors.toList());

		ulcCodes.forEach(code -> bpUlcToXAtomicService.convert(code, code));

		List<BpUniqueLearningComponent> ulces = bpUlcService.findAllWcUlces(
				lcps,
				request.getCapCode(),
				request.getGgs(),
				request.getDfdls()
		);

		return ulces.stream().map(BpUniqueLearningComponent::getXdsc).noneMatch(Objects::isNull);
	}

	@Override
	public List<BpUniqueLearningComponent> findAllUgesByCapLcpGgDfdl(String cass, String lcp, String gg, String dfdl) {
		return bpUniqueLearningComponentRepository.findAllUgesByCapLcpGgDfdl(cass, lcp, gg, dfdl);
	}

	public void scheduleShifts(
			String pt,
			String gg,
			String dfdl,
			Timestamp time,
			String specificLcpSh
	) {
		CalendarPeriod cady = calendarPeriodService
				.getCAPByTime(time, CalendarPeriodTypeEnum.DAY.getCode());

		List<BpClagClassgroup> clags = clagClassgroupService.findBy(pt, gg, dfdl,
				cady,
				ClagType.PERMANANT.getCode());

		if (CollectionUtils.isEmpty(clags)) {
			log.info("Couldn't find clags for pt: {} gg: {} dfdl: {} cady: {} clagType: {}", pt, gg, dfdl,
					cady.getCode(),
					ClagType.PERMANANT.getCode());
			return;
		}

		AccYear accYear = accYearService.findByTime(new Date(time.getTime()));
		CurriculumProgramPackage crpp = crppService.getCrppByAccYearAndTimeAndPt(accYear.getCode(), time, pt);
		CurriculumPeriod cudy = curriculumPeriodService.getCUDY2(time, crpp.getCode(), gg,
				getCupNo(pt, DateUtils.getClassDay(cady.getEndTime())));
		BpUsiDuty bpUsiDuty = usiDutyService.findCashStart(accYear.getCode(), crpp.getMyTerm(), pt, gg, dfdl);


		for (BpLCP lcp : bpLCPService.findLCPSHByPTFromBP(pt)) {
			if (
					StringUtils.isNotBlank(specificLcpSh) && !lcp.getCode().equals(specificLcpSh)
			) {
				continue;
			}

			for (CalendarPeriod cash : calendarPeriodService
					.findByParentAndCapType(cady.getCode(), CalendarPeriodTypeEnum.SHIFT.getCode())) {

				List<BpPodProductOfDeal> clagPods = bpPodProductOfDealService.findPodCodeByClag(clags.stream()
								.map(BpClagClassgroup::getCode).collect(Collectors.toList()),
						Collections.singletonList(USTEnum.ST.getName()),
						cash.getStartTime()
				);

				if (Objects.nonNull(bpUsiDuty)
						&& bpUsiDuty.getMycashsta().equals(cash.getCashStart())
						&& lcp.getMyprd().equals(cash.getMyPrd())) {
					log.info("scheduleShifts clagPods  lcp-{}, cash-{}, gg-{}, dfdl-{}, pt-{} size-{}",
							lcp, cash, gg, dfdl, pt, clagPods.size());

					if (ProductTypeEnum.BC.getName().equals(pt)) {
						List<BpPodProductOfDeal> gesPods = bpPodProductOfDealService.findByListUsi(lmsService
								.getGESStudentUsername(gg, dfdl, DateUtils.format(cady.getEndTime(), DateUtils.SQL_DATE_PATTERN)));
						List<String> students = gesPods.stream().map(BpPodProductOfDeal::getMyst).collect(Collectors.toList());
						log.info("scheduleShifts gesPods {}", gesPods.size());

						if (lcp.getMylct().equals(LCPLCTLCKEnum.GES_75MI.getCode())) {
							CalendarPeriod cass = calendarPeriodService.getCASS(cash.getCode(), Lcp.GES_75MI_FD1_GE_75MI.getLcperiodno(), lcp.getMystructure());

							clags = bpClagClassgroupService.createClagDynDfge(
									pt, gg, dfdl, cass, bpLCPService.findByCode(Lcp.GES_75MI_FD1_GE_75MI.getCode()), gesPods
							);

							clagPods = gesPods;
						} else {
							clagPods = clagPods.stream().filter(c -> !students.contains(c.getMyst()))
									.collect(Collectors.toList());
						}
					}

					if (clagPods.isEmpty()) {
						break;
					}

					slackService.notifySlack(
							":red_circle: Started processing a schedule request with parameters: cap = " + cash.getCode()
									+ ", pt = " + pt
									+ ", lcp = " + lcp.getCode()
									+ ", gg = " + gg
									+ ", dfdl = " + dfdl
					);

					scheduleULC(null, lcp, cash, gg, dfdl,
							clags, pt, true, clagPods, null, null, cudy);

					slackService.notifySlack(
							":red_circle: Finished processing a schedule request with parameters: cap = " + cash.getCode()
									+ ", pt = " + pt
									+ ", lcp = " + lcp.getCode()
									+ ", gg = " + gg
									+ ", dfdl = " + dfdl
					);
				}
			}
		}

		mergeULC(gg, dfdl, cady, true, false);
		mergeULC(gg, dfdl, cady, false, true);
		List<String> listTE = Arrays.asList("DTE", "QO", "GTE", "CTE", "LTE");
		// Assign TE
		bpClagClassgroupService.bppAssignTEtoCLAG2(cady.getCode(), listTE, Collections.singletonList(pt),
				Collections.singletonList(gg), Collections.singletonList(dfdl));
	}

	@Override
	public void scheduleMPForOM(String clagCode, String podCode) throws Exception {
		BpPodProductOfDeal pod = bpPodProductOfDealService.findByCode(podCode);

		if (pod.getMypt().equals(ProductTypeEnum.OM.getName())) {
			if (!pod.getUsi().isScheduleOM()) {
				log.info("Start scheduleMPForOM for usi : {}", pod.getUsi().getCode());
				BpLCP lcp = bpLCPService.findByCode("PKOM-1PK-FDX-MP40L-1MN");
				BpClagClassgroup clag = clagClassgroupService.findByCode(clagCode);
				String ulcCode = Stream.of(lcp.getCode(), lcp.getMylct(), clag.getMygg(), clag.getMydfdl())
						.filter(Objects::nonNull).collect(Collectors.joining("-"));

				BpULCResponse ulcResponse = createULCAndClagUlcAndCUIAndCuie(null, ulcCode, lcp, null,
						clag.getMygg(), "OM", clag.getMydfdl(),
						Collections.singletonList(clag), Collections.singletonList(pod), clag.getMydfge(),
						null, null);

				CurriculumProgramSheet crps = curriculumProgramSheetService.getCrpsForOM(clag.getMypt(), clag.getMygg(), DateUtils.startOfDay(pod.getFromDate()));

				List<BpLCP> lcpKids = bpLCPService.findLCPKids(lcp.getMylct(), LCLEnum.LCSS.getName());

				for (BpLCP lcpKid : lcpKids) {
					if (Objects.isNull(lcpKid.getNolcp())) {
						CurriculumPeriod cup = curriculumPeriodService.getCUP(crps.getCode(), lcpKid.getLcperiodno(), CurriculumPeriodEnum.CURR_SESSION.getCode());

						createUlcTree(ulcResponse.getCode(), lcpKid, null, clag.getMygg(), clag.getMydfdl(),
								Collections.singletonList(clag), clag.getMypt(), true,
								Collections.singletonList(pod), clag.getMydfge(), null, null, null, cup);
					} else {
						for (int i = 1; i <= lcpKid.getNolcp(); i++) {
							CurriculumPeriod cup = curriculumPeriodService.getCUP(crps.getCode(), lcpKid.getLcperiodno(), i, CurriculumPeriodEnum.CURR_SESSION.getCode());

							createUlcTree(ulcResponse.getCode(), lcpKid, null, clag.getMygg(), clag.getMydfdl(),
									Collections.singletonList(clag), clag.getMypt(), true,
									Collections.singletonList(pod), clag.getMydfge(), null, i, null, cup);
						}
					}
				}

				pod.getUsi().setScheduleOM(true);
				userItemService.save(pod.getUsi());
			} else {
				throw new BadRequestException("Already scheduleMPForOM for pod: " + podCode);
			}
		}

	}

	private void createMyJoinULC(List<ULCMergeInfoProjection> ulcMergeInfoProjections, boolean isudlm, boolean isugem) {
		ULCMergeInfoProjection mainUlc = ulcMergeInfoProjections.stream().filter(ULCMergeInfoProjection::getIsmain)
				.findFirst().orElse(null);
		if (Objects.nonNull(mainUlc)) {
			ulcMergeInfoProjections.forEach(p -> {
				log.info("ulc code: {} - lcp: {} - ismain: {}", p.getCode(), p.getMylcp(), p.getIsmain());
				if (!p.getIsmain()) {
					ulcMergeService.createOrUpdate(ULCMerge.builder()
							.code(p.getCode())
							.isudlm(isudlm)
							.isugem(isugem)
							.published(true)
							.mainulc(mainUlc.getCode())
							.build());
				}
			});
		}
	}

	private String getCupNo(String pt, Integer classDay) {
		if (pt.equals(ProductTypeEnum.OM.getName())) {
			return "1";
		}
		if (classDay == 2
				|| classDay == 3
				|| classDay == 4) {
			return "1";
		} else {
			return "2";
		}
	}

	@Override
	public void scheduleULC(String ulcParentCode, BpLCP lcp, CalendarPeriod cap, String gg, String dfdl,
							List<BpClagClassgroup> clags, String pt, boolean needCreateKids,
							List<BpPodProductOfDeal> clagPods,
							String dfge, Integer parentIndex, CurriculumPeriod parentCup) {
		log.info("scheduleULC parent-{} lcp-{} cap-{} gg-{} dfdl-{} pt-{} dfge-{} needKid-{} clags-{} clagPod-{}",
				ulcParentCode, lcp.getCode(), Objects.nonNull(cap) ? cap.getCode() : null, gg, dfdl, pt, dfge, needCreateKids,
				clags.size(), clagPods.size());
		if (Objects.nonNull(lcp.getNolcp())) {
			for (int i = 1; i <= lcp.getNolcp(); i++) {
				scheduleULC1(ulcParentCode, lcp, cap, gg, dfdl, clags, pt, needCreateKids,
						clagPods, dfge, parentIndex, i, parentCup, null);
			}
		} else {
			scheduleULC1(ulcParentCode, lcp, cap, gg, dfdl, clags, pt, needCreateKids,
					clagPods, dfge, parentIndex, null, parentCup, null);
		}
	}

	private void scheduleULC1(String ulcParentCode, BpLCP lcp, CalendarPeriod cap, String gg, String dfdl,
							  List<BpClagClassgroup> clags, String pt, boolean needCreateKids,
							  List<BpPodProductOfDeal> clagPods, String dfge,
							  Integer parentIndex, Integer index, CurriculumPeriod parentCup, String ulcCode) {
		log.info("scheduleULC1 ulcParentCode: {}, lcp: {}, cap: {}, gg: {}, dfdl: {}, clags: {}, pt: {}," +
						" needCreateKids: {}, clagPods: {}, dfge: {}, parentIndex: {}, index: {}, ulcCode: {}, cup: {}",
				ulcParentCode, lcp.getCode(), Objects.nonNull(cap) ? cap.getCode() : null, gg, dfdl,
				clags.size(), pt, needCreateKids, clagPods.size(),
				dfge, parentIndex, index, ulcCode, Objects.nonNull(parentCup) ? parentCup.getCode() : null);

		CurriculumPeriod cup = Objects.nonNull(parentCup) ? curriculumPeriodService.getCup(pt, null, parentCup.getCode(),
				dfdl, lcp.getLct().getCode(), dfge, lcp.getLcperiodno(), index,
				CurriculumPeriodEnum.findByLCL(lcp.getLct().getMyLcl())) : null;

		createUlcTree(ulcParentCode, lcp, cap, gg, dfdl, clags, pt, needCreateKids, clagPods, dfge,
				parentIndex, index, ulcCode, cup);
	}

	private void createUlcTree(String ulcParentCode, BpLCP lcp, CalendarPeriod cap, String gg,
							   String dfdl, List<BpClagClassgroup> clags, String pt, boolean needCreateKids,
							   List<BpPodProductOfDeal> clagPods, String dfge, Integer parentIndex,
							   Integer index, String ulcCode, CurriculumPeriod cup) {
		log.info("createUlcTree ulcParentCode: {}, lcp: {}, cap: {}, gg: {}, dfdl: {}, clags: {}, pt: {}," +
						" needCreateKids: {}, clagPods: {}, dfge: {}, parentIndex: {}, index: {}, ulcCode: {}, cup: {}",
				ulcParentCode, lcp.getCode(), Objects.nonNull(cap) ? cap.getCode() : null, gg, dfdl,
				clags.size(), pt, needCreateKids, clagPods.size(),
				dfge, parentIndex, index, ulcCode, Objects.nonNull(cup) ? cup.getCode() : null);
		BpULCResponse ulc;

		if (Objects.isNull(ulcCode)) {
			ulc = scheduleByCGBR(ulcParentCode, lcp, cap, pt, gg, dfdl, clags,
					clagPods, dfge, parentIndex, index, cup);

		} else {
			ulc = createULCAndClagUlcAndCUIAndCuie(ulcParentCode, ulcCode, lcp, cap,
					gg, pt, dfdl, clags, clagPods, dfge, cup, Objects.nonNull(index) ? index : parentIndex);
		}

		// SCHEDULE ULC KIDS
		if (needCreateKids && Objects.nonNull(ulc)) {
			String lcl = Objects.nonNull(lcp.getLct()) ? lcp.getLct().getMyLcl() : null;
			String newUlcParentCode = ulc.getCode();
			List<BpLCP> lcpKids = bpLCPService.findByMylctparentToSchedule(lcp.getMylct());

			Map<String, CalendarPeriod> capKidMap = Collections.emptyMap();
			if (LCLEnum.LCSH.getName().equals(lcl)) {
				capKidMap = calendarPeriodService.findCapKid(cap.getCode(), lcp.getMystructure())
						.stream().collect(Collectors.toMap(CalendarPeriod::getNumberAsChild, v -> v));
			}
			for (BpLCP lcpKid : lcpKids) {
				BpLearningComponentType lctKid = lcpKid.getLct();

				String lclKid = Objects.nonNull(lctKid) ? lctKid.getMyLcl() : null;
				CalendarPeriod capKid = LCLEnum.LCSS.getName().equals(lclKid)
						&& Objects.nonNull(lcpKid.getLcperiodno())
						&& lcpKid.getLcperiodno().startsWith("FD")
						? capKidMap.getOrDefault(lcpKid.getLcperiodno(), cap)
						: cap;
				if (lcpKid.getLct().getCode().contains("GE")
						&& LCLEnum.LCSS.getName().equals(lclKid)
						&& LIST_GE_PT.contains(pt)) {
					if (clags.stream().noneMatch(c -> c.getClagtype().equals(ClagType.DYNAMIC.getCode()))) {
						clags = bpClagClassgroupService.createClagDynDfge(pt, gg, dfdl, capKid, lcpKid, clagPods);
					}

					bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK3);

					clags.stream().collect(Collectors.groupingBy(BpClagClassgroup::getMydfge)).forEach(
							(k, v) -> {
								scheduleULC(newUlcParentCode, lcpKid, capKid, gg, dfdl, v, pt, true,
										clagPods, k, index, cup);
							}
					);
				} else {
					scheduleULC(newUlcParentCode, lcpKid, capKid, gg, dfdl, clags, pt, true,
							clagPods, dfge, index, cup);
				}
			}
		}
	}

	private BpULCResponse createULCAndClagUlcAndCUIAndCuie
			(String ulcParentCode, String ulcCode, BpLCP lcp, CalendarPeriod cap, String gg, String pt, String dfdl,
			 List<BpClagClassgroup> clags, List<BpPodProductOfDeal> clagPods,
			 String dfge, CurriculumPeriod cup, Integer index) {
		String capCode = Objects.nonNull(cap) ? cap.getCode() : null;
		BpULCResponse ulc = bpUlcService.setULC(ulcParentCode, ulcCode, null, null,
				lcp.getMylct(), gg, pt, capCode, dfdl, dfge, lcp.getCode(), null,
				index, true);
		bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK1);

		for (BpClagClassgroup clag : clags) {
			try {
				bpUlcService.createOrUpdateClagUlc(clag.getCode(), ulc.getCode());
			} catch (Exception e) {
				log.error("Got exception: {} when createOrUpdateClagUlc for clag: {} and ulc: {}", e.getLocalizedMessage(), clag.getCode(), ulc.getCode());
				e.printStackTrace();
			}
			bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK2);
		}

		// CREATE CUI MAIN
		String cti = Objects.nonNull(cup) ? cup.getMyCti() : null;
		cuiEventService.createCUIAndCuiEvent(ulc.getCode(), "AU", cti, lcp.getCode(),
				null, capCode);
		bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK4);
		bpeEventService.createBpeEvent(BpeEventTypeEnum.BPEScheduleCAWK1);

		// CREATE CUI ST
		for (BpPodProductOfDeal p : clagPods) {
			if (Objects.nonNull(p.getUsi())) {
				// TODO Find CTI by POD and ULC -> LCP -> LCT
				cuiService.createCui(p.getUsi().getCode(), null, ulc.getCode());
				bpsStepService.createBpsStep(BpsStepTypeEnum.BPSScheduleCAWK4);
				cuiEventService.createCUIJoinEvent(ulc.getCode(), p.getUsi().getCode(),
						lcp.getCode(), null, capCode);
				bpeEventService.createBpeEvent(BpeEventTypeEnum.BPEScheduleCAWK1);
			} else {
				log.warn("POD missing usi: " + p.getCode() + "!!!");
			}
		}

		return ulc;
	}

	private BpULCResponse scheduleByCGBR(
			String ulcParent, BpLCP lcp, CalendarPeriod cap, String pt, String gg,
			String dfdl, List<BpClagClassgroup> clags, List<BpPodProductOfDeal> clagPods,
			String dfge, Integer parentIndex, Integer index, CurriculumPeriod cup
	) {
		String ulcCode = generateUlcCode(lcp.getCode(), Objects.nonNull(cap) ? cap.getCode() : null,
				gg, dfdl, dfge, pt, parentIndex, index);

		if ("ACG".equals(lcp.getCgbr())) {
			return createULCAndClagUlcAndCUIAndCuie(ulcParent, ulcCode, lcp, cap, gg, pt, dfdl,
					clags, clagPods, dfge, cup, index);
		} else if ("ECG".equals(lcp.getCgbr())) {
			for (BpClagClassgroup clag : clags) {
				List<BpPodProductOfDeal> pods = clagPods;

				if (!"X".equals(dfge)) {
					pods = bpPodProductOfDealService.findPodCodeByClag(
							Collections.singletonList(clag.getCode()), Collections.singletonList(USTEnum.ST.getName()),
							cap.getStartTime()
					);

					pods.removeIf(pod -> clagPods.stream().noneMatch(cp -> cp.getMyst().equals(pod.getMyst())));
				}

				createUlcTree(ulcParent, lcp, cap, gg, dfdl, Collections.singletonList(clag), pt, true,
						pods, dfge, parentIndex, index,
						ulcCode + "-" + clag.getCode()
						, cup);
			}

			return null;
		} else if ("EPOD".equals(lcp.getCgbr())) {
			for (BpPodProductOfDeal pod : clagPods) {
				createUlcTree(ulcParent, lcp, cap, gg, dfdl, Collections.emptyList(), pt, true,
						Collections.singletonList(pod), dfge, parentIndex, index,
						ulcCode + "-" + pod.getMyst(), cup);
			}

			return null;
		} else {
			throw new ConflictException("Unable to create ULC for lcp: " + lcp.getCode() + " with cgbr: " + lcp.getCgbr());
		}

	}

	@Override
	public BpUniqueLearningComponent findUBWFromBP(String cady, String lcp, String lct) {
		return bpUniqueLearningComponentRepository.findFirstByMyCapCodeAndMyLcpCodeAndMyLctCode(cady, lcp, lct)
				.orElse(null);
	}

	@Override
	public List<BpUniqueLearningComponent> findUlcFromParentUlcAndLcp(String ush, String lcp) {
		return bpUniqueLearningComponentRepository.findUlcByParentUlcAndLcp(ush, lcp);
	}

	@Override
	public List<BpUniqueLearningComponent> findUlcFromParentUlcAndLct(String parentUlc, String lct) {
		return bpUniqueLearningComponentRepository.findAllByMyParentAndMyLctAndPublishedTrue(parentUlc, lct);
	}

	@Override
	public GeneralPageResponse<ULCResponse> getULCSHs(int page, int size, List<
			String> lctCodes, List<String> ggCodes, List<String> dfdlCodes, String from, String to) {
		int offset = page < 1 ? 0 : page - 1;
		Pageable pageable = PageRequest.of(offset, size);

		StringBuilder selectQueryBuilder = new StringBuilder(
				"SELECT ulc.id, ulc.code AS ulc_code, bcc.code AS cap_code, " +
						"bcc.startperiod, bcc.endperiod, ulc.mylct AS lct_code, " +
						"ulc.mydfdl AS dfdl_code, ulc.mygg AS gg_code, ulc.mylcp AS lcp_code , " +
						"ulc.published AS published ");
		StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) ");

		StringBuilder queryBuilder = new StringBuilder(
				"FROM bp_ulc_uniquelearningcomponent ulc " +
						"         INNER JOIN bp_cap_calendarperiod bcc ON ulc.mycap = bcc.code " +
						"         INNER JOIN bp_lct_learningcomponenttype bll ON ulc.mylct = bll.code " +
						"    AND bll.mylcl = 'SH' " +
						"WHERE true ");

		if (!CollectionUtils.isEmpty(lctCodes)) {
			queryBuilder.append(" AND ulc.mylct IN (").append(StrUtils.concatToQueryStringWith(lctCodes, ",")).append(") ");
		}

		if (!CollectionUtils.isEmpty(ggCodes)) {
			queryBuilder.append(" AND ulc.mygg IN (").append(StrUtils.concatToQueryStringWith(ggCodes, ",")).append(") ");
		}

		if (!CollectionUtils.isEmpty(dfdlCodes)) {
			queryBuilder.append(" AND ulc.mydfdl IN (").append(StrUtils.concatToQueryStringWith(dfdlCodes, ",")).append(") ");
		}

		if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
			queryBuilder.append(" AND bcc.startperiod BETWEEN ")
					.append("'").append(from).append("'").append(" AND ").append("'").append(to).append("'");
		}

		List<ULCInfoDTO> dtos = jdbcTemplate.query(selectQueryBuilder.append(queryBuilder).append(" LIMIT " + size + " OFFSET " + pageable.getOffset()).toString(), new BeanPropertyRowMapper<>(ULCInfoDTO.class));
		Long count = jdbcTemplate.queryForObject(countQueryBuilder.append(queryBuilder).toString(), Long.class);
		return GeneralPageResponse.toResponse(new PageImpl<>(dtos.stream().map(d -> ULCResponse.builder()
						.id(d.getId())
						.ulcUlccode(d.getUlcCode())
						.ulcMycap(d.getCapCode())
						.ulcMylct(d.getLctCode())
						.ulcLcpcode(d.getUlcCode())
						.dfdlCode(d.getDfdlCode())
						.startPeriod(d.getStartperiod())
						.endPeriod(d.getEndperiod())
						.ggCode(d.getGgCode())
						.ulcPublished(d.getPublished())
						.build())
				.collect(Collectors.toList()), pageable, count));
	}

	@Override
	@Transactional
	public void publishULCSH(List<Long> ids) {
		bpUniqueLearningComponentRepository.findAllById(ids.stream().map(Math::toIntExact).collect(Collectors.toList()))
				.forEach(u -> u.setPublished(true));
	}

	@Override
	public ULCDetailInfoResponse getUlcDetail(Integer id) {
		return bpUniqueLearningComponentRepository.findById(id)
				.map(this::convertToDetailResponse)
				.orElseThrow(
						() -> new NotFoundException("Couldn't find ULC with id: " + id)
				);
	}

	@Override
	public List<BpUniqueLearningComponent> findUlcByCapFromBp(String capCode, String lcpCode) {
		return bpUniqueLearningComponentRepository.findByMyCapCodeAndMyLcpCode(capCode, lcpCode);
	}

	@Override
	public void createCuiULC(String lcp, String cap, String ust) {
		if (LIST_UST_SCHEDULE_ULC.contains(ust)) {
			List<BpUniqueLearningComponent> ulcs = findUlcByCapFromBp(cap, lcp);
			ulcs.forEach(ulc -> {
				List<BpClagClassgroup> clags;
				if (USTEnum.ST.getName().equals(ust) || USTEnum.TE.getName().equals(ust)) {
					clags = clagClassgroupService.findClagByUlcAndType(ulc.getCode(), ClagType.PERMANANT.getCode());
				} else {
					clags = clagClassgroupService.findClagFromULC(ulc.getCode());
				}

				if (CollectionUtils.isEmpty(clags)) {
					log.warn("Found 0 clag for ulc with code: " + ulc.getCode());
					return;
				}

				List<String> clagCodes = clags.stream().map(BpClagClassgroup::getCode).collect(Collectors.toList());

				List<BpPodProductOfDeal> pods;
				if (LIST_EM_UST.contains(ust)) {
					pods = bpPodProductOfDealService.findPodCodeByClag(clagCodes, LIST_EM_UST);
				} else {
					pods = bpPodProductOfDealService.findPodCodeByClag(clagCodes,
							Objects.equals(ust, UsiTypeEnum.TE.getName()) ? UsiTypeEnum.getListTeType() : Collections.singletonList(ust));
				}

				if (CollectionUtils.isEmpty(pods)) {
					log.warn("Found 0 pod for clagCodes: " + clagCodes + " and ust: " + (LIST_EM_UST.contains(ust) ? LIST_EM_UST : ust));
					return;
				}

				for (BpPodProductOfDeal p : pods) {
					if (Objects.nonNull(p.getUsi())) {
						cuiService.createCui(p.getUsi().getCode(), null, ulc.getCode());
						cuiEventService.createCUIJoinEvent(ulc.getCode(), p.getUsi().getCode(), lcp, null, cap);
					} else {
						log.warn("POD missing usi: " + p.getCode() + "!!!");
					}
				}
			});
		} else {
			throw new BadRequestException("Not support to createCUI for ust: " + ust);
		}
		List<BpLCP> lcpKids = bpLCPService.findLCPKids(bpLCPService.findByCode(lcp).getMylct());

		for (BpLCP lcpKid : lcpKids) {
			for (
					CalendarPeriod capKid : calendarPeriodService.findCapKid(cap, bpLCPService.findByCode(lcp).getMystructure())
			) {
				createCuiULC(lcpKid.getCode(), capKid.getCode(), ust);
			}
		}
	}

	@Override
	public void createCuiMainOfULC(String lcp, String cap) {
		List<BpUniqueLearningComponent> ulcs = findUlcByCapFromBp(cap, lcp);
		ulcs.forEach(ulc -> {
			cuiService.createCui(null, null, ulc.getCode());
			cuiEventService.createCUIJoinEvent(ulc.getCode(), null, lcp, null, cap);
		});
		List<BpLCP> lcpKids = bpLCPService.findLCPKids(bpLCPService.findByCode(lcp).getMylct());

		for (BpLCP lcpKid : lcpKids) {
			for (CalendarPeriod capKid : calendarPeriodService.findCapKid(cap, bpLCPService.findByCode(lcp).getMystructure())) {
				createCuiMainOfULC(lcpKid.getCode(), capKid.getCode());
			}
		}
	}


	@Override
	public ULCDetailInfoResponse convertToDetailResponse(BpUniqueLearningComponent u) {
		BpLCP lcp = u.getMyLcp();
		CalendarPeriod cap = u.getMyCap();
		BpLearningComponentType lct = u.getMyLct();
		return ULCDetailInfoResponse.builder()
				.id(u.getId())
				.ulcUlccode(u.getCode())
				.ulcLcpcode(Objects.nonNull(lcp) ? lcp.getCode() : null)
				.ulcMylct(Objects.nonNull(lct) ? lct.getCode() : null)
				.ulcMycap(Objects.nonNull(cap) ? cap.getCode() : null)
				.dfgeCode(u.getMyDfge())
				.dfdlCode(u.getMyDfdl())
				.ggCode(u.getMyGg())
				.clags(listUtils.mapAll(clagClassgroupService.findClagDetailFromULC(u.getCode()), BpClagClassgroupResponse.class))
				.children(bpUniqueLearningComponentRepository.findByMyParentInAndPublishedTrue(Collections.singletonList(u.getCode())).stream().map(this::convertToDetailResponse).collect(Collectors.toList()))
				.build();
	}

	@Override
	public List<BpUniqueLearningComponent> findUlcByCapLcpGgDfdlDfge(String cap, String lcp, String gg, String
			dfdl, String dfge) {
		return bpUniqueLearningComponentRepository.findUlcByCapLcpGgDfdlDfge(cap, lcp, gg, dfdl, dfge);
	}

	@Override
	public List<BpUniqueLearningComponent> findUlcByCap(String cap) {
		return bpUniqueLearningComponentRepository.findAllByMyCapCodeAndPublished(cap, true);
	}

	@Override
	public void mergeULC(String gg, String dfdl, CalendarPeriod cap, Boolean isudlm, Boolean isugem) {
		List<ULCMergeInfoProjection> ulcMergeInfoProjections = bpUniqueLearningComponentRepository.findUDLMInfo(gg, dfdl, cap.getStartTime(), cap.getEndTime(), isudlm, isugem);

		if (isugem) {
			Map<String, List<ULCMergeInfoProjection>> ugeListMap = ulcMergeInfoProjections.stream().collect(Collectors.groupingBy(ULCMergeInfoProjection::getMydfge));

			for (Map.Entry<String, List<ULCMergeInfoProjection>> e : ugeListMap.entrySet()) {
				createMyJoinULC(e.getValue(), isudlm, isugem);
			}
		} else {
			createMyJoinULC(ulcMergeInfoProjections, isudlm, isugem);
		}
	}

	@Override
	@Transactional
	public void changeCLAGTEBPPJoinRequestGETE(String xst, Long xpt, String xcash, String xsessionggroup, Timestamp
			cuieActualtimeFet, boolean published) {
		valueSaving.doClean();
		String bppCode = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPP_JOIN_REQUEST_GE_TE);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPP_JOIN_REQUEST_GE_TE.getCode())
				.build());

		PODResponse response = bpPodProductOfDealService.getPOD(xst, xpt);
		BpPODCLAGResponse response1 = bpClagClassgroupService.setPOD_CLAGDYN_TE(response.getCode(), xsessionggroup, xcash, published);
		BpULCResponse response2 = getCLAGDYN_UGE(response1.getMyclag());
		if (published) {
			cuiEventService.createCUIJoinEventGETE(response2.getCode(), response.getMyst(), response2.getMyLcp(), cuieActualtimeFet, response2.getMyCap());
		} else {
			cuiService.unpublishCUI(response2.getCode(), response.getMyst());
		}
	}

	@Override
	@Transactional
	public void changeCLAGTEBppChangeClag(String xst, Long xpt, String xcash, String xsessionggroup, Timestamp
			cuieActualtimeFet, boolean published) {
		valueSaving.doClean();
		String bppCode = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPP_CHANGE_CLAG);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPP_CHANGE_CLAG.getCode())
				.build());

		PODResponse response = bpPodProductOfDealService.getPOD(xst, xpt);
		BpPODCLAGResponse response1 = bpClagClassgroupService.setPOD_CLAGDYN_TE(response.getCode(), xsessionggroup, xcash, published);
		BpULCResponse response2 = getCLAGDYN_UGE(response1.getMyclag());
		if (published) {
			cuiEventService.createCUIChangeClag(response2.getCode(), response.getMyst(), response2.getMyLcp(), cuieActualtimeFet, response2.getMyCap());
		} else {
			cuiService.unpublishCUI(response2.getCode(), response.getMyst());
		}
	}

	@Override
	@Transactional
	public void changeCLAGTEBppChangeTE(String xst, Long xpt, String xcash, String xsessionggroup, Timestamp
			cuieActualtimeFet, boolean published) {
		valueSaving.doClean();
		String bppCode = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPP_CHANGE_TE);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPP_CHANGE_TE.getCode())
				.build());

		PODResponse response = bpPodProductOfDealService.getPOD(xst, xpt);
		BpPODCLAGResponse response1 = bpClagClassgroupService.setPOD_CLAGDYN_TE(response.getCode(), xsessionggroup, xcash, published);
		BpULCResponse response2 = getCLAGDYN_UGE(response1.getMyclag());
		if (published) {
			cuiEventService.createCUITE(response2.getCode(), response.getMyst(), response2.getMyLcp(), cuieActualtimeFet, response2.getMyCap());
		} else {
			cuiService.unpublishCUITE(response2.getCode(), response.getMyst());
		}
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.BPP_SCHEDULE_UGES)
	public void bppScheduleUGES(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq,
								BPScheduleUssRequest sussReq) {

		String bppCode = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPPSCHEDULE_UGES);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPPSCHEDULE_UGES.getCode())
				.build());

		contentsReq.setGcGg(ggService.getGGFromX(contentsReq.getXGg()).getCode());
		contentsReq.setGcDfdl(dfdlService.getDFDLFromX(contentsReq.getXDfdl()).getCode());

		String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.A_CREATE_UGES);
		valueSaving.setBpsCode(bpsCode, false);
		bpUlcService.createUGES(ulcReq, contentsReq);
		if (bpsCode.equals(valueSaving.getBpsCode())) {
			publisher.publish(BpsStepDTO.builder()
					.name(bpsCode)
					.code(bpsCode)
					.bpstype(BpsStepTypeEnum.A_CREATE_UGES.getCode())
					.build());
		}
		valueSaving.setBpsCode(null, false);

		bpUlcService.collectUGEParameters1(ulcReq, contentsReq, sussReq);

		bpUlcService.collectUGEParameters2(ulcReq, contentsReq, sussReq);

		bpDfgeDifficultgetRepository.findByCodeIsNot("X").forEach(d -> {
			contentsReq.setGcDfge(d.getCode());
			bpUlcService.suggestCLAGUGE(ulcReq, sussReq, d.getCode());
			bpUlcService.bppScheduleUSS(ulcReq, contentsReq, sussReq, "DYN");
		});
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.SUGGEST_CLAG_UGE)
	public void suggestCLAGUGE(BPUlcRequest ulcReq, BPScheduleUssRequest sussReq, String dfgeCode) {
//		List<BPClagRequest> listClag = new ArrayList<>();
//		sussReq.getSussClagUgeList().forEach(c -> {
//			BpClagClassgroupResponse clag = bpClagClassgroupService
//					.getCLAGDYNFromX(c.getXClag(), DateUtils.format(ulcReq.getTime(), DateUtils.UTC_TIME_ZONE, DateUtils.MEDIUM_PATTERN));
//			bpClagClassgroupService.createOrUpdateDynamicClag(
//					clag.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl(), clag.getMydfge(), clag.getMywso(),
//					clag.getMaxtotalstudents(), clag.getClagtype(), clag.getXsessiongroup(), clag.getXcash());
//
//			listClag.add(BPClagRequest.builder()
//					.clagTe(c.getClagTe()).clagDfge(c.getClagDfge())
//					.build());
//		});
//		sussReq.setSussClagList(listClag);

		sussReq.setSussClagList(sussReq.getSussClagUgeList().stream()
				.filter(c -> c.getClagDfge().equals(dfgeCode))
				.collect(Collectors.toList()));
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.CREATE_UGES)
	public void createUGES(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq) {
		ulcReq.setUlcLcpcode(LCPEnum.BC_1MN_C21_GES_75MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.GES_75MI.getCode());

		CalendarPeriod cady = calendarPeriodService.getCAPByTime(ulcReq.getTime(), CalendarPeriodTypeEnum.DAY.getCode());
		CalendarPeriod cash = calendarPeriodService.getCASH(cady.getCode(), contentsReq.getGcGg(), ulcReq.getUlcMylct(), "", "", "");
		ulcReq.setUlcMycap(cash.getCode());

		createUDLC(ulcReq, contentsReq);
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_UGE_PARAMETERS)
	public void collectUGEParameters(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest
			sussReq) {
		// Find parent UDLG
		ulcReq.setUlcLcpcode(LCPEnum.PM_1MN_C18_DLG_90MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.DLG_90MI.getCode());
		CalendarPeriod cady = calendarPeriodService.getCAPByTime(ulcReq.getTime(), CalendarPeriodTypeEnum.DAY.getCode());
		CalendarPeriod cash = calendarPeriodService.getCASH(cady.getCode(), contentsReq.getGcGg(), ulcReq.getUlcMylct(), "", "", "");
		ulcReq.setUlcMycap(cash.getCode());
		ulcReq.setUlcMyparentulc(getULCCode(ulcReq.getUlcLcpcode(), ulcReq.getUlcMycap(),
				contentsReq.getGcGg(), contentsReq.getGcDfdl(), contentsReq.getGcDfge()));

		// Set param UGE
		ulcReq.setUlcLcpcode(LCPEnum.DLG_90MI_F3_GE_45MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.GE_45MI.getCode());
		contentsReq.setGcSsno("3");
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_UGE_PARAMETERS_1)
	public void collectUGEParameters1(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest
			sussReq) {
		ulcReq.setUlcLcpcode(LCPEnum.GES_75MI_F1_GE_75MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.GE_45MI.getCode());
		contentsReq.setGcSsno("1");
		//		ulcReq.setUlcMyparentulc(ulcReq.getUlcUlccode());
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_UGE_PARAMETERS_2)
	public void collectUGEParameters2(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest
			sussReq) {
		CalendarPeriod cass = calendarPeriodService.getCAP(ulcReq.getUlcMycap(),
				CalendarPeriodTypeEnum.SESSION.getCode(), contentsReq.getGcSsno());
		sussReq.setSussCass(cass.getCode());
		sussReq.setSussCassStartPeriod(cass.getStartTime());
		sussReq.setSussLcp(ulcReq.getUlcLcpcode());
		ulcReq.setUlcMycap(cass.getCode());

		sussReq.setSussCti("22-23-MT-BC-G3-WK18-DY1-C2-GES-SS1-C.SSL"); // Hard code
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_UDLC_PARAMETERS)
	public void collectUDLCParameters(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq) {
		ulcReq.setUlcLcpcode(LCPEnum.BC_1MN_C17_DLC_75MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.DLC_75MI.getCode());
		CalendarPeriod cady = calendarPeriodService.getCAPByTime(ulcReq.getTime(), CalendarPeriodTypeEnum.DAY.getCode());
		CalendarPeriod cash = calendarPeriodService.getCASH(cady.getCode(), contentsReq.getGcGg(), ulcReq.getUlcMylct(), "", "", "");
		ulcReq.setUlcMycap(cash.getCode());
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_UDL_PARAMETERS)
	public void collectUDLParameters(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest
			sussReq) {
		ulcReq.setUlcLcpcode(LCPEnum.DLG_90MI_F1_DL_40MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.DL_40MI.getCode());
		contentsReq.setGcSsno("1");
		CalendarPeriod cass = calendarPeriodService.getCAP(ulcReq.getUlcMycap(),
				CalendarPeriodTypeEnum.SESSION.getCode(), contentsReq.getGcSsno());
		ulcReq.setUlcMycap(cass.getCode());
		sussReq.setSussCass(cass.getCode());
		sussReq.setSussCassStartPeriod(cass.getStartTime());
		sussReq.setSussLcp(ulcReq.getUlcLcpcode());

		sussReq.setSussCti("22-23-MT-BC-G3-WK18-DY1-C2-GES-SS1-C.SSL"); // Hard code
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_UDL_PARAMETERS_1)
	public void collectUDLParameters1(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest
			sussReq) {
		ulcReq.setUlcLcpcode(LCPEnum.DLC_75MI_F1_DL_40MI.getCode());
		ulcReq.setUlcMylct(LCPLCTLCKEnum.DL_40MI.getCode());
		contentsReq.setGcSsno("1");
		CalendarPeriod cass = calendarPeriodService.getCAP(ulcReq.getUlcMycap(),
				CalendarPeriodTypeEnum.SESSION.getCode(), contentsReq.getGcSsno());
		ulcReq.setUlcMycap(cass.getCode());
		sussReq.setSussCass(cass.getCode());
		sussReq.setSussCassStartPeriod(cass.getStartTime());
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_UDL_PARAMETERS_2)
	public void collectUDLParameters2(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest
			sussReq) {
		sussReq.setSussCti("22-23-MT-BC-G3-WK18-DY1-C2-GES-SS1-C.SSL"); // Hard code
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.BPP_SCHEDULE_USS)
	public void bppScheduleUSS(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq,
							   BPScheduleUssRequest sussReq, String clagType) {
		log.info("bppScheduleUSS {}", ulcReq.getUlcUlccode());
		String bppCode = bpBppProcessService.generateBppCode(BppProcessTypeEnum.BPPSCHEDULE_USS);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPPSCHEDULE_USS.getCode())
				.build());

		// 2. Schedule_MyChild-UDL
		/// 2.1 Create UDL
		String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.A_CREATE_USS);
		valueSaving.setBpsCode(bpsCode, false);
		String udlCode = getULCCode(ulcReq.getUlcLcpcode(), ulcReq.getUlcMycap(), contentsReq.getGcGg(),
				contentsReq.getGcDfdl(), contentsReq.getGcDfge());
		BpULCResponse uDL = setULC(ulcReq.getUlcMyparentulc(), udlCode, "SYSTEM CREATE", ulcReq.getUlcMyjoinulc(), ulcReq.getUlcMylct(),
				contentsReq.getGcGg(), null, ulcReq.getUlcMycap(), contentsReq.getGcDfdl(), contentsReq.getGcDfge(),
				ulcReq.getUlcLcpcode(), ulcReq.getXDsc(), null, ulcReq.getUlcPublished());
		ulcReq.setUlcUlccode(uDL.getCode());
		if (bpsCode.equals(valueSaving.getBpsCode())) {
			publisher.publish(BpsStepDTO.builder()
					.name(bpsCode)
					.code(bpsCode)
					.bpstype(BpsStepTypeEnum.A_CREATE_USS.getCode())
					.myprocess(bppCode)
					.build());
		}
		valueSaving.setBpsCode(null, false);

		/// 2.2 Create CLAG-UDL
		if (Objects.equals(clagType, "DYN")) {
//			sussReq.getSussClagList().forEach(c -> {
//				setCLAGDYN_UGE(c, uDL.getCode());
//			});
		} else {
			sussReq.getSussClagList().forEach(c -> {
				String bpsCode1 = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.SET_CLAG_ULC);
				valueSaving.setBpsCode(bpsCode, false);
				setCLAGPERM_UDL(c.getClagCode(), uDL.getCode());
				if (bpsCode.equals(valueSaving.getBpsCode())) {
					publisher.publish(BpsStepDTO.builder()
							.name(bpsCode1)
							.code(bpsCode1)
							.bpstype(BpsStepTypeEnum.SET_CLAG_ULC.getCode())
							.myprocess(bppCode)
							.build());
				}
				valueSaving.setBpsCode(null, false);
			});
		}
		/// 2.3 Schedule_My_CUI__USS
		cuiService.scheduleMyCUI(ulcReq, sussReq);
		valueSaving.doClean();
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_CLAG_LIST_DLC)
	public void collectCLAGListDLC(BPScheduleUssRequest sussReq) {
		List<BPClagRequest> listClag = new ArrayList<>();
		sussReq.getSussClagBcList().forEach(c -> {
			BpClagClassgroupResponse clag = bpClagClassgroupService.getCLAGPERMFromX(c.getXClag());
			listClag.add(BPClagRequest.builder()
					.clagTe(c.getClagTe())
					.clagCode(clag.getCode())
					.build());
		});
		sussReq.setSussClagList(listClag);
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.COLLECT_CLAG_LIST_DLG)
	public void collectCLAGListDLG(BPScheduleUssRequest sussReq) {
		List<BPClagRequest> listClag = new ArrayList<>();
		sussReq.getSussClagPmList().forEach(c -> {
			BpClagClassgroupResponse clag = bpClagClassgroupService.getCLAGPERMFromX(c.getXClag());
			listClag.add(BPClagRequest.builder()
					.clagCode(clag.getCode())
					.clagTe(c.getClagTe())
					.build());
		});
		sussReq.setSussClagList(listClag);
	}

	private String getULCCode(String lcp, String ca, String gg, String dfdl, String dfge) {
		String result;
		if (StringUtils.isNotBlank(ca)) {
			result = String.join("-", lcp, ca, gg, dfdl);
		} else {
			result = String.join("-", lcp, gg, dfdl);
		}

		return dfge == null ? result : String.join("-", result, dfge);
	}

	@Override
	public String generateUlcCode(
			String lcp, String ca, String gg, String dfdl, String dfge,
			String pt, Integer parentIndex, Integer index
	) {
		return Stream.of(pt, lcp, ca, gg, dfdl, dfge, parentIndex, index)
				.filter(Objects::nonNull)
				.map(String::valueOf)
				.collect(Collectors.joining("-"));
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getULC")
	public BpULCResponse getUlc(String clagCode, String capCode, String lcpCode) {
		BpUniqueLearningComponent ulc =
				bpUniqueLearningComponentRepository.findByClagAndLcpAndCap(clagCode, capCode, lcpCode).orElseThrow(
						() -> new NotFoundException("Coun't find ulc by clag_code : " + clagCode + " and calPeriod_code : " + capCode
								+ " and  lcp_code : " + lcpCode)
				);
		BpULCResponse ulcResponse = modelMapper.map(ulc, BpULCResponse.class);
		ulcResponse.setMyLct(Objects.isNull(ulc.getMyLct()) ? null : ulc.getMyLct().getCode());
		ulcResponse.setMyLcp(Objects.isNull(ulc.getMyLcp()) ? null : ulc.getMyLcp().getCode());
		return ulcResponse;
	}


	@Override
	@WriteUnitTestLog
	@UnitFunctionName("SetCLAG-ULC")
	@Transactional
	public BpCLAGULCResponse createOrUpdateClagUlc(String clagCode, String ulcCode) {
		BpCLAGULC clagulc = bpCLAGULCRepository.createOrUpdate(BpCLAGULC.builder()
				.code(clagCode + "-" + ulcCode)
				.myclag(clagCode)
				.myulc(ulcCode)
				.build());
		return modelMapper.map(clagulc, BpCLAGULCResponse.class);
	}

	@Override
	public List<BpCLAGULC> getByULC(String ulcCode) {
		return bpCLAGULCRepository.findByMyulc(ulcCode);
	}

	@WriteBPUnitTestLog(BPLogProcessEnum.Create_Or_Update_CLag_Ulc)
	public void CreateOrUpdateClagUlc(String clagCode, String ulcCode) {
		if (clagRepository.existsByCode(clagCode)) {
			bpCLAGULCRepository.createOrUpdate(BpCLAGULC.builder()
					.code(clagCode + "-" + ulcCode)
					.myclag(clagCode)
					.myulc(ulcCode)
					.build());
		}
	}

//	@Override
//	public MessageResponseDTO scheduleShiftLock(ScheduleRequest scheduleRequest) {
//		List<String> lstGG = scheduleRequest.getGgs();
//		List<String> lstDFDL = scheduleRequest.getDfdls();
//		if (scheduleRequest.getGgs() == null || scheduleRequest.getGgs().isEmpty()) {
//			lstGG = bpGGGradeGroupRepository.findAllByPublishedTrue().stream().map(BpGGGradeGroup::getCode).collect(Collectors.toList());
//		}
//		if (scheduleRequest.getDfdls() == null || scheduleRequest.getDfdls().isEmpty()) {
//			lstDFDL = bpDfdlDifficultygradeRepository.findAllByPublishedTrue().stream().map(BpDfdlDifficultygrade::getCode).collect(Collectors.toList());
//		}
//		CalendarPeriod cady = calendarPeriodService.findByCode(scheduleRequest.getCady());
//		boolean isDone = false; // check schedule done
//		boolean isFirstTime = true; // kiểm tra là lần đầu
//		boolean isError = false; // check lỗi
//		for (String gg : lstGG) {
//			if (gg != null) {
//				for (String dfdl : lstDFDL) {
//					if (dfdl != null) {
//						String taskName = String.format(
//								"scheduleShifts-%s-%s-%s-%s-%s",
//								scheduleRequest.getPt(), gg, dfdl, cady.getStartTime(), null
//						);
//						isDone = false;
//						if (!StringUtils.isEmpty(bpTaskInfoService.getTaskInfo(taskName))) {
//							isFirstTime = false;
//						}
//						if (BpTaskStatusEnum.PROCESSING.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
//							throw new BadRequestException("A task for " + taskName + " is processing!!!");
//						}
//						if (BpTaskStatusEnum.DONE.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
//							isDone = true;
//						}
//						if (BpTaskStatusEnum.ERROR.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
//							isError = true;
//							slackService.notifySlack(
//									":red_circle: Error scheduleShift with parameters: cady = " + scheduleRequest.getCady()
//											+ ", pt = " + scheduleRequest.getPt()
//											+ ", gg = " + gg
//											+ ", dfdl = " + dfdl
//							);
//						}
//					}
//				}
//			}
//		}
//		if (isDone || isFirstTime || isError) {
//			for (String gg : lstGG) {
//				if (gg != null) {
//					for (String dfdl : lstDFDL) {
//						if (dfdl != null) {
//							CompletableFuture.runAsync(() -> {
//								scheduleShift(scheduleRequest.getPt(), gg, dfdl, cady.getStartTime(), null);
//							});
//						}
//					}
//				}
//			}
//			return MessageResponseDTO.builder()
//					.code(HttpStatus.ok().status())
//					.message("OK").build();
//		}
//		return null;
//	}

	@Override
	public MessageResponseDTO scheduleShiftLock(ScheduleRequest scheduleRequest) {
		List<String> lstGG = scheduleRequest.getGgs();
		List<String> lstDFDL = scheduleRequest.getDfdls();
		if (scheduleRequest.getGgs() == null || scheduleRequest.getGgs().isEmpty()) {
			lstGG = bpGGGradeGroupRepository.findAllByPublishedTrue().stream().map(BpGGGradeGroup::getCode).collect(Collectors.toList());
		}
		if (scheduleRequest.getDfdls() == null || scheduleRequest.getDfdls().isEmpty()) {
			lstDFDL = bpDfdlDifficultygradeRepository.findAllByPublishedTrue().stream().map(BpDfdlDifficultygrade::getCode).collect(Collectors.toList());
		}
		CalendarPeriod cady = calendarPeriodService.findByCode(scheduleRequest.getCady());
		for (String pt : scheduleRequest.getPt()) {
			for (String gg : lstGG) {
				if (gg != null) {
					for (String dfdl : lstDFDL) {
						if (dfdl != null) {
							scheduleShift(pt, gg, dfdl, cady.getStartTime(), null);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void removeCacheScheduleShift(ScheduleRequest scheduleRequest) {
		List<String> lstGG = scheduleRequest.getGgs();
		List<String> lstDFDL = scheduleRequest.getDfdls();
		CalendarPeriod cady = calendarPeriodService.findByCode(scheduleRequest.getCady());
		for (String pt : scheduleRequest.getPt()) {
			for (String gg : lstGG) {
				if (gg != null) {
					for (String dfdl : lstDFDL) {
						if (dfdl != null) {
							String taskName = String.format(
									"scheduleShifts-%s-%s-%s-%s-%s",
									pt, gg, dfdl, cady.getStartTime(), null
							);
							if (BpTaskStatusEnum.DONE.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
								bpTaskInfoService.delete(taskName);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void removeCacheScheduleMonth(ScheduleRequest scheduleRequest) {
		List<String> lstGG = scheduleRequest.getGgs();
		List<String> lstDFDL = scheduleRequest.getDfdls();
		CalendarPeriod camn = calendarPeriodService.findByCode(scheduleRequest.getCapCode());
		for (String pt : scheduleRequest.getPt()) {
			for (String gg : lstGG) {
				for (String dfdl : lstDFDL) {
					String taskName = String.format(
							"scheduleCamn-%s-%s-%s-%s",
							pt, gg, dfdl, camn.getCode()
					);
					if (BpTaskStatusEnum.DONE.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
						bpTaskInfoService.delete(taskName);
					}
				}
			}
		}
	}

	@Override
	public BpUniqueLearningComponent createUlc(String pt, String lck, String xclass, String currentUsi) {
		BpClagClassgroup claG = Optional.ofNullable(clagClassgroupService.findByXClass(xclass)).orElseThrow(
				() -> new NotFoundException(String.format("Not found claG with xclass %s", xclass))
		);

		BpLCP lcp = Optional.ofNullable(bpLCPService.findUlcSL(pt, lck)).orElseThrow(
				() -> new NotFoundException(String.format("Not found lcp with pt %s, lck %s ", pt, lck))
		);

		CalendarPeriod cap = calendarPeriodService.getCAPByTime(
				new Timestamp(System.currentTimeMillis()),
				CalendarPeriodTypeEnum.DAY.getCode()
		);
		log.info("Find cady with current time: {}", Objects.nonNull(cap) ? cap.getCode() : null);

		Integer currentSecond = Integer.parseInt(String.valueOf(System.currentTimeMillis() / 1000));
		String ulcCode = generateUlcCode(lcp.getCode(), Objects.nonNull(cap) ? cap.getCode() : null,
				claG.getMygg(), claG.getMydfdl(), claG.getMydfge(), claG.getMypt(), null, currentSecond);
		ulcCode = ulcCode.concat("-").concat(currentUsi);

		log.info(String.format("Create ulc with code %s", ulcCode));
		BpUniqueLearningComponent ulc = BpUniqueLearningComponent.builder()
				.code(ulcCode)
				.name(ulcCode)
				.myCap(cap)
				.myDfdl(claG.getMydfdl())
				.myDfge(claG.getMydfge())
				.myPt(claG.getMypt())
				.myLcp(lcp)
				.myGg(claG.getMygg())
//				.myParent("") // TODO: 07/06/2023 myParent lấy như nào?
				.published(true)
				.build();
		return bpUniqueLearningComponentRepository.save(ulc);
	}

	@Override
	public void scheduleShiftForPodClag(String pt, String gg, String dfdl,
										Timestamp time, String specificLcpSh,
										List<BpClagClassgroup> clags, Map<String, List<BpPodProductOfDeal>> pods) {
		CalendarPeriod cady = calendarPeriodService
				.getCAPByTime(time, CalendarPeriodTypeEnum.DAY.getCode());
		log.info("Start schedule shift ver 2 for pt :{} ,gg:{} , dfdl :{} , cady: {} , clag-{} ",
				pt, gg, dfdl, cady.getCode(), clags.size());

		AccYear accYear = accYearService.findByTime(new Date(time.getTime()));
		CurriculumProgramPackage crpp = crppService.getCrppByAccYearAndTimeAndPt(accYear.getCode(), time, pt);
		CurriculumPeriod cudy = curriculumPeriodService.getCUDY2(time, crpp.getCode(), gg,
				getCupNo(pt, DateUtils.getClassDay(cady.getEndTime())));
		BpUsiDuty bpUsiDuty = usiDutyService.findCashStart(accYear.getCode(), crpp.getMyTerm(), pt, gg, dfdl);


		for (BpLCP lcp : bpLCPService.findLCPSHByPTFromBP(pt)) {
			if (
					StringUtils.isNotBlank(specificLcpSh) && !lcp.getCode().equals(specificLcpSh)
			) {
				continue;
			}

			for (CalendarPeriod cash : calendarPeriodService
					.findByParentAndCapType(cady.getCode(), CalendarPeriodTypeEnum.SHIFT.getCode())) {

				List<BpPodProductOfDeal> clagPods = new ArrayList<>();
				for (BpClagClassgroup clag : clags) {
					clagPods.addAll(pods.get(clag.getCode()));
				}
				if (ProductTypeEnum.BC.getName().equals(pt)) {
					List<BpPodProductOfDeal> gesPods = bpPodProductOfDealService.findByListUsi(lmsService
							.getGESStudentUsername(gg, dfdl, DateUtils.format(cady.getEndTime(), DateUtils.SQL_DATE_PATTERN)));
					List<String> students = gesPods.stream().map(BpPodProductOfDeal::getMyst).collect(Collectors.toList());
					log.info("scheduleShifts gesPods {}", gesPods.size());

					if (lcp.getMylct().equals(LCPLCTLCKEnum.GES_75MI.getCode())) {
						clagPods = gesPods;
					} else {
						clagPods = clagPods.stream().filter(c -> !students.contains(c.getMyst()))
								.collect(Collectors.toList());
					}
				}


				if (Objects.nonNull(bpUsiDuty)
						&& bpUsiDuty.getMycashsta().equals(cash.getCashStart())
						&& lcp.getMyprd().equals(cash.getMyPrd())) {
					log.info("scheduleShifts clagPods  lcp-{}, cash-{}, gg-{}, dfdl-{}, pt-{} size-{}",
							lcp, cash, gg, dfdl, pt, clagPods.size());

					if (clagPods.isEmpty()) {
						break;
					}

					slackService.notifySlack(
							":red_circle: Started processing a schedule request with parameters: cap = " + cash.getCode()
									+ ", pt = " + pt
									+ ", lcp = " + lcp.getCode()
									+ ", gg = " + gg
									+ ", dfdl = " + dfdl
					);

					scheduleULC(null, lcp, cash, gg, dfdl,
							clags, pt, true, clagPods, null, null, cudy);

					slackService.notifySlack(
							":red_circle: Finished processing a schedule request with parameters: cap = " + cash.getCode()
									+ ", pt = " + pt
									+ ", lcp = " + lcp.getCode()
									+ ", gg = " + gg
									+ ", dfdl = " + dfdl
					);
				}
			}
		}
		mergeULC(gg, dfdl, cady, false, true);
		log.info("schedule shift ver 2 for pt :{} ,gg:{} , dfdl :{} , cady: {} , clag-{} success ",
				pt, gg, dfdl, cady.getCode(), clags.size());
	}

	public MessageResponseDTO scheduleWeekLock(ScheduleRequest scheduleRequest) {
		long start = System.currentTimeMillis();
		log.info("Will schedule batch week ");
		final List<String> lstGGFinal = scheduleRequest.getGGWithDefault(bpGGGradeGroupRepository);
		final List<String> lstDFDLFinal = scheduleRequest.getDffLWithDefault(bpDfdlDifficultygradeRepository);
		final List<String> ptsFinal = scheduleRequest.getPt();

		CalendarPeriod capWeek = calendarPeriodService.findByCode(scheduleRequest.getCapCode());
		List<ComboUlcFunction.Data> comboInput = ComboUlcFunction.flatToCombo(ptsFinal, lstGGFinal, lstDFDLFinal);

		comboInput.forEach(fo -> {
			log.info("Schedule for item with value: pt {}, gg {}, dfdl {}, capWeek {} ",
					fo.getPt(),
					fo.getGg(),
					fo.getDfdL(),
					capWeek.getCode());
			try {
				scheduleWcService.scheduleWCAll(fo.getPt(), fo.getGg(), fo.getDfdL(), capWeek.getCode());
			} catch (Exception e) {
				log.error("Schedule failed pt {}, gg {}, dfdl {}, capWeek {} with message {}",
						fo.getPt(),
						fo.getGg(),
						fo.getDfdL(),
						capWeek.getCode(),
						e.getMessage()
				);
			}
		});

		long end = System.currentTimeMillis();
		log.info("End of function schedule week batch [size = {}], in {} seconds", comboInput.size(), (end - start) / 1000);
		return null;
	}

	@Override
	public List<BpUniqueLearningComponent> findAllWcUlces(
			List<String> lcps, String cap, List<String> ggs, List<String> dfdls) {
		return bpUniqueLearningComponentRepository.findAllWcUlces(
				lcps,
				cap,
				ggs,
				dfdls
		);
	}

}
