package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.CurriculumProgramSheet;

import java.sql.Timestamp;

public interface CurriculumProgramSheetService {
	CurriculumProgramSheet getCrps(String crppCode, String ggCode);

	CurriculumProgramSheet getCrpsForOM(String pt, String gg, Timestamp timestamp);
}
