package vn.edu.clevai.bplog.payload.response.teacher.tocap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpTeachingScheduleAssigningSlotGroupResponse {

	@JsonProperty("products")
	private Set<Integer> products;

	@JsonProperty("product_grades")
	private Set<BpTeachingScheduleAssigningSlotResponse> productGrades;

	@JsonProperty("product_grade_class_levels")
	private Set<BpTeachingScheduleAssigningSlotResponse> productGradeClassLevels;

}
