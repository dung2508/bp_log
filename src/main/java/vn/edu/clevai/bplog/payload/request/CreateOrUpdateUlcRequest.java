package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrUpdateUlcRequest {
	@NotBlank
	private String code;

	private String myParent;

	private String name;

	@JsonProperty("my_join_ulc")
	private String myJoinUlc;

	@NotBlank
	private String mylct;

	@NotBlank
	private String mygg;

	private String mypt;

	@NotBlank
	private String mycap;

	@NotBlank
	private String mydfdl;

	@NotBlank
	private String mydfge;

	@NotBlank
	private String mylcp;

	private String xdsc;

	private Boolean published;
}
