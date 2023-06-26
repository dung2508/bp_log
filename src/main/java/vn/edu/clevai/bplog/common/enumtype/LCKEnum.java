package vn.edu.clevai.bplog.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LCKEnum {
	LCK_OM_SS_HW("HORG"),
	LCK_OM_SS_RL("RL"),
	LCK_OM_SS_QA("QA");
	private final String code;

}
