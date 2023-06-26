package vn.edu.clevai.bplog.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

public class ComboUlcFunction<T> {

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Data {
		private String pt;
		private String gg;
		private String dfdL;
	}

	public static List<Data> flatToCombo(List<String> productTypes,
										List<String> gradeGroups,
										List<String> difficultyGrades) {

		List<Data> pt_gg_dfdL = new ArrayList<>();
		productTypes.stream().filter(pt -> !isBlank(pt))
				.forEach(pt -> {
					gradeGroups.stream().filter(gg -> !isBlank(gg))
							.forEach(gg -> {
								difficultyGrades.stream().filter(dfdL -> !isBlank(dfdL))
										.forEach(dfdL -> {
											pt_gg_dfdL.add(Data.builder()
													.pt(pt)
													.gg(gg)
													.dfdL(dfdL)
													.build());

										});
							});
				});

		return pt_gg_dfdL;
	}
}
