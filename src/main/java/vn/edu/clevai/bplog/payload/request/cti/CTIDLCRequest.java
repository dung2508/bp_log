package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CTIDLCRequest {
	private String shiftName;

	private List<CTIBL3Request> ctibl3;

	// DL
	private CTISSLRequest ctiDlSsl;

	private CTIVDLRequest ctiVdl;

	private CTIRVLRequest ctiRvl;

	private List<CTIDSCRequest> ctiDsc;

	// RC
	private CTIRCRequest ctiRc;

	// HW
	private CTIHRGRequest ctiHrg;

	private CTIHAVRequest ctiHav;
}

