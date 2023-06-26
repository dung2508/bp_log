package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.BPPService;
import vn.edu.clevai.common.api.constraint.DateStringFormat;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/bpp")
@Slf4j
public class BPPController {

	private final BPPService bppService;

	public BPPController(BPPService bppService) {
		this.bppService = bppService;
	}

	@PostMapping("bppAssignGG")
	public ResponseEntity<ApiResponse<?>> bppAssignGG(@RequestParam Long xdeal) {
		bppService.bppAssignGG(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppAssignCLAGPERM")
	public ResponseEntity<ApiResponse<?>> bppAssignCLAGPERM(@RequestParam Long xdeal) {
		bppService.bppAssignCLAGPERM(xdeal, null);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppAssignWSO")
	public ResponseEntity<ApiResponse<?>> bppAssignWSO(@RequestParam Long xdeal) {
		bppService.bppAssignWSO(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppAssignDFDL")
	public ResponseEntity<ApiResponse<?>> bppAssignDFDL(@RequestParam Long xdeal) {
		bppService.bppAssignDFDL(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppAssignStudent")
	public ResponseEntity<ApiResponse<?>> bppAssignStudent(@RequestParam Long xdeal) {
		bppService.bppAssignStudent(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppChangeGG")
	public ResponseEntity<ApiResponse<?>> bppChangeGG(@RequestParam Long xdeal) {
		bppService.bppChangeGG(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppChangeDFDL")
	public ResponseEntity<ApiResponse<?>> bppChangeDFDL(@RequestParam Long xdeal) {
		bppService.bppChangeDFDL(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppChangeWSO")
	public ResponseEntity<ApiResponse<?>> bppChangeWSO(@RequestParam Long xdeal) {
		bppService.bppChangeWSO(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppFixPod")
	public ResponseEntity<ApiResponse<?>> bppChangeWSOs(@RequestParam List<Long> xdeals) {
		xdeals.forEach(xdeal -> {
			try {
				bppService.bppPurchase(xdeal);
			} catch (Exception e) {
				log.error("bppChangeWSOs {}, {}", xdeal, e.getMessage());
			}
		});
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppPurchase")
	public ResponseEntity<ApiResponse<?>> bppPurchase(@RequestParam Long xdeal) {
		bppService.bppPurchase(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppRenewRepeat")
	public ResponseEntity<ApiResponse<?>> bppRenewRepeat(@RequestParam Long xdeal) {
		bppService.bppRenewRepeat(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppRenewCrossSell")
	public ResponseEntity<ApiResponse<?>> bppRenewCrossSell(@RequestParam Long xdeal) {
		bppService.bppRenewCrossSell(xdeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppRenewTransfer")
	public ResponseEntity<ApiResponse<?>> bppRenewTransfer
			(@RequestParam Long xdeal1,
			 @RequestParam(required = false) Timestamp startDateDeal1,
			 @RequestParam(required = false) Timestamp endDateDeal1, @RequestParam Long xdeal2) {
		bppService.bppRenewTransfer(xdeal1, startDateDeal1, endDateDeal1, xdeal2);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppRenewTopUp")
	public ResponseEntity<ApiResponse<?>> bppRenewTopUp
			(@RequestParam Long xdeal1,
			 @RequestParam(required = false) Timestamp startDateDeal1,
			 @RequestParam(required = false) Timestamp endDateDeal1, @RequestParam Long xdeal2) {
		bppService.bppRenewTopUp(xdeal1, startDateDeal1, endDateDeal1, xdeal2);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppDeferAfterSignup")
	public ResponseEntity<ApiResponse<?>> bppDeferAfterSignup
			(@RequestParam Long xdeal,
			 @RequestParam(required = false) Timestamp newStartDateDeal,
			 @RequestParam(required = false) Timestamp newEndDateDeal) {
		bppService.bppDeferAfterSignup(xdeal, newStartDateDeal, newEndDateDeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppDeferBeforeSignup")
	public ResponseEntity<ApiResponse<?>> bppDeferBeforeSignup
			(@RequestParam Long xdeal,
			 @RequestParam(required = false) Timestamp newStartDateDeal,
			 @RequestParam(required = false) Timestamp newEndDateDeal) {
		bppService.bppDeferBeforeSignup(xdeal, newStartDateDeal, newEndDateDeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppSuspend")
	public ResponseEntity<ApiResponse<?>> bppSuspend
			(@RequestParam Long xdeal,
			 @RequestParam(required = false) Timestamp newStartDateDeal,
			 @RequestParam(required = false) Timestamp newEndDateDeal) {
		bppService.bppSuspend(xdeal, newStartDateDeal, newEndDateDeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppUnSuspend")
	public ResponseEntity<ApiResponse<?>> bppUnSuspend
			(@RequestParam Long xdeal,
			 @RequestParam(required = false) Timestamp newStartDateDeal,
			 @RequestParam(required = false) Timestamp newEndDateDeal) {
		bppService.bppUnSuspend(xdeal, newStartDateDeal, newEndDateDeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppRefund")
	public ResponseEntity<ApiResponse<?>> bppRefund
			(@RequestParam Long xdeal,
			 @RequestParam(required = false) Timestamp newStartDateDeal,
			 @RequestParam(required = false) Timestamp newEndDateDeal) {
		bppService.bppRefund(xdeal, newStartDateDeal, newEndDateDeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppExtend")
	public ResponseEntity<ApiResponse<?>> bppExtend
			(@RequestParam Long xdeal,
			 @RequestParam(required = false) Timestamp newStartDateDeal,
			 @RequestParam(required = false) Timestamp newEndDateDeal) {
		bppService.bppExtend(xdeal, newStartDateDeal, newEndDateDeal);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping("bppAssignCLAGDYN")
	public ResponseEntity<ApiResponse<?>> bppAssignCLAGDYN(
			@RequestParam Long xdeal,
			@RequestParam @DateStringFormat(checkBlank = true) String xcady
	) throws ParseException {
		bppService.bppAssignCLAGDYN(xdeal, xcady);
		return ResponseEntity.ok(ApiResponse.success(null));
	}
}