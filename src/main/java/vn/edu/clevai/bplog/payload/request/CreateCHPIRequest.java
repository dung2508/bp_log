package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCHPIRequest {

	@NotNull
	@JsonProperty(value = "CHPT_code")
	private String CHPTCode;

	@NotNull
	@JsonProperty(value = "CTI1_code")
	private String CTI1Code;

	@NotNull
	@JsonProperty(value = "CTI2_code")
	private String CTI2Code;

	@NotNull
	@JsonProperty(value = "CTI3_code")
	private String CTI3Code;

	@JsonProperty(value = "ToSendEmail")
	private String toSendEmail;

	@NotNull
	@JsonProperty(value = "CUIEvent_code")
	private String CUIEventCode;
}
