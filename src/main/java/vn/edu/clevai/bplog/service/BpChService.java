package vn.edu.clevai.bplog.service;

import java.util.List;

import vn.edu.clevai.bplog.payload.request.BpChliUpdateRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpChliResponse;

/**
 * @author trungnt9
 *
 */
public interface BpChService {
	/**
	 * @param chliCode
	 * @return
	 */
	BpChliResponse getCHLTFromCHLI(String chliCode);
	
	/**
	 * @param requests
	 */
	void updateByListChli(List<BpChliUpdateRequest> requests);
}
