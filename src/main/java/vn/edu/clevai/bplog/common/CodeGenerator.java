package vn.edu.clevai.bplog.common;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;

public class CodeGenerator {

	public final static String DASH_CHARACTOR = "-";

	public static String toCode(String parentCode, String itemCode, Integer index) {
		Asserts.notBlank(parentCode, "Parent code must be not blank");
		if (StringUtils.isBlank(itemCode)) {
			return parentCode.concat(DASH_CHARACTOR).concat(String.valueOf(1));
		}
		return parentCode.concat(DASH_CHARACTOR)
				.concat(String.valueOf(extractCodeIndex(itemCode) + 1 + (Objects.nonNull(index) ? index : 1)));
	}

	private static Integer extractCodeIndex(String code) {
		Pattern p = Pattern.compile("(.*)-(\\d+)");
		Matcher m = p.matcher(code);
		if (m.matches()) {
			return Integer.parseInt(m.group(2));
		}
		return 0;
	}

	public static String buildNormalCode(String... params) {
		return String.join(DASH_CHARACTOR, params);
	}
}
