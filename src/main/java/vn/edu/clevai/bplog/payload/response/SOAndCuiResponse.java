package vn.edu.clevai.bplog.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SOAndCuiResponse {

	@JsonProperty("code")
	private String code;

	@JsonProperty("full_name")
	private String fullName;
	
	@JsonProperty("list_te")
	private List<TeacherAndCuiResponse> listTE;
}
