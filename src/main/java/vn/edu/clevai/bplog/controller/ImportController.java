package vn.edu.clevai.bplog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.data.importation.ImportService;
import vn.edu.clevai.bplog.service.data.importation.MultipleImportService;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

	@Autowired
	@Qualifier("CrppImportingServiceImpl")
	private ImportService crppImportService;

	@Autowired
	@Qualifier("AbliSheetImportingServiceImpl")
	private ImportService abliImportService;

	@Autowired
	@Qualifier("CoqSheetImportingServiceImpl")
	private ImportService coqImportService;

	@Autowired
	@Qualifier("SSLSheetImportingServiceImpl")
	private ImportService sslImportService;

	@Autowired
	@Qualifier("UsidRegisterSO5Impl")
	private ImportService UsidRegisterSO5Impl;

	@Autowired
	@Qualifier("TeachingProgramImportServiceImpl")
	private ImportService teachingProgramImportServiceImpl;

	@Autowired
	@Qualifier("MultipleImportService")
	private MultipleImportService multipleImportService;

	@Autowired
	@Qualifier("CrppMpImportingServiceImpl")
	private ImportService crppMpImportingService;

	@Autowired
	@Qualifier("CtiPcImportService")
	private ImportService ctiPcImportService;


	@PutMapping("/crpp-sheet")
	public ResponseEntity<Object> importABLSFromSheet(@RequestParam String url) throws Exception {
		crppImportService.doImport("", "", url);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PutMapping("/abli-sheet")
	public ResponseEntity<Object> importABLIFromSheet(@RequestParam String sheetCode,
													  @RequestParam String name,
													  @RequestParam String url) throws Exception {
		abliImportService.doImport(sheetCode, name, url);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PutMapping("/coq-sheet")
	public ResponseEntity<Object> importCoqFromSheet(@RequestParam String sheetCode,
													 @RequestParam String name,
													 @RequestParam String url) throws Exception {
		coqImportService.doImport(sheetCode, name, url);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PutMapping("/cti-pc-sheet")
	public ResponseEntity<Object> importCtiPcFromSheet(@RequestParam String sheetCode,
													   @RequestParam String name,
													   @RequestParam String url) throws Exception {
		ctiPcImportService.doImport(sheetCode, name, url);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PutMapping("/ssl-sheet")
	public ResponseEntity<Object> importSSLFromSheet(@RequestParam String url) throws Exception {
		sslImportService.doImport("", "", url);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PutMapping("/crpp-mp-sheet")
	public ResponseEntity<Object> importOMFromSheet(@RequestParam String sheetCode,
													@RequestParam String name,
													@RequestParam String url) throws Exception {
		crppMpImportingService.doImport(sheetCode, name, url);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PutMapping("/bpe-register5-SO")
	public ResponseEntity<Object> importRegisterSO5(@RequestParam String url) throws Exception {
		UsidRegisterSO5Impl.doImport("", "", url);
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@PutMapping("/by-name")
	public ResponseEntity<Object> multipleImport(@RequestParam String url,
												 @RequestParam List<String> sheetNames,
												 @RequestParam String importType) {

		CompletableFuture.runAsync(() -> multipleImportService.importList(url, sheetNames, importType));
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}
}