package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.dto.cep200.CEP200StudentDTO;
import vn.edu.clevai.bplog.payload.request.*;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100StudentResponse;
import vn.edu.clevai.bplog.payload.response.logdb.BpChptCheckProcessTempResponse;
import vn.edu.clevai.bplog.payload.response.logdb.CHLIResponse;
import vn.edu.clevai.bplog.payload.response.logdb.CHPIResponse;
import vn.edu.clevai.bplog.payload.response.logdb.CHSIResponse;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.clag.CEP100Service;
import vn.edu.clevai.bplog.service.clag.CEP200Service;
import vn.edu.clevai.bplog.service.clag.ClagFacadeService;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.*;
import vn.edu.clevai.common.proxy.lms.payload.response.SessionGroupStudentResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping({"/checklist"})
@Configurable
@Slf4j
public class BPController {

	private final BpService bpService;

	private final BpClagPODService bpClagPODService;

	private final CEP100Service cep100Service;

	private final BpCibPodService bpCibPodService;

	private final CEP200Service cep200Service;

	private final ClagFacadeService clagFacadeService;

	private final BpGgStService bpGgStService;

	private final BpClagClassgroupService bpClagClassgroupService;

	private final ModelMapper mapper;

	public BPController(BpService bpService,
						BpClagPODService bpClagPODService,
						CEP100Service cep100Service,
						BpCibPodService bpCibPodService,
						CEP200Service cep200Service,
						ClagFacadeService clagFacadeService,
						BpGgStService bpGgStService,
						@Lazy BpClagClassgroupService bpClagClassgroupService,
						ModelMapper mapper) {
		this.bpService = bpService;
		this.bpClagPODService = bpClagPODService;
		this.cep100Service = cep100Service;
		this.bpCibPodService = bpCibPodService;
		this.cep200Service = cep200Service;
		this.clagFacadeService = clagFacadeService;
		this.bpGgStService = bpGgStService;
		this.bpClagClassgroupService = bpClagClassgroupService;
		this.mapper = mapper;
	}

	@PostMapping("/create-chpi")
	public ResponseEntity<ApiResponse<CHPIResponse>> BPCreateCHPI(@RequestBody @Validated CreateCHPIRequest request) {
		log.info("Create CHPI");
		return ResponseEntity.ok(ApiResponse.success(mapper.map(
				bpService.createBpCHPI(request.getCHPTCode(), request.getCTI1Code(), request.getCTI2Code(),
						request.getCTI3Code(), request.getCUIEventCode(), request.getToSendEmail()),
				CHPIResponse.class)));
	}

	@GetMapping("/find-chpt5")
	public ResponseEntity<ApiResponse<BpChptCheckProcessTempResponse>> findCHPT5(
			@RequestParam(value = "lct_code", required = false) String lctCode,
			@RequestParam(value = "lcet_code", required = false) String lcetCode,
			@RequestParam(value = "chpt_type", required = false) String chptType,
			@RequestParam(value = "trigger_user_type", required = false) String triggerUserType,
			@RequestParam(value = "checker_user_type", required = false) String checkerUserType) {
		return ResponseEntity.ok(ApiResponse
				.success(bpService.findCHPT5(lctCode, lcetCode, triggerUserType, checkerUserType, chptType)));
	}

	@GetMapping("/find-chpt4")
	public ResponseEntity<ApiResponse<BpChptCheckProcessTempResponse>> findCHPT4(
			@RequestParam(value = "cui_event_code", required = false) String cuiEventCode) throws Exception {
		return ResponseEntity.ok(ApiResponse.success(bpService.findCHPT4(cuiEventCode)));

	}

	@PostMapping("/create-chsi")
	public ResponseEntity<ApiResponse<CHSIResponse>> bpCreateCHSI(@RequestBody CreateCHSIRequest request) {
		return ResponseEntity
				.ok(ApiResponse.success(bpService.bpCreateCHSI(request.getChstCode(), request.getChpiCode())));
	}

	@PostMapping("/create-chli")
	public ResponseEntity<ApiResponse<CHLIResponse>> bpCreateCHLI(@RequestBody CreateCHLIRequest request) {
		return ResponseEntity.ok(ApiResponse
				.success(bpService.bpCreateCHLI(request.getCHlTCode(), request.getCHLICode(), request.getCHSICode())));
	}

	@GetMapping("/find-usi")
	public ResponseEntity<ApiResponse<String>> bpFindUsi(
			@RequestParam(value = "CHRT_code", required = false) String chrtCode,
			@RequestParam(value = "CUIEvent_code", required = false) String cuiEventCode) {
		return ResponseEntity.ok(ApiResponse.success(bpService.bpFindUsiCode(chrtCode, cuiEventCode)));
	}

	@RequestMapping("/BPGetGG")
	public ResponseEntity<ApiResponse<BpGGResponse>> bpGetGG(@RequestParam("podCode") String podCode) throws Exception {
		return ResponseEntity.ok(ApiResponse.success(clagFacadeService.BPGetGG(podCode)));
	}

	@RequestMapping("/BPGetWSO")
	public ResponseEntity<ApiResponse<BpWsoResponse>> bpGetWSO(@RequestParam("podCode") String podCode)
			throws Exception {
		return ResponseEntity.ok(ApiResponse.success(clagFacadeService.BPGetWSO(podCode)));
	}

	@RequestMapping("/BPGetDFDL")
	public ResponseEntity<ApiResponse<BpDfdlResponse>> bpGetDFDL(@RequestParam("podCode") @NotEmpty String podCode,
																 @RequestParam("clagTypeCode") @NotEmpty String clagTypeCode) throws Exception {
		return ResponseEntity.ok(ApiResponse.success(clagFacadeService.BPGetDFDL(podCode, clagTypeCode)));
	}

	@RequestMapping("/BPGetDFGE")
	public ResponseEntity<ApiResponse<BpDfgeResponse>> BPGetDFGE(@RequestParam String podCode) {
		/* 1. Change use cep200Service */
		CEP200StudentDTO cep200Student = cep200Service.getStudent(podCode);
		/* 2. student's mobile --> cep100 student_id */
		CEP100StudentResponse studentRes = cep100Service.getCEPStudent(cep200Student.getUsername());

		/* 3. student_id --> session_group_id of the last daily_scheduled_class_id */
		/* 4. student_id --> daily_scheduled_class_id */
		SessionGroupStudentResponse response = cep100Service.getSessionGroupStudent(studentRes.getStudentId());

		/* 5. (session_group_id, daily_scheduled_class_id) --> dfge */
		return ResponseEntity
				.ok(ApiResponse.success(BpDfgeResponse.builder().podCode(podCode).usiCode(cep200Student.getMyst())
						.dfgeCode(Cep100TransformUtils.toDfgeCode(response.getCategory())).build()));
	}

	@PostMapping("/BPSetGG")
	public ResponseEntity<ApiResponse<BpGgStResponse>> BPSetGG(@Valid @RequestBody BPSetGGRequest request) {
		return ResponseEntity.ok(ApiResponse.success(bpGgStService.BPSetGG(request.getPodCode(), request.getGgCode())));
	}

	@PostMapping("/BPSetDFDL")
	public ResponseEntity<ApiResponse<BpCipPodResponse>> BPSetDFDL(@Valid @RequestBody BPSetDFDLRequest request) {
		return ResponseEntity
				.ok(ApiResponse.success(bpCibPodService.BPSetDFDL(request.getPodCode(), request.getDfdlCode())));
	}

	@PostMapping("/BPSetWSO")
	public ResponseEntity<ApiResponse<BpCipPodResponse>> BPSetWSO(@Valid @RequestBody BPSetWSORequest request) {
		return ResponseEntity
				.ok(ApiResponse.success(bpCibPodService.BPSetWSO(request.getPodCode(), request.getWsoCode())));
	}

	@PostMapping("/BPSetCLAGPOD")
	public ResponseEntity<ApiResponse<BpClagPODResponse>> BPSetCLAGPOD(
			@Valid @RequestBody BPSetCLAGPODRequest request) {
		return ResponseEntity.ok(ApiResponse.success(
				bpClagPODService.BPSetCLAGPOD(request.getPodCode(), request.getClagCode(), request.getClagTypeCode())));
	}

	@PostMapping("/BPSetCLAG")
	public ResponseEntity<ApiResponse<?>> BPSetCLAG(@Valid @RequestBody BPSetCLAGRequest request) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.BPSetCLAG(request.getName(),
				request.getMypt(), request.getMygg(), request.getMydfdl(), request.getMywso(), request.getMydfge(),
				request.getClagtype(), request.getMaxtotalstudents(), request.getDescription(), request.getXclag(),
				request.getClassIndex())));
	}

	@GetMapping("/BPGetCLAG")
	public ResponseEntity<ApiResponse<ClagResponse>> getClag(@RequestParam("podCode") String podCode,
															 @RequestParam("clagTypeCode") String clagTypeCode) {
		return ResponseEntity.ok(ApiResponse.success(clagFacadeService.BPGetCLAG(podCode, clagTypeCode)));
	}

	@GetMapping("/SendEmailCHSI")
	public ResponseEntity<ApiResponse<?>> SendEmailCHSI(@RequestParam(value = "chsi_code") String chsiCode)
			throws Exception {
		bpService.sendEmailCHSI(chsiCode);
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@GetMapping("/sendEmailCHPI")
	public ResponseEntity<ApiResponse<?>> sendEmailCHPI(@RequestParam(value = "chpi_code") String chpiCode)
			throws Exception {
		bpService.sendEmailCHPI(chpiCode);
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

}
