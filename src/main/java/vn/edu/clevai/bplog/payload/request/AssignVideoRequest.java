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
public class AssignVideoRequest {

	@JsonProperty("x_session_group")
	private String xSessionGroup;

	@JsonProperty("x_cash")
	private String xCash;

	@JsonProperty("assign_usi")
	private String assignUsi;

	@JsonProperty("video_url")
	private String videoUrl;

}