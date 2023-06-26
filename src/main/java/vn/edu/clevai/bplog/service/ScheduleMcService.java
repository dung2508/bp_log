package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpPODCLAG;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.common.api.model.MessageResponseDTO;

public interface ScheduleMcService {
	MessageResponseDTO scheduleMcAll(ScheduleRequest scheduleRequest, CalendarPeriod camn);

	void scheduleMC(CalendarPeriod capmn, String pt, String gg, String dfdl, BpPODCLAG podclag);
}
