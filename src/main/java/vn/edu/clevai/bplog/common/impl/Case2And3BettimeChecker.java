package vn.edu.clevai.bplog.common.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import vn.edu.clevai.bplog.common.BetTimeChecker;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.common.api.util.DateUtils;

import java.sql.Timestamp;
import java.util.Objects;

@Slf4j
public class Case2And3BettimeChecker implements BetTimeChecker {

	private final Integer FIRST_SLICE_MINUTE = 120;

	@Override
	public boolean doCheck(CalendarPeriod period, Timestamp betTime) {
		if (Objects.isNull(period) || Objects.isNull(betTime)) {
			log.error("Period or bettime is null");
			return false;
		}
		Timestamp start = period.getStartTime();
		Timestamp end = period.getEndTime();
		if (!ObjectUtils.allNotNull(end)) {
			log.error("Period endtime is null");
			return false;
		}
		return DateUtils.isBetween(betTime, start, DateUtils.addSecondToTimestamp(end, FIRST_SLICE_MINUTE * 60));
	}

}
