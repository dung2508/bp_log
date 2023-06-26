package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.CurriculumProgramSheet;
import vn.edu.clevai.bplog.repository.CurriculumProgramSheetRepository;
import vn.edu.clevai.bplog.service.CurriculumProgramSheetService;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.sql.Timestamp;

@Service
public class CurriculumProgramSheetServiceImpl implements CurriculumProgramSheetService {
	@Autowired
	private CurriculumProgramSheetRepository curriculumProgramSheetRepository;

	@Override
	@WriteUnitTestLog
	public CurriculumProgramSheet getCrps(String crppCode, String ggCode) {
		return curriculumProgramSheetRepository
				.findAllByMyCrppAndMyGGAndPublishedTrue(crppCode, ggCode).orElseThrow(
						() -> new NotFoundException("Coun't find crps by crpp :" + crppCode + ggCode));
	}

	@Override
	public CurriculumProgramSheet getCrpsForOM(String pt, String gg, Timestamp timestamp) {
		return curriculumProgramSheetRepository.getCrps(pt, gg, timestamp)
				.orElseThrow(
						() -> new NotFoundException("Couldn't find crps with pt: " + pt + " gg: " + gg + " timestamp: " + timestamp)
				);
	}
}
