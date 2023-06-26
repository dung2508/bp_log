package vn.edu.clevai.bplog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.service.CurriculumImportingService;
import vn.edu.clevai.bplog.service.CurriculumService;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.scheduling.payload.request.CurriculumFilterRequest;
import vn.edu.clevai.common.proxy.scheduling.payload.response.CurriculumResponse;

import java.util.List;

@RestController
@RequestMapping("/curriculums")
@RequiredArgsConstructor
public class CurriculumController {

	private final CurriculumService curriculumService;
	private final CurriculumImportingService curriculumImportingService;

	@PostMapping
	public ResponseEntity<List<CurriculumResponse>> getCurriculums(@RequestBody CurriculumFilterRequest request) {
		return curriculumService.getCurriculums(request);
	}

	@PutMapping("/import-from-sheet")
	public ResponseEntity<Object> importFromSheet() {
		curriculumImportingService.importFromSheet();
		return ResponseEntity.ok(ApiResponse.success(null));
	}

}