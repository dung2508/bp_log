package vn.edu.clevai.bplog.service.bplog;

import vn.edu.clevai.bplog.dto.redis.BpeEventDTO;
import vn.edu.clevai.bplog.dto.redis.BppProcessDTO;
import vn.edu.clevai.bplog.dto.redis.BpsStepDTO;

public interface BppLog {
	void saveBpeEvent(BpeEventDTO dto);
	
	void saveBppProcess(BppProcessDTO dto);
	
	void saveBpsStep(BpsStepDTO dto);
}
