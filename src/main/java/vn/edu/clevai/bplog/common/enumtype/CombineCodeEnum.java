package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CombineCodeEnum {
	AY(1, "ayCode", ""),
	TERM(2, "trmCode", "-"),
	PT(3, "ptCode", "-"),
	GG(4, "ggCode", "-"),
	WKNO(5, "wkNo", "-WK"),
	DYNO(6, "dyNo", "-DY"),
	DFDL(7, "dfdlCode", "-"),
	LCT(8, "lctCode", "-"),
	DFGE(9, "dfgeCode", "-"),
	SCINDEX(10, "scIndex", "-"),
	CUPNO(11, "cupNo", "-"),
	INDEX(12, "slIndex", "-");

	private Integer order;
	private String objectName;
	private String prefix;
}
