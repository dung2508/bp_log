package vn.edu.clevai.bplog.common.impl;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.common.BetTimeChecker;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.common.api.util.DateUtils;

import java.sql.Timestamp;
import java.util.Objects;

@Slf4j
public class Case1ABettimeChecker implements BetTimeChecker {

	private final Integer FIRST_SLICE_MINUTE = 0;
	private final Integer SECOND_SLICE_MINUTE = 30;

	@Override
	public boolean doCheck(CalendarPeriod period, Timestamp betTime) {
		if (Objects.isNull(period) || Objects.isNull(betTime)) {
			log.error("Period or bettime is null");
			return false;
		}

		Timestamp start = period.getStartTime();
		if (Objects.isNull(start)) {
			log.error("Period start time is null");
			return false;
		}
		return DateUtils.isBetween(betTime, DateUtils.addSecondToTimestamp(start, FIRST_SLICE_MINUTE * 60),
				DateUtils.addSecondToTimestamp(start, SECOND_SLICE_MINUTE * 60));
	}

}
