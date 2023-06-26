package vn.edu.clevai.bplog.payload.response.teacher.tocap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingScheduleAssigningQuantityResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("date")
	private LocalDate date;

	@JsonProperty("registered_quantity")
	private Integer registeredQuantity;

	@JsonProperty("required_quantity")
	private TeacherQuantityResponse requiredQuantity;

	@JsonProperty("assigned_quantity")
	private TeacherAssignedQuantityResponse assignedQuantity;

}