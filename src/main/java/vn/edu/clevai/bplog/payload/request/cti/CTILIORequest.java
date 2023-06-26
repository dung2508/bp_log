package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CTILIORequest {
	private String shiftName;

	// DL
	private CTISSLRequest ctiDlSsl;

	private CTIVDLRequest ctiVdl;

	private CTIRVLRequest ctiRvl;
}

