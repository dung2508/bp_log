package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.CurriculumProgramPackage;
import vn.edu.clevai.bplog.repository.CurriculumProgramPackageRepository;
import vn.edu.clevai.bplog.service.CurriculumProgramPackageService;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.sql.Timestamp;

@Service
public class CurriculumProgramPackageServiceImpl implements CurriculumProgramPackageService {
	@Autowired
	private CurriculumProgramPackageRepository curriculumProgramPackageRepository;

	@Override
	@WriteUnitTestLog
	public CurriculumProgramPackage
	getCrppByAccYearAndTimeAndPt(String ayCode, Timestamp time, String ptCode) {
		return curriculumProgramPackageRepository
				.findByMyAccYearAndTimeAndMyPt(ayCode, time, ptCode).orElseThrow(
						() -> new NotFoundException("Coun't find crpp by accyear :" + ayCode + time.toString() + ptCode));
	}

	@Override
	public CurriculumProgramPackage getCrppByAccYearAndTermAndPt(String ayCode, String term, String ptCode) {
		return curriculumProgramPackageRepository
				.findByMyAccYearAndMyTermAndMyPtAndPublishedTrue(ayCode, term, ptCode).orElseThrow(
						() -> new NotFoundException("Coun't find crpp by accyear :" + ayCode + term + ptCode));
	}

	@Override
	public CurriculumProgramPackage findByPtAndTime(String pt, Timestamp time) {
		return curriculumProgramPackageRepository
				.findByPtAndTime(pt, time)
				.orElseThrow(
						() -> new NotFoundException("Could not find crpp for pt = " + pt + ", time = " + time)
				);
	}
}
