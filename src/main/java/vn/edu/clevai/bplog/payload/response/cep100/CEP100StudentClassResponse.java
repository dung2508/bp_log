package vn.edu.clevai.bplog.payload.response.cep100;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CEP100StudentClassResponse {

	@JsonProperty("class_id")
	private Long classId;

	@JsonProperty("class_code")
	private String classCode;

	@JsonProperty("class_name")
	private String className;
}
