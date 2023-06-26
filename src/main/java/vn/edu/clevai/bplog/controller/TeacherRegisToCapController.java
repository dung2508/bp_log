package vn.edu.clevai.bplog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.payload.request.filter.TeacherRegisterToCapAssigneeFilter;
import vn.edu.clevai.bplog.payload.request.teacher.RegisToCapPublishRequest;
import vn.edu.clevai.bplog.payload.response.BPProductGradeClassLevelResponse;
import vn.edu.clevai.bplog.payload.response.ChildResponse;
import vn.edu.clevai.bplog.payload.response.teacher.tocap.*;
import vn.edu.clevai.bplog.service.TeacherRegisToCapService;
import vn.edu.clevai.common.api.controller.BaseController;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.api.model.GeneralPageResponse;

import java.util.List;

@RestController
@RequestMapping("/register-to-cap")
public class TeacherRegisToCapController extends BaseController {

	@Autowired
	private TeacherRegisToCapService regisToCapService;

	@GetMapping("/gte-quantity")
	public ResponseEntity<ApiResponse<List<QuantityResponse>>> listGteQuantity(
			@RequestParam(value = "productId", required = true) Integer productId, @RequestParam(value = "gradeId", required = false) Integer gradeId,
			@RequestParam(value = "classLevelId", required = false) Integer classLevelId, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam(value = "subjectId", required = false) Integer subjectId) {
		return ResponseEntity.ok(ApiResponse.success(
				regisToCapService.gteQuantity(productId, gradeId, classLevelId, startDate, endDate, subjectId)));

	}

	@GetMapping("/dte-quantity")
	public ResponseEntity<ApiResponse<List<QuantityResponse>>> listDteQuantity(
			@RequestParam(value = "productId", required = true) Integer productId, @RequestParam(value = "gradeId", required = false) Integer gradeId,
			@RequestParam(value = "classLevelId", required = false) Integer classLevelId, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam(value = "subjectId", required = false) Integer subjectId) {
		return ResponseEntity.ok(ApiResponse.success(
				regisToCapService.dteQuantity(productId, gradeId, classLevelId, startDate, endDate, subjectId)));
	}

	@GetMapping("/qo-quantity")
	public ResponseEntity<ApiResponse<List<QuantityResponse>>> listQoQuantity(
			@RequestParam(value = "productId", required = true) Integer productId, @RequestParam(value = "gradeId", required = false) Integer gradeId,
			@RequestParam(value = "classLevelId", required = false) Integer classLevelId, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam(value = "subjectId", required = false) Integer subjectId) {
		return ResponseEntity.ok(ApiResponse.success(
				regisToCapService.qoQuantity(productId, gradeId, classLevelId, startDate, endDate, subjectId)));
	}

	@GetMapping("/lte-quantity")
	public ResponseEntity<ApiResponse<List<QuantityResponse>>> listLteQuantity(
			@RequestParam(value = "productId", required = false) Integer productId, @RequestParam(value = "gradeId", required = false) Integer gradeId,
			@RequestParam(value = "classLevelId", required = false) Integer classLevelId, @RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam(value = "subjectId", required = false) Integer subjectId) {
		return ResponseEntity.ok(ApiResponse.success(
				regisToCapService.lteQuantity(productId, gradeId, classLevelId, startDate, endDate, subjectId)));
	}

	@GetMapping("/assignee")
	public ResponseEntity<GeneralPageResponse<TeacherApprovedAndReportResponse>> getAssignee(
			@Validated TeacherRegisterToCapAssigneeFilter teacherRegisterToCapAssigneeFilter,
			Pageable pageable
	) {
		return ResponseEntity.ok(regisToCapService.getAssignee(
				teacherRegisterToCapAssigneeFilter,
				pageable
		));
	}

	@PutMapping("/publish")
	public ResponseEntity<ApiResponse<String>> publish(@RequestBody RegisToCapPublishRequest regisToCap) {
		String userName = getUserName();
		regisToCapService.publish(regisToCap, userName);
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@GetMapping("/list-product-grade-class-level")
	public ResponseEntity<ApiResponse<BPProductGradeClassLevelResponse>> getListProductGradeClassLevel(@RequestParam(required = false) String startDate) {
		return ResponseEntity.ok(ApiResponse.success(regisToCapService.getListProductGradeClassLevel(startDate)));
	}

	@GetMapping("/get-teacher-with-filter")
	public ResponseEntity<GeneralPageResponse<UserAccountResponse>> getTeacherWithFilter(String username, Pageable pageable) {
		return ResponseEntity.ok(regisToCapService.findAllTeacherWithFilter(username, pageable));
	}

	@GetMapping("/get-available-slots")
	public ResponseEntity<ApiResponse<BpTeachingScheduleAssigningSlotGroupResponse>> getAvailableSlots(String bpp) {
		return ResponseEntity.ok(ApiResponse.success(regisToCapService.getAvailableSlots(bpp)));
	}

	@GetMapping("/ust/all")
	public ResponseEntity<ApiResponse<List<UserTypeResponse>>> getAllUserType() {
		return ResponseEntity.ok(ApiResponse.success(regisToCapService.findAll()));
	}

	@GetMapping("/list-menu")
	public ResponseEntity<ApiResponse<List<ChildResponse>>> getAllMenu(){
		return ResponseEntity.ok(ApiResponse.success(regisToCapService.getAllMenu()));
	}
}
