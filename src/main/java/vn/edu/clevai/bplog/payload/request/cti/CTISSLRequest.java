package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CTISSLRequest {
	private String sslName;
	private String sslTeacher;
	private String sslStudent;
}

