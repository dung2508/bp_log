package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCHLIRequest {
	@JsonProperty(value = "CHLT_code")
	private String CHlTCode;

	@JsonProperty(value = "CHLI_code")
	private String CHLICode;

	@JsonProperty(value = "CHSI_code")
	private String CHSICode;
}
