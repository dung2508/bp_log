package vn.edu.clevai.bplog.utils;

import vn.edu.clevai.common.api.exception.NotFoundException;

public class Cep200ToC100Utils {

	public static Long toC100PtId(String ptCode) {
		switch (ptCode) {
			case "BC":
				return 2L;
			case "PM":
				return 8L;
			case "TP10":
				return 14L;
			case "PV":
				return 28L;
			case "MD":
				return 29L;
			default:
				throw new NotFoundException("Unable to convert ptCode: " + ptCode + " toC100PtId!!!");
		}
	}

	public static Long toC100GradeId(String ggCode) {
		try {
			String[] split = ggCode.split("G");

			return Long.parseLong(split[1]) + 4;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException("Unable to convert ggCode: " + ggCode + " toC100GradeId with error: " + e.getLocalizedMessage());
		}
	}

	public static Long toC100DfdlId(String dfdlCode) {
		switch (dfdlCode) {
			case "C1":
				return 1L;
			case "C2":
				return 2L;
			default:
				throw new NotFoundException("Unable to convert dfdlCode: " + dfdlCode + " toC100DfdlId!!!");
		}
	}

	public static Long toC100SubjectId(String ptCode) {
		if ("TP10".equals(ptCode)) {
			return 43L;
		} else {
			return 1L;
		}
	}
}
