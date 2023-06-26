package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum WSOEnum {
	MON(2, "monday"),
	TUE(3, "tuesday"),
	WED(4, "wednesday"),
	THU(5, "thursday"),
	FRI(6, "friday"),
	SAT(7, "saturday"),
	SUN(8, "sunday");

	private Integer numberOfWeek;
	private String objectCode;

	public static WSOEnum findByObjectCode(String code) {
		return Arrays.stream(values()).filter(v -> v.getObjectCode().equals(code))
				.findFirst().orElse(null);
	}
}
