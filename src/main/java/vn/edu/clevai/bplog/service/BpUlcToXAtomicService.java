package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;

import java.util.List;

public interface BpUlcToXAtomicService {
	void convert(String ulcCode);

	void convertMonth(String ulcCode);

	void convert(String ulcCode, String xdsc);

	void convert(List<String> ulcCodes, String xdsc);

	void convert(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs);
}
