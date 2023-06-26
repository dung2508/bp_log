package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.*;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
	void schedule(String cap);

	void schedule(CalendarPeriod cap);

	void schedule(CalendarPeriod cap, BpLCP lcp);

	void schedule(CalendarPeriod cap, BpLCP lcp, CurriculumPeriod cup, String pt, String gg, String dfdl);

	void schedule(CalendarPeriod cap, BpLCP lcp, CurriculumPeriod cup, String pt, String gg, String dfdl, List<BpClagClassgroup> clags, Map<String, List<BpPodProductOfDeal>> pods);

	void schedule(
			CalendarPeriod cap,
			BpLCP lcp,
			CurriculumPeriod cup,
			String pt,
			String gg,
			String dfdl,
			List<BpClagClassgroup> clags,
			Map<String, List<BpPodProductOfDeal>> pods,
			String parentCode,
			String code,
			Integer parentIndex,
			Integer index
	);
}
