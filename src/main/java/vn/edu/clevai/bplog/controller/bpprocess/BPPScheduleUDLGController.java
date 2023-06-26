package vn.edu.clevai.bplog.controller.bpprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.payload.request.bp.BPRequest;
import vn.edu.clevai.bplog.service.BpULCService;
import vn.edu.clevai.common.api.model.ApiResponse;

@RestController
@RequestMapping({"/bpp-110"})
@Configurable
public class BPPScheduleUDLGController {

	@Autowired
	private BpULCService ulcService;

	@PostMapping("/scheduleUDLGUDL")
	public ResponseEntity<ApiResponse<?>> scheduleUDLGUDL(@RequestBody BPRequest request) {
		ulcService.scheduleUDLGUDL(request.getBpUlcRequest(),
				request.getBpGetContentsRequest(), request.getBpScheduleUssRequest());
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PostMapping("/scheduleUDLGUGE")
	public ResponseEntity<ApiResponse<?>> scheduleUDLGUGE(@RequestBody BPRequest request) {
		ulcService.scheduleUDLGUGE(request.getBpUlcRequest(),
				request.getBpGetContentsRequest(), request.getBpScheduleUssRequest());
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}
}
