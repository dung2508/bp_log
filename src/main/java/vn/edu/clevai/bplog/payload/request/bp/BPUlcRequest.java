package vn.edu.clevai.bplog.payload.request.bp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPUlcRequest {
	private Timestamp time;

	@JsonProperty("x_dsc")
	private String xDsc;

	private String ulcUlccode;
	private String ulcMyparentulc;
	private String ulcMycap;
	private String ulcLcpcode;
	private String ulcMylct;
	private String ulcMyjoinulc;
	private Boolean ulcPublished;
}
