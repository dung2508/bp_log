package vn.edu.clevai.bplog.payload.request;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import vn.edu.clevai.bplog.annotation.BPLogParamName;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class TeacherSubmitReportRequest {

	@JsonProperty("session_group_code")
	@NotNull
	@BPLogParamName("session_group_code")
	private String sessionGroupCode;

	@JsonProperty("live_at")
	@NotNull
	@BPLogParamName("live_at")
	private String liveAt;
	
	@NotEmpty
	@JsonProperty("students")
	private List<TeacherSubmitReportStudentRequest> students;

}
