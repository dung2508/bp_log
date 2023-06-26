package vn.edu.clevai.bplog.service.impl;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.clevai.common.proxy.scheduling.payload.request.CurriculumFilterRequest;
import vn.edu.clevai.common.proxy.scheduling.payload.response.CurriculumResponse;
import vn.edu.clevai.bplog.entity.Curriculum;
import vn.edu.clevai.bplog.repository.CurriculumRepository;
import vn.edu.clevai.bplog.service.CurriculumService;

@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

	private final CurriculumRepository curriculumRepo;

	private final ModelMapper modelMapper;

	@Override
	public ResponseEntity<List<CurriculumResponse>> getCurriculums(CurriculumFilterRequest request) {

		if (CollectionUtils.isEmpty(request.getSubjectIds())
				|| CollectionUtils.isEmpty(request.getGradeIds())
				|| CollectionUtils.isEmpty(request.getClassLevelIds())
				|| CollectionUtils.isEmpty(request.getTrainingTypeIds())) {
			return ResponseEntity.ok(Collections.emptyList());
		}

		List<Curriculum> curriculums = curriculumRepo.findCurriculums(
				request.getSubjectIds(),
				request.getGradeIds(),
				request.getClassLevelIds(),
				request.getTrainingTypeIds(),
				Date.valueOf(request.getStartDate()),
				Date.valueOf(request.getEndDate())
		);

		List<CurriculumResponse> responses = curriculums.stream()
				.map(curriculum -> modelMapper.map(curriculum, CurriculumResponse.class))
				.collect(Collectors.toList());
		return ResponseEntity.ok(responses);

	}
}