package vn.edu.clevai.bplog.payload.request.bp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPClagRequest {
	private String clagCode;

	private String clagDfge;

	@JsonProperty("x_clag")
	private String xClag;

	private String clagTe;
}
