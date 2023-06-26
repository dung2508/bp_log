package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetPublishedUSHRequest {

	@JsonProperty("xdsc")
	private String xdsc;

	@JsonProperty("published")
	private boolean published;
}
