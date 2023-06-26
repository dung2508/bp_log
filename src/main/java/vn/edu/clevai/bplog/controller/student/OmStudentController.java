package vn.edu.clevai.bplog.controller.student;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.clevai.bplog.payload.response.student.StudentResponse;
import vn.edu.clevai.bplog.service.clag.ClagFacadeService;
import vn.edu.clevai.common.api.controller.BaseController;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.request.Cep100ChangeWsoRequest;
import vn.edu.clevai.common.proxy.bplog.payload.request.Cep200ChangeClagStudentRequest;

@RestController
@RequestMapping("/om-student")
@RequiredArgsConstructor
public class OmStudentController extends BaseController {

	@Autowired
	private ClagFacadeService clagFacadeService;
	
	@PutMapping("/wso/change/synch")
	public ResponseEntity<ApiResponse<String>> xToBpChangeWsoAndSynchClag(
			@RequestBody Cep200ChangeClagStudentRequest request) {
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<StudentResponse>> me() {
		return null;
	}

	@PostMapping("/change-wso")
	public ResponseEntity<String> changeWso(@RequestBody @Valid Cep100ChangeWsoRequest request) throws Exception {
		clagFacadeService.omStudentChangeWso(request);
		return ResponseEntity.ok(ApiResponse.SUCCESS);
	}

}
