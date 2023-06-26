package vn.edu.clevai.bplog.service;

import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODDFDLResponse;

public interface BpPODDFDLService {

	BpPODDFDLResponse setPOD_DFDL(String podCode, String dfdlCode);
}
