package vn.edu.clevai.bplog.controller;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.payload.request.teacher.TeacherRegisterRequest;
import vn.edu.clevai.bplog.payload.response.teacher.GetAvailableResponse;
import vn.edu.clevai.bplog.payload.response.teacher.ProducGradeShiftResponse;
import vn.edu.clevai.bplog.payload.response.teacher.ProductGradeShiftDetailResponse;
import vn.edu.clevai.bplog.payload.response.teacher.YourselfResponse;
import vn.edu.clevai.bplog.service.TeacherTentativeService;
import vn.edu.clevai.common.api.controller.BaseController;
import vn.edu.clevai.common.api.model.ApiResponse;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/teacher-register")
public class TeacherTentativeController extends BaseController {

	private final String DEFAULT_HIGHLEVEL_MATH_CODE = "MA";
	private final TeacherTentativeService teacherRegistrationService;

	public TeacherTentativeController(TeacherTentativeService teacherRegistrationService) {
		this.teacherRegistrationService = teacherRegistrationService;
	}

	@GetMapping("/settings/all")
	public ResponseEntity<ApiResponse<ProducGradeShiftResponse>> loadSetting() {
		return null;
	}

	@GetMapping("/list-product-grade-shift/{highLevelSubjectCode}")
	public ResponseEntity<ApiResponse<ProductGradeShiftDetailResponse>> getProductGradeShiftInfo(
			@PathVariable(value = "highLevelSubjectCode") @DefaultValue(DEFAULT_HIGHLEVEL_MATH_CODE) String highLevelSubjectCode) {
		return ResponseEntity
				.ok(ApiResponse.success(teacherRegistrationService.getProductGradeShiftInfo(highLevelSubjectCode)));
	}

	@GetMapping("/yourself")
	public ResponseEntity<ApiResponse<YourselfResponse>> yourself() {
		return null;
	}

	@GetMapping("/get-available-slot")
	public ResponseEntity<ApiResponse<GetAvailableResponse>> getAvailableSlot(
			@RequestParam("start_time") @NotNull Long start, @RequestParam("end_time") @NotNull Long end) {
		return ResponseEntity
				.ok(ApiResponse.success(teacherRegistrationService.getAvailableSlot(getUserName(), start, end)));
	}

	@PostMapping("/save")
	public ResponseEntity<String> save(@RequestParam("start_time") @NotNull Long start,
									   @RequestParam("end_time") @NotNull Long end, @RequestBody TeacherRegisterRequest request) {
		teacherRegistrationService.doSave(getUserName(), start, end, request);
		return ResponseEntity.ok(ApiResponse.SUCCESS);
	}

}
