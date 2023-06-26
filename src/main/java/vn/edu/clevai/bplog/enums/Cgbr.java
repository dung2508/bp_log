package vn.edu.clevai.bplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Cgbr {
	ACG,
	ECG,
	EPOD,
	;

	/* Just a wrapper. */
	public static Cgbr findByName(String name) {
		return valueOf(name);
	}
}
