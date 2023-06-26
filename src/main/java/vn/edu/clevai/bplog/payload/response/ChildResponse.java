package vn.edu.clevai.bplog.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildResponse {
	@JsonProperty("label")
	private String label;
	@JsonProperty("children")
	private List<ChildResponse> children;
	@JsonProperty("enable")
	private Boolean enable;
	@JsonProperty("key")
	private String key;
}
