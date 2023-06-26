package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CTIULCRequest {
	private String shiftName;

	private List<CTIBL3Request> ctibl3;

	// DL
	private CTISSLRequest ctiDlSsl;

	private CTIVDLRequest ctiVdl;

	private CTIRVLRequest ctiRvl;

	private List<CTIDSCRequest> ctiDsc;

	// RC
	private CTIRCRequest ctiRc;

	// HRG
	private CTIHRGRequest ctiHrg;

	// HAV
	private CTIHAVRequest ctiHav;

	// GE
	private CTISSLRequest ctiGeSsl;

	// RLOM
	private CTIRLRequest ctiRLOM;

	// PC
	private CTIPCRequest ctiPc;
}

