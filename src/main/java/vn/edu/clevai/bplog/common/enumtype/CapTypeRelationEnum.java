package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CapTypeRelationEnum {
	CHILD(1),
	GRAND_CHILD(2);

	private Integer deltaLevel;

	public static CapTypeRelationEnum findByDeltaLevel(Integer deltaLevel) {
		return Arrays.stream(values()).filter(v -> v.getDeltaLevel().equals(deltaLevel))
				.findFirst().orElse(null);
	}
}
