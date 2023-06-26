package vn.edu.clevai.bplog.controller.bpprocess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.common.enumtype.LCPEnum;
import vn.edu.clevai.bplog.payload.request.bp.BPRequest;
import vn.edu.clevai.bplog.service.BpULCService;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.sql.Timestamp;

@RestController
@RequestMapping({"/bpp-120"})
@Configurable
public class BPPScheduleUGESController {
	@Autowired
	CuiEventService cuiEventService;
	@Autowired
	BpULCService bpULCService;

	@PostMapping("/createUGES")
	public ResponseEntity<ApiResponse<?>> createUDLC(@RequestBody BPRequest request) {
		bpULCService.bppScheduleUGES(request.getBpUlcRequest(),
				request.getBpGetContentsRequest(), request.getBpScheduleUssRequest());
		return ResponseEntity.ok(ApiResponse.success("OK"));
	}

	@PostMapping("/BPPJoinRequest-GE-GES")
	public ResponseEntity<?> bppJoinRequestGeGes(
			@RequestParam Long xdeal,
			@RequestParam String xcady,
			@RequestParam Timestamp cuieActualtimeFet
	) {
		cuiEventService.bppJoinGEGES(
				xdeal,
				xcady,
				cuieActualtimeFet,
				LCPEnum.GES_75MI_F1_GE_75MI.getCode()
		);

		return ResponseEntity.ok(ApiResponse.success(true));
	}
}
