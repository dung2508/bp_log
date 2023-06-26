package vn.edu.clevai.bplog.utils;

import vn.edu.clevai.bplog.common.enumtype.TermEnum;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static java.time.temporal.TemporalAdjusters.lastInMonth;

public class Utils {

	public static String getMyTermFromTime(Date date) {
		LocalDate startDateMainTerm = getFirstMondayOfSeptember(date);
		LocalDate endDateMainTerm = getLastSundayOfMay(date);

		int startDayOfYear = startDateMainTerm.getDayOfYear();
		int endDayOfYear = endDateMainTerm.getDayOfYear();

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int dayOfYear = localDate.getDayOfYear();
		if (dayOfYear > endDayOfYear && dayOfYear < startDayOfYear) {
			return TermEnum.ST.getName();
		}
		return TermEnum.MT.getName();
	}


	public static LocalDate getFirstMondayOfSeptember(Date date) {
		Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
		LocalDate localDate = LocalDate.of(calendar.get(Calendar.YEAR),9, 1);
		LocalDate firstMonday = localDate.with(firstInMonth(DayOfWeek.MONDAY));
		return firstMonday;
	}

	public static LocalDate getLastSundayOfMay(Date date) {
		Calendar calendar =Calendar.getInstance();
		calendar.setTime(date);
		LocalDate localDate = LocalDate.of(calendar.get(Calendar.YEAR),5, 1);
		LocalDate lastSunday = localDate.with(lastInMonth(DayOfWeek.SUNDAY));
		return lastSunday;
	}
}
