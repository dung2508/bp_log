package vn.edu.clevai.bplog.common.enumtype;

import lombok.Getter;

@Getter
public enum LCPPeriodNoEnum {

	DLC("C17"),

	GES("C21");

	private final String code;

	LCPPeriodNoEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
