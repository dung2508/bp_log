package vn.edu.clevai.bplog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.entity.projection.ScheduleMonthCalendarPJ;
import vn.edu.clevai.bplog.entity.projection.UlcScheduleShiftPJ;
import vn.edu.clevai.bplog.payload.request.FindPodByClagRequest;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.impl.CuiServiceImpl;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.api.model.MessageResponseDTO;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

	private final BpPodProductOfDealService podService;

	private final CuiServiceImpl cuiService;

	private final BpUniqueLearningComponentService learningComponentService;

	private final CalendarPeriodService calendarPeriodService;

	private final ScheduleMcService scheduleMcService;

	private final ScheduleWcService scheduleWcService;

	private final BpULCService bpULCService;


	@GetMapping("/findPodByClag")
	public ResponseEntity<List<BpPodProductOfDeal>> findPod(@RequestBody FindPodByClagRequest request) {
		return ResponseEntity.ok(podService.findPodCodeByClag(request.getClagCode(), request.getUst()));
	}


	@GetMapping("/schedule-month-calendar/preview")
	public ResponseEntity<Page<ScheduleMonthCalendarPJ>> findAllCuiPreview(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "pts", required = false) ArrayList<String> pts,
			@RequestParam(value = "ggs", required = false) ArrayList<String> ggs,
			@RequestParam(value = "dfdls", required = false) ArrayList<String> dfdls,
			@RequestParam(value = "cap") String capCode,
			@RequestParam(value = "lcl", defaultValue = "MN") String lcl,
			@RequestParam(value = "lck", defaultValue = "MC") String lck
	) {

		ScheduleRequest request = ScheduleRequest.builder()
				.page(page)
				.size(size)
				.pt(pts)
				.ggs(ggs)
				.dfdls(dfdls)
				.capCode(capCode)
				.lcl(lcl)
				.lck(lck)
				.build();

		return ResponseEntity.ok(cuiService.findAllByCondition(request));
	}

	@GetMapping("/schedule-week-calendar/preview")
	public ResponseEntity<Page<ScheduleMonthCalendarPJ>> findAllScheduleWeek(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "pts", required = false) ArrayList<String> pts,
			@RequestParam(value = "ggs", required = false) ArrayList<String> ggs,
			@RequestParam(value = "dfdls", required = false) ArrayList<String> dfdls,
			@RequestParam(value = "cap") String capCode,
			@RequestParam(value = "lck", defaultValue = "WC") String lck
	) {

		ScheduleRequest request = ScheduleRequest.builder()
				.page(page)
				.size(size)
				.pt(pts)
				.ggs(ggs)
				.dfdls(dfdls)
				.capCode(capCode)
				.lck(lck)
				.build();

		return ResponseEntity.ok(cuiService.findAllScheduleWeek(request));
	}


	@GetMapping("/schedule-shift/preview")
	public ResponseEntity<Page<UlcScheduleShiftPJ>> findAll(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "pt", required = false) ArrayList<String> pt,
			@RequestParam(value = "ggs", required = false) ArrayList<String> ggs,
			@RequestParam(value = "dfdls", required = false) ArrayList<String> dfdls,
			@RequestParam(value = "cady") String cady
	) {
		ScheduleRequest request = ScheduleRequest.builder()
				.page(page)
				.size(size)
				.pt(pt)
				.ggs(ggs)
				.dfdls(dfdls)
				.cady(cady)
				.build();
		return ResponseEntity.ok(learningComponentService.findAllByCondition(request));
	}

	@PostMapping("/shift/scheduleLock")
	public ResponseEntity<ApiResponse<MessageResponseDTO>> scheduleShiftLock(@RequestBody ScheduleRequest scheduleRequest) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.scheduleShiftLock(scheduleRequest)));
	}

	@PostMapping("/week/do-schedule")
	public ResponseEntity<ApiResponse<String>> scheduleWeekLock(@RequestBody @Valid ScheduleRequest scheduleRequest) {
		CompletableFuture.runAsync(() -> bpULCService.scheduleWeekLock(scheduleRequest));
		return ResponseEntity.ok(ApiResponse.success("Schedule is runing in background"));
	}

	@PostMapping("/shift/removeCacheSchedule")
	public ResponseEntity<ApiResponse<?>> removeCacheScheduleShift(@RequestBody ScheduleRequest scheduleRequest) {
		bpULCService.removeCacheScheduleShift(scheduleRequest);
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@PostMapping("/shift/convertToXLock")
	public ResponseEntity<ApiResponse<Boolean>> shiftConvertToX(@RequestBody ScheduleRequest scheduleRequest) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.convertBpToX(scheduleRequest)));
	}

	@PostMapping("/camn/scheduleLock")
	public ResponseEntity<ApiResponse<MessageResponseDTO>> scheduleCamnLock(@RequestBody ScheduleRequest scheduleRequest) {
		CalendarPeriod camn = calendarPeriodService.findByCode(scheduleRequest.getCapCode());
		return ResponseEntity.ok(ApiResponse.success(scheduleMcService.scheduleMcAll(scheduleRequest, camn)));
	}

	@PostMapping("/camn/removeCacheSchedule")
	public ResponseEntity<ApiResponse<?>> removeCacheScheduleMonth(@RequestBody ScheduleRequest scheduleRequest) {
		bpULCService.removeCacheScheduleMonth(scheduleRequest);
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@PostMapping("/camn/convertToXLock")
	public ResponseEntity<ApiResponse<Boolean>> camnConvertToX(@RequestBody ScheduleRequest scheduleRequest) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.convertBpToXMonth(scheduleRequest)));
	}

	@PostMapping("/cawk/convertToXLock")
	public ResponseEntity<ApiResponse<Boolean>> cawkConvertToX(@RequestBody ScheduleRequest scheduleRequest) {
		return ResponseEntity.ok(ApiResponse.success(bpULCService.convertBpToXWeek(scheduleRequest)));
	}

	@PostMapping("/schedule-wc")
	public ResponseEntity<ApiResponse<?>> scheduleWc(
			@RequestParam String pt,
			@RequestParam String gg,
			@RequestParam String dfdl,
			@RequestParam String cawk
	) {
		scheduleWcService.scheduleWCAll(pt, gg, dfdl, cawk);

		return ResponseEntity.ok(
				ApiResponse.success(
						"Scheduling wc in background with parameters: pt = " + pt
								+ ", gg = " + gg
								+ ", dfdl = " + dfdl
								+ ", cawk = " + cawk
				)
		);
	}
}
