package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitiateCheckingRequest {

	@JsonProperty("cui_event")
	private String cuiEvent;

	@JsonProperty("cti1")
	private String cti1;

	@JsonProperty("cti2")
	private String cti2;

	@JsonProperty("cti3")
	private String cti3;

	@JsonProperty("to_send_email")
	private String toSendEmail;

}