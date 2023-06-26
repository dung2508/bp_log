package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPSetGGRequest {
	@JsonProperty("pod_code")
	@NotBlank
	private String podCode;

	@JsonProperty("gg_code")
	@NotBlank
	private String ggCode;
}
