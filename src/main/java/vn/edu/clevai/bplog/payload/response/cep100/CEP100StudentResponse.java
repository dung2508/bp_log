package vn.edu.clevai.bplog.payload.response.cep100;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CEP100StudentResponse {

	@JsonProperty("student_di")
	private Long studentId;
}
