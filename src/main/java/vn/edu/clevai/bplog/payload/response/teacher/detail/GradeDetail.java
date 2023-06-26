package vn.edu.clevai.bplog.payload.response.teacher.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeDetail {
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("grade_code")
	private String code;
	
	@JsonProperty("grade_name")
	private String name;
}
