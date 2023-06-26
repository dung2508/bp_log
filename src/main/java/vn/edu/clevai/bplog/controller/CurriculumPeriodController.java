package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.payload.request.CombineCodeRequest;
import vn.edu.clevai.bplog.payload.request.CurriculumPeriodFilterRequest;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;

@RestController
@RequestMapping("/curr-period")
@Slf4j
public class CurriculumPeriodController {
	@Autowired
	private CurriculumPeriodService curriculumPeriodService;

	@PostMapping("/combine-code")
	public ResponseEntity<?> combineCode(@RequestBody CombineCodeRequest request) {
		log.info("Combine code request {}", request.toString());
		return ResponseEntity.ok(curriculumPeriodService.combineCurrCode(request));
	}

	@PostMapping("/break-code")
	public ResponseEntity<?> breakCurrCode(@RequestParam(name = "cup_code") String cupCode) {
		log.info("Break code request {}", cupCode);
		return ResponseEntity.ok(curriculumPeriodService.breakCurrCode(cupCode));
	}

	@PostMapping("/get-cuwk")
	public ResponseEntity<?> getCUWK(@RequestBody CurriculumPeriodFilterRequest request) {
		log.info("getCUWK {}", request.toString());
		return ResponseEntity.ok(curriculumPeriodService
				.getCUPByCAP(request.getCrpsCode(), request.getCapCode(), request.getCupType()));
	}
}
