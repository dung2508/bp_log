package vn.edu.clevai.bplog.payload.request;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CuiTeacherRequest {

	@JsonProperty("teacher_code")
	@NotBlank
	private String teacherCode;
	
	@JsonProperty("cui_code")
	@NotBlank
	private String cuiCode;
}
