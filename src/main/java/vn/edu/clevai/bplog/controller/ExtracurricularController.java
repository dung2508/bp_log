package vn.edu.clevai.bplog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.ExtracurricularService;
import vn.edu.clevai.common.proxy.scheduling.payload.request.ExtracurricularFilterRequest;
import vn.edu.clevai.common.proxy.scheduling.payload.response.ExtracurricularResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/extracurriculars")
@RequiredArgsConstructor
public class ExtracurricularController {

	private final ExtracurricularService extracurricularService;

	@GetMapping
	public ResponseEntity<List<ExtracurricularResponse>> getExtracurriculars(
			@RequestParam(value = "grade-id", required = false) Long gradeId,
			@RequestParam("start-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("end-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		log.debug("Get Extra curriculars");
		return extracurricularService.getExtracurriculars(ExtracurricularFilterRequest.builder().gradeId(gradeId)
				.startTime(startDate.atTime(LocalTime.MIN)).endTime(endDate.atTime(LocalTime.MAX)).build());
	}

}