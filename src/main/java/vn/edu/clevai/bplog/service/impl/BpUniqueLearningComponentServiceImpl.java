package vn.edu.clevai.bplog.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.projection.UlcScheduleShiftPJ;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.service.BpUniqueLearningComponentService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BpUniqueLearningComponentServiceImpl implements BpUniqueLearningComponentService {

	private final BpUniqueLearningComponentRepository ulcRepository;

	@Override
	public Page<UlcScheduleShiftPJ> findAllByCondition(ScheduleRequest request) {

		request.setDfdlsIsNull(CollectionUtils.isEmpty(request.getDfdls()));
		request.setGgsIsNull(CollectionUtils.isEmpty(request.getGgs()));
		String like = "%" + request.getCady() + "%";
		request.setCady(like);
		return ulcRepository.findAllByCondition(request, PageRequest.of(request.getPage() - 1, request.getSize()));
	}
}
