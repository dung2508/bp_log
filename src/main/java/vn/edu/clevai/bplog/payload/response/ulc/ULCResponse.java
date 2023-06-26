package vn.edu.clevai.bplog.payload.response.ulc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ULCResponse {

	Long id;

	@JsonProperty("ulc_code")
	String ulcUlccode;

	@JsonProperty("cap_code")
	String ulcMycap;

	@JsonProperty("lcp_code")
	String ulcLcpcode;

	@JsonProperty("lct_code")
	String ulcMylct;

	String ulcMyjointudl;

	@JsonProperty("published")
	Boolean ulcPublished;

	@JsonProperty("start_period")
	Timestamp startPeriod;

	@JsonProperty("end_period")
	Timestamp endPeriod;

	@JsonProperty("dfdl_code")
	String dfdlCode;

	@JsonProperty("dfge_code")
	String dfgeCode;

	@JsonProperty("gg_code")
	String ggCode;
}
