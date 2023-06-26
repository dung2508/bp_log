package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.bplog.entity.BpPRD;
import vn.edu.clevai.bplog.repository.BpPRDRepository;
import vn.edu.clevai.bplog.service.BpPeriodService;

@Service
public class BpPeriodServiceImpl implements BpPeriodService {

	private final BpPRDRepository bpPRDRepository;

	public BpPeriodServiceImpl(BpPRDRepository bpPRDRepository) {
		this.bpPRDRepository = bpPRDRepository;
	}

	@Override
	public BpPRD findByLengthAndUnit(Integer length, String unit) {
		return bpPRDRepository.findByPeriodAndLength(length, unit)
				.orElseThrow(
						() -> new NotFoundException("Couldn't find by length " + length + " unit " + unit)
				);
	}
}
