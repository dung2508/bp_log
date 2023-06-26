package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CTIDLRequest {
	private CTISSLRequest ctiSsl;

	private CTIVDLRequest ctiVdl;

	private CTIRVLRequest ctiRvl;

	private List<CTIDSCRequest> ctiDsc;
}

