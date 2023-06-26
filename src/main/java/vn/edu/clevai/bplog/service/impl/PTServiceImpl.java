package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.repository.BpPTProductTypeRepository;
import vn.edu.clevai.bplog.service.PTService;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

@Service
public class PTServiceImpl implements PTService {

	private final BpPTProductTypeRepository bpPTProductTypeRepository;

	private final List<String> TEACHER_SCHEDULE_ASSIGN_PRODUCT = Arrays.asList("BC", "PM", "TH", "OM");

	public PTServiceImpl(BpPTProductTypeRepository bpPTProductTypeRepository) {
		this.bpPTProductTypeRepository = bpPTProductTypeRepository;
	}

	@Override
	public List<BpPTProductType> findAll() {
		return bpPTProductTypeRepository.findAll();
	}

	@Override
	public List<BpPTProductType> findProductTeacherScheduleAssign() {
		return bpPTProductTypeRepository.findByCodeInAndPublishedTrue(TEACHER_SCHEDULE_ASSIGN_PRODUCT);
	}

	@Override
	public BpPTProductType findByCode(String code) {
		return bpPTProductTypeRepository.findByCode(code).orElseThrow(() ->
				new NotFoundException("Not found BpPTProductType with code: " + code));
	}

	@Override
	public BpPTProductType findById(Integer id) {
		return bpPTProductTypeRepository.findById(id).orElseThrow(() ->
				new NotFoundException("Not found BpPTProductType with id: " + id));
	}
}
