package vn.edu.clevai.bplog.payload.response.teacher;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAvailableResponse {
	@JsonProperty("acc_year")
	private String accYear;
	
	@JsonProperty("term")
	private String term;
	
	private Set<Integer> products;

	@JsonProperty("product_grades")
	private Set<ProductGradeResponse> productGrades;

	@JsonProperty("product_grade_days")
	private Set<ProductGradeDay> productGradeDays;

	@JsonProperty("product_grade_day_time_slots")
	private Set<ProductGradeDayShiftResponse> productGradeDayShift;
}
