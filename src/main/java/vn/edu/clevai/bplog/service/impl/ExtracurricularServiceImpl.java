package vn.edu.clevai.bplog.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.clevai.common.proxy.scheduling.payload.request.ExtracurricularFilterRequest;
import vn.edu.clevai.common.proxy.scheduling.payload.response.ExtracurricularResponse;
import vn.edu.clevai.bplog.entity.Extracurricular;
import vn.edu.clevai.bplog.repository.ExtracurricularRepository;
import vn.edu.clevai.bplog.service.ExtracurricularService;

@Service
@RequiredArgsConstructor
public class ExtracurricularServiceImpl implements ExtracurricularService {

	private final ExtracurricularRepository extracurricularRepo;

	private final ModelMapper modelMapper;

	@Override
	public ResponseEntity<List<ExtracurricularResponse>> getExtracurriculars(ExtracurricularFilterRequest request) {

		List<Extracurricular> extracurriculars = extracurricularRepo.findByGradeIdAndTimeRange(
				request.getGradeId(),
				Timestamp.valueOf(request.getStartTime()),
				Timestamp.valueOf(request.getEndTime()));

		List<ExtracurricularResponse> responses = extracurriculars.stream()
				.map(extracurricular -> modelMapper.map(extracurricular, ExtracurricularResponse.class))
				.collect(Collectors.toList());
		return ResponseEntity.ok(responses);

	}
}