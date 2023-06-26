package vn.edu.clevai.bplog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.service.BpUlcToXAtomicService;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/bp-ulc-to-x-atomic")
public class BpUlcToXAtomicController {
	@Autowired
	private BpUlcToXAtomicService bpUlcToXAtomicService;

	@PostMapping(value = "/sync")
	public ResponseEntity<ApiResponse<?>> sync(
			@RequestParam(name = "xdsc") String xdsc,
			@RequestBody List<String> ulcCodes
	) {
		bpUlcToXAtomicService.convert(ulcCodes, xdsc);

		/* Please define response here */
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	@PostMapping(value = "/sync", params = {"ush"})
	public ResponseEntity<ApiResponse<?>> sync(
			@RequestParam String ush
	) {
		bpUlcToXAtomicService.convert(ush);

		/* Please define response here */
		return ResponseEntity.ok(ApiResponse.success(null));
	}
}