package vn.edu.clevai.bplog.controller.student;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.payload.response.cti.LeaningObjectResponse;
import vn.edu.clevai.bplog.payload.response.student.StudentLearningPackageResponse;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.common.api.constant.ClevaiConstant;
import vn.edu.clevai.common.api.controller.BaseController;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectWithRewardResponse;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class ContentItemController extends BaseController {

	private final CuiService cuiService;

	@GetMapping("/learning-object")
	public ResponseEntity<ApiResponse<GeneralPageResponse<LeaningObjectResponse>>> getLearningPackageAndLo(
			@RequestParam String pt, @RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "page", required = false, defaultValue = ClevaiConstant.DEFAULT_STARTER_PAGE) int page,
			@RequestParam(value = "size", required = false, defaultValue = ClevaiConstant.DEFAULT_EXERCISE_PAGE_SIZE) int size) {
		return ResponseEntity.ok(ApiResponse.success(
				GeneralPageResponse.toResponse(cuiService.getHomeworkCtiFromSt(getUserName(), pt, id, page, size))));
	}

	@GetMapping("/home-work")
	public ResponseEntity<ApiResponse<List<StudentLearningPackageResponse>>> getAllLearningPackage(
			@RequestParam String pt
	) {
		return ResponseEntity.ok(ApiResponse.success(
				cuiService.getAllLearningPackage(getUserName(), pt)
		));
	}

	@GetMapping("/learning-objects/search")
	public ResponseEntity<GeneralPageResponse<LearningObjectWithRewardResponse>> search(
			@RequestParam(name = "keyword") String keyword,
			@RequestParam(name = "pt") String pt,
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			@RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		return ResponseEntity.ok(cuiService.search(keyword, pt, getUserName(), page, size));
	}
}
