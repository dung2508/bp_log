package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum RegisterEnum {
	REGISTER1_SETTING("Register1-Setting"),
	REGISTER2_REQUEST("Register2-Request"),
	REGISTER3_CONFIRM("Register3-Confirm"),
	REGISTER4_ALLOCATE("Register4-Allocate"),
	REGISTER5_TRANSFORM("Register5-Transform");
	private final String name;

	public static List<String> getNames() {
		return Arrays.stream(RegisterEnum.values()).map(RegisterEnum::getName).collect(Collectors.toList());
	}
}
