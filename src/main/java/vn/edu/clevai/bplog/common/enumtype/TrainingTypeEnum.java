package vn.edu.clevai.bplog.common.enumtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum TrainingTypeEnum {

	DILIVE(1L, "DILIVE", "DL"),
	GET(2L, "GET", "GE"),
	PLUS(5L, "PLUS", "PM");

	private final Long id;
	private final String name;
	private final String code;

	public static TrainingTypeEnum ofId(Long id) {
		return Stream.of(values())
				.filter(trainingTypeEnum -> trainingTypeEnum.getId().equals(id))
				.findFirst()
				.orElse(null);
	}

}
