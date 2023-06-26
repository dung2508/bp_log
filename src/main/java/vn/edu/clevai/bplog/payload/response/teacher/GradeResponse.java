package vn.edu.clevai.bplog.payload.response.teacher;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponse {
	private Integer grade;
	
	@JsonProperty("grade_code")
	private String gradeCode;
	
	@JsonProperty("origin_name")
	private String originName;
}
