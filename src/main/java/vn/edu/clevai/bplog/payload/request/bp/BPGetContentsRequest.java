package vn.edu.clevai.bplog.payload.request.bp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPGetContentsRequest {
	private String gcAcyear;
	private String gcTerm;
	private String gcPt;
	private String gcGg;

	@JsonProperty("x_gg")
	private Long xGg;

	private String gcWkno;
	private String gcDyno;
	private String gcCrpp;
	private String gcCrps;
	private String gcCuwk;
	private String gcCudy;
	private String gcCush;
	private String gcDfdl;

	@JsonProperty("x_dfdl")
	private Integer xDfdl;

	private String gcLct;
	private String gcSsno;
	private String gcDfge;
	private String gcScno;
	private String gcCuss;
	private String gcCusc;
	private String gcSsl;
	private String gcBl3qg;
	private String gcBl5qp1;
	private String gcBl5qp2;
	private String gcBl5qp3;
}
