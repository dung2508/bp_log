package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.payload.request.BpChliUpdateRequest;
import vn.edu.clevai.bplog.service.BpChService;
import vn.edu.clevai.bplog.service.BpService;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpChliResponse;

import javax.mail.MessagingException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/checking")
@Slf4j
public class CHController {

	@Autowired
	private BpChService chService;

	@Autowired
	private BpService bpService;

	@GetMapping("/chli/{chli_code}")
	@WriteUnitTestLog
	public ResponseEntity<ApiResponse<BpChliResponse>> getAllChltFromChli(@PathVariable("chli_code") String chliCode) {
		log.debug("Get all CHLT from chli code {}", chliCode);
		return ResponseEntity.ok(ApiResponse.success(chService.getCHLTFromCHLI(chliCode)));
	}

	@PostMapping("/chli/update")
	@WriteUnitTestLog
	public ResponseEntity<ApiResponse<String>> updateChli(@RequestBody @NotEmpty List<BpChliUpdateRequest> requests) {
		chService.updateByListChli(requests);
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@GetMapping("/assign")
	public ResponseEntity<ApiResponse<String>> bpAssign(
			@RequestParam(value = "CHSI_code", required = false) String chsiCode,
			@RequestParam(value = "CHRI_code", required = false) String chriCode) {
		return ResponseEntity.ok(ApiResponse.success(bpService.bpAssignData(chsiCode, chriCode)));
	}

	@GetMapping("/send-email")
	public ResponseEntity<ApiResponse<String>> bpSendEmail(
			@RequestParam(value = "CHSI_code", required = false) String chsiCode,
			@RequestParam(value = "CHRI_code", required = false) String chriCode,
			@RequestParam(value = "CHII_code", required = false) String chiiCode) throws MessagingException {
		bpService.bpSendEmail(chsiCode, chriCode);
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PostMapping("/assign-chri/{chpi_code}")
	public ResponseEntity<ApiResponse<String>> assignChri(@PathVariable("chpi_code") @NotBlank String chpiCode)
			throws Exception {
		bpService.bpAssignChri(chpiCode);
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}
}
