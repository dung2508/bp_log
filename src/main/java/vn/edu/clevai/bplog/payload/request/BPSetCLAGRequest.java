package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPSetCLAGRequest {
	private String name;

	@NotBlank
	private String mypt;

	@NotBlank
	private String mygg;

	@NotBlank
	private String mydfdl;

	@NotBlank
	private String mywso;

	private String mydfge;

	@NotBlank
	private String clagtype;

	@NotNull
	@Min(0)
	private Integer maxtotalstudents;

	private String description;

	private String xclag;

	@NotNull
	@Min(1)
	@JsonProperty("class_index")
	private Integer classIndex;
}
