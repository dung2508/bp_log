package vn.edu.clevai.bplog.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import vn.edu.clevai.common.proxy.scheduling.payload.request.ExtracurricularFilterRequest;
import vn.edu.clevai.common.proxy.scheduling.payload.response.ExtracurricularResponse;

public interface ExtracurricularService {

	ResponseEntity<List<ExtracurricularResponse>> getExtracurriculars(ExtracurricularFilterRequest request);

}