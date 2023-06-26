package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherFinishClassRequest {

	@JsonIgnore
	private String teacherCode;

	@JsonProperty("x_session_group")
	private String xSessionGroup;

	@JsonProperty("x_cash")
	private String xCash;

	@JsonProperty("bet_time")
	private Timestamp betTime;

	@JsonProperty("fet_time")
	private Timestamp fetTime;

}