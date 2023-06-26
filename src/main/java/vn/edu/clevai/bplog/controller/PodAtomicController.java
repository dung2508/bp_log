package vn.edu.clevai.bplog.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.payload.request.CreateOrUpdateDynamicClagRequest;
import vn.edu.clevai.bplog.payload.request.CreateOrUpdateUlcRequest;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.common.api.constraint.DateStringFormat;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.payload.request.*;
import vn.edu.clevai.common.proxy.bplog.payload.response.*;
import vn.edu.clevai.common.proxy.lms.payload.response.ClassInfoResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.XSessionGroupInfoResponse;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pod-atomic")
public class PodAtomicController {
	@Autowired
	@Lazy
	private BpDfdlDifficultygradeService bpDfdlDifficultygradeService;

	@Autowired
	private BpDfgeDifficultgetService bpDfgeDifficultgetService;

	@Autowired
	private BpPodProductOfDealService bpPodProductOfDealService;

	@Autowired
	private BpUsiUserItemService bpUsiUserItemService;

	@Autowired
	private PodService podService;

	@Autowired
	private BpGGGradeGroupService bpGGGradeGroupService;

	@Autowired
	@Lazy
	private BpPODDFDLService bpPODDFDLService;

	@Autowired
	@Lazy
	private BpWsoWeeklyscheduleoptionService bpWsoWeeklyscheduleoptionService;

	@Autowired
	private BpGgStService bpGgStService;

	@Autowired
	private Cep100LmsService cep100LmsService;

	@Autowired
	@Lazy
	private BpClagClassgroupService bpClagClassgroupService;

	@Autowired
	@Lazy
	private BpULCService bpULCService;

	@Autowired
	private ModelMapper mapper;

	@GetMapping("/getPTfromX")
	public ResponseEntity<ApiResponse<PTResponse>> getPTfromX(
			@RequestParam(value = "xpt_id", required = false) Long xptId
	) {
		return ResponseEntity.ok(ApiResponse.success(mapper.map(
				podService.getPTFromX(xptId), PTResponse.class
		)));
	}

	@PostMapping("/getPOD")
	public ResponseEntity<ApiResponse<List<BpPODResponse>>> getPOD(@RequestBody MigratePodWsoRequest request) {
		return ResponseEntity.ok(ApiResponse.success(
				podService.getPod(request.getFromId(), request.getToId(), request.getPageSize())
						.stream().map(pod -> mapper.map(pod, BpPODResponse.class)).collect(Collectors.toList())
		));
	}

	@GetMapping("/getPODFromX")
	public ResponseEntity<ApiResponse<PODResponse>> getPODFromX(
			@RequestParam Long xdeal
	) {
		return ResponseEntity.ok(ApiResponse.success(bpPodProductOfDealService.getPODFromX(xdeal)));
	}

	@GetMapping("/findXPT")
	public ResponseEntity<ApiResponse<Integer>> findXPT(
			@RequestParam(value = "xdeal") Integer xdeal
	) {
		return ResponseEntity.ok(ApiResponse.success(podService.findXPT(xdeal)));
	}

	@GetMapping("/getDFDLFromX")
	ResponseEntity<ApiResponse<BpDfdlDifficultgradeResponse>> getDFDLFromX(@RequestParam Integer xDfdl) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpDfdlDifficultygradeService.getDFDLFromX(xDfdl)
				)
		);
	}

	@GetMapping("/getPOD_PT")
	public ResponseEntity<ApiResponse<PTResponse>> getPOD_PT(
			@RequestParam(value = "pod_code") String podCode
	) {
		BpPTProductType productType = podService.getPOD_PT(podCode);
		return ResponseEntity.ok(ApiResponse.success(mapper.map(productType, PTResponse.class)));
	}

	@PutMapping("/setPOD_PT")
	public ResponseEntity<ApiResponse<?>> setPOD_PT(
			@RequestParam(value = "pod_code") String podCode,
			@RequestParam(value = "pt_code") String ptCode
	) {
		podService.setPOD_PT(podCode, ptCode);
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@GetMapping("/findXDFDL")
	public ResponseEntity<ApiResponse<Integer>> findXDFDL(
			@RequestParam Long xdeal
	) {
		return ResponseEntity.ok(ApiResponse.success(bpDfdlDifficultygradeService.findXDFDL(xdeal)));
	}

	@GetMapping("/getUSI")
	public ResponseEntity<ApiResponse<List<BpUsiUserItemResponse>>> getUSI(@RequestParam String ust, Pageable pageable) {
		return ResponseEntity.ok(ApiResponse.success(bpUsiUserItemService.getUSI(ust, pageable)));
	}

	@GetMapping("/getSTFromX")
	public ResponseEntity<ApiResponse<BpUsiUserItemResponse>> getSTFromX(@RequestParam String xSt) {
		return ResponseEntity.ok(
				ApiResponse.success(bpUsiUserItemService.getSTFromX(xSt))
		);
	}

	@GetMapping("/getPOD-DFDL")
	public ResponseEntity<ApiResponse<BpDfdlDifficultgradeResponse>> getPOD_DFDL(
			@RequestParam("pod_code") String podCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpDfdlDifficultygradeService.getPOD_DFDL(podCode)));
	}

	@PostMapping("setPOD-DFDL")
	public ResponseEntity<ApiResponse<BpPODDFDLResponse>> setPOD_DFDL(
			@RequestParam("pod_code") String podCode,
			@RequestParam String dfdl
	) {
		return ResponseEntity.ok(ApiResponse.success(bpPODDFDLService.setPOD_DFDL(podCode, dfdl)));
	}

	@PutMapping("/setPOD_ST")
	public ResponseEntity<ApiResponse<PODResponse>> setPOD_ST(
			@RequestParam(value = "pod_code") String podCode,
			@RequestParam(value = "st_code") String stCode
	) {
		return ResponseEntity.ok(ApiResponse.success(podService.setPOD_ST(podCode, stCode)));
	}

	@GetMapping("getWSOFromX")
	public ResponseEntity<ApiResponse<BpWsoWeeklyscheduleoptionResponse>> getWSOFromX(
			@RequestParam String xwso
	) {
		return ResponseEntity.ok(ApiResponse.success(mapper.map(bpWsoWeeklyscheduleoptionService.getWSOFromX(xwso), BpWsoWeeklyscheduleoptionResponse.class)));
	}

	@GetMapping("/findXST")
	public ResponseEntity<ApiResponse<String>> findXST(
			@RequestParam(value = "xdeal") Integer dealId
	) {
		return ResponseEntity.ok(ApiResponse.success(podService.findXST(dealId.longValue())));
	}

	@GetMapping("/getPOD_ST")
	public ResponseEntity<ApiResponse<?>> getPOD_ST(
			@RequestParam(value = "pod_code") String podCode
	) {
		return ResponseEntity.ok(ApiResponse.success(podService.getPOD_ST(podCode)));
	}

	@GetMapping("getPOD-WSO")
	public ResponseEntity<ApiResponse<BpWsoWeeklyscheduleoptionResponse>> getPOD_WSO(
			@RequestParam("pod_code") String podCode
	) {
		return ResponseEntity.ok(ApiResponse.success(mapper.map(bpWsoWeeklyscheduleoptionService.getPOD_WSO(podCode), BpWsoWeeklyscheduleoptionResponse.class)));
	}

	@GetMapping("findXWSO")
	public ResponseEntity<ApiResponse<String>> findXWSO(
			@RequestParam Long xdeal
	) {
		return ResponseEntity.ok(ApiResponse.success(bpWsoWeeklyscheduleoptionService.findXWSO(xdeal)));
	}

	@GetMapping("/getGGFromX")
	public ResponseEntity<ApiResponse<BpGgGradegroupResponse>> getGGFromX(@RequestParam Long xGg) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpGGGradeGroupService.getGGFromX(xGg)
				)
		);
	}

	@PostMapping("setPOD-WSO")
	public ResponseEntity<ApiResponse<BpPODWSOResponse>> setPOD_WSO(
			@RequestParam("pod_code") String podCode,
			@RequestParam("wso_code") String wsoCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpWsoWeeklyscheduleoptionService.setPOD_WSO(podCode, wsoCode)));
	}

	@GetMapping("/findXGG")
	public ResponseEntity<ApiResponse<Long>> findXGG(@RequestParam String xSt) {
		return ResponseEntity.ok(
				ApiResponse.success(bpGGGradeGroupService.findXGG(xSt))
		);
	}

	@GetMapping("/getST-GG")
	public ResponseEntity<ApiResponse<BpGgGradegroupResponse>> getST_GG(@RequestParam String stCode) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpGGGradeGroupService.getST_GG(stCode)
				)
		);
	}

	@PostMapping("/setST-GG")
	public ResponseEntity<ApiResponse<BpGgStResponse>> setST_GG(@Valid @RequestBody StGgCreationRequest request) {
		return ResponseEntity.ok(ApiResponse.success(bpGgStService.setST_GG(request.getStCode(), request.getGgCode())));
	}

	@GetMapping("getCLAGPERMFromX")
	public ResponseEntity<ApiResponse<BpClagClassgroupResponse>> getCLAGPERMFromX(
			@RequestParam String xclass
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.getCLAGPERMFromX(xclass)));
	}

	@PostMapping("/setPOD_CLAGPERM")
	public ResponseEntity<ApiResponse<?>> setPOD_CLAGPERM(
			@RequestParam("POD") String pod,
			@RequestParam("CLAGPERM") String clagperm,
			@RequestParam("ASSIGNED_AT") @DateTimeFormat(pattern = DateUtils.MEDIUM_PATTERN) Date assignedAt,
			@RequestParam("UNASSIGNED_AT") @DateTimeFormat(pattern = DateUtils.MEDIUM_PATTERN) Date unassignedAt,
			@RequestParam("MEMBERTYPE") String membertype
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.setPOD_CLAGPERM(
				pod,
				clagperm,
				assignedAt,
				unassignedAt,
				membertype,
				null
		)));
	}

	@GetMapping("/getDFGEFromX")
	public ResponseEntity<ApiResponse<?>> getDFGEFromX(String xDfge) {
		return ResponseEntity.ok(
				ApiResponse.success(bpDfgeDifficultgetService.getDFGEFromX(xDfge))
		);
	}

	@GetMapping("findXCLASS")
	public ResponseEntity<ApiResponse<String>> findXCLASS(
			@RequestParam Long xdeal
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.findXCLASS(xdeal)));
	}

	@GetMapping("/getPOD_CLAGPERM")
	public ResponseEntity<ApiResponse<BpClagClassgroupResponse>> getPOD_CLAGPERM(
			@RequestParam("pod") String pod
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.getPOD_CLAGPERM(pod)));
	}

	@PutMapping("/setCLAGDYN-DFGE")
	public ResponseEntity<ApiResponse<BpClagClassgroupResponse>> setCLAGDYN_DFGE(
			@RequestParam String clagdynCode,
			@RequestParam String dfgeCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.setCLAGDYN_DFGE(clagdynCode, dfgeCode)));
	}

	@GetMapping("/findXDFGE")
	public ResponseEntity<ApiResponse<String>> findXDFGE(@RequestParam String xsessiongroup, @RequestParam String xcash) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpDfgeDifficultgetService.findXDFGE(xsessiongroup, xcash)
				)
		);
	}

	@GetMapping("getUDLFromX")
	public ResponseEntity<ApiResponse<List<BpULCResponse>>> getUDLFromX(
			@RequestParam String xdsc
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getUDLFromX(xdsc)));
	}

	@GetMapping("/getCLAGDYN-DFGE")
	public ResponseEntity<ApiResponse<BpDfgeDifficultgetResponse>> getCLAGDYN_DFGE(@RequestParam String clagdynCode) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpDfgeDifficultgetService.getCLAGDYN_DFGE(clagdynCode)
				)
		);
	}

	@GetMapping("/getCLAGDYNFromX")
	public ResponseEntity<ApiResponse<BpClagClassgroupResponse>> getCLAGDYNFromX(@RequestParam String xsessiongroup, @RequestParam String xcash) {
		return ResponseEntity.ok(
				ApiResponse.success(bpClagClassgroupService.getCLAGDYNFromX(xsessiongroup, xcash))
		);
	}

	@GetMapping(value = "/findXDSC", params = "xclass")
	public ResponseEntity<ApiResponse<String>> findXDSC(
			@RequestParam String xclass
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.findXDSC(xclass)));
	}

	@GetMapping(value = "/findXDSC", params = {"xsessiongroup", "xcash"})
	public ResponseEntity<ApiResponse<String>> findXDSC(
			@RequestParam String xsessiongroup,
			@RequestParam String xcash
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.findXDSC(xsessiongroup, xcash)));
	}

	@GetMapping("getCLAGPERM-UDL")
	public ResponseEntity<ApiResponse<BpULCResponse>> getCLAGPERM_UDL(
			@RequestParam("clag_code") String clagCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getCLAGPERM_UDL(clagCode)));
	}

	@PostMapping("setCLAGPERM-UDL")
	public ResponseEntity<ApiResponse<BpCLAGULCResponse>> setCLAGPERM_UDL(
			@RequestParam("clag_code") String clagCode,
			@RequestParam("ulc_code") String ulcCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.setCLAGPERM_UDL(clagCode, ulcCode)));
	}

	@GetMapping("/getUGEFromX")
	public ResponseEntity<ApiResponse<List<BpULCResponse>>> getUGEFromX(@RequestParam("XDSC") String xdsc) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getUGEFromX(xdsc)));
	}

	@GetMapping("/getCLAGDYN_UGE")
	public ResponseEntity<ApiResponse<BpULCResponse>> getCLAGDYN_UGE(
			@RequestParam("CLAGDYN") String clagdyn
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getCLAGDYN_UGE(clagdyn)));
	}

	@PostMapping("/setCLAGDYN_UGE")
	public ResponseEntity<ApiResponse<BpCLAGULCResponse>> setCLAGDYN_UGE(
			@RequestParam("CLAGDYN") String clagdyn,
			@RequestParam("UGE") String uge
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.setCLAGDYN_UGE(clagdyn, uge)));
	}

	@PostMapping("/setST")
	public ResponseEntity<ApiResponse<BpUsiUserItemResponse>> setST(@Valid @RequestBody CreateOrUpdateUsiRequest request) {
		return ResponseEntity.ok(
				ApiResponse.success(bpUsiUserItemService.createOrUpdateUsi(
						request.getCode(), request.getLastname(), request.getFirstname(), request.getMyust(),
						request.getUsername(), request.getFullName(), request.getPhone(), null)
				)
		);
	}

	@PostMapping("/setPOD")
	public ResponseEntity<ApiResponse<BpPODResponse>> setPOD(@Valid @RequestBody CreateOrUpdatePODRequest request) {
		return ResponseEntity.ok(
				ApiResponse.success(podService.setPOD(
						request.getCode(), request.getMypt(), request.getMyst(), request.getMyprd()
						, request.getFromDate(), request.getToDate(), request.getXdeal()
				))
		);
	}

	@PostMapping("/setCLAGPERM")
	public ResponseEntity<ApiResponse<BpClagClassgroupResponse>> setCLAGPERM(@Valid @RequestBody CreateOrUpdatePermanentClagRequest request) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpClagClassgroupService.createOrUpdatePermanentClag(
								request.getCode(),
								request.getMypt(),
								request.getMygg(),
								request.getMydfdl(),
								request.getMywso(),
								request.getClagtype(),
								request.getXclass(),
								request.getMaxtotalstudents()
						)
				)
		);
	}

	@GetMapping("/getXCLASS")
	public ResponseEntity<GeneralPageResponse<ClassInfoResponse>> getXCLASS(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer size
	) {
		return ResponseEntity.ok(cep100LmsService.getXPermanentClasses(page, size));
	}

	@GetMapping("getExpireDate")
	public ResponseEntity<ApiResponse<Date>> getExpireDate(
			@RequestParam("pod_code") String podCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpPodProductOfDealService.getExpireDate(podCode)));
	}

	@PostMapping("/setCLAGDYN")
	public ResponseEntity<ApiResponse<BpClagClassgroupResponse>> setCLAGDYN(
			@Valid @RequestBody CreateOrUpdateDynamicClagRequest request) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpClagClassgroupService.createOrUpdateDynamicClag(
								request.getCode(),
								request.getMypt(),
								request.getMygg(),
								request.getMydfdl(),
								request.getMydfge(),
								request.getMywso(),
								request.getMaxtotalstudents(),
								request.getClagtype(),
								request.getXsessiongroup(),
								request.getXsessiongroup()
						)
				)
		);
	}

	@PostMapping("/setULC")
	public ResponseEntity<ApiResponse<BpULCResponse>> setULC(
			@Valid @RequestBody CreateOrUpdateUlcRequest request
	) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpULCService.setULC(
								request.getMyParent(),
								request.getCode(),
								request.getName(),
								request.getMyJoinUlc(),
								request.getMylct(),
								request.getMygg(),
								request.getMypt(),
								request.getMycap(),
								request.getMydfdl(),
								request.getMydfge(),
								request.getMylcp(),
								request.getXdsc(),
								null,
								request.getPublished()
						)
				)
		);
	}

	@GetMapping("/getUSHFromX")
	public ResponseEntity<ApiResponse<List<BpULCResponse>>> getUSHFromX(@RequestParam String xdsc) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getUSHFromX(xdsc)));
	}

	@GetMapping("/getGESFromX")
	public ResponseEntity<ApiResponse<List<BpULCResponse>>> getGESFromX(@RequestParam String xdsc) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getGESFromX(xdsc)));
	}

	@PostMapping("/setCLAGPERM_USH")
	public ResponseEntity<ApiResponse<BpCLAGULCResponse>> setCLAGPERM_USH(
			@RequestParam("clag_code") String clagCode,
			@RequestParam("ush_code") String ushCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.setCLAGPERM_USH(clagCode, ushCode)));
	}

	@PostMapping("/setCLAGDYN_GES")
	public ResponseEntity<ApiResponse<BpCLAGULCResponse>> setCLAGDYN_GES(
			@RequestParam("clag_code") String clagCode,
			@RequestParam("ush_code") String ushCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.setCLAGDYN_GES(clagCode, ushCode)));
	}

	@GetMapping("/getCLAGPERM_USH")
	public ResponseEntity<ApiResponse<BpULCResponse>> getCLAGPERM_USH(
			@RequestParam("clag_code") String clagCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getCLAGPERM_USH(clagCode)));
	}

	@GetMapping("/getCLAGDYN_GES")
	public ResponseEntity<ApiResponse<BpULCResponse>> getCLAGDYN_GES(
			@RequestParam("clag_code") String clagCode
	) {
		return ResponseEntity.ok(
				ApiResponse.success(
						bpULCService.getCLAGDYN_GES(clagCode)
				)
		);
	}

	@GetMapping("getPOD_CLAGDYN")
	public ResponseEntity<ApiResponse<BpPODCLAGResponse>> getPOD_CLAGDYN(
			@RequestParam("pod_code") String podCode,
			@RequestParam("cap_code") String capCode
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.getPOD_CLAGDYN(podCode, capCode)));
	}

	@GetMapping("findXSESSIONGROUP")
	public ResponseEntity<ApiResponse<XSessionGroupInfoResponse>> findXSESSIONGROUP(
			@RequestParam Long xdeal,
			@RequestParam String xcash
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.findXSESSIONGROUP(xdeal, xcash)));
	}

	@PostMapping("/setPOD_CLAGDYN")
	public ResponseEntity<ApiResponse<BpPODCLAGResponse>> setPOD_CLAGDYN(
			@RequestParam("POD") String podCode,
			@RequestParam("CLAGDYN") String clagdyn,
			@RequestParam("ASSIGNED_AT") @DateTimeFormat(pattern = DateUtils.MEDIUM_PATTERN) Date assignedAt,
			@RequestParam("UNASSIGNED_AT") @DateTimeFormat(pattern = DateUtils.MEDIUM_PATTERN) Date unassignedAt,
			@RequestParam("MEMBERTYPE") String membertype
	) {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.setPOD_CLAGDYN(
				podCode,
				clagdyn,
				assignedAt,
				unassignedAt,
				membertype
		)));
	}

	@GetMapping("getXCASH")
	public ResponseEntity<ApiResponse<String>> getXCASH(
			@RequestParam @DateStringFormat(checkBlank = true) String xcady,
			@RequestParam Long xgg
	) throws Exception {
		return ResponseEntity.ok(ApiResponse.success(bpClagClassgroupService.getXCASH(xcady, xgg)));
	}
}