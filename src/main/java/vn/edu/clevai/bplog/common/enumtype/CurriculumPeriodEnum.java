package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CurriculumPeriodEnum {
	CURR_MONTH("CUMN", "CurrMonth", "MN"),
	CURR_WEEK("CUWK", "CurrWeek", "WK"),
	CURR_DAY("CUDY", "CurrDay", "DY"),
	CURR_SHIFT("CUSH", "CurrShift", "SH"),
	CURR_SESSION("CUSS", "CurrSession", "SS"),
	CURR_SCENE("CUSC", "CurrScene", "SC"),
	CURR_SLOT("CUSL", "CurrSlot", "SL");

	private String code;
	private String name;
	private String lclevel;

	public static CurriculumPeriodEnum findByLCL(String lcl) {
		return Arrays.stream(values()).filter(v -> v.getLclevel().equals(lcl))
				.findFirst().orElse(null);
	}
}
