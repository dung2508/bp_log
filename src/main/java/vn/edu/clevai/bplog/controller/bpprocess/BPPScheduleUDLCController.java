package vn.edu.clevai.bplog.controller.bpprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.payload.request.bp.BPRequest;
import vn.edu.clevai.bplog.service.BpULCService;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.sql.Timestamp;

@RestController
@RequestMapping({"/bpp-100"})
@Configurable
public class BPPScheduleUDLCController {
	@Autowired
	BpULCService bpULCService;

	@Autowired
	private CuiEventService cuiEventService;

	@PostMapping("/createUDLC")
	public ResponseEntity<ApiResponse<?>> createUDLC(@RequestBody BPRequest request) {
		bpULCService.bppScheduleUDLC(request.getBpUlcRequest(),
				request.getBpGetContentsRequest(), request.getBpScheduleUssRequest());
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PostMapping("/bppScheduleUDLC1")
	public ResponseEntity<ApiResponse<?>> bppScheduleUDLC1(@RequestBody BPRequest request) {
		bpULCService.bppScheduleUDLC1(request.getBpUlcRequest(),
				request.getBpGetContentsRequest(), request.getBpScheduleUssRequest());
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PutMapping("BPPJoinRequest-DL")
	public ResponseEntity<ApiResponse<?>> bppJoinDL(
			String xst,
			Timestamp eventactualtime_fet
	) {
		cuiEventService.bppJoinDL(xst, eventactualtime_fet);
		return ResponseEntity.ok(ApiResponse.success(null));
	}
}
