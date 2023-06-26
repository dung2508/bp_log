package vn.edu.clevai.bplog.controller.bpprocess;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum;
import vn.edu.clevai.bplog.payload.request.BaseRequestIds;
import vn.edu.clevai.bplog.payload.response.ULCDetailInfoResponse;
import vn.edu.clevai.bplog.payload.response.ulc.ULCResponse;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.BpULCService;
import vn.edu.clevai.bplog.service.CalendarPeriodService;
import vn.edu.clevai.common.api.constraint.DateStringFormat;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping({"/bpp-te"})
@Validated
@Slf4j
public class BPPTEController {

	private final BpULCService bpULCService;

	private final BpClagClassgroupService bpClagClassgroupService;

	private final CalendarPeriodService calendarPeriodService;

	public BPPTEController(BpULCService bpULCService, BpClagClassgroupService bpClagClassgroupService, CalendarPeriodService calendarPeriodService) {
		this.bpULCService = bpULCService;
		this.bpClagClassgroupService = bpClagClassgroupService;
		this.calendarPeriodService = calendarPeriodService;
	}

	@PostMapping("/BPPJoinRequest-GE-GES-TE")
	public ResponseEntity<ApiResponse<?>> BPPJoinRequest_GE_GES_TE(
			@RequestParam String xst,
			@RequestParam Long xpt,
			@RequestParam String xcash,
			@RequestParam String xsessionggroup,
			@RequestParam Timestamp cuieActualtimeFet
	) {
		bpULCService.changeCLAGTEBPPJoinRequestGETE(xst, xpt, xcash, xsessionggroup, cuieActualtimeFet, true);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("BPPChangeCLAG-GE-GES-TE")
	public ResponseEntity<ApiResponse<?>> BPPChangeCLAG_GE_GES_TE(
			@RequestParam String xst,
			@RequestParam Long oldXpt,
			@RequestParam String oldXcash,
			@RequestParam String oldXsessionggroup,
			@RequestParam Long xpt,
			@RequestParam String xcash,
			@RequestParam String xsessionggroup,
			@RequestParam Timestamp cuieActualtimeFet) {
		bpULCService.changeCLAGTEBppChangeClag(xst, oldXpt, oldXcash, oldXsessionggroup, cuieActualtimeFet, false);
		bpULCService.changeCLAGTEBppChangeClag(xst, xpt, xcash, xsessionggroup, cuieActualtimeFet, true);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("BPPChangeTE")
	public ResponseEntity<ApiResponse<?>> BPPChangeTE(
			@RequestParam String oldXst,
			@RequestParam String xst,
			@RequestParam Long xpt,
			@RequestParam String xcash,
			@RequestParam String xsessionggroup,
			@RequestParam Timestamp cuieActualtimeFet
	) {
		bpULCService.changeCLAGTEBppChangeTE(oldXst, xpt, xcash, xsessionggroup, cuieActualtimeFet, false);
		bpULCService.changeCLAGTEBppChangeTE(xst, xpt, xcash, xsessionggroup, cuieActualtimeFet, true);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("SUIScheduleCAWK")
	public ResponseEntity<ApiResponse<?>> SUIScheduleCAWK(
			@RequestParam Timestamp date
	) throws ParseException {
		bpULCService.SUIScheduleCAWK(date);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("/scheduleShift")
	public ResponseEntity<ApiResponse<?>> scheduleShift(
			@RequestParam String pt,
			@RequestParam String gg,
			@RequestParam String dfdl,
			@RequestParam Timestamp time,
			@RequestParam(required = false) String lcp
	) {
		CompletableFuture.runAsync(
				() -> bpULCService.scheduleShift(
						pt, gg, dfdl, time, lcp
				)
		);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("BPPAssignTEtoCLAG")
	public ResponseEntity<ApiResponse<?>> bppAssignTEtoCLAG(
			@RequestParam Timestamp date,
			@RequestParam(value = "ust", required = false) List<String> usts,
			@RequestParam(value = "captype") String capType,
			@RequestParam(value = "gg", required = false) List<String> ggs,
			@RequestParam(value = "dfdl", required = false) List<String> dfdls,
			@RequestParam(value = "pt", required = false) List<String> pts
	) throws ParseException {
		if (capType.equals(CalendarPeriodTypeEnum.WEEK.getCode())) {
			bpClagClassgroupService.bppAssignTeToClagCAWK(date, usts, pts, ggs, dfdls);
		}
		if (capType.equals(CalendarPeriodTypeEnum.DAY.getCode())) {
			bpClagClassgroupService.bppAssignTeToClagCADY(date, usts, pts, ggs, dfdls);
		}
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("BPPAssignEMtoCLAG")
	public ResponseEntity<ApiResponse<?>> bppAssignEMtoCLAG(
			@RequestParam Timestamp date,
			@RequestParam(value = "ust", required = false) List<String> usts,
			@RequestParam(value = "captype") String capType,
			@RequestParam(value = "gg", required = false) List<String> ggs,
			@RequestParam(value = "dfdl", required = false) List<String> dfdls,
			@RequestParam(value = "pt", required = false) List<String> pts
	) throws ParseException {
		if (capType.equals(CalendarPeriodTypeEnum.WEEK.getCode())) {
			bpClagClassgroupService.bppAssignEmToClagCAWK(date, usts, pts, ggs, dfdls);
		}
		if (capType.equals(CalendarPeriodTypeEnum.DAY.getCode())) {
			bpClagClassgroupService.bppAssignEmToClagCADY(date, usts, pts, ggs, dfdls);
		}
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping("getULCSHs")
	public ResponseEntity<ApiResponse<GeneralPageResponse<ULCResponse>>> getULCSHs(
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "size", required = false, defaultValue = "20") int size,
			@RequestParam(value = "lct", required = false) String[] lctCodes,
			@RequestParam(value = "gg", required = false) String[] ggCodes,
			@RequestParam(value = "dfdl", required = false) String[] dfdlCodes,
			@RequestParam(value = "from", required = false) @DateStringFormat String from,
			@RequestParam(value = "to", required = false) @DateStringFormat String to
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getULCSHs(page, size,
				Objects.isNull(lctCodes) ? null : Arrays.asList(lctCodes)
				, Objects.isNull(ggCodes) ? null : Arrays.asList(ggCodes)
				, Objects.isNull(dfdlCodes) ? null : Arrays.asList(dfdlCodes),
				from, to)));
	}

	@PutMapping("publishULCSH")
	public ResponseEntity<ApiResponse<?>> publishULCSH(
			@RequestBody @Valid BaseRequestIds requestIds
	) {
		bpULCService.publishULCSH(requestIds.getIds());
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@GetMapping("/ulc/{id}")
	public ResponseEntity<ApiResponse<ULCDetailInfoResponse>> getULCDetail(
			@PathVariable("id") Integer id
	) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.getUlcDetail(id)));
	}

	@PostMapping("createCuiSTofULC")
	public ResponseEntity<ApiResponse<?>> createCuiSTofULC(
			@RequestParam String lcp,
			@RequestParam String cap
	) {
		bpULCService.createCuiULC(lcp, cap, USTEnum.ST.getName());
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("createCuiTETofULC")
	public ResponseEntity<ApiResponse<?>> createCuiTETofULC(
			@RequestParam String lcp,
			@RequestParam String cap
	) {
		bpULCService.createCuiULC(lcp, cap, USTEnum.TE.getName());
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("createCuiEMtofULC")
	public ResponseEntity<ApiResponse<?>> createCuiEMtofULC(
			@RequestParam String lcp,
			@RequestParam String cap
	) {
		bpULCService.createCuiULC(lcp, cap, USTEnum.CO.getName());
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("createCuiMainOfULC")
	public ResponseEntity<ApiResponse<?>> createCuiMainOfULC(
			@RequestParam String lcp,
			@RequestParam String cap
	) {
		bpULCService.createCuiMainOfULC(lcp, cap);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("/merge-ulc")
	public ResponseEntity<?> mergeULC(
			@RequestParam("gg") String gg,
			@RequestParam("dfdl") String dfdl,
			@RequestParam("cash") String cash,
			@RequestParam("isudlm") Boolean isudlm,
			@RequestParam("isugem") Boolean isugem
	) {

		if (isudlm.equals(isugem)) {
			throw new BadRequestException("isudlm must <> isugem");
		}

		bpULCService.mergeULC(gg, dfdl, calendarPeriodService.findByCode(cash), isudlm, isugem);
		return ResponseEntity.ok(null);
	}

	@PostMapping("/scheduleMPForOM")
	public ResponseEntity<?> scheduleMPForOM(@RequestParam String clagCode, @RequestParam String podCode)
			throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				bpULCService.scheduleMPForOM(clagCode, podCode);
			} catch (Exception e) {
				log.error("Error when run schedule MP for OM", e);
			}
		});
		return ResponseEntity.ok(null);
	}

	
}
