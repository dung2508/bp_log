package vn.edu.clevai.bplog.payload.response;

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
public class SessionOperatorAndCuiResponse {

	@JsonProperty("cui_code")
	private String cuiCode;

	@JsonProperty("lcp_code")
	private String lcpCode;

	@JsonProperty("start_time")
	private Timestamp startTime;

	@JsonProperty("gg_code")
	private String ggCode;

	@JsonProperty("dfdl_code")
	private String dfdlCode;

	@JsonProperty("dfge_code")
	private String dfgeCode;

	@JsonProperty("usi_code")
	private String usiCode;

	@JsonProperty("usi_full_name")
	private String usiFullName;

	@JsonProperty("usi_phone")
	private String usiPhone;

}