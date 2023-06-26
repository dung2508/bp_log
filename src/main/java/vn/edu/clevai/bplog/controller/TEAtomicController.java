package vn.edu.clevai.bplog.controller;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.api.constraint.DateStringFormat;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.api.util.ListUtils;
import vn.edu.clevai.common.proxy.bplog.payload.request.SetUsiDutyFromXRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/te-atomic")
public class TEAtomicController {

	private final CuiEventService cuiEventService;

	private final CuiService cuiService;

	private final BpPodProductOfDealService bpPodProductOfDealService;

	private final ClassCategoryService classCategoryService;

	private final BpULCService bpULCService;

	private final BpPodProductOfDealService productOfDealService;

	private final BpLCPService bpLCPService;

	private final CalendarPeriodService calendarPeriodService;

	private final PTService ptService;

	private final BpGGGradeGroupService ggService;

	private final BpDfdlDifficultygradeService dfdlService;

	private final BpClagClassgroupService bpClagClassgroupService;

	private final BpUsiUserItemService userItemService;

	private final BpClagClassgroupService clagClassgroupService;

	private final BpUSIDutyService usiDutyService;

	private final BpClagPODService clagPODService;

	private final ListUtils listUtils;

	private final ModelMapper modelMapper;

	private final LmsService lmsService;

	private final BpTaskInfoService bpTaskInfoService;


	public TEAtomicController(CuiEventService cuiEventService,
							  CuiService cuiService,
							  BpPodProductOfDealService bpPodProductOfDealService,
							  ClassCategoryService classCategoryService, @Lazy BpULCService bpULCService,
							  BpPodProductOfDealService productOfDealService, BpLCPService bpLCPService,
							  CalendarPeriodService calendarPeriodService,
							  PTService ptService,
							  BpGGGradeGroupService ggService,
							  BpDfdlDifficultygradeService dfdlService,
							  BpClagClassgroupService bpClagClassgroupService,
							  BpUsiUserItemService userItemService, BpClagClassgroupService clagClassgroupService,
							  BpUSIDutyService usiDutyService,
							  BpClagPODService clagPODService,
							  @Lazy ListUtils listUtils,
							  ModelMapper modelMapper, LmsService lmsService, BpTaskInfoService bpTaskInfoService) {
		this.cuiEventService = cuiEventService;
		this.cuiService = cuiService;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.classCategoryService = classCategoryService;
		this.bpULCService = bpULCService;
		this.productOfDealService = productOfDealService;
		this.bpLCPService = bpLCPService;
		this.calendarPeriodService = calendarPeriodService;
		this.ptService = ptService;
		this.ggService = ggService;
		this.dfdlService = dfdlService;
		this.bpClagClassgroupService = bpClagClassgroupService;
		this.userItemService = userItemService;
		this.clagClassgroupService = clagClassgroupService;
		this.usiDutyService = usiDutyService;
		this.clagPODService = clagPODService;
		this.listUtils = listUtils;
		this.modelMapper = modelMapper;
		this.lmsService = lmsService;
		this.bpTaskInfoService = bpTaskInfoService;
	}

	@PostMapping("/createCUIJoinEvent")
	public ResponseEntity<ApiResponse<?>> createCUIJoinEvent(
			@RequestParam String ulcCode,
			@RequestParam String stCode,
			@RequestParam String lcp,
			@RequestParam Timestamp cuieActualtimeFet,
			@RequestParam String myCap
	) {
		cuiEventService.createCUIJoinEvent(ulcCode, stCode, lcp, cuieActualtimeFet, myCap);
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PostMapping("/createCUI")
	public ResponseEntity<ApiResponse<?>> createCUI(
			@RequestParam String podCode,
			@RequestParam String ctiCode,
			@RequestParam String ulcCode
	) {
		BpPodProductOfDeal pod = bpPodProductOfDealService.findByCode(podCode);
		return ResponseEntity.ok(ApiResponse.success(cuiService.createCui(pod.getMyst(), ctiCode, ulcCode)));
	}

	@GetMapping("/findPodCodeByClag")
	public ResponseEntity<ApiResponse<?>> findPodCodeByClag(
			@RequestParam List<String> clagCode,
			@RequestParam List<String> ust
	) {
		return ResponseEntity.ok(ApiResponse.success(bpPodProductOfDealService.findPodCodeByClag(clagCode, ust)));
	}

	@GetMapping("/findAllPT")
	public ResponseEntity<ApiResponse<?>> findAllPT() {
		return ResponseEntity.ok(ApiResponse.success(listUtils.mapAll(ptService.findAll(), PTResponse.class)));
	}

	@GetMapping("/findAllGG")
	public ResponseEntity<ApiResponse<?>> findAllGG() {
		return ResponseEntity.ok(ApiResponse.success(listUtils.mapAll(ggService.findAll(), BpGGResponse.class)));
	}

	@GetMapping("/findAllDfdl")
	public ResponseEntity<ApiResponse<?>> findAllDfdl() {
		return ResponseEntity.ok(ApiResponse.success(listUtils.mapAll(dfdlService.findAll(), BpDfdlDifficultgradeResponse.class)));
	}

	@GetMapping("/findCASH")
	public ResponseEntity<ApiResponse<?>> findCASH(@RequestParam String cady) {
		return ResponseEntity.ok(ApiResponse.success(calendarPeriodService
				.findByParentAndCapType(cady, CalendarPeriodTypeEnum.SHIFT.getCode())));
	}

	@GetMapping("/findCLAGfromBP")
	public ResponseEntity<ApiResponse<?>> findCLAGfromBP(
			@RequestParam String pt,
			@RequestParam String gg,
			@RequestParam String dfdl,
			@RequestParam String cap,
			@RequestParam String clagType
	) {
		return ResponseEntity.ok(ApiResponse.success(listUtils.mapAll(bpClagClassgroupService.findBy(pt, gg, dfdl, calendarPeriodService.findByCode(cap), clagType), BpClagClassgroupResponse.class)));
	}

	@GetMapping("findLCTByCAPFromBP")
	public ResponseEntity<ApiResponse<String>> findLCTByCAPFromBP(
			@RequestParam String cap
	) {
		return ResponseEntity.ok(ApiResponse.success(calendarPeriodService.findByCode(cap).getMyLct()));
	}

	@GetMapping("findLCPSHByPTFromBP")
	public ResponseEntity<ApiResponse<List<LCPResponse>>> findLCPSHByPTFromBP(
			@RequestParam String lct,
			@RequestParam String pt
	) {
		return ResponseEntity.ok(ApiResponse.success(bpLCPService.findLCPSHByPTFromBP(lct).stream()
				.map(e -> modelMapper.map(e, LCPResponse.class)).collect(Collectors.toList())));
	}

	@PostMapping("scheduleULC")
	public ResponseEntity<ApiResponse<?>> scheduleULC(
			@RequestParam String ulcParent,
			@RequestParam String lcp,
			@RequestParam String cap,
			@RequestParam String gg,
			@RequestParam String dfdl,
			@RequestParam List<String> clags,
			@RequestParam String pt
	) {
		// TODO: fix this later!!!
//		bpULCService.scheduleULC(ulcParent, bpLCPService.findByCode(lcp), cap, gg, dfdl, clagClassgroupService.findByCodeIn(clags), pt);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("setULC")
	public ResponseEntity<ApiResponse<?>> setULC(
			@RequestParam String myParent,
			@RequestParam String code,
			@RequestParam String name,
			@RequestParam String myJoinUlc,
			@RequestParam String mylct,
			@RequestParam String mygg,
			@RequestParam String mypt,
			@RequestParam String mycap,
			@RequestParam String mydfdl,
			@RequestParam String mydfge,
			@RequestParam String mylcp,
			@RequestParam String xdsc,
			@RequestParam Boolean published
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.setULC(
				myParent, code, name, myJoinUlc, mylct, mygg, mypt, mycap, mydfdl, mydfge, mylcp, xdsc, null, published)));
	}

	@GetMapping("getLCLbyLCPFromBP")
	public ResponseEntity<ApiResponse<?>> getLCLbyLCPFromBP(
			@RequestParam String lcpCode
	) {
		BpLCP lcp = bpLCPService.findByCode(lcpCode);
		return ResponseEntity.ok(ApiResponse.success(Objects.nonNull(lcp.getLct()) ? lcp.getLct().getMyLcl() : null));
	}

	@GetMapping("getDFGERateFromX")
	public ResponseEntity<ApiResponse<?>> getDFGERateFromX(
			@RequestParam String cap,
			@RequestParam String lcp,
			@RequestParam String gg,
			@RequestParam String dfdl
	) {
		CalendarPeriod calendarPeriod = calendarPeriodService.findByCode(cap);
		BpLCP bpLCP = bpLCPService.findByCode(lcp);
		return ResponseEntity.ok(ApiResponse.success(lmsService.getSAGPRate(Cep200ToC100Utils.toC100PtId(bpLCP.getMypt()),
				Cep200ToC100Utils.toC100GradeId(gg), Cep200ToC100Utils.toC100DfdlId(dfdl),
				String.valueOf(DateUtils.getClassDay(calendarPeriod.getEndTime())),
				calendarPeriod.getStartTime(),
				Cep200ToC100Utils.toC100SubjectId(bpLCP.getMypt()))));
	}

	@GetMapping("/getMyPODSHNo")
	public ResponseEntity<ApiResponse<?>> getMyPODSHNo(
			@RequestParam String clagCode,
			@RequestParam String calendarPeriodCode
	) {
		BpPodProductOfDeal podCodeByClag = bpPodProductOfDealService.findByCode(clagCode);
		CalendarPeriod calendarPeriod = calendarPeriodService.findByCode(calendarPeriodCode);
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.getMyPODSHNo(podCodeByClag, calendarPeriod)));
	}

	@GetMapping("/getPeriodNo")
	public ResponseEntity<ApiResponse<?>> getPeriodNo(
			@RequestParam Long podSHNo
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.getPeriodNo(podSHNo)));
	}

	@GetMapping("/findLCPKids")
	public ResponseEntity<ApiResponse<?>> findLCPKids(
			@RequestParam String bpLCPCode
	) {
		BpLCP lcp = bpLCPService.findByCode(bpLCPCode);
		return ResponseEntity.ok(ApiResponse.success(listUtils.mapAll(bpLCPService.findLCPKids(lcp.getMylct()), LCPResponse.class)));
	}

	@GetMapping("/findCapKid")
	public ResponseEntity<ApiResponse<?>> findCapKid(
			@RequestParam String calendarPeriodCode,
			@RequestParam String lcpKidCode
	) {
		return ResponseEntity.ok(ApiResponse.success(listUtils.mapAll(calendarPeriodService.findCapKid(calendarPeriodCode, lcpKidCode), CAPResponse.class)));
	}

	@PostMapping("/setClagULC")
	public ResponseEntity<ApiResponse<?>> saveClagULC(
			@RequestParam String clagCode,
			@RequestParam String ulcCode
	) {
		bpULCService.CreateOrUpdateClagUlc(clagCode, ulcCode);
		return ResponseEntity.ok(ApiResponse.success(null));
	}


	@GetMapping("/assignTeToClag/findClagFromULC")
	public ResponseEntity<ApiResponse<?>> findClagFromULC(
			@RequestParam String ulcCode
	) {
		return ResponseEntity.ok(ApiResponse.success(clagClassgroupService.findClagFromULC(ulcCode)));
	}

	@GetMapping("/assignTeToClag/findUsi")
	public ResponseEntity<ApiResponse<?>> findUsi(
			@RequestParam String lcet,
			@RequestParam String ust,
			@RequestParam(required = false) String capCode,
			@RequestParam(required = false) String chrt,
			@RequestParam(required = false) String lcp,
			@RequestParam(required = false) List<String> excludeUsi,
			@RequestParam(required = false) String gg,
			@RequestParam(required = false) String dfdl,
			@RequestParam(required = false) String dfge
	) {
		return ResponseEntity.ok(ApiResponse.success(bpPodProductOfDealService.findBy(lcet, ust, capCode, chrt, lcp, excludeUsi, gg, dfdl, dfge)));
	}

	@PostMapping("/assignTeToClag/setPODClag")
	public ResponseEntity<ApiResponse<?>> setPODClag(
			@RequestParam String podCode,
			@RequestParam String clagCode
	) {
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping("findCLAGFromULCPT")
	public ResponseEntity<ApiResponse<?>> findCLAGFromULC(
			@RequestParam String ulc,
			@RequestParam String pt
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.findClagByUlcAndPt(ulc, pt)));
	}

	@PostMapping("/assignTeToClag/setPOD_CLAGPERM")
	public ResponseEntity<ApiResponse<?>> setPOD_CLAGPERM(
			@RequestParam String podCode,
			@RequestParam(required = false) String podMyclagperm,
			@RequestParam(required = false) Long assignedAt,
			@RequestParam(required = false) Long unassignedAt,
			@RequestParam(required = false) String membertype
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.setPOD_CLAGPERM(podCode, podMyclagperm, new Date(assignedAt), new Date(unassignedAt), membertype, null)));
	}

	@PostMapping("/assignTeToClag/setPodToClag")
	public ResponseEntity<ApiResponse<?>> setPODCLAG(
			@RequestParam String podCode,
			@RequestParam String clag,
			@RequestParam String cady,
			@RequestParam String ust
	) {
		return ResponseEntity.ok(ApiResponse.success(clagPODService.setPODClag(podCode, clag, cady, ust)));
	}

	@GetMapping("/findUBWFromBP")
	public ResponseEntity<ApiResponse<?>> findUBWFromBP(
			@RequestParam String cady,
			@RequestParam String lcp,
			@RequestParam String lct
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.findUBWFromBP(cady, lcp, lct)));
	}

	@GetMapping("/registerTetoCap/findLCPFromPTUSTLCL")
	public ResponseEntity<?> findLcpPtUstLcl(
			@RequestParam String ptCode,
			@RequestParam String ustCode,
			@RequestParam String lclCode
	) {
		return ResponseEntity.ok(bpLCPService.findLcpFromPtUst(ptCode, ustCode, lclCode));
	}

	@PutMapping("/registerTetoCap/setUSIDutyFromX")
	public ResponseEntity<?> setUSIDutyFromX(
			@RequestParam String lcpCode,
			@RequestParam String capCode,
			@RequestParam String ustCode,
			@RequestParam String usiCode,
			@RequestParam String lcet,
			@RequestParam String chrt
	) {
		return ResponseEntity.ok(usiDutyService.setUsiDutyFromX(lcpCode, capCode, ustCode, usiCode, lcet, chrt));
	}

	@GetMapping("/registerTetoCap/findCashStaFromBp")
	public ResponseEntity<?> findCashStaFromBp(
			@RequestParam String accYear,
			@RequestParam String mt,
			@RequestParam String pt,
			@RequestParam String gg,
			@RequestParam String wso,
			@RequestParam String dfdl
	) {
		return ResponseEntity.ok(classCategoryService.findClassCategoryFromBp(accYear, mt, pt, gg, wso, dfdl));
	}

	@GetMapping("/registerTetoCap/findCLC-CSH")
	public ResponseEntity<?> findClcCsh(
			@RequestParam String accYear,
			@RequestParam String mt,
			@RequestParam String gg,
			@RequestParam boolean published
	) {
		return ResponseEntity.ok(classCategoryService.findClcCsh(accYear, gg, mt, published));
	}

	@PutMapping("/registerTetoCap/setUsiDutyClc")
	public ResponseEntity<?> setUsiDutyCashSta(
			@RequestParam String clcCode,
			@RequestParam String usiDutyCode,
			@RequestParam Boolean isEnable
	) {
		return ResponseEntity.ok(usiDutyService.setUsiDutyClc(clcCode, usiDutyCode, isEnable));
	}

	@PostMapping("/registerTetoCap/registerTetoCap")
	public ResponseEntity<?> registerTeToCap(@RequestBody SetUsiDutyFromXRequest request) {
		usiDutyService.registerTeToCap(request);
		return ResponseEntity.ok("chờ đợi cái gì không có đầu ra đâu");
	}

	@GetMapping("/findULCByCAPFromBp")
	public ResponseEntity<ApiResponse<List<BpULCResponse>>> findUlcByCapFromBp(
			@RequestParam String capCode,
			@RequestParam String lcpCode
	) {
		return ResponseEntity.ok(ApiResponse.success(listUtils.mapAll(bpULCService.findUlcByCapFromBp(capCode, lcpCode), BpULCResponse.class)));
	}

	@PutMapping("/registerTetoCap/setCLC-TED")
	public ResponseEntity<?> setClcTed(
			@RequestParam String accYear,
			@RequestParam String mt,
			@RequestParam String pt,
			@RequestParam String gg,
			@RequestParam String wso,
			@RequestParam String dfdl,
			@RequestParam String dfge
	) {
		return ResponseEntity.ok(classCategoryService.setClcTed(
				accYear, mt, pt, gg, wso, dfdl, dfge, true
		));
	}

	@GetMapping("getScheduleCAWKStatus")
	public ResponseEntity<ApiResponse<String>> getScheduleCAWKStatus(
			@RequestParam @DateStringFormat(checkBlank = true) Timestamp date
	) throws ParseException {
		CalendarPeriod cawk = calendarPeriodService
				.getCAPByTime(date, CalendarPeriodTypeEnum.WEEK.getCode());

		if (Objects.isNull(cawk)) {
			throw new NotFoundException("Couldn't find CAWK  with date: " + date);
		}

		String taskName = "SUIScheduleCAWK" + cawk.getCode();
		return ResponseEntity.ok(ApiResponse.success(bpTaskInfoService.getTaskInfo(taskName)));
	}

	@GetMapping("getScheduleCAMNStatus")
	public ResponseEntity<ApiResponse<String>> getScheduleCAMNStatus(
			@RequestParam @DateStringFormat(checkBlank = true) Timestamp date
	) throws ParseException {
		CalendarPeriod camn = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.MONTH.getCode());

		if (Objects.isNull(camn)) {
			throw new NotFoundException("Couldn't find CAMN  with date: " + date);
		}

		String taskName = "SUIScheduleCAMN" + camn.getCode();
		return ResponseEntity.ok(ApiResponse.success(bpTaskInfoService.getTaskInfo(taskName)));
	}

	@GetMapping("getBppAssignTEToClagStatus")
	public ResponseEntity<ApiResponse<String>> getBppAssignTEToClagStatus(
			@RequestParam @DateStringFormat(checkBlank = true) Timestamp date,
			@RequestParam(value = "captype") String capType
	) throws ParseException {
		if (capType.equals(CalendarPeriodTypeEnum.WEEK.getCode())) {
			CalendarPeriod cawk = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.WEEK.getCode());
			if (Objects.isNull(cawk)) {
				throw new NotFoundException("Couldn't find CAWK  with date: " + date);
			}
			String taskName = "bppAssignTeToClagCAWK" + cawk.getCode();
			return ResponseEntity.ok(ApiResponse.success(bpTaskInfoService.getTaskInfo(taskName)));
		}
		if (capType.equals(CalendarPeriodTypeEnum.DAY.getCode())) {
			CalendarPeriod cady = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.DAY.getCode());
			if (Objects.isNull(cady)) {
				throw new NotFoundException("Couldn't find cady  with date: " + date);
			}
			String taskName = "bppAssignTeToClagCADY" + cady.getCode();
			return ResponseEntity.ok(ApiResponse.success(bpTaskInfoService.getTaskInfo(taskName)));
		}
		return ResponseEntity.ok(ApiResponse.error("captype is wrong"));
	}

	@GetMapping("getBppAssignEMToClagStatus")
	public ResponseEntity<ApiResponse<String>> getBppAssignEMToClagStatus(
			@RequestParam @DateStringFormat(checkBlank = true) Timestamp date,
			@RequestParam(value = "captype") String capType
	) throws ParseException {
		if (capType.equals(CalendarPeriodTypeEnum.WEEK.getCode())) {
			CalendarPeriod cawk = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.WEEK.getCode());
			if (Objects.isNull(cawk)) {
				throw new NotFoundException("Couldn't find CAWK  with date: " + date);
			}
			String taskName = "bppAssignEmToClagCAWK" + cawk.getCode();
			return ResponseEntity.ok(ApiResponse.success(bpTaskInfoService.getTaskInfo(taskName)));
		}
		if (capType.equals(CalendarPeriodTypeEnum.DAY.getCode())) {
			CalendarPeriod cady = calendarPeriodService.getCAPByTime(date, CalendarPeriodTypeEnum.DAY.getCode());
			if (Objects.isNull(cady)) {
				throw new NotFoundException("Couldn't find cady  with date: " + date);
			}
			String taskName = "bppAssignEmToClagCADY" + cady.getCode();
			return ResponseEntity.ok(ApiResponse.success(bpTaskInfoService.getTaskInfo(taskName)));
		}
		return ResponseEntity.ok(ApiResponse.error("captype is wrong"));
	}


	@GetMapping("/findPodByUsi")
	public ResponseEntity<?> findPod(
			@RequestParam String usi,
			@RequestParam String pt
	) {
		return ResponseEntity.ok(bpPodProductOfDealService.findByUsi(usi, pt));
	}

	@GetMapping("/findUSHFromBp")
	public ResponseEntity<?> findUSHFromBp(
			@RequestParam String cash,
			@RequestParam(value = "gg", required = false) String gg,
			@RequestParam(value = "dfdl", required = false) String dfdl,
			@RequestParam(value = "dfge", required = false) String dfge
	) {
		return ResponseEntity.ok(bpULCService.findUlcByCapLcpGgDfdlDfge(cash, null, gg, dfdl, dfge));
	}

	@PostMapping("createUBW")
	public ResponseEntity<?> createUBW(
			@RequestParam String cady
	) throws Exception {

		CalendarPeriod calendarPeriod = calendarPeriodService.findByCode(cady);

		if (!CalendarPeriodTypeEnum.DAY.getCode().equals(calendarPeriod.getCapType())) {
			throw new BadRequestException("This API only accept CADY!!!");
		}

		return ResponseEntity.ok(bpULCService.createUBW(calendarPeriod));
	}
}