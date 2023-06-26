package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductTypeEnum {
	BC("BC"),
	PM("PM"),
	TP10("TH"),
	OCPM("OCPM"),
	OCPE("OCPE"),
	OM("OM"),
	PO("PO"),
	TH("TH"),
	OE("OE");

	private final String name;
}
