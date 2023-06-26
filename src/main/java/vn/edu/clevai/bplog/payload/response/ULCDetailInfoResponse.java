package vn.edu.clevai.bplog.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ULCDetailInfoResponse {

	Integer id;

	@JsonProperty("ulc_code")
	String ulcUlccode;

	@JsonProperty("cap_code")
	String ulcMycap;

	@JsonProperty("lcp_code")
	String ulcLcpcode;

	@JsonProperty("lct_code")
	String ulcMylct;

	@JsonProperty("published")
	String ulcPublished;

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

	List<BpClagClassgroupResponse> clags;

	// TODO: define later!!!
	List<Object> ctis;

	List<ULCDetailInfoResponse> children;
}
