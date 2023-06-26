package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.common.enumtype.BpsStepTypeEnum;

public interface BpBpsStepService {

	String generateBpsCode(BpsStepTypeEnum bpsStepTypeEnum);

	void createBpsStep(BpsStepTypeEnum type);

}