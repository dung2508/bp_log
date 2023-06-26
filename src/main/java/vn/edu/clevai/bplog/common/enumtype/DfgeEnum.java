package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum DfgeEnum {
	A("A"),
	B("B"),
	C("C"),
	D("D"),
	X("X"),

	;
	private final String code;

	public static List<String> getCodes() {
		return Arrays.stream(DfgeEnum.values()).map(DfgeEnum::getCode).collect(Collectors.toList());
	}

	public static List<String> getCodesIgnore(DfgeEnum... vals) {
		List<DfgeEnum> toList = (vals == null || vals.length == 0) ? new ArrayList<>() : Arrays.asList(vals);
		return Arrays.stream(DfgeEnum.values())
				.filter(f -> !toList.contains(f))
				.map(DfgeEnum::getCode)
				.collect(Collectors.toList());
	}
}
