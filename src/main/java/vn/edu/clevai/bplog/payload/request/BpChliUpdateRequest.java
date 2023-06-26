package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BpChliUpdateRequest {
	@JsonProperty("code")
	private String code;

	@JsonProperty("score1")
	private String score1;

	@JsonProperty("score_type1")
	private String scoreType1;

	@JsonProperty("score2")
	private String score2;

	@JsonProperty("score_type2")
	private String scoreType2;

	@JsonProperty("description")
	private String description;
}
