package vn.edu.clevai.bplog.common;

import vn.edu.clevai.bplog.dto.bp.TimeSessionDto;

public class TimeSessionCommon {
	public static String encodeTimeSession(String myusi, Long start, Long end) {
		return null;
	}

	public static TimeSessionDto decodeTimeSession(String timeSession) {
		return null;
	}

	public static void main(String args[]) {
		String time = "1950";
		Integer hour = Integer.parseInt(time.substring(0, 2));
		Integer minute = Integer.parseInt(time.substring(2, time.length()));
		System.out.println(minute);
		Integer minuteAdd = 50;
		Integer addHour = (minute + minuteAdd) / 60;
		if (addHour > 0) {
			hour = hour + addHour;
			System.out.println(hour%24);
		}
		Integer next = (minute + minuteAdd) % 60;
		System.out.println(next);

	}
}
