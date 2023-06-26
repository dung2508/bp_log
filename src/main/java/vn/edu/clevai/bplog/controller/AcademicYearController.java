package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.AcademicYearService;
import vn.edu.clevai.common.proxy.bplog.payload.response.AcademicYearResponse;

import java.util.List;

@RestController
@RequestMapping("/academic-year")
@Slf4j
public class AcademicYearController {
	@Autowired
	private AcademicYearService academicYearService;

	@GetMapping()
	public ResponseEntity<List<AcademicYearResponse>> getAcademicYear() {
		log.debug("Get academic year controller");
		return academicYearService.getAllAcademicYear();
	}
}
