package vn.edu.clevai.bplog.payload.request.teacher;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherRegisterRequest {
	
	@JsonProperty("adds")
	private List<TeacherRegisterDetailRequest> adds;
	
	@JsonProperty("cancel")
	private List<TeacherRegisterDetailRequest> cancel;
}
