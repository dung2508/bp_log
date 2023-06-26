package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum LCLEnum {
	LCSH("SH"),
	LCSS("SS"),
	LCDY("DY"),
	LCMN("MN"),
	LCPH("PH"),
	LCSC("SC"),
	LCSL("SL"),
	LCWK("WK"),
	LCPK("PK");
	private final String name;

}
