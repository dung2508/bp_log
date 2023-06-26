package vn.edu.clevai.bplog.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuiDetailResponse {
	
	@JsonProperty("cui_code")
	private String cuiCode;
	
	@JsonProperty("ulc_code")
	private String ulcCode;
	
	@JsonProperty("start_time")
	private Long startTime;
	
	@JsonProperty("end_time")
	private Long endTime;
}
