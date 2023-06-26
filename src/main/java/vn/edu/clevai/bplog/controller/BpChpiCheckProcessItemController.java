package vn.edu.clevai.bplog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.payload.request.InitiateCheckingRequest;
import vn.edu.clevai.bplog.service.BpChpiCheckProcessItemService;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/bp-chpi-check-process-items")
@RequiredArgsConstructor
public class BpChpiCheckProcessItemController {

	private final BpChpiCheckProcessItemService bpChpiCheckProcessItemService;

	@PostMapping("/initiateChecking")
	public ResponseEntity<ApiResponse<List<String>>> initiateChecking(@RequestBody InitiateCheckingRequest request) throws Exception {
		return ResponseEntity.ok(ApiResponse.success(bpChpiCheckProcessItemService.prepareChecking(
				request.getCuiEvent(),
				request.getCti1(),
				request.getCti2(),
				request.getCti3(),
				request.getToSendEmail()
		)));
	}

}