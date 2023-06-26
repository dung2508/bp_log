package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpGgStResponse;

public interface BpGgStService {
	BpGgStResponse BPSetGG(String podCode, String ggCode);

	@WriteUnitTestLog
	BpGgStResponse setST_GG(String stCode, String ggCode);
}
