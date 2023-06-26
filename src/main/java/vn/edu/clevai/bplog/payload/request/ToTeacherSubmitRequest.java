package vn.edu.clevai.bplog.payload.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ToTeacherSubmitRequest {
	
	@JsonProperty("teacher_username")
	private String teacherUsername;
	
	@JsonProperty("session_group_code")
	@NotNull
	private String sessionGroupCode;

	@JsonProperty("live_at")
	@NotNull
	private String liveAt;
}
