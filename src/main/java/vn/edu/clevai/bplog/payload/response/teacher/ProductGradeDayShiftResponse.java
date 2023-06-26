package vn.edu.clevai.bplog.payload.response.teacher;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductGradeDayShiftResponse {
	@JsonProperty("id")
	private Long id;

	@JsonProperty("product_id")
	private Integer productId;

	@JsonProperty("grade_id")
	private Integer gradeId;

	@JsonProperty("day_of_week")
	private Integer dayOfWeek;

	@JsonProperty("time_of_slot_id")
	private Integer timeOfSlotId;
}
