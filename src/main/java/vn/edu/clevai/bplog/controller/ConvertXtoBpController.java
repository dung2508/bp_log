package vn.edu.clevai.bplog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.BpUSIDutyService;
import vn.edu.clevai.common.proxy.bplog.payload.request.SetUsiDutyFromXRequest;

@RequestMapping("/convert")
@RestController
public class ConvertXtoBpController {

	private final BpUSIDutyService usiDutyService;

	public ConvertXtoBpController(BpUSIDutyService usiDutyService) {
		this.usiDutyService = usiDutyService;
	}

	@PostMapping("/RegisterTEtoCAP")
	public ResponseEntity<String> registerTEtoCAP(@RequestBody SetUsiDutyFromXRequest request) {
		usiDutyService.registerTeToCap(request);
		return ResponseEntity.ok("OK");
	}

}
