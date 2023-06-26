package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ScheduleEnum {
	 MONTH_CALENDAR("Month Calendar"),
	WEEK_CALENDAR("Week Calendar"),
	SHIFT("Shift");
	private final String name;

	public static List<String> getNames() {
		return Arrays.stream(ScheduleEnum.values()).map(ScheduleEnum::getName).collect(Collectors.toList());
	}
}
