package vn.edu.clevai.bplog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.clevai.bplog.service.TermService;
import vn.edu.clevai.common.proxy.bplog.payload.response.TrimesterResponse;

import java.util.List;

@RestController
@RequestMapping("/trimester")
public class TermController {
	@Autowired
	private TermService trimesterService;

	@GetMapping()
	public ResponseEntity<List<TrimesterResponse>> getTrimesterResponse() {
		return trimesterService.getAllTrimesters();
	}
}
