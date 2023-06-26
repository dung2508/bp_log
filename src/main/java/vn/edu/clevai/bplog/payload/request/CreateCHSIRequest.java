package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCHSIRequest {

	@NotNull
	@JsonProperty(value = "CHST_code")
	private String chstCode;

	@NotNull
	@JsonProperty(value = "CHPI_code")
	private String chpiCode;
}
