package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class CurriculumPeriodFilterRequest {
	private String crpsCode;

	private String inputCupCode;

	@NotNull
	private String cupType;

	private String capCode;

	private String cupNo;

	private String dfdlCode;

	private String lctCode;

	private String dfgeCode;
}

