package vn.edu.clevai.bplog.payload.response.teacher.tocap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherQuantityResponse {

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

	@JsonProperty("backup")
	private Integer backup;

	@JsonProperty("total_student")
	private Integer totalStudent;

}
