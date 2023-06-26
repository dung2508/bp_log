package vn.edu.clevai.bplog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.BpUsiUserItemService;
import vn.edu.clevai.bplog.service.data.importation.migrate.Cat5MigrateService;
import vn.edu.clevai.common.api.model.ApiResponse;

@RestController
@RequestMapping("/migrate")
@Slf4j
public class MigrateController {

	@Autowired
	private Cat5MigrateService service;

	@Autowired
	private BpUsiUserItemService userItemService;

	private final String csvFilePath = "/Volumes/Working/clevai/git/clevai-bp-log-service/st-migrate/cats5-student-all.csv";

	@PostMapping("/st-pod")
	public ResponseEntity<ApiResponse<String>> migrateSTAndPod() {
		log.info("Start migrate T0 for ST and POD");
		service.doMigrate(csvFilePath);
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@PostMapping("/te-role")
	public ResponseEntity<?> migrateTeRole() {
		log.info("Start migrate TE role");
		userItemService.migrateTERole();
		return ResponseEntity.ok("OK");
	}

}
