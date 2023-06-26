package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpPODCLAG;
import vn.edu.clevai.bplog.entity.CalendarPeriod;

public interface ScheduleWcService {
	void scheduleWCAll(
			String pt,
			String gg,
			String dfdl,
			String cawk
	);

	void scheduleWC(
			CalendarPeriod cawk,
			String pt,
			String gg,
			String dfdl,
			BpPODCLAG podclag
	);
}
