package vn.edu.clevai.bplog.common.enumtype;

import lombok.Getter;
import vn.edu.clevai.common.api.exception.BadRequestException;

import java.util.Arrays;

public class CtiQuestionEnum {

	@Getter
	public enum StatusEnum {
		SUBMITTED("SUBMITTED"),
		RESOLVED("RESOLVED"),
		;

		StatusEnum(String code) {
			this.code = code;
		}
		private static String ALL_CODE_STR = "";
		static {
			Arrays.stream(StatusEnum.values()).forEach(fo -> {
				ALL_CODE_STR = ALL_CODE_STR.concat(fo.getCode()).concat(",");
			});
			try {
				ALL_CODE_STR = ALL_CODE_STR.substring(0, ALL_CODE_STR.length() - 1);
			}catch (Exception ignore) {}
		}

		public static boolean validate(String input) {
			Arrays.stream(StatusEnum.values()).filter(f -> f.code.equals(input)).findFirst().orElseThrow(
					() -> new BadRequestException(String.format("Value must be %s ", ALL_CODE_STR))
			);
			return true;
		}
		private final String code;
	}
}
