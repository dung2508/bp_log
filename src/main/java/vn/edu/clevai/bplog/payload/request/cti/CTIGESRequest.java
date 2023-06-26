package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CTIGESRequest {
	// GE
	private CTISSLRequest ctiGeSsl;

	// HW
	private CTIHRGRequest ctiHrg;

	private CTIHAVRequest ctiHav;
}

