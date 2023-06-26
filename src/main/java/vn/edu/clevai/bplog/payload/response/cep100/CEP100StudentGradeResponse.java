package vn.edu.clevai.bplog.payload.response.cep100;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CEP100StudentGradeResponse {
	@JsonProperty("grade_id")
	private Integer gradeId;
}
