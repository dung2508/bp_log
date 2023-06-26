package vn.edu.clevai.bplog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum SystemUser {
	AU("AU") /* Default main users. */,
	;
	private String code;

	public static SystemUser findByCode(String code) {
		return Arrays.stream(values()).filter(v -> v.code.equals(code)).findFirst()
				.orElseThrow(() -> new NotFoundException("Could not find SystemUser using code = " + code));
	}
}
