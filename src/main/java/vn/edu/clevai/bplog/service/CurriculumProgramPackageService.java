package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.CurriculumProgramPackage;

import java.sql.Timestamp;

public interface CurriculumProgramPackageService {
	CurriculumProgramPackage getCrppByAccYearAndTimeAndPt(String accYear, Timestamp time, String ptCode);

	CurriculumProgramPackage getCrppByAccYearAndTermAndPt(String accYear, String term, String ptCode);

	CurriculumProgramPackage findByPtAndTime(String pt, Timestamp t);
}
