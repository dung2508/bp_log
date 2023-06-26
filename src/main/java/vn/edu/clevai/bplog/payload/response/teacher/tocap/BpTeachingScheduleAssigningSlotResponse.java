package vn.edu.clevai.bplog.payload.response.teacher.tocap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BpTeachingScheduleAssigningSlotResponse {

	@EqualsAndHashCode.Include
	@JsonProperty("product_id")
	private Integer productId;

	@EqualsAndHashCode.Include
	@JsonProperty("grade_id")
	private Integer gradeId;

	@EqualsAndHashCode.Include
	@JsonProperty("class_level_id")
	private Integer classLevelId;

}
