package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.dto.CalendarPeriodDTO;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.payload.request.CalendarPeriodFilterRequest;
import vn.edu.clevai.bplog.service.CalendarPeriodService;

import java.util.List;

@RestController
@RequestMapping("/cap-period")
@Slf4j
public class CalendarPeriodController {
	@Autowired
	private CalendarPeriodService calendarPeriodService;

	@PostMapping("/get-cap-by-time")
	public ResponseEntity<CalendarPeriod> getCAPByTime(@RequestBody CalendarPeriodFilterRequest request) {
		log.info("getCAPByTime {}", request.toString());
		return ResponseEntity.ok(calendarPeriodService.getCAPByTime(request.getTime(),
				request.getCapType()));
	}

	@PostMapping("/get-cap")
	public ResponseEntity<CalendarPeriod> getCAP(@RequestBody CalendarPeriodFilterRequest request) {
		log.info("getCAP {}", request.toString());
		return ResponseEntity.ok(calendarPeriodService.getCAP(request.getInputCap(), request.getCapType(), request.getCapNo()));
	}

	@PostMapping("/get-cawk")
	public ResponseEntity<CalendarPeriod> getCAWK(@RequestParam String cuwk) {
		log.info("getCAWK {}", cuwk);
		return ResponseEntity.ok(calendarPeriodService.getCAWK(cuwk));
	}

	@PostMapping("/get-cash")
	public ResponseEntity<CalendarPeriod> getCASH(@RequestParam String cady,
												  @RequestParam(name = "prd") String prd,
												  @RequestParam(name = "cash_start") String cashStart) {
		log.info("getCASH {} {} {}", cady, prd, cashStart);
		return ResponseEntity.ok(calendarPeriodService.getCASH(cady, prd, cashStart, "", "", ""));
	}

	@GetMapping("/find-by-type")
	public ResponseEntity<List<CalendarPeriodDTO>> findAllCalendarPeriod(
			@RequestParam(value = "types", defaultValue = "CAWK,CADY") List<String> types) {
		return ResponseEntity.ok(calendarPeriodService.findAllCalendarPeriod(types));
	}

	@GetMapping("/find-by-type-month")
	public ResponseEntity<List<CalendarPeriodDTO>> findAllCalendarPeriod(
			@RequestParam(value = "type") String type) {
		return ResponseEntity.ok(calendarPeriodService.findAllCalendarPeriodMonth(type));
	}



}
