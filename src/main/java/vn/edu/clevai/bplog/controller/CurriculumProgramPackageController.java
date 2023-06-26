package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.entity.CurriculumProgramPackage;
import vn.edu.clevai.bplog.payload.request.CurriculumProgramPackageRequest;
import vn.edu.clevai.bplog.service.CurriculumProgramPackageService;

@RestController
@RequestMapping("/curriculum-program-package")
@Slf4j
public class CurriculumProgramPackageController {
	@Autowired
	private CurriculumProgramPackageService curriculumProgramPackageService;

	@PostMapping
	public ResponseEntity<CurriculumProgramPackage>
	getCPPResponseByRequestFilter(@RequestBody CurriculumProgramPackageRequest request) {
		log.info("CURRICULUM_PROGRAM_PACKAGE_FILTER_WITH_PARAM: {}", request.toString());
		return ResponseEntity.ok((curriculumProgramPackageService
				.getCrppByAccYearAndTimeAndPt(request.getAyCode(), request.getTime(), request.getPtCode())));
	}
}
