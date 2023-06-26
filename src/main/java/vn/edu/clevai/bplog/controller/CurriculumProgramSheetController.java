package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.entity.CurriculumProgramSheet;
import vn.edu.clevai.bplog.payload.request.CurriculumProgramSheetRequest;
import vn.edu.clevai.bplog.service.CurriculumProgramSheetService;

@RestController
@RequestMapping("/curriculum-program-sheet")
@Slf4j
public class CurriculumProgramSheetController {
	@Autowired
	private CurriculumProgramSheetService curriculumProgramSheetService;

	@PostMapping
	public ResponseEntity<CurriculumProgramSheet>
	getCPSResponseByNameAndCode(@RequestBody CurriculumProgramSheetRequest request) {
		log.info("CURRICULUM_PROGRAM_SHEET_FILTER_WITH_PARAM: {}", request.toString());
		return ResponseEntity.ok(curriculumProgramSheetService.getCrps(request.getCrppCode(), request.getGgCode()));
	}
}
