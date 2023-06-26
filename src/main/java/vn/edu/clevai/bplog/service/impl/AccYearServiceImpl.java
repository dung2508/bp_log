package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.entity.AccYear;
import vn.edu.clevai.bplog.repository.AccYearRepository;
import vn.edu.clevai.bplog.service.AccYearService;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;

import java.util.Date;

@Service
public class AccYearServiceImpl implements AccYearService {

	private final AccYearRepository accYearRepository;

	public AccYearServiceImpl(AccYearRepository accYearRepository) {
		this.accYearRepository = accYearRepository;
	}

	@Override
	public AccYear findByTime(Date input) {
		return accYearRepository.findByTime(DateUtils.endOfDay(input.getTime())).orElseThrow(
				() -> new NotFoundException("Coun't find acc year by time :" + input));
	}

}
