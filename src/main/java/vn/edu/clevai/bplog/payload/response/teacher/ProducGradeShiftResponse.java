package vn.edu.clevai.bplog.payload.response.teacher;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProducGradeShiftResponse {
	@JsonProperty("products")
	private List<ProductResponse> products;

	@JsonProperty("grades")
	private List<GradeResponse> grades;

	@JsonProperty("time_slots")
	private List<ShiftResponse> shifts;

}
