package vn.edu.clevai.bplog.service;

import org.springframework.http.ResponseEntity;
import vn.edu.clevai.common.proxy.scheduling.payload.request.CurriculumFilterRequest;
import vn.edu.clevai.common.proxy.scheduling.payload.response.CurriculumResponse;

import java.util.List;

public interface CurriculumService {

	ResponseEntity<List<CurriculumResponse>> getCurriculums(CurriculumFilterRequest request);

}