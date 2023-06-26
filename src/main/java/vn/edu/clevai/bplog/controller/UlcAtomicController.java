package vn.edu.clevai.bplog.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.payload.request.ChangeStatusCuiEventRequest;
import vn.edu.clevai.bplog.payload.request.SetClagUlcRequest;
import vn.edu.clevai.bplog.payload.request.SetPublishedUSHRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPCuiEventRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPRequest;
import vn.edu.clevai.bplog.service.BpULCService;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCLAGULCResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCUIEventResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCUIResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpULCResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ulc-atomic")
public class UlcAtomicController {

	@Autowired
	private BpULCService ulcService;

	@Autowired
	private CuiEventService cuiEventService;

	@Autowired
	private ModelMapper mapper;

	@GetMapping("/getULC")
	public ResponseEntity<ApiResponse<BpULCResponse>> getUlc(
			@RequestParam(value = "clag_code") String clagCode,
			@RequestParam(value = "CalPeriod_code") String capCode,
			@RequestParam(value = "lcp_code") String lcpCode
	) {
		return ResponseEntity.ok(ApiResponse.success(ulcService.getUlc(clagCode, capCode, lcpCode)));
	}

	@GetMapping("/getCUI")
	public ResponseEntity<ApiResponse<BpCUIResponse>> getCui(
			@RequestParam(value = "ulc_code") String ulcCode,
			@RequestParam(value = "usi_code") String usiCode
	) {
		return ResponseEntity.ok(ApiResponse.success(cuiEventService.getCui(ulcCode, usiCode)));
	}

	@GetMapping("/getCUIEvents")
	public ResponseEntity<ApiResponse<Map<String, Integer>>> getCuiEvents(
			@RequestParam(value = "cui_code") String cuiCode
	) {
		return ResponseEntity.ok(ApiResponse.success(cuiEventService.bpGetCuiEvents(cuiCode)));
	}

	@PutMapping("/SetCLAG-ULC")
	public ResponseEntity<ApiResponse<BpCLAGULCResponse>> setClagUlc(
			@RequestBody SetClagUlcRequest request) throws Exception {
		return ResponseEntity.ok(ApiResponse.success(
				ulcService.createOrUpdateClagUlc(request.getClagCode(), request.getUlcCode())
		));
	}

	@PostMapping("/createCUIEvent")
	public ResponseEntity<?> createCuiEvent(@RequestBody BPCuiEventRequest request) {
		cuiEventService.createCuiEvent(request);
		return ResponseEntity.ok("");
	}

	@PutMapping("/writeEventPlanTime")
	public ResponseEntity<ApiResponse<BpCUIEventResponse>> writeEventPlanTime(
			@RequestParam(value = "cuie_code") String cuieCode,
			@RequestParam(value = "event_plan_time") Integer eventPlanTime,
			@RequestParam(value = "cass_code") String cassCode
	) {
		return ResponseEntity.ok(ApiResponse.success(
				cuiEventService.writeEventPlanTime(cuieCode, eventPlanTime, cassCode)
		));
	}

	@GetMapping("/findCUIEventsByPlanTime")
	public ResponseEntity<ApiResponse<List<BpCUIEventResponse>>> findCuiEventsByPlanTime() {
		return ResponseEntity.ok(ApiResponse.success(cuiEventService.findCuiEventsByPlanTime()));
	}

	@PutMapping("/writeCUIEventActualTime")
	public ResponseEntity<ApiResponse<?>> writeCuiEventActualTime(
			@RequestParam(value = "cuie_code") String cuieCode
	) {
		return ResponseEntity.ok(ApiResponse.success(cuiEventService.writeEventActualTime(cuieCode)));
	}

	@GetMapping("/triggerPlannedCUIEvents")
	public ResponseEntity<?> triggerPlannedCuiEvents() {
		cuiEventService.triggerPlannedCuiEvents();
		return ResponseEntity.ok("OK");
	}

	@PostMapping("/planCuiEvents")
	public ResponseEntity<ApiResponse<List<BpCUIEventResponse>>> planCuiEvents(@RequestBody BPRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(
				cuiEventService.planCuiEvents(request.getBpCuiEventRequest(), request.getBpScheduleUssRequest())
		));
	}

	@PutMapping("/publish")
	public ResponseEntity<ApiResponse<BpCUIEventResponse>> publishCuiEvent(
			@RequestBody ChangeStatusCuiEventRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(
				mapper.map(cuiEventService.changeStatusCuiEvent(request.getCuiEventCode(), request.getPublishStatus())
						, BpCUIEventResponse.class)
		));
	}

	@PutMapping("setPublishedUSH")
	public ResponseEntity<ApiResponse<?>> setPublishedUSH(
			@RequestBody SetPublishedUSHRequest request
	) {
		ulcService.setPublishedUSH(request.getXdsc(), request.isPublished());
		return ResponseEntity.ok(ApiResponse.success(null));
	}

}
