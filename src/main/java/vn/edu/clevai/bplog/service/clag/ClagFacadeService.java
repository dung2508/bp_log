package vn.edu.clevai.bplog.service.clag;

import vn.edu.clevai.common.proxy.bplog.payload.request.Cep100ChangeWsoRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfdlResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpGGResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpWsoResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.ClagResponse;

public interface ClagFacadeService {
	ClagResponse BPGetCLAG(String podCode, String clagTypeCode);

	BpGGResponse BPGetGG(String podCode) throws Exception;

	BpWsoResponse BPGetWSO(String podCode) throws Exception;

	BpDfdlResponse BPGetDFDL(String podCode, String clagTypeCode) throws Exception;
	
	void omStudentChangeWso(Cep100ChangeWsoRequest request) throws Exception ;
}
