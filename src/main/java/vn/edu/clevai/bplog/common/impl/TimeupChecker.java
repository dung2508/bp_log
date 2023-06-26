package vn.edu.clevai.bplog.common.impl;

import java.sql.Timestamp;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.common.BetTimeChecker;
import vn.edu.clevai.bplog.entity.CalendarPeriod;

@Slf4j
public class TimeupChecker implements BetTimeChecker {

	@Override
	public boolean doCheck(CalendarPeriod period, Timestamp betTime) {
		if (Objects.isNull(period) || Objects.isNull(betTime)) {
			log.error("Period or bettime is null");
			return false;
		}
		Timestamp end = period.getEndTime();
		if (Objects.isNull(end)) {
			log.error("Period endtime is null");
		}
		return betTime.after(end);
	}

}
