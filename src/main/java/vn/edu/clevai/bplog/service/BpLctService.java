package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentType;

public interface BpLctService {
	BpLearningComponentType findByCode(String code);

}
