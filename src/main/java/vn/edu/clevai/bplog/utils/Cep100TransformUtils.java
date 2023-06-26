package vn.edu.clevai.bplog.utils;

import vn.edu.clevai.bplog.common.enumtype.LCPLCTLCKEnum;
import vn.edu.clevai.bplog.common.enumtype.ProductTypeEnum;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.Objects;

public class Cep100TransformUtils {
	public static String trainingTypeIdToLct(Long trainingTypeId) {
		if (Objects.isNull(trainingTypeId)) {
			return null;
		}

		switch (trainingTypeId.intValue()) {
			case 1:
				return LCPLCTLCKEnum.DLC_75MI.getCode();
			case 2:
				return LCPLCTLCKEnum.GES_75MI.getCode();
			case 4:
				return LCPLCTLCKEnum.DL0_75MI.getCode();
			case 3:
			case 5:
				return LCPLCTLCKEnum.DLG_90MI.getCode();
			default:
				throw new NotFoundException("Could not convert trainingTypeId = " + trainingTypeId + " to LCT");
		}
	}

	public static String toGGCode(Long gradeId) {
		if (gradeId == null || gradeId < 5 || gradeId > 16) {
			throw new NotFoundException("Grade id of " + gradeId + " is not supported");
		}

		return "G" + (gradeId - 4);
	}

	public static String toPtCode(Long xpt) { /* xpt is product_id in cep100. */
		if (Objects.isNull(xpt))
			return null;
		switch (xpt.intValue()) {
			case 2:
				return "BC";
			case 8:
				return "PM";
			case 14:
				return "TP10";
			case 28:
				return "PV";
			case 29:
				return "MD";
			case 30:
				return "PO";
			case 31:
				return "OM";
			default:
				return null;
		}
	}

	public static String toDfgeCode(String category) {
		if (Objects.isNull(category)) {
			return null;
		}

		switch (category) {
			case "A":
				return "GETA";
			case "B":
				return "GETB";
			case "C":
				return "GETC";
			case "D":
				return "GETD";
			default:
				throw new NotFoundException("Could not convert xDfge = " + category + " to DFGE");
		}
	}

	public static String toDfdlCode(Integer classLevelId) {
		if (Objects.isNull(classLevelId))
			return null;
		switch (classLevelId) {
			case 1:
				return "C1";
			case 2:
			case 3:
				return "C2";
			default:
				return null;
		}
	}

	public static String toPtCode(Integer trainingTypeId) {
		if (Objects.isNull(trainingTypeId))
			return null;
		switch (trainingTypeId) {
			case 1:
			case 2:
				return "BC";
			case 3:
				return "TP10";
			case 4:
				return "TPU";
			case 5:
				return "PM";
			case 6:
				return "OM";
			default:
				return null;
		}
	}

	public static String toPtCodeVer2(Integer trainingTypeId, String classCode) {
		if (Objects.isNull(trainingTypeId))
			return null;
		switch (trainingTypeId) {
			case 1:
			case 2:
				return "BC";
			case 3:
				return "TP10";
			case 4:
				return "TPU";
			case 5:
				if (Objects.nonNull(classCode) && classCode.contains(ProductTypeEnum.PO.getName())) {
					return ProductTypeEnum.PO.getName();
				} else {
					return ProductTypeEnum.PM.getName();
				}
			case 6:
				return "OM";
			default:
				return null;
		}
	}
}
