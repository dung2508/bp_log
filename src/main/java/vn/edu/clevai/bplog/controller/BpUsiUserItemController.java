package vn.edu.clevai.bplog.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.service.BpUsiUserItemService;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.AssignDoerResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/bp-usi-user-items")
@RequiredArgsConstructor
public class BpUsiUserItemController {

	private final BpUsiUserItemService userItemService;

	@GetMapping("/findUSI")
	public ResponseEntity<ApiResponse<BpUsiUserItemResponse>> findUsi(
			@RequestParam String lcet,
			@RequestParam String ust,
			@RequestParam String cap,
			@RequestParam String chrt,
			@RequestParam String lcp,
			@RequestParam(required = false) String[] excludeUSI
	) {
		return ResponseEntity.ok(ApiResponse.success(userItemService.findUSI(
				lcet,
				ust,
				cap,
				chrt,
				lcp,
				Arrays.asList(ObjectUtils.defaultIfNull(excludeUSI, new String[0]))
		)));
	}

	@GetMapping("/find-list-doer")
	public ResponseEntity<ApiResponse<List<AssignDoerResponse>>> findListDoer(
			@RequestParam String lcet,
			@RequestParam String ust,
			@RequestParam Timestamp date,
			@RequestParam String chrt,
			@RequestParam(required = false) String[] excludeUSI
	) {
		return ResponseEntity.ok(ApiResponse.success(userItemService
				.findListDoer(lcet, ust, date, chrt)));
	}

	@GetMapping
	public ResponseEntity<BpUsiUserItemResponse> findByCode(
			@RequestParam String usi
	) {
		return ResponseEntity.ok(userItemService.findByCodeVer2(usi));
	}

	@PostMapping("/createEXTAccount")
	public ResponseEntity<?> createEXTAccount(@RequestParam String pt, @RequestParam String usi) {
		return ResponseEntity.ok(userItemService.createEXTAccount(pt, usi));
	}

	@GetMapping("/findEXTAccount")
	public ResponseEntity<BpUsiUserItemResponse> findEXTAccount(@RequestParam String pt, @RequestParam String usi) {
		return ResponseEntity.ok(userItemService.findEXTAccount(pt, usi));
	}
}