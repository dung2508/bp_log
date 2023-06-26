package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CLCTypeEnum {
	CLC_TED("CLC-TED"),
	CLC_SHS("CLC-SHS"),

	;

	private final String code;

}
