package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpDfgeDifficultget;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfgeDifficultgetResponse;

import java.util.List;

public interface BpDfgeDifficultgetService {
	BpDfgeDifficultgetResponse getDFGEFromX(String xDfge);

	BpDfgeDifficultget findByCode(String code);

	String findXDFGE(String xsessiongroup, String xcash);

	BpDfgeDifficultgetResponse getCLAGDYN_DFGE(String clagdynCode);

	List<BpDfgeDifficultget> findAll();

}
