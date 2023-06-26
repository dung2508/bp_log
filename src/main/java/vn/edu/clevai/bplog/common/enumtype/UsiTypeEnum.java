package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum UsiTypeEnum {
	DTE("DTE"),
	GTE("GTE"),
	CTE("CTE"),
	DST("DST"),
	LTE("LTE"),

	TE("TE"),

	EM_SO("SO"),
	EM_TO("TO"),
	EM("EM"),
	QO("QO"),

	EXT("EXT");
	private final String name;

	public static List<String> getListTeType() {
		return Arrays.asList(DTE.getName(), GTE.getName(), CTE.getName(), DST.getName(), QO.getName());
	}

	public static List<String> getListEmType() {
		return Arrays.asList(EM_SO.getName(), EM_TO.getName());
	}

	public static Optional<UsiTypeEnum> optionalOf(String s) {
		return Arrays.stream(UsiTypeEnum.values()).filter(f -> f.getName().equals(s)).findFirst();
	}
}
