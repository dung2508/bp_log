package vn.edu.clevai.bplog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.common.enumtype.ModifyTypeEnum;
import vn.edu.clevai.bplog.payload.request.teacher.ModifyTeacherRequest;
import vn.edu.clevai.bplog.service.ModifyService;
import vn.edu.clevai.common.api.model.ApiResponse;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/modify")
public class ModifyController {

	private final ModifyService modifyService;

	public ModifyController(ModifyService modifyService) {
		this.modifyService = modifyService;
	}

	@PutMapping("/student-CIB")
	public ResponseEntity<ApiResponse<?>> modifyStudent(
			@RequestParam(value = "old_pod_clag") String oldPodClag,
			@RequestParam(value = "new_pod_clag") String newPodClag
	) {
		CompletableFuture.runAsync(() -> {
			modifyService.bppSyncST(oldPodClag, newPodClag, ModifyTypeEnum.CHANGE_CIB.getName());
		});
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PutMapping("/teacher-CIB")
	public ResponseEntity<ApiResponse<?>> modifyTeacher(
			@RequestBody @NotNull List<ModifyTeacherRequest> request
	) {
		CompletableFuture.runAsync(() -> {
			request.forEach(req -> {
				modifyService.bppSyncTE(
						req.getClag(), req.getOldUsi(), req.getNewUsi(), req.getCady());
			});
		});
		return ResponseEntity.ok(ApiResponse.success("Running"));
	}
}
