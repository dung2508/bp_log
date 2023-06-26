package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CalendarPeriodTypeEnum {
	YEAR(1, "CAYR"),
	MONTH(2, "CAMN"),
	WEEK(3, "CAWK"),
	DAY(4, "CADY"),
	SHIFT(5, "CASH"),
	SESSION(6, "CASS");

	private Integer level;
	private String code;

	public static CalendarPeriodTypeEnum findByCode(String code) {
		return Arrays.stream(values()).filter(v -> v.getCode().equals(code))
				.findFirst().orElse(null);
	}

	public static boolean validate(List<String> input) {
		input.forEach(item -> {
			if (CalendarPeriodTypeEnum.findByCode(item) == null)
				throw new NotFoundException(String.format("Cap type không hợp lệ %s", item));
		});

		return true;
	}
}
