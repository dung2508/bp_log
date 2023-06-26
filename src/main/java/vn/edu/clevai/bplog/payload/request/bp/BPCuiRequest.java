package vn.edu.clevai.bplog.payload.request.bp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPCuiRequest {
	private String cuiCuicode;
	private String cuiUlccode;
	private String cuiMylcp;
	private String cuiUsicode;
	private String cuiUst;
	private String cuiMycti;
	private String cuiPodpcode;
	private String cuiMypodpparent;
	private String cuiMypod;
	private Boolean cuiPublished;
}
