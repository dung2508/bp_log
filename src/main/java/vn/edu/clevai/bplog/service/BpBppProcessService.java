package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum;

public interface BpBppProcessService {

	String generateBppCode(BppProcessTypeEnum bppProcessTypeEnum);

	void createBppProcess(BppProcessTypeEnum type, String myparent);


}