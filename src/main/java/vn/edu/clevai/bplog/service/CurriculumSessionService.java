package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.CurriculumPeriod;
import vn.edu.clevai.bplog.payload.request.ContentRequest;

public interface CurriculumSessionService {
	CurriculumPeriod getCurriculumShift(ContentRequest request);
}
