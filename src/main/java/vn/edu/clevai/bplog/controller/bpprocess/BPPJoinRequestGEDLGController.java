package vn.edu.clevai.bplog.controller.bpprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.common.enumtype.LCPEnum;
import vn.edu.clevai.bplog.payload.request.bp.BPCuiEventRequest;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.sql.Timestamp;

@RestController
@RequestMapping({"/bpp-510"})
@Configurable
public class BPPJoinRequestGEDLGController {
	@Autowired
	CuiEventService cuiEventService;

	@PostMapping("/writeCUIEvent510")
	public ResponseEntity<ApiResponse<?>> writeCUIEvent510(@RequestBody BPCuiEventRequest request) {
		cuiEventService.writeCUIEvent510(request);
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PostMapping("/findCUIEvent")
	public ResponseEntity<ApiResponse<?>> findCUIEvent(@RequestBody BPCuiEventRequest request) {
		cuiEventService.findCUIEventJoinRequest(request);
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PostMapping("/BPPJoinRequest-GE-DLG")
	public ResponseEntity<?> bppJoinRequestGeDlg(
			@RequestParam Long xdeal,
			@RequestParam String xcady,
			@RequestParam Timestamp cuieActualtimeFet
	) {
		cuiEventService.bppJoinGEDLG(
				xdeal,
				xcady,
				cuieActualtimeFet,
				LCPEnum.DLG_90MI_F3_GE_45MI.getCode()
		);
		return ResponseEntity.ok(ApiResponse.success(true));
	}
}