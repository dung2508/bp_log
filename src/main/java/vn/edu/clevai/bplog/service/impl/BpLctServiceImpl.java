package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentType;
import vn.edu.clevai.bplog.repository.bplog.BpLearningComponentTypeRepository;
import vn.edu.clevai.bplog.service.BpLctService;
import vn.edu.clevai.common.api.exception.NotFoundException;

@Service
public class BpLctServiceImpl implements BpLctService {
	@Autowired
	private BpLearningComponentTypeRepository bpLearningComponentTypeRepository;

	@Override
	public BpLearningComponentType findByCode(String code) {
		return bpLearningComponentTypeRepository
				.findByCode(code)
				.orElseThrow(() -> new NotFoundException("Could not find BpLearningComponentType using code = " + code));
	}
}
