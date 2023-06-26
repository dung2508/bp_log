package vn.edu.clevai.bplog.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountReportResponse {
	String usi;
	String fullname;
	String cuiCode;
	Long startTime;
	Long endTime;
	String day;
	@JsonProperty("join_time")
	Long joinTime;
	Long substantTime;
	Long finishTime;
	
}
