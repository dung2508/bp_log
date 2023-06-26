package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PositionAssignEnum {

	MAIN(1, "MAIN"), BACKUP(2, "BACKUP"),
	;

	private Integer id;
	private String name;

	public static PositionAssignEnum findById(Integer id) {
		for (PositionAssignEnum ass : values()) {
			if (ass.getId().equals(id))
				return ass;
		}
		return null;
	}

	public static String findByUstAndUlc(String ust, Boolean isBackUpUlc) {
		if (isBackUpUlc.equals(true))
			return BACKUP.getName();
		if (isBackUpUlc.equals(false)) {
			if (ust.equals(UsiTypeEnum.DTE.getName())
					|| ust.equals(UsiTypeEnum.GTE.getName())
					|| ust.equals(UsiTypeEnum.LTE.getName())
					|| ust.equals(UsiTypeEnum.QO.getName()))
				return MAIN.getName();
			return null;
		}
		return null;
	}
}
