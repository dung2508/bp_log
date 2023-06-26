package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CombineCodeRequest {
	private String ayCode;

	private String trmCode;

	private String ptCode;

	private String ggCode;

	private String wkNo;

	private String dyNo;

	private String dfdlCode;

	private String lctCode;

	private String ssNo;

	private String dfgeCode;

	private String scNo;
}

