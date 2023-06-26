package vn.edu.clevai.bplog.payload.request.teacher;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherRegisterDetailRequest {
	@JsonProperty("product_ids")
	private List<String> products;

	@JsonProperty("grade_ids")
	private List<String> grades;

	@JsonProperty("days_of_week")
	private List<String> dayOfWeeks;

	@JsonProperty("time_slot_ids")
	private List<String> shiftIds;
}
