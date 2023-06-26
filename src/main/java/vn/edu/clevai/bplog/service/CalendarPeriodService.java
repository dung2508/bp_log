package vn.edu.clevai.bplog.service;


import vn.edu.clevai.bplog.dto.CalendarPeriodDTO;
import vn.edu.clevai.bplog.entity.CalendarPeriod;

import java.sql.Timestamp;
import java.util.List;

public interface CalendarPeriodService {
	CalendarPeriod getCAPByTime(Timestamp time, String capType);

	CalendarPeriod getCAP(String inputCap, String capType, String capNo);

	CalendarPeriod getCAWK(String cuwk);

	CalendarPeriod getCASS(String inputCapCode, String lcpPeriodNo, String capStructure);

	CalendarPeriod getCASH(String cady, String pt, String gg, String dfdl, String prd, String cashStart);

	CalendarPeriod getCADY(String cudy, String wsoCode);

	CalendarPeriod findByCode(String code);

	CalendarPeriod findByCodeAndCapType(String capCode, String capType);

	List<CalendarPeriod> findByMyGrandParentAndCapType(String capCode, String capType);

	List<CalendarPeriod> findCapKid(String capCode, String capStructure);

	List<CalendarPeriod> findByParentAndCapType(String parent, String capType);

	CalendarPeriod findByMyParentAndNumberAsChild(String inputCapCode, String capNo);

	List<CalendarPeriodDTO> findAllCalendarPeriod(List<String> types);

	List<CalendarPeriodDTO> findAllCalendarPeriodMonth(String type);

	List<CalendarPeriod> getCadyFromWsoAndCawk(Timestamp from, Timestamp to, List<Integer> wso);

	List<CalendarPeriod> getCadyListScheduledOfClag(String clag);

	List<CalendarPeriod> findCapListScheduledForEPOD(
			String usi, Timestamp from, Timestamp to, String pt, String gg, String dfdl, String parentCapType);

}

