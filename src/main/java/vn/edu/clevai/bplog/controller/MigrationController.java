package vn.edu.clevai.bplog.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.payload.request.MigrationRequest;
import vn.edu.clevai.bplog.service.BpUsiUserItemService;
import vn.edu.clevai.bplog.service.PermanentClagMigrationService;
import vn.edu.clevai.bplog.service.PodService;
import vn.edu.clevai.bplog.service.TeacherPodMigrationService;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.request.MigratePodWsoRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.MigrationResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/migrations")
public class MigrationController {

	@Autowired
	private PodService podService;

	@Autowired
	private BpUsiUserItemService bpUsiUserItemService;

	@Autowired
	private PermanentClagMigrationService permanentClagMigrationService;

	@Autowired
	private TeacherPodMigrationService teacherPodMigrationService;

	@PutMapping("/MigratePOD")
	public ResponseEntity<String> migratePod(@RequestParam(value = "deal_id_lt") Long dealIdLt) throws Exception {
		podService.migratePod(dealIdLt);
		return ResponseEntity.ok("SUCCESS");
	}

	@PutMapping(value = "/MigratePODTE", params = {"username"})
	public ResponseEntity<ApiResponse<List<BpPODResponse>>> migratePodTe(
			@RequestParam String username,
			@RequestParam String ust
	) {
		return ResponseEntity.ok(ApiResponse.success(teacherPodMigrationService.migrateSingleTeacher(username, ust)));
	}

	@PutMapping(value = "/MigratePODTE", params = {"page", "size"})
	public ResponseEntity<?> migrateMultiplePodTe(
			@RequestParam Integer page, @RequestParam Integer size) {

		Map<String, String> mdc = MDC.getCopyOfContextMap();

		CompletableFuture.runAsync(
				() -> {
					MDC.setContextMap(mdc);
					teacherPodMigrationService.migrateTeacherPods(page - 1, size);
				}
		);

		return ResponseEntity.ok(
				ApiResponse.success("Migrating teacher pods, please check data in DB ")
		);
	}


	@PutMapping(value = "/MigratePODTE")
	public ResponseEntity<?> migratePodTe() {

		Map<String, String> mdc = MDC.getCopyOfContextMap();

		CompletableFuture.runAsync(
				() -> {
					MDC.setContextMap(mdc);

					teacherPodMigrationService.migrateAllTeacherPods();
				}
		);

		return ResponseEntity.ok(
				ApiResponse.success("Migrating teacher pods, please check data in DB ")
		);

	}

	@PutMapping("/MigratePOD-WSO")
	public ResponseEntity<String> migratePodWso(@RequestBody MigratePodWsoRequest request) {
		podService.migratePodWso(request.getFromId(), request.getToId(), request.getPageSize());
		return ResponseEntity.ok("SUCCESS");
	}


	@PutMapping("/MigratePOD-DFDL")
	public ResponseEntity<MigrationResponse> migratePodDfdl(@RequestBody MigrationRequest request) {
		request.setFromId(ObjectUtils.defaultIfNull(request.getFromId(), 0L));
		request.setToId(ObjectUtils.defaultIfNull(request.getToId(), Long.MAX_VALUE));
		request.setPageSize(ObjectUtils.defaultIfNull(request.getPageSize(), 1000));
		Map<String, String> mdc = MDC.getCopyOfContextMap();
		CompletableFuture.runAsync(() -> {
			MDC.setContextMap(mdc);
			podService.migratePodDfdl(request);
		});
		return ResponseEntity.ok(MigrationResponse.builder()
				.processId(MDC.get("X-B3-TraceId"))
				.build());
	}

	@PutMapping("/MigrateST-GG")
	public ResponseEntity<MigrationResponse> migrateStGg(@RequestBody MigrationRequest request) {
		request.setFromId(ObjectUtils.defaultIfNull(request.getFromId(), 0L));
		request.setToId(ObjectUtils.defaultIfNull(request.getToId(), Long.MAX_VALUE));
		request.setPageSize(ObjectUtils.defaultIfNull(request.getPageSize(), 1000));
		Map<String, String> mdc = MDC.getCopyOfContextMap();
		CompletableFuture.runAsync(() -> {
			MDC.setContextMap(mdc);
			bpUsiUserItemService.migrateStGg(request);
		});
		return ResponseEntity.ok(MigrationResponse.builder()
				.processId(MDC.get("X-B3-TraceId"))
				.build());
	}

	@PostMapping(value = "/Migrate-CLAG-PERM", params = "xclass")
	public ResponseEntity<ApiResponse<BpClagClassgroupResponse>> migrateSingleClagPerm(@RequestParam String xclass) {
		return ResponseEntity.ok(ApiResponse.success(permanentClagMigrationService.migrateClag(xclass)));
	}

	@PostMapping(value = "/Migrate-CLAG-PERM", params = {"page", "size"})
	public ResponseEntity<?> migrateMultiplePermanentClags(@RequestParam Integer page, @RequestParam Integer size) {

		Map<String, String> mdc = MDC.getCopyOfContextMap();

		CompletableFuture.runAsync(
				() -> {
					MDC.setContextMap(mdc);
					permanentClagMigrationService.migrateClagPage(page, size);
				}
		);

		return ResponseEntity.ok(
				ApiResponse.success("Migrating permanent clags, please check data in DB ")
		);
	}

	@PostMapping(value = "/Migrate-CLAG-PERM")
	public ResponseEntity<?> migrateAllPermanentClags() {
		Map<String, String> mdc = MDC.getCopyOfContextMap();

		CompletableFuture.runAsync(
				() -> {
					MDC.setContextMap(mdc);
					permanentClagMigrationService.migrateAllClags();
				}
		);

		return ResponseEntity.ok(
				ApiResponse.success("Migrating permanent clags, please check data in DB ")
		);
	}

	@PutMapping("/MigratePOD-CLAGPERM")
	public ResponseEntity<MigrationResponse> migratePodClagperm(@RequestBody MigrationRequest request) {
		request.setFromId(ObjectUtils.defaultIfNull(request.getFromId(), 0L));
		request.setToId(ObjectUtils.defaultIfNull(request.getToId(), Long.MAX_VALUE));
		request.setPageSize(ObjectUtils.defaultIfNull(request.getPageSize(), 1000));
		Map<String, String> mdc = MDC.getCopyOfContextMap();
		CompletableFuture.runAsync(() -> {
			MDC.setContextMap(mdc);
			podService.migratePodClagperm(request);
		});
		return ResponseEntity.ok(MigrationResponse.builder()
				.processId(MDC.get("X-B3-TraceId"))
				.build());
	}

}
