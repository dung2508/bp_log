package vn.edu.clevai.bplog.common.impl;

import java.sql.Timestamp;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.common.BetTimeChecker;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.common.api.util.DateUtils;

@Slf4j
public class Case1BBettimeChecker implements BetTimeChecker {

	private final Integer FIRST_SLICE_MINUTE = 35;

	@Override
	public boolean doCheck(CalendarPeriod period, Timestamp betTime) {
		if (Objects.isNull(period) || Objects.isNull(betTime)) {
			log.error("Period or bettime is null");
			return false;
		}
		Timestamp start = period.getStartTime();
		Timestamp end = period.getEndTime();
		if (!ObjectUtils.allNotNull(start, end)) {
			log.error("Period start or endtime is null");
			return false;
		}

		return betTime.after(DateUtils.addSecondToTimestamp(start, FIRST_SLICE_MINUTE * 60));
	}

}
