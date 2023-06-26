package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPSetWSORequest {
	@JsonProperty("pod_code")
	@NotBlank
	private String podCode;

	@JsonProperty("wso_code")
	@NotBlank
	private String wsoCode;
}
