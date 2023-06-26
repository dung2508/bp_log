package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.common.enumtype.BpeEventTypeEnum;

public interface BpBpeEventService {

	String generateBpeCode(BpeEventTypeEnum bpeEventTypeEnum);

	void createBpeEvent(BpeEventTypeEnum type);

}