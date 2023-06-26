package vn.edu.clevai.bplog.common;

import java.sql.Timestamp;

import vn.edu.clevai.bplog.entity.CalendarPeriod;

public interface BetTimeChecker {
	boolean doCheck(CalendarPeriod period, Timestamp betTime);
}
