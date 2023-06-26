package vn.edu.clevai.bplog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.BpULCService;
import vn.edu.clevai.common.api.model.ApiResponse;

@RestController
@RequestMapping("/bp-ulc-to-x")
public class BpUlcToXController {

	@Autowired
	private BpULCService bpULCService;

	@PostMapping(value = "/sync", params = "ulc-code")
	public ResponseEntity<ApiResponse<?>> sync(
			@RequestParam(name = "ulc-code") String ulcCode
	) {

		bpULCService.convertBpToX(ulcCode);
		return ResponseEntity.ok(ApiResponse.success(sync(ulcCode, ulcCode)));
	}

	@PostMapping(value = "/sync", params = {"ulc-code", "xdsc"})
	public ResponseEntity<ApiResponse<?>> sync(
			@RequestParam(name = "ulc-code") String ulcCode,
			@RequestParam(name = "xdsc") String xdsc
	) {

		/* Please define response here */
		return ResponseEntity.ok(ApiResponse.success(null));
	}
}