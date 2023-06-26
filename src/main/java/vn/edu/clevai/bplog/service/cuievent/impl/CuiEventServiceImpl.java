package vn.edu.clevai.bplog.service.cuievent.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.annotation.BPLogParamName;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.BetTimeChecker;
import vn.edu.clevai.bplog.common.CodeGenerator;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.*;
import vn.edu.clevai.bplog.common.impl.Case1ABettimeChecker;
import vn.edu.clevai.bplog.common.impl.Case1BBettimeChecker;
import vn.edu.clevai.bplog.common.impl.Case2And3BettimeChecker;
import vn.edu.clevai.bplog.dto.redis.BpeEventDTO;
import vn.edu.clevai.bplog.dto.redis.BppProcessDTO;
import vn.edu.clevai.bplog.dto.redis.BpsStepDTO;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.entity.logDb.*;
import vn.edu.clevai.bplog.payload.request.*;
import vn.edu.clevai.bplog.payload.request.bp.BPCuiEventRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPScheduleUssRequest;
import vn.edu.clevai.bplog.payload.response.SessionOperatorAndCuiResponse;
import vn.edu.clevai.bplog.queue.RedisMessagePublisher;
import vn.edu.clevai.bplog.repository.BpUsiUserItemRepository;
import vn.edu.clevai.bplog.repository.bplog.*;
import vn.edu.clevai.bplog.repository.projection.SessionOperatorAndCuiProjection;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.impl.BpServiceImpl;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.payload.response.*;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static vn.edu.clevai.bplog.common.enumtype.BpeEventTypeEnum.SET_PUBLISH_CUI_EVENTS;
import static vn.edu.clevai.bplog.common.enumtype.BpeEventTypeEnum.WRITE_CUI_EVENT_JOIN_REQUEST;
import static vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum.BPP_JOIN_REQUEST_DL;
import static vn.edu.clevai.bplog.common.enumtype.BpsStepTypeEnum.GET_USI;

@Service
@Slf4j
public class CuiEventServiceImpl implements CuiEventService {
	private final ContentItemService contentItemService;

	private final BpChptCheckProcessTempRepository chptRepository;

	private final BpChpiCheckProcessItemService checkProcessItemService;

	private final BpChpiCheckProcessItemRepository chpiRepository;

	private final BpServiceImpl bpService;

	private final ModelMapper mapper;

	private final BpCuiEventRepository cuiEventRepository;

	private final CalendarPeriodService calendarPeriodService;

	private final BpLearningComponentEventTypeRepository lcetRepo;

	private final BpUsiUserItemService usiService;

	private final CuiEventService cuiEventService;

	private final BpULCService bpULCService;

	private final BpClagClassgroupService bpClagService;

	private final BpUniqueLearningComponentRepository ulcRepository;

	private final BpUsiUserItemRepository usiRepo;

	private final CuiService cuiService;

	private final BPPService bppService;

	private final BpPodProductOfDealService bpPodProductOfDealService;
	private final LocalValueSaving valueSaving;

	private final BpBppProcessService bppProcessService;
	private final BpBpsStepService bpsStepService;
	private final BpBpeEventService bpeEventService;
	private final RedisMessagePublisher publisher;

	private final String MAIN_USI = "AU";

	private static final Map<LCETCodeEnum, BpsStepTypeEnum> bpsStepTypeEnumFindCuiMap = new HashMap<>();
	private static final Map<LCETCodeEnum, BpsStepTypeEnum> bpsStepTypeEnumCreateCuiEMap = new HashMap<>();
	private static final Map<LCETCodeEnum, BpeEventTypeEnum> bpeStepTypeEnumFindCuiMap = new HashMap<>();
	private static final Map<LCETCodeEnum, BpeEventTypeEnum> bpeStepTypeEnumCreateCuiEMap = new HashMap<>();


	static {
		bpsStepTypeEnumFindCuiMap.put(LCETCodeEnum.SUBMIT_REPORT_1A, BpsStepTypeEnum.FIND_CUIEVENT_SUBMITREPORT1_TE);
		bpsStepTypeEnumFindCuiMap.put(LCETCodeEnum.SUBMIT_REPORT_1B, BpsStepTypeEnum.FIND_CUIEVENT_SUBMITREPORT1_TE);
		bpsStepTypeEnumFindCuiMap.put(LCETCodeEnum.SUBMIT_REPORT_3, BpsStepTypeEnum.FIND_CUIEVENT_SUBMITREPORT3_TE);
		bpsStepTypeEnumCreateCuiEMap.put(LCETCodeEnum.SUBMIT_REPORT_1A, BpsStepTypeEnum.CREATE_CUIEVENT_JOIN_SUCCESSFUL_ST);
		bpsStepTypeEnumCreateCuiEMap.put(LCETCodeEnum.SUBMIT_REPORT_1B, BpsStepTypeEnum.CREATE_CUIEVENT_JOIN_SUCCESSFUL_ST);
		bpsStepTypeEnumCreateCuiEMap.put(LCETCodeEnum.SUBMIT_REPORT_3, BpsStepTypeEnum.CREATE_CUIEVENT_JOINREQUEST_ST);
		bpeStepTypeEnumFindCuiMap.put(LCETCodeEnum.SUBMIT_REPORT_1A, BpeEventTypeEnum.SUBMIT_REPORT1);
		bpeStepTypeEnumFindCuiMap.put(LCETCodeEnum.SUBMIT_REPORT_1B, BpeEventTypeEnum.SUBMIT_REPORT1);
		bpeStepTypeEnumFindCuiMap.put(LCETCodeEnum.SUBMIT_REPORT_3, BpeEventTypeEnum.FIND_CUIEVENT_SUBMITREPORT3_TE);
		bpeStepTypeEnumCreateCuiEMap.put(LCETCodeEnum.SUBMIT_REPORT_1A, BpeEventTypeEnum.CREATE_CUIEVENT_JOIN_SUCCESSFUL_ST);
		bpeStepTypeEnumCreateCuiEMap.put(LCETCodeEnum.SUBMIT_REPORT_1B, BpeEventTypeEnum.CREATE_CUIEVENT_JOIN_SUCCESSFUL_ST);
		bpeStepTypeEnumCreateCuiEMap.put(LCETCodeEnum.SUBMIT_REPORT_3, BpeEventTypeEnum.CREATE_CUIEVENT_JOINREQUEST_ST);

	}

	public CuiEventServiceImpl(BpCuiEventRepository cuiEventRepository,
							   ContentItemService contentItemService,
							   BpChpiCheckProcessItemService checkProcessItemService,
							   @Lazy BpClagClassgroupService bpClagService,
							   BpChptCheckProcessTempRepository chptRepository,
							   BpChpiCheckProcessItemRepository chpiRepository,
							   BpServiceImpl bpService,
							   ModelMapper mapper,
							   CalendarPeriodService calendarPeriodService,
							   @Lazy BpULCService bpULCService,
							   @Lazy CuiService cuiService,
							   BpPodProductOfDealService bpPodProductOfDealService,
							   BpUsiUserItemRepository usiRepo,
							   BPPService bppService,
							   BpLearningComponentEventTypeRepository lcetRepo,
							   BpUsiUserItemService usiService,
							   BpUniqueLearningComponentRepository ulcRepository,
							   @Lazy CuiEventService cuiEventService,
							   LocalValueSaving localValueSaving,
							   RedisMessagePublisher publisher, BpBppProcessService bppProcessService,
							   BpBpsStepService bpsStepService,
							   BpBpeEventService bpeEventService) {
		this.cuiEventRepository = cuiEventRepository;
		this.contentItemService = contentItemService;
		this.checkProcessItemService = checkProcessItemService;
		this.bpClagService = bpClagService;
		this.chptRepository = chptRepository;
		this.chpiRepository = chpiRepository;
		this.bpService = bpService;
		this.mapper = mapper;
		this.calendarPeriodService = calendarPeriodService;
		this.bpULCService = bpULCService;
		this.cuiService = cuiService;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.usiRepo = usiRepo;
		this.bppService = bppService;
		this.lcetRepo = lcetRepo;
		this.usiService = usiService;
		this.ulcRepository = ulcRepository;
		this.cuiEventService = cuiEventService;
		this.valueSaving = localValueSaving;
		this.publisher = publisher;
		this.bppProcessService = bppProcessService;
		this.bpsStepService = bpsStepService;
		this.bpeEventService = bpeEventService;
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.WRITE_CUIEVENT_JOIN_REQUEST_ACTUAL_TIME)
	public BpCUIEventResponse writeCUIEvent510(BPCuiEventRequest request) {
		// findCUIEvent
		BpCuiEvent bpCuiEvent = cuiEventService.findCUIEventJoinRequest(request);

		// writeCUIEventJoinRequestActualTime
		bpCuiEvent.setEventActualTimeBet(new Timestamp(System.currentTimeMillis()));

		return BpCUIEventResponse.builder().id(bpCuiEvent.getId()).cuieCuiecode(bpCuiEvent.getCode())
				.cuieCuicode(bpCuiEvent.getMyCui().getCode()).cuieMyusi(bpCuiEvent.getMyUsi())
				.cuieMylcet(bpCuiEvent.getMyLcet().getCode()).cuiePlantime(bpCuiEvent.getEventPlanTime())
				.cuieActualTimeBet(bpCuiEvent.getEventActualTimeBet())
				.cuieActualTimeFet(bpCuiEvent.getEventActualTimeFet()).build();
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_CUIEVENT_JOIN_REQUEST)
	public BpCuiEvent findCUIEventJoinRequest(BPCuiEventRequest request) {
		Date now = new Date(System.currentTimeMillis());
		Date before45Min = new Date(System.currentTimeMillis() - (90 * 60 * 1000));

		return cuiEventRepository.findByMyUsiAndMyLcetCodeAndEventPlanTimeBetween
						(request.getCuieMyusi(), "DR-JN-JRQ", before45Min, now)
				.orElseThrow(() -> new NotFoundException("Could not find bpCUIEvent usi = " + request.getCuieMyusi()));
	}

	@Override
	public Page<SessionOperatorAndCuiResponse> listAllSessionOperatorAndCui(String username, LocalDate date,
																			Pageable pageable) {

		BpUsiUserItem userItem = usiService.findByUsername(username);

		Page<SessionOperatorAndCuiProjection> sessionOperatorAndCuiProjections = cuiEventRepository
				.findSessionOperatorAndCui(userItem.getCode(), date, pageable);

		List<SessionOperatorAndCuiResponse> responses = sessionOperatorAndCuiProjections.stream()
				.map(p -> SessionOperatorAndCuiResponse.builder().cuiCode(p.getCuiCode()).lcpCode(p.getLcpCode())
						.startTime(p.getStartTime()).ggCode(p.getGgCode()).dfdlCode(p.getDfdlCode())
						.dfgeCode(p.getDfgeCode()).usiCode(p.getUsiCode()).usiFullName(p.getUsiFullname())
						.usiPhone(p.getUsiPhone()).build())
				.collect(Collectors.toList());

		return new PageImpl<>(responses, pageable, sessionOperatorAndCuiProjections.getTotalElements());

	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.CREATE_CUI_EVENT)
	public BpCuiEvent createCuiEvent(BPCuiEventRequest request) {
		BpCuiEvent cuie = BpCuiEvent.builder()
				.myCui(cuiService.findByCode(request.getCuieCuicode()))
				.myLcet(lcetRepo.findFirstByCode(request.getCuieMylcet()).orElseThrow(
						() -> new NotFoundException("Couldn't find lcet by lcet_code: " + request.getCuieMylcet())))
				.myUsi(request.getCuieMyusi())
				.eventPlanTime(request.getCuiePlantime())
				.eventActualTimeBet(request.getCuieActualtimeBet())
				.eventActualTimeFet(request.getCuieActualtimeFet())
				.planbpe(valueSaving.getBpeCode())
				.actualbpe(valueSaving.getActualBpe())
				.publishbpe(valueSaving.getPublishBpe())
				.unpublishbpe(valueSaving.getUnPublishBpe())
				.published(request.getCuiePublished())
				.code(getCuiEventCode(request))
				.name(getCuiEventCode(request))
				.build();

		cuie = cuiEventRepository.createOrUpdate(cuie);
		valueSaving.setBpeCode(cuie.getPlanbpe(), false);
		return cuie;
	}

	private String getCuiEventCode(BPCuiEventRequest request) {
		return String.join("-", request.getCuieCuicode(), request.getCuieMylcet()) + "_" + System.currentTimeMillis();
	}

	@Override
	@Transactional
	@WriteBPUnitTestLog(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_1A)
	public void teacherSubmitReport1A(@BPLogParamName("teacher_username") String tUsername,
									  TeacherSubmitReportRequest request, Timestamp fetTime) throws Exception {
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPSUBMITREPORT1_TE_GE, null);
		tSubmitReport(tUsername, request, fetTime, LCETCodeEnum.SUBMIT_REPORT_1A, LCETCodeEnum.JOIN_SUCCESSFUL,
				new Case1ABettimeChecker());
	}

	@Override
	@Transactional
	@WriteBPUnitTestLog(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_1B)
	public void teacherSubmitReport1B(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime)
			throws Exception {
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPSUBMITREPORT1_TE_GE, null);
		tSubmitReport(tUsername, request, fetTime, LCETCodeEnum.SUBMIT_REPORT_1B, LCETCodeEnum.JOIN_SUBSTANTIAL,
				new Case1BBettimeChecker());
	}

	@Override
	@Transactional
	@WriteBPUnitTestLog(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_2)
	public void teacherSubmitReport2(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime)
			throws Exception {
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPSUBMITREPORT2_TE_GE, null);
		teacherSubmitReport2(tUsername, request, fetTime, LCETCodeEnum.SUBMIT_REPORT_2, LCETCodeEnum.RECEIVE_SPEAK_OUT,
				new Case2And3BettimeChecker());
	}

	private void teacherSubmitReport2(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime,
									  LCETCodeEnum submitLCETEnum, LCETCodeEnum submitToLCETEnum, BetTimeChecker timeChecker) throws Exception {
		BpUniqueLearningComponent bpUlc = getByXSessionGroupAndXLiveAt(request.getSessionGroupCode(),
				request.getLiveAt());
		BpUsiUserItem tSubmitUsi = usiRepo.findByUsername(tUsername)
				.orElseThrow(() -> new NotFoundException("Cant found Teacher Usi"));

		Map<BpUsiUserItem, BpCuiContentUserUlc> map = new HashMap<>();
		Map<BpUsiUserItem, Integer> studentReceiveSpeechOutMap = new HashMap<>();
		for (TeacherSubmitReportStudentRequest student : request.getStudents()) {
			BpUsiUserItem usi = usiService.findByUsername(student.getUsername());
			if (Objects.nonNull(usi)) {
				try {
					BpCuiContentUserUlc studentCui = cuiService.getOrCreateCUI(bpUlc.getCode(), usi.getCode(),
							null, true);
					map.put(usi, studentCui);
					studentReceiveSpeechOutMap.put(usi, student.getReceiveSpeechOut());
				} catch (Exception e) {
					log.error("tSubmitReport create cui error {} {} {}", bpUlc.getCode(), usi.getCode(), e.getMessage());
				}
			}
		}
		submitReportReport2(tSubmitUsi, bpUlc, map, fetTime, submitLCETEnum, submitToLCETEnum, timeChecker, studentReceiveSpeechOutMap);
	}

	private void submitReportReport2(BpUsiUserItem submitUsi, BpUniqueLearningComponent submitUlc,
									 Map<BpUsiUserItem, BpCuiContentUserUlc> mapUserCui, Timestamp fetTime, LCETCodeEnum submitLCETEnum,
									 LCETCodeEnum submitToLCETEnum, BetTimeChecker checker, Map<BpUsiUserItem, Integer> studentReceiveSpeechOutMap) {
		CalendarPeriod period = submitUlc.getMyCap();
		log.info("submitReport mycap {}", period.getCode());
		if (checker.doCheck(period, fetTime)) {
			bpsStepService.createBpsStep(BpsStepTypeEnum.FIND_CUI);
			BpCuiContentUserUlc sCui = cuiService.getOrCreateCUIWithBps(submitUlc.getCode(), submitUsi.getCode(), null, valueSaving.getBpsCode(), true);

			List<BpLearningComponentEventType> listLcet = lcetRepo
					.findAllByCodeIn(Arrays.asList(submitLCETEnum.getCode(), submitToLCETEnum.getCode()));
			BpLearningComponentEventType submitLCET = listLcet.stream()
					.filter(k -> k.getCode().equals(submitLCETEnum.getCode())).findAny().orElse(null);
			List<BpCuiEvent> results = new ArrayList<BpCuiEvent>();
			results.add(buildSubmitCuiEvent(submitUsi, submitLCET, sCui, submitUlc.getCode(), fetTime));
			BpLearningComponentEventType submitToLCET = listLcet.stream()
					.filter(k -> k.getCode().equals(submitToLCETEnum.getCode()))
					.findAny()
					.orElseThrow(() -> new NotFoundException("Not found LCET with code: " + submitToLCETEnum.getCode()));
			bpsStepService.createBpsStep(BpsStepTypeEnum.FOR_EACH_USI);
			for (Map.Entry<BpUsiUserItem, BpCuiContentUserUlc> e : mapUserCui.entrySet()) {
				Integer receiveSpeechOut = studentReceiveSpeechOutMap != null ?
						studentReceiveSpeechOutMap.get(e.getKey()) : null;
				if (Objects.nonNull(e.getValue())) {
					Timestamp now = DateUtils.now();
					results.add(BpCuiEvent.builder()
							.code(CodeGenerator.buildNormalCode(e.getValue().getCode(), e.getKey().getCode(),
									submitToLCET.getCode(), String.valueOf(System.currentTimeMillis()), RandomStringUtils.randomAlphabetic(4)))
							.myCui(e.getValue())
							.myUsi(e.getKey().getCode())
							.myLcet(submitToLCET)
							.triggerAt(now)
							.eventPlanTime(now)
							.eventActualTimeFet(fetTime)
							.eventActualTimeBet(now)
							.value1(receiveSpeechOut != null ? String.valueOf(receiveSpeechOut) : null)
							.published(true)
							.build());
				} else {
					log.warn("user {} has cui ulc null", e.getKey());
				}
			}
			bpeEventService.createBpeEvent(BpeEventTypeEnum.RECEIVE_SPEAKOUT_ST);
			results.forEach(bpCuiEvent -> bpCuiEvent.setActualbpe(valueSaving.getBpeCode()));
			cuiEventRepository.saveAll(results);

		} else {
			log.warn("Cant submit request because out of fettime");
		}
	}

	@Override
	@Transactional
	@WriteBPUnitTestLog(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_3)
	public void teacherSubmitReport3(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime)
			throws Exception {
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPSUBMITREPORT3_TE_GE, null);
		tSubmitReport(tUsername, request, fetTime, LCETCodeEnum.SUBMIT_REPORT_3, LCETCodeEnum.RECEIVE_COMMENT,
				new Case2And3BettimeChecker());
	}

	private void tSubmitReport(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime,
							   LCETCodeEnum submitLCETEnum, LCETCodeEnum submitToLCETEnum, BetTimeChecker timeChecker) throws Exception {
		BpUniqueLearningComponent bpUlc = getByXSessionGroupAndXLiveAt(request.getSessionGroupCode(),
				request.getLiveAt());
		BpUsiUserItem tSubmitUsi = usiRepo.findByUsername(tUsername)
				.orElseThrow(() -> new NotFoundException("Cant found Teacher Usi"));

		Map<BpUsiUserItem, BpCuiContentUserUlc> map = new HashMap<>();
		Map<BpUsiUserItem, Integer> studentReceiveSpeechOutMap = new HashMap<>();
		for (TeacherSubmitReportStudentRequest student : request.getStudents()) {
			BpUsiUserItem usi = usiService.findByUsername(student.getUsername());
			if (Objects.nonNull(usi)) {
				try {
					BpCuiContentUserUlc studentCui = cuiService.getOrCreateCUI(bpUlc.getCode(), usi.getCode(),
							null, true);
					map.put(usi, studentCui);
					studentReceiveSpeechOutMap.put(usi, student.getReceiveSpeechOut());
				} catch (Exception e) {
					log.error("tSubmitReport create cui error {} {} {}", bpUlc.getCode(), usi.getCode(), e.getMessage());
				}
			}
		}
		submitReportTE(tSubmitUsi, bpUlc, map, fetTime, submitLCETEnum, submitToLCETEnum, timeChecker, studentReceiveSpeechOutMap);
	}

	private BpUniqueLearningComponent getByXSessionGroupAndXLiveAt(String xSessionGroup, String xLiveAt)
			throws Exception {
		BpClagClassgroupResponse bpClag = bpClagService.getCLAGDYNFromX(xSessionGroup, xLiveAt);
		if (Objects.isNull(bpClag)) {
			throw new NotFoundException("Cant found BpClag with seesion: " + xSessionGroup + " and liveat:" + xLiveAt);
		}

		try {
			bpClagService.createOrUpdateDynamicClag(
					bpClag.getCode(),
					bpClag.getMypt(),
					bpClag.getMygg(),
					bpClag.getMydfdl(),
					bpClag.getMydfge(),
					bpClag.getMywso(),
					bpClag.getMaxtotalstudents(),
					bpClag.getClagtype(),
					bpClag.getXsessiongroup(),
					bpClag.getXcash()
			);
		} catch (Exception e) {
			// Spam api from front end
			log.error("createOrUpdateDynamicClag error {}", e.getMessage());
		}

		BpULCResponse bpUlcRes = bpULCService.getCLAGDYN_UGE(bpClag.getCode());
		if (Objects.isNull(bpUlcRes)) {
			throw new NotFoundException("Cant found BpULC with clag code: " + bpClag.getCode());
		}
		String bpUlcCode = bpUlcRes.getCode();
		return ulcRepository.findFirstByCode(bpUlcCode)
				.orElseThrow(() -> new NotFoundException("Cant found ulc by code " + bpUlcCode));
	}

	@Override
	@Transactional
	@WriteBPUnitTestLog(BPLogProcessEnum.TO_SUBMIT_REPORT_1A)
	public void toSubmitReport1A(String toUsername, List<ToTeacherSubmitRequest> listTeacher, Timestamp fetTime)
			throws Exception {
		valueSaving.doClean();
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPSUBMITREPORT1_TO, null);
		toSubmitReport(toUsername, listTeacher, fetTime, LCETCodeEnum.JOIN_SUCCESSFUL, LCETCodeEnum.SUBMIT_REPORT_1A,
				new Case1ABettimeChecker());
	}

	@Override
	@Transactional
	@WriteBPUnitTestLog(BPLogProcessEnum.TO_SUBMIT_REPORT_1B)
	public void toSubmitReport1B(String toUsername, List<ToTeacherSubmitRequest> listTeacher, Timestamp fetTime)
			throws Exception {
		valueSaving.doClean();
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPSUBMITREPORT1_TO, null);
		toSubmitReport(toUsername, listTeacher, fetTime, LCETCodeEnum.JOIN_SUBSTANTIAL, LCETCodeEnum.SUBMIT_REPORT_1B,
				new Case1BBettimeChecker());
	}

	private void toSubmitReport(String toUsername, List<ToTeacherSubmitRequest> listTeacher, Timestamp fetTime,
								LCETCodeEnum submitLCETEnum, LCETCodeEnum submitToLCETEnum, BetTimeChecker timeChecker) throws Exception {
		BpUsiUserItem toSubmitUsi = usiRepo.findByUsername(toUsername)
				.orElseThrow(() -> new NotFoundException("Cant found Teacher Operator Usi"));
		Map<BpUsiUserItem, BpCuiContentUserUlc> map = new HashMap<>();
		for (ToTeacherSubmitRequest teacher : listTeacher) {
			BpUsiUserItem tUsi = usiService.findByUsername(teacher.getTeacherUsername());
			if (Objects.nonNull(tUsi)) {
				BpUniqueLearningComponent teUlc = getByXSessionGroupAndXLiveAt(teacher.getSessionGroupCode(),
						teacher.getLiveAt());
				if (Objects.nonNull(teUlc)) {
					// create bps
					bpsStepService.createBpsStep(BpsStepTypeEnum.FIND_CUI_TO_SO);
					BpCuiContentUserUlc teCui = cuiService
							.getOrCreateCUI(teUlc.getCode(), tUsi.getCode(), null, true);
					if (Objects.nonNull(teCui)) {
						map.put(tUsi, teCui);
					} else {
						log.error("Cant found CUI with usi: {}, ulc: {}", tUsi.getCode(), teUlc.getCode());
					}
				}
				submitReport(toSubmitUsi, teUlc, map, fetTime, submitToLCETEnum, submitLCETEnum, timeChecker, null);
			}
		}
	}

	private void submitReport(BpUsiUserItem submitUsi, BpUniqueLearningComponent submitUlc,
							  Map<BpUsiUserItem, BpCuiContentUserUlc> mapUserCui, Timestamp fetTime, LCETCodeEnum submitLCETEnum,
							  LCETCodeEnum submitToLCETEnum, BetTimeChecker checker, Map<BpUsiUserItem, Integer> studentReceiveSpeechOutMap) {
		CalendarPeriod period = submitUlc.getMyCap();
		log.info("submitReport mycap {}", period.getCode());
		if (checker.doCheck(period, fetTime)) {
			// create bps
			bpsStepService.createBpsStep(BpsStepTypeEnum.FIND_CUI_TO_TE);
			BpCuiContentUserUlc sCui = cuiService.getOrCreateCUI(submitUlc.getCode(), submitUsi.getCode(), null, true);

			List<BpLearningComponentEventType> listLcet = lcetRepo
					.findAllByCodeIn(Arrays.asList(submitLCETEnum.getCode(), submitToLCETEnum.getCode()));
			BpLearningComponentEventType submitLCET = listLcet.stream()
					.filter(k -> k.getCode().equals(submitLCETEnum.getCode())).findAny().orElse(null);
			List<BpCuiEvent> results = new ArrayList<BpCuiEvent>();
			results.add(buildSubmitCuiEvent(submitUsi, submitLCET, sCui, submitUlc.getCode(), fetTime));
			BpLearningComponentEventType submitToLCET = listLcet.stream()
					.filter(k -> k.getCode().equals(submitToLCETEnum.getCode()))
					.findAny()
					.orElseThrow(() -> new NotFoundException("Not found LCET with code: " + submitToLCETEnum.getCode()));
			for (Map.Entry<BpUsiUserItem, BpCuiContentUserUlc> e : mapUserCui.entrySet()) {
				Integer receiveSpeechOut = studentReceiveSpeechOutMap != null ?
						studentReceiveSpeechOutMap.get(e.getKey()) : null;
				if (Objects.nonNull(e.getValue())) {
					// create bpe
					bpeEventService.createBpeEvent(BpeEventTypeEnum.CREATE_CUIEVENT_JOIN_SUCCESSFUL);
					Timestamp now = DateUtils.now();
					results.add(BpCuiEvent.builder()
							.code(CodeGenerator.buildNormalCode(e.getValue().getCode(), e.getKey().getCode(),
									submitToLCET.getCode(), String.valueOf(System.currentTimeMillis()), RandomStringUtils.randomAlphabetic(4)))
							.myCui(e.getValue())
							.myUsi(e.getKey().getCode())
							.myLcet(submitToLCET)
							.triggerAt(now)
							.eventPlanTime(now)
							.eventActualTimeFet(fetTime)
							.eventActualTimeBet(now)
							.value1(receiveSpeechOut != null ? String.valueOf(receiveSpeechOut) : null)
							.published(true)
							.actualbpe(valueSaving.getBpeCode())
							.build());
				} else {
					log.warn("user {} has cui ulc null", e.getKey());
				}
			}
			cuiEventRepository.saveAll(results);

		} else {
			log.warn("Cant submit request because out of fettime");
		}
	}

	private void submitReportTE(BpUsiUserItem submitUsi, BpUniqueLearningComponent submitUlc,
								Map<BpUsiUserItem, BpCuiContentUserUlc> mapUserCui, Timestamp fetTime, LCETCodeEnum submitLCETEnum,
								LCETCodeEnum submitToLCETEnum, BetTimeChecker checker, Map<BpUsiUserItem, Integer> studentReceiveSpeechOutMap) {
		CalendarPeriod period = submitUlc.getMyCap();
		log.info("submitReport mycap {}", period.getCode());
		if (checker.doCheck(period, fetTime)) {
			bpsStepService.createBpsStep(bpsStepTypeEnumFindCuiMap.get(submitLCETEnum));
			bpeEventService.createBpeEvent(bpeStepTypeEnumFindCuiMap.get(submitLCETEnum));
			BpCuiContentUserUlc sCui = cuiService.getOrCreateCUIWithBps(submitUlc.getCode(), submitUsi.getCode(), null, valueSaving.getBpsCode(), true);

			List<BpLearningComponentEventType> listLcet = lcetRepo
					.findAllByCodeIn(Arrays.asList(submitLCETEnum.getCode(), submitToLCETEnum.getCode()));
			BpLearningComponentEventType submitLCET = listLcet.stream()
					.filter(k -> k.getCode().equals(submitLCETEnum.getCode())).findAny().orElse(null);
			List<BpCuiEvent> results = new ArrayList<BpCuiEvent>();
			results.add(buildSubmitCuiEvent(submitUsi, submitLCET, sCui, submitUlc.getCode(), fetTime));
			BpLearningComponentEventType submitToLCET = listLcet.stream()
					.filter(k -> k.getCode().equals(submitToLCETEnum.getCode()))
					.findAny()
					.orElseThrow(() -> new NotFoundException("Not found LCET with code: " + submitToLCETEnum.getCode()));
			for (Map.Entry<BpUsiUserItem, BpCuiContentUserUlc> e : mapUserCui.entrySet()) {
				Integer receiveSpeechOut = studentReceiveSpeechOutMap != null ?
						studentReceiveSpeechOutMap.get(e.getKey()) : null;
				if (Objects.nonNull(e.getValue())) {
					Timestamp now = DateUtils.now();
					results.add(BpCuiEvent.builder()
							.code(CodeGenerator.buildNormalCode(e.getValue().getCode(), e.getKey().getCode(),
									submitToLCET.getCode(), String.valueOf(System.currentTimeMillis()), RandomStringUtils.randomAlphabetic(4)))
							.myCui(e.getValue())
							.myUsi(e.getKey().getCode())
							.myLcet(submitToLCET)
							.triggerAt(now)
							.eventPlanTime(now)
							.eventActualTimeFet(fetTime)
							.eventActualTimeBet(now)
							.value1(receiveSpeechOut != null ? String.valueOf(receiveSpeechOut) : null)
							.published(true)
							.build());
				} else {
					log.warn("user {} has cui ulc null", e.getKey());
				}
			}
			bpsStepService.createBpsStep(bpsStepTypeEnumCreateCuiEMap.get(submitLCETEnum));
			bpeEventService.createBpeEvent(bpeStepTypeEnumCreateCuiEMap.get(submitLCETEnum));
			results.forEach(bpCuiEvent -> bpCuiEvent.setActualbpe(valueSaving.getBpeCode()));
			cuiEventRepository.saveAll(results);

		} else {
			log.warn("Cant submit request because out of fettime");
		}
	}

	private BpCuiEvent buildSubmitCuiEvent(BpUsiUserItem submitUsi, BpLearningComponentEventType sLCET,
										   BpCuiContentUserUlc sCui, String sUlcCode, Timestamp fetTime) {
		Timestamp now = DateUtils.now();
		return BpCuiEvent.builder()
				.code(buildCuieCode(sUlcCode, submitUsi.getCode(), sLCET.getCode()))
				.myCui(sCui)
				.myUsi(submitUsi.getCode())
				.myLcet(sLCET)
				.eventPlanTime(now)
				.triggerAt(DateUtils.now())
				.eventActualTimeFet(fetTime)
				.eventActualTimeBet(now)
				.published(true)
				.build();
	}

	private String buildCuieCode(String sUlcCode, String usiCode, String lcetCode) {
		return CodeGenerator.buildNormalCode(sUlcCode, usiCode, lcetCode, String.valueOf(System.currentTimeMillis()),
				RandomStringUtils.randomAlphabetic(4));
	}

	@Override
	public BpCUIEventResponse findCUIEvent(BPCuiEventRequest request) {
		return null;
	}

	@Override
	public String finishClass(TeacherFinishClassRequest request) throws Exception {

		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPFINISH_GE, null);
		// 621
		BpUsiUserItem usi = cuiEventService.getUsiTe(request.getTeacherCode(), request.getFetTime());

		// 622
		BpClagClassgroupResponse clagdyn = bpClagService.getCLAGDYNFromX(request.getXSessionGroup(),
				request.getXCash());
		BpULCResponse ulc = bpULCService.getCLAGDYN_UGE(clagdyn.getCode());
		cuiEventService.findUlcTe(clagdyn.getCode(), ulc.getCode(), ulc.getMyCap(), ulc.getMyLcp(), ulc.getMyLct(),
				ulc.getMyJoinUlc(), String.valueOf(ulc.getPublished()));

		// 623
		bpsStepService.createBpsStep(BpsStepTypeEnum.CUI_OF_TE_FINISH_CLICK);

		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(ulc.getCode(), usi.getCode(), null, true);

		BpLearningComponentEventType lcet = lcetRepo.findFirstByCode(LCETCodeEnum.TIME_UP.getCode())
				.orElseThrow(() -> new NotFoundException("Not found LCET with code: " + LCETCodeEnum.TIME_UP.getCode()));
		bpeEventService.createBpeEvent(BpeEventTypeEnum.WRITE_CUIEVENT_TIMEUP_TE_ACTUAL_TIME);
		BpCuiEvent cuiEvent = BpCuiEvent.builder()
				.code(buildCuieCode(ulc.getCode(), usi.getCode(), lcet.getCode()))
				.name(buildCuieCode(ulc.getCode(), usi.getCode(), lcet.getCode()))
				.myUsi(usi.getCode())
				.myCui(cui)
				.myLcet(lcet)
				.eventPlanTime(request.getBetTime())
				.eventActualTimeFet(request.getFetTime())
				.eventActualTimeBet(request.getBetTime())
				.actualbpe(valueSaving.getBpeCode())
				.published(true)
				.build();
		cuiEventRepository.save(cuiEvent);
		return cuiEvent.getCode();
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.GET_USI_TE)
	public BpUsiUserItem getUsiTe(@BPLogParamName("sussTe") String usiCode,
								  @BPLogParamName("cuieActualtime") Timestamp fetTime) {
		return usiService.findByCode(usiCode);
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_ULC_TE)
	public BpULCResponse findUlcTe(@BPLogParamName("clagClagcode") String clagCode,
								   @BPLogParamName("ulcUlccode") String ulcCode, @BPLogParamName("ulcMycap") String ulcCap,
								   @BPLogParamName("ulcLcpcode") String ulcLcp, @BPLogParamName("ulcMylct") String ulcLct,
								   @BPLogParamName("ulcMyjoinulc") String ulcMyJoinUlc, @BPLogParamName("ulcPublished") String ulcPublished) {
		return BpULCResponse.builder().build();
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.WRITE_CUI_EVENT_TIMEUP_TE_ACTUAL_TIME)
	public void writeCuiEventTimeupTeActualTime(@BPLogParamName("cuiUlc__code") String cuiUlcCode,
												@BPLogParamName("cuiMy_lcp") String cuiMylcp, @BPLogParamName("cuiUsi_code") String cuiUsiCode,
												@BPLogParamName("cuiUst") String cuiUst, @BPLogParamName("cuiPublished") String cuiPubliched,
												@BPLogParamName("cuieMylcet") String cuieMylcet,
												@BPLogParamName("cuieActualtimefet") Timestamp cuieActualtimefet,
												@BPLogParamName("cuieCuicode") String cuieCuiCode, @BPLogParamName("cuieMyusi") String cuieMyusi,
												@BPLogParamName("cuieMylcp") String cuieMylcp, @BPLogParamName("cuiCui_code") String cuiCode,
												@BPLogParamName("cuieActualtimebet") Timestamp cuieActualtimebet,
												@BPLogParamName("cuieCuiecode") String cuiCuieCode) {

	}

	@Override
	public String assignVideo(AssignVideosRequest request) {
		request.getAssignVideos().forEach(assignVideo -> {
			// 83111
			BpClagClassgroupResponse clagdyn = bpClagService.getCLAGDYNFromX(assignVideo.getXSessionGroup(),
					assignVideo.getXCash());
			BpULCResponse uge = bpULCService.getUGEFromX
							(bpULCService.findXDSC(assignVideo.getXSessionGroup(), assignVideo.getXCash()))
					.stream()
					.filter(bpULC -> Objects.equals(bpULC.getMyDfge(), clagdyn.getMydfge()))
					.findFirst()
					.orElseThrow(() -> new NotFoundException("Not found UGE with" +
							" xSS: " + assignVideo.getXSessionGroup() +
							" xCash: " + assignVideo.getXCash() +
							" dfge: " + clagdyn.getMydfge()));
			BpUniqueLearningComponent ulc = ulcRepository.findFirstByCode(uge.getCode())
					.orElseThrow(() -> new NotFoundException("Not found ulc with code: " + uge.getCode()));

			// 83112
			bpeEventService.createBpeEvent(BpeEventTypeEnum.FINDCUI_MAIN_CUIEVENT_ASSIGNVIDEO);
			BpCuiContentUserUlc cui = cuiService.getOrCreateCUIWithBps(ulc.getCode(), MAIN_USI,
					null, valueSaving.getBpsCode(), true);
			BpCuiEvent cuiEvent = cuiEventRepository.findFirstByCuiAndLcet(cui.getCode(), LCETCodeEnum.AUDIT_VIDEO.getCode())
					.orElseThrow(() -> new NotFoundException("Not found cui event with cui: " + cui.getCode() +
							" and lcet:" + LCETCodeEnum.AUDIT_VIDEO.getCode()));

			// 83113
			BpChpiCheckProcessItem chpi = chpiRepository.findFirstByCuiEvent(cuiEvent.getCode())
					.orElseThrow(() -> new NotFoundException("Not found chpi with cui event " + cuiEvent.getCode()));

			// 83114
			BpContentItem cti = BpContentItem.builder()
					.code(String.join("-", uge.getCode(), assignVideo.getAssignUsi(),
							CTTEnum.VIDEO.getCode()))
					.myCtt(CTTEnum.VIDEO.getCode())
					.fileBeginUrl(assignVideo.getVideoUrl()).build();
			contentItemService.createOrUpdate(cti);

			chpi.setMyCti2(cti.getCode());
			chpiRepository.save(chpi);

			// 83115
			bpeEventService.createBpeEvent(BpeEventTypeEnum.WRITE_CUIEVENT_AUDITVIDEO_TO_ACTUAL_TIME);
			BpCuiContentUserUlc cui2 = cuiService.getOrCreateCUI(ulc.getCode(), assignVideo.getAssignUsi(),
					null, true);

			BpLearningComponentEventType lcet = lcetRepo.findFirstByCode(LCETCodeEnum.ASSIGN_VIDEO.getCode())
					.orElseThrow(() -> new NotFoundException("Not found lcet with code: " + LCETCodeEnum.ASSIGN_VIDEO.getCode()));
			BpCuiEvent cuiEvent2 = BpCuiEvent.builder()
					.code(buildCuieCode(ulc.getCode(), cui2.getMyUsi().getCode(), LCETCodeEnum.ASSIGN_VIDEO.getCode()))
					.name(buildCuieCode(ulc.getCode(), cui2.getMyUsi().getCode(), LCETCodeEnum.ASSIGN_VIDEO.getCode()))
					.myCui(cui2)
					.myUsi(assignVideo.getAssignUsi())
					.myLcet(lcet)
					.eventPlanTime(new Timestamp(System.currentTimeMillis()))
					.eventActualTimeFet(request.getFetTime())
					.eventActualTimeBet(new Timestamp(System.currentTimeMillis()))
					.actualbpe(valueSaving.getBpeCode())
					.published(true)
					.build();
			cuiEventRepository.save(cuiEvent2);
		});

		return "OK";

	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.GET_USI)
	public void getUsi(@BPLogParamName("cuieActualtime") Timestamp fetTime) {
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_CUI_CUI_EVENT)
	public BpCUIEventResponse findCuiCuiEvent(@BPLogParamName("cuiUlc__code") String cuiUlcCode,
											  @BPLogParamName("cuiMy_lcp") String cuiMylcp, @BPLogParamName("cuiUsi_code") String cuiUsiCode,
											  @BPLogParamName("cuiUst") String cuiUst, @BPLogParamName("cuieMylcet") String cuieMylcet,
											  @BPLogParamName("cuieCuicode") String cuieCuiCode, @BPLogParamName("cuiCui_code") String cuiCode,
											  @BPLogParamName("cuieCuiecode") String cuiCuieCode) {
		return BpCUIEventResponse.builder().build();
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_CHPI)
	public BpChpiCheckProcessItem findCHPI(@BPLogParamName("chpiChpi_code") String chpiCode,
										   @BPLogParamName("chpiMycuie") String chpiMycuie) {
		return BpChpiCheckProcessItem.builder().build();
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.CREATE_CTI)
	public BpContentItem createCti(String myContentType, String fileBeginUrl, String ctiCode, String cti2Code) {
		return BpContentItem.builder().build();
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.WRITE_CUI_EVENT_ASSIGN_VIDEO_TE_ACTUAL_TIME)
	public void writeCuiEventAssignVideoTeActualTime(@BPLogParamName("cuiUlc__code") String cuiUlcCode,
													 @BPLogParamName("cuiMy_lcp") String cuiMylcp, @BPLogParamName("cuiUsi_code") String cuiUsiCode,
													 @BPLogParamName("cuiUst") String cuiUst, @BPLogParamName("cuiPublished") String cuiPubliched,
													 @BPLogParamName("cuieMylcet") String cuieMylcet,
													 @BPLogParamName("cuieActualtimefet") Timestamp cuieActualtimefet,
													 @BPLogParamName("cuieCuicode") String cuieCuiCode, @BPLogParamName("cuieMyusi") String cuieMyusi,
													 @BPLogParamName("cuieMylcp") String cuieMylcp, @BPLogParamName("cuiCui_code") String cuiCode,
													 @BPLogParamName("cuieActualtimebet") Timestamp cuieActualtimebet,
													 @BPLogParamName("cuieCuiecode") String cuiCuieCode) {

	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.PLAN_CUI_EVENT)
	public List<BpCUIEventResponse> planCuiEvents(BPCuiEventRequest request, BPScheduleUssRequest sussReq) {
		List<BpCUIEventResponse> cuiEventList = new ArrayList<>();
		BpCuiContentUserUlc cui = cuiService.findByCode(request.getCuieCuicode());

		request.setCuieMyusi(cui.getMyUsi().getCode());
		sussReq.setSussCass(cui.getMyUlc().getMyCap().getCode());
		Map<String, Integer> listLcetTime = bpGetCuiEvents(request.getCuieCuicode());
		listLcetTime.keySet().forEach(lcet -> {
			request.setCuieMylcet(lcet);
			BpCuiEvent cuie = cuiEventService.createCuiEvent(request);
			cuiEventService.writeEventPlanTime(cuie.getCode(), listLcetTime.get(lcet), sussReq.getSussCass());
			cuiEventList.add(BpCUIEventResponse.builder().id(cuie.getId()).cuieCuiecode(cuie.getCode()).cuieMylcet(lcet)
					.cuiePlantime(cuie.getEventPlanTime()).build());

			try {
				checkProcessItemService.prepareChecking(cuie.getCode(), sussReq.getSussCti(),
						null, null, ToSendEmailEnum.FALSE.getCode());
			} catch (Exception e) {
				log.error("prepareChecking error {} {}", cuie.getCode(), e.getMessage());
			}

		});
		return cuiEventList;

	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getCUI")
	public BpCUIResponse getCui(String ulcCode, String usiCode) {
		BpCuiContentUserUlc cuiContentUserUlc = cuiService.getOrCreateCUI(ulcCode, usiCode, null, true);
		BpCUIResponse cuiResponse = mapper.map(cuiContentUserUlc, BpCUIResponse.class);
		cuiResponse.setMyUlc(ulcCode);
		cuiResponse
				.setMyUsi(Objects.isNull(cuiContentUserUlc.getMyUsi()) ? null : cuiContentUserUlc.getMyUsi().getCode());
		cuiResponse
				.setMyCti(Objects.isNull(cuiContentUserUlc.getMyCti()) ? null : cuiContentUserUlc.getMyCti().getCode());
		cuiResponse.setMyUsi(usiCode);
		return cuiResponse;
	}

	@Override
	public BpCuiEvent changeStatusCuiEvent(String cuiEventCode, Boolean publishedStatus) {
		BpCuiEvent cuiEvent = cuiEventRepository.findFirstByCode(cuiEventCode)
				.orElseThrow(() -> new NotFoundException("Coun't find cui event by cuiEventCode : " + cuiEventCode));
		cuiEvent.setPublished(publishedStatus);
		return cuiEvent;
	}

	@Override
	@Transactional
	public void setPublishedCUIEvents(List<String> ulcCodes, boolean published) {
		Map<String, String> mapCuiBPS = valueSaving.getLocal().get().getMapCuiBps();
		cuiEventRepository.findByMycuiIn(ulcCodes).forEach(c -> {
			String bpeCode = bpeEventService.generateBpeCode(SET_PUBLISH_CUI_EVENTS);
			c.setPublished(published);
			if (published) {
				c.setPublishbpe(bpeCode);
			} else {
				c.setUnpublishbpe(bpeCode);
			}

			publisher.publish(
					BpeEventDTO.builder()
							.name(bpeCode)
							.code(bpeCode)
							.bpetype(SET_PUBLISH_CUI_EVENTS.getCode())
							.mybps(mapCuiBPS.get(c.getMyCui().getCode()))
							.build()
			);
		});
	}

	@Override
	public void unpublishedCUIE(String cuiCode, Boolean isPublished) {
		List<BpCuiEvent> cuiEventList = cuiEventRepository.findByMyCuiCode(cuiCode);
		cuiEventList.forEach(cuie -> cuie.setPublished(isPublished));
		cuiEventRepository.saveAll(cuiEventList);
	}

	@Override
	public void bppJoinDL(String xst, Timestamp eventactualtimeFet) {
		String bppCode = bppProcessService.generateBppCode(BPP_JOIN_REQUEST_DL);
		publisher.publish(
				BppProcessDTO.builder()
						.bpptype(BPP_JOIN_REQUEST_DL.getCode())
						.code(bppCode)
						.name(bppCode)
						.build()
		);
		String bpsCode = bpsStepService.generateBpsCode(GET_USI);
		publisher.publish(
				BpsStepDTO.builder()
						.bpstype(GET_USI.getCode())
						.myprocess(bppCode)
						.code(bpsCode)
						.name(bpsCode)
						.build()
		);

		Timestamp now = DateUtils.now();
		BpCuiEvent last = cuiEventRepository.bppJoinDL(xst, now)
				.orElseThrow(
						() -> new NotFoundException("Could not find cuiEvent for xst: " +
								xst + " eventactualtimeFet: " + eventactualtimeFet)
				);


		if (last.getEventActualTimeBet() == null) {
			last.setEventActualTimeFet(eventactualtimeFet);
			last.setEventActualTimeBet(now);

			cuiEventRepository.save(last);
		} else {
			BPCuiEventRequest cuieJoinReq = BPCuiEventRequest
					.builder()
					.cuieCuicode(last.getMyCui().getCode())
					.cuieMyusi(last.getMyUsi())
					.cuieMylcet(last.getMyLcet().getCode())
					.cuiePlantime(last.getEventPlanTime())
					.cuiePublished(last.getPublished())
					.cuieActualtimeFet(eventactualtimeFet)
					.cuieActualtimeBet(DateUtils.now())
					.build();
			BPCuiEventRequest cuieJoinSuccessfulReq = BPCuiEventRequest
					.builder()
					.cuieCuicode(last.getMyCui().getCode())
					.cuieMyusi(last.getMyUsi())
					.cuieMylcet(LCETCodeEnum.JOIN_SUCCESSFUL.getCode())
					.cuiePlantime(DateUtils.now())
					.cuiePublished(last.getPublished())
					.cuieActualtimeFet(eventactualtimeFet)
					.cuieActualtimeBet(DateUtils.now())
					.build();
			cuiEventService.createCuiEvent(cuieJoinReq);
			String bpeCodeJoinRequest = bpeEventService.generateBpeCode(WRITE_CUI_EVENT_JOIN_REQUEST);
			publisher.publish(
					BpeEventDTO.builder()
							.mybps(bpsCode)
							.code(bpeCodeJoinRequest)
							.name(bpeCodeJoinRequest)
							.build()
			);

			cuiEventService.createCuiEvent(cuieJoinSuccessfulReq);
			String bpeCodeJoinSuccess = bpeEventService.generateBpeCode(WRITE_CUI_EVENT_JOIN_REQUEST);
			publisher.publish(
					BpeEventDTO.builder()
							.mybps(bpsCode)
							.code(bpeCodeJoinSuccess)
							.name(bpeCodeJoinSuccess)
							.build()
			);
		}
	}

	@Override
	@SneakyThrows
	public void bppJoinGEDLG(Long xdeal, String xcady, Timestamp cuieActualtimeFet, String cuieMylcp) {
		valueSaving.doClean();
		// create Bpp
		String bppCode = bppProcessService.generateBppCode(BppProcessTypeEnum.BPP_JOIN_REQUEST_GE_DLG);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPP_JOIN_REQUEST_GE_DLG.getCode())
				.build());

//		publisher.publish();

		BpPODCLAGResponse podClag = bppService.bppAssignCLAGDYN(xdeal, xcady);
		PODResponse pod = bpPodProductOfDealService.getPODFromX(xdeal);
		String stCode = pod.getMyst();

		BpULCResponse bpULCResponse = bpULCService.getCLAGDYN_UGE(podClag.getMyclag());


		bpULCService.setULC(
				bpULCResponse.getMyParent(),
				bpULCResponse.getCode(),
				bpULCResponse.getName(),
				bpULCResponse.getMyJoinUlc(),
				bpULCResponse.getMyLct(),
				bpULCResponse.getMyGg(),
				null,
				bpULCResponse.getMyCap(),
				bpULCResponse.getMyDfdl(),
				bpULCResponse.getMyDfge(),
				bpULCResponse.getMyLcp(),
				bpULCResponse.getXdsc(),
				null,
				bpULCResponse.getPublished()
		);

		bpULCService.setCLAGDYN_UGE_GEDLG(podClag.getMyclag(), bpULCResponse.getCode());

		createCUIJoinEventGEDLG(bpULCResponse.getCode(), stCode, cuieMylcp, cuieActualtimeFet, bpULCResponse.getMyCap());
	}

	@Override
	@Transactional
	public void createCUIJoinEventGEDLG(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap) {
		// create bps
		String bpsCode = bpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_GE_DLC);
		valueSaving.setBpsCode(bpsCode, true);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.CREATE_CUI_GE_DLC.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());

		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulcCode,
				stCode,
				null,
				true
		);

		// create Bpe
		String bpeCode = bpeEventService.generateBpeCode(BpeEventTypeEnum.WRITE_CUIEVENT_JOIN_REQUEST_GE_DLG);
		valueSaving.setActualBpe(bpeCode, true);
		publisher.publish(BpeEventDTO.builder()
				.name(bpeCode)
				.code(bpeCode)
				.bpetype(BpeEventTypeEnum.WRITE_CUIEVENT_JOIN_REQUEST_GE_DLG.getCode())
				.mybps(bpsCode));

		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		cuieReq.setCuieCuicode(cui.getCode());
		cuieReq.setCuieMyusi(stCode);
		cuieReq.setCuieMylcet("DR-JN-JRQ"); // Hard code
		cuieReq.setCuieMylcp(lcp); //
		cuieReq.setCuiePlantime(
				calendarPeriodService.findByCode(myCap).getStartTime()
		);
		cuieReq.setCuiePublished(cui.getPublished());
		cuieReq.setCuieActualtimeFet(cuieActualtimeFet);
		cuieReq.setCuieActualtimeBet(DateUtils.now());

		cuiEventService.createCuiEvent(cuieReq);

	}


	@Override
	@SneakyThrows
	public void bppJoinGEGES(Long xdeal, String xcady, Timestamp cuieActualtimeFet, String cuieMylcp) {
		// create Bpp
		valueSaving.doClean();
		String bppCode = bppProcessService.generateBppCode(BppProcessTypeEnum.BPP_JOIN_REQUEST_GE_GES);
		valueSaving.setBppCode(bppCode, true);
		publisher.publish(BppProcessDTO.builder()
				.name(bppCode)
				.code(bppCode)
				.bpptype(BppProcessTypeEnum.BPP_JOIN_REQUEST_GE_GES.getCode())
				.build());

		BpPODCLAGResponse podClag = bppService.bppAssignCLAGDYN(xdeal, xcady);
		PODResponse pod = bpPodProductOfDealService.getPODFromX(xdeal);
		String stCode = pod.getMyst();

		BpULCResponse bpULCResponse = bpULCService.getCLAGDYN_UGE(podClag.getMyclag());

		bpULCService.setULC(
				bpULCResponse.getMyParent(),
				bpULCResponse.getCode(),
				bpULCResponse.getName(),
				bpULCResponse.getMyJoinUlc(),
				bpULCResponse.getMyLct(),
				bpULCResponse.getMyGg(),
				null,
				bpULCResponse.getMyCap(),
				bpULCResponse.getMyDfdl(),
				bpULCResponse.getMyDfge(),
				bpULCResponse.getMyLcp(),
				bpULCResponse.getXdsc(),
				null,
				bpULCResponse.getPublished()
		);

		bpULCService.setCLAGDYN_UGE_GEGES(podClag.getMyclag(), bpULCResponse.getCode());
		createCUIJoinEventGEGES(bpULCResponse.getCode(), stCode, cuieMylcp, cuieActualtimeFet, bpULCResponse.getMyCap());
	}

	@Override
	@Transactional
	public void createCUIJoinEventGEGES(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap) {
		// create bps
		String bpsCode = bpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_GE_GES);
		valueSaving.setBpsCode(bpsCode, true);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.CREATE_CUI_GE_GES.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());

		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulcCode,
				stCode,
				null,
				true
		);
		// create bpe
		String bpeCode = bpeEventService.generateBpeCode(BpeEventTypeEnum.WRITE_CUIEVENT_JOIN_REQUEST_GE_GES);
		valueSaving.setActualBpe(bpeCode, true);
		publisher.publish(BpeEventDTO.builder()
				.name(bpeCode)
				.code(bpeCode)
				.bpetype(BpeEventTypeEnum.WRITE_CUIEVENT_JOIN_REQUEST_GE_GES.getCode())
				.mybps(bpsCode));

		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		cuieReq.setCuieCuicode(cui.getCode());
		cuieReq.setCuieMyusi(stCode);
		cuieReq.setCuieMylcet("DR-JN-JRQ"); // Hard code
		cuieReq.setCuieMylcp(lcp); //
		cuieReq.setCuiePlantime(
				calendarPeriodService.findByCode(myCap).getStartTime()
		);
		cuieReq.setCuiePublished(cui.getPublished());
		cuieReq.setCuieActualtimeFet(cuieActualtimeFet);
		cuieReq.setCuieActualtimeBet(DateUtils.now());

		cuiEventService.createCuiEvent(cuieReq);


	}

	@Override
	@Transactional
	public void createCUIJoinEventGETE(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap) {
		// create bps
		String bpsCode = bpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_GE_TE);
		valueSaving.setBpsCode(bpsCode, true);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.CREATE_CUI_GE_TE.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());

		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulcCode,
				stCode,
				null,
				true
		);
		// create bpe
		String bpeCode = bpeEventService.generateBpeCode(BpeEventTypeEnum.CREATE_CUIEVENT_JOIN_REQUEST_GE_TE);
		valueSaving.setActualBpe(bpeCode, true);
		publisher.publish(BpeEventDTO.builder()
				.name(bpeCode)
				.code(bpeCode)
				.bpetype(BpeEventTypeEnum.CREATE_CUIEVENT_JOIN_REQUEST_GE_TE.getCode())
				.mybps(bpsCode));

		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		cuieReq.setCuieCuicode(cui.getCode());
		cuieReq.setCuieMyusi(stCode);
		cuieReq.setCuieMylcet("DR-JN-JRQ"); // Hard code
		cuieReq.setCuieMylcp(lcp); //
		cuieReq.setCuiePlantime(
				calendarPeriodService.findByCode(myCap).getStartTime()
		);
		cuieReq.setCuiePublished(cui.getPublished());
		cuieReq.setCuieActualtimeFet(cuieActualtimeFet);
		cuieReq.setCuieActualtimeBet(DateUtils.now());

		cuiEventService.createCuiEvent(cuieReq);

	}

	@Override
	@Transactional
	public void createCUIChangeClag(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap) {

		// create bps
		String bpsCode = bpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_CHANGE_CLAG);
		valueSaving.setBpsCode(bpsCode, true);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.CREATE_CUI_CHANGE_CLAG.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());

		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulcCode,
				stCode,
				null,
				true
		);
		// create bpe
		String bpeCode = bpeEventService.generateBpeCode(BpeEventTypeEnum.CREATE_CUIEVENT_CHANGE_CLAG);
		valueSaving.setActualBpe(bpeCode, true);
		publisher.publish(BpeEventDTO.builder()
				.name(bpeCode)
				.code(bpeCode)
				.bpetype(BpeEventTypeEnum.CREATE_CUIEVENT_CHANGE_CLAG.getCode())
				.mybps(bpsCode));
		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		cuieReq.setCuieCuicode(cui.getCode());
		cuieReq.setCuieMyusi(stCode);
		cuieReq.setCuieMylcet("DR-JN-JRQ"); // Hard code
		cuieReq.setCuieMylcp(lcp); //
		cuieReq.setCuiePlantime(
				calendarPeriodService.findByCode(myCap).getStartTime()
		);
		cuieReq.setCuiePublished(cui.getPublished());
		cuieReq.setCuieActualtimeFet(cuieActualtimeFet);
		cuieReq.setCuieActualtimeBet(DateUtils.now());

		cuiEventService.createCuiEvent(cuieReq);

	}

	@Override
	@Transactional
	public void createCUITE(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap) {
		// create bps
		String bpsCode = bpsStepService.generateBpsCode(BpsStepTypeEnum.PUBLISH_CUIEVENT_TE);
		valueSaving.setBpsCode(bpsCode, true);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.PUBLISH_CUIEVENT_TE.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());
		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulcCode,
				stCode,
				null,
				true
		);

		// create Bpe
		String bpeCode = bpeEventService.generateBpeCode(BpeEventTypeEnum.PUBLISH_CUIEVENT_TE);
		valueSaving.setPublishBpe(bpeCode, true);
		publisher.publish(BpeEventDTO.builder()
				.name(bpeCode)
				.code(bpeCode)
				.bpetype(BpeEventTypeEnum.PUBLISH_CUIEVENT_TE.getCode())
				.mybps(bpsCode)
				.build());

		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		cuieReq.setCuieCuicode(cui.getCode());
		cuieReq.setCuieMyusi(stCode);
		cuieReq.setCuieMylcet("DR-JN-JRQ"); // Hard code
		cuieReq.setCuieMylcp(lcp); //
		cuieReq.setCuiePlantime(
				calendarPeriodService.findByCode(myCap).getStartTime()
		);
		cuieReq.setCuiePublished(cui.getPublished());
		cuieReq.setCuieActualtimeFet(cuieActualtimeFet);
		cuieReq.setCuieActualtimeBet(DateUtils.now());

		cuiEventService.createCuiEvent(cuieReq);

	}

	@Override
	@Transactional
	public void createCUIJoinEvent(String ulcCode, String stCode,
								   String lcp, Timestamp cuieActualtimeFet, String myCap) {
		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulcCode,
				stCode,
				null,
				true
		);

		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		cuieReq.setCuieCuicode(cui.getCode());
		cuieReq.setCuieMyusi(stCode);
		cuieReq.setCuieMylcet(LCETCodeEnum.PL_SC.getCode());
		cuieReq.setCuieMylcp(lcp);
		cuieReq.setCuiePlantime(
				StringUtils.isBlank(myCap) ? null : calendarPeriodService.findByCode(myCap).getStartTime()
		);
		cuieReq.setCuiePublished(cui.getPublished());
//		cuieReq.setCuieActualtimeFet(cuieActualtimeFet);
//		cuieReq.setCuieActualtimeBet(DateUtils.now());
		cuieReq.setCuiePublished(true);

		cuiEventService.createCuiEvent(cuieReq);
	}

	@Override
	@Transactional
	public void createCUIAndCuiEvent(String ulcCode, String stCode, String cti,
									 String lcp, Timestamp cuieActualtimeFet, String myCap) {
		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulcCode,
				stCode,
				cti,
				true
		);

		//value dung cho create qa247
		valueSaving.setCuiCode(cui.getCode(), false);
		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		cuieReq.setCuieCuicode(cui.getCode());
		cuieReq.setCuieMyusi(stCode);
		cuieReq.setCuieMylcet(LCETCodeEnum.PL_SC.getCode());
		cuieReq.setCuieMylcp(lcp);
		cuieReq.setCuiePlantime(
				StringUtils.isBlank(myCap) ? null : calendarPeriodService.findByCode(myCap).getStartTime()
		);
		cuieReq.setCuiePublished(cui.getPublished());
		cuieReq.setCuiePublished(true);
		cuieReq.setCuieActualtimeFet(cuieActualtimeFet);
		cuieReq.setCuieActualtimeBet(new Timestamp(System.currentTimeMillis()));
		cuiEventService.createCuiEvent(cuieReq);
	}

	@Override
	public List<BpCuiEvent> createAllCuiEventByCui(String cuiCode) {
		List<BpCuiEvent> cuiEventList = new ArrayList<>();
		BPCuiEventRequest cuiEventRequest = new BPCuiEventRequest();
		BPScheduleUssRequest scheduleUssRequest = new BPScheduleUssRequest();
		cuiEventRequest.setCuieCuicode(cuiCode);

		BpCuiContentUserUlc cui = cuiService.findByCode(cuiCode);
		cuiEventRequest.setCuieMyusi(cui.getMyUsi().getCode());
		scheduleUssRequest.setSussCass(cui.getMyUlc().getMyCap().getCode());
		Map<String, Integer> listLcetTime = bpGetCuiEvents(cuiCode);
		listLcetTime.keySet().forEach(lcet -> {
			cuiEventRequest.setCuieMyusi(lcet);
			BpCuiEvent cuie = cuiEventService.createCuiEvent(cuiEventRequest);
			cuiEventService.writeEventPlanTime(cuie.getCode(), listLcetTime.get(lcet), scheduleUssRequest.getSussCass());
			cuiEventList.add(cuie);
		});
		return cuiEventList;
	}

	@Override
	public void setPublishCUIE(String ulc, String usi, Boolean isPublished) {
		BpCuiContentUserUlc cui = cuiService.getOrCreateCUI(
				ulc,
				usi,
				null,
				true
		);
		List<BpCuiEvent> cuiEventList = new ArrayList<>();
		for (BpCuiEvent cuie : cuiEventRepository.findByMyCuiCode(cui.getCode())) {
			cuie.setPublished(isPublished);
			cuie.setPublishbpe(valueSaving.getBpeCode());
			cuiEventList.add(cuie);
		}
		cuiEventRepository.saveAll(cuiEventList);
	}

	@Override
	public void unpublishedCuieUnnecessaryOfModifyStudent(String usi, Timestamp from, Timestamp to, Boolean published) {
		cuiEventRepository.unpublishedCuieUnnecessaryOfModifyStudent(usi, from, to, published);
	}

	@Override
	public void unpublishedCuieUnnecessaryForScheduleMPForOM(String usi, Boolean published) {
		cuiEventRepository.unpublishedCuieUnnecessaryForScheduleMPForOM(usi, published);
	}

	@Override
	public List<BpCuiEvent> findCuieByCapAndClagAndUsi(String usi, String clag, Timestamp from, Timestamp to) {
		return cuiEventRepository.findCuieByCapAndClagAndUsi(usi, clag, from, to);
	}

	@Override
	public void saveAll(List<BpCuiEvent> cuiEventList) {
		cuiEventList.forEach(cuiEventRepository::createOrUpdate);
	}

	public BpCuiEvent getLastCuiEByCTI(String code) {
		return null;
	}

	@Override
	public List<BpCuiEvent> findCuiEByCui(String cuiCode) {
		return cuiEventRepository.findByMyCuiCode(cuiCode);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("bpGetCUIEvents")
	public Map<String, Integer> bpGetCuiEvents(String cuiCode) {
		BpCuiContentUserUlc cui = cuiService.findByCode(cuiCode);
		String lcpCode = cui.getMyUlc().getMyLcp().getCode();
		if (Objects.isNull(lcpCode))
			return null;
		Map<String, Integer> result = new HashMap<>();
		chptRepository.findAllByMyLcpCode(lcpCode).forEach(chpt -> {
			String lcetCode = Objects.isNull(chpt.getMyLcet()) ? null : chpt.getMyLcet().getCode();
			Integer timeOffSetMinutes = 0;
			if (Objects.equals(chpt.getTimeOffSetUnit(), "Hours"))
				timeOffSetMinutes = chpt.getTimeOffSetValue() * 60;
			else if (Objects.equals(chpt.getTimeOffSetUnit(), "Minutes"))
				timeOffSetMinutes = chpt.getTimeOffSetValue();
			if (!result.containsKey(lcetCode))
				result.put(lcetCode, timeOffSetMinutes);
		});
		return result;
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.WRITE_EVENT_PLAN_TIME)
	public BpCUIEventResponse writeEventPlanTime(String cuiEventCode, Integer eventPlanTime, String cassCode) {
		BpCuiEvent cuie = cuiEventRepository.findFirstByCode(cuiEventCode)
				.orElseThrow(() -> new NotFoundException("Coun't find cuie by cuie_code: " + cuiEventCode));
		CalendarPeriod calendarPeriod = calendarPeriodService.findByCode(cassCode);
		cuie.setEventPlanTime(
				new Timestamp(calendarPeriod.getStartTime().getTime() + TimeUnit.MINUTES.toMillis(eventPlanTime)));

		return transformToCUIEventResponse(cuie);
	}

	private BpCUIEventResponse transformToCUIEventResponse(BpCuiEvent cuie) {
		if (Objects.isNull(cuie)) {
			return null;
		}

		return BpCUIEventResponse.builder()
				.cuieActualTimeBet(cuie.getEventActualTimeBet())
				.cuieActualTimeFet(cuie.getEventActualTimeFet())
				.cuieCuicode(Objects.isNull(cuie.getMyCui()) ? null : cuie.getMyCui().getCode())
				.cuieCuiecode(cuie.getCode())
				.cuieMyusi(cuie.getMyUsi())
				.cuieMylcet(Objects.isNull(cuie.getMyLcet()) ? null : cuie.getMyLcet().getCode())
				.cuiePlantime(cuie.getEventPlanTime())
				.build();
	}

	@Override
	@WriteUnitTestLog
	public List<BpCUIEventResponse> findCuiEventsByPlanTime() {
		return internalFindCUIEventsByPlanTime().stream().map(this::transformToCUIEventResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	@WriteBPUnitTestLog(BPLogProcessEnum.WRITE_EVENT_ACTUAL_TIME)
	public BpCUIEventResponse writeEventActualTime(String cuiEventCode) {
		BpCuiEvent cuiEvent = cuiEventRepository.findFirstByCode(cuiEventCode)
				.orElseThrow(() -> new NotFoundException("Coun't find cui event by cuie_code: " + cuiEventCode));
		cuiEvent.setEventActualTimeBet(new Timestamp(System.currentTimeMillis()));
		return transformToCUIEventResponse(cuiEvent);
	}

	@Override
	@WriteBPUnitTestLog(BPLogProcessEnum.TRIGGER_PLANNED_CUI_EVENTS)
	public void triggerPlannedCuiEvents() {
		log.info("triggerPlannedCuiEvents");
		List<BpCuiEvent> cuiEventList = this.internalFindCUIEventsByPlanTime();
		if (cuiEventList.size() != 0) {
			cuiEventList.forEach(cuie -> {
				cuiEventService.writeEventActualTime(cuie.getCode());
				List<BpChpiCheckProcessItem> listChpi = chpiRepository.findAllByMyCuiEventCode(cuie.getCode());
				listChpi.forEach(chpi -> {
					try {
						bpService.sendEmailCHPI(chpi.getCode());
					} catch (MessagingException e) {
						log.error("sendEmail ERROR {}", e.getMessage());
					}
				});
			});
		}
	}

	protected List<BpCuiEvent> internalFindCUIEventsByPlanTime() {
		return cuiEventRepository.findAllByEventPlanTimeNearly(5);
	}
}
