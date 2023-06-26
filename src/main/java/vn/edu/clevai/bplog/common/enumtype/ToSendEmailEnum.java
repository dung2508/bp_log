package vn.edu.clevai.bplog.common.enumtype;

import lombok.Getter;

@Getter
public enum ToSendEmailEnum {
	FALSE("F"),

	TRUE("T");

	private final String code;

	ToSendEmailEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
