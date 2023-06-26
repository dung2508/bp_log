package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrUpdateDynamicClagRequest {
	@NotBlank
	private String code;

	@NotBlank
	private String mypt;

	@NotBlank
	private String mygg;

	@NotBlank
	private String mydfdl;

	@NotBlank
	private String mydfge;

	@NotBlank
	private String mywso;

	@NotBlank
	private String clagtype;

	@NotBlank
	private String xclass;

	@NotBlank
	private String xsessiongroup;

	@NotBlank
	private String xcash;

	@NotNull
	@Min(0)
	private Integer maxtotalstudents;
}
