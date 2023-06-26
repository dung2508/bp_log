package vn.edu.clevai.bplog.payload.response.teacher.tocap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuantityResponse {

	@JsonProperty("date")
	private String dateInput;

	@JsonProperty("registered_quantity")
	private Integer registeredQuantity;

	@JsonProperty("required_quantity")
	private RequiredQuantity requiredQuantity;
	
	@JsonProperty("assigned_quantity")
	private AssignedQuantity assignedQuantity;

	@Builder
	public static class RequiredQuantity {
		
		@JsonProperty("main")
		private Integer main;

		@JsonProperty("main_a")
		private Integer mainA;

		@JsonProperty("main_b")
		private Integer mainB;

		@JsonProperty("main_c")
		private Integer mainC;

		@JsonProperty("main_d")
		private Integer mainD;
		
		@JsonProperty("total_student")
		private Integer totalStudent;
	}
	
	@Builder
	public static class AssignedQuantity {
		
		@JsonProperty("main")
		private Integer main;

		@JsonProperty("main_confirmed")
		private Integer mainConfirmed;

		@JsonProperty("main_temporary")
		private Integer mainTemporary;

		@JsonProperty("backup")
		private Integer backup;

		@JsonProperty("main_a")
		private Integer mainA;

		@JsonProperty("main_b")
		private Integer mainB;

		@JsonProperty("main_c")
		private Integer mainC;

		@JsonProperty("main_d")
		private Integer mainD;
	}
}
