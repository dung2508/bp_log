package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpLCP;

import java.util.List;

public interface BpLCPService {

	List<BpLCP> findAllForSchedulingMc();

	List<BpLCP> findAllForSchedulingWc();

	BpLCP findByCode(String code);

	BpLCP findFirstByMyptAndMylct(String mypt, String mylct);

	BpLCP findShiftLcp(String mypt, String mylct);

	List<BpLCP> findLCPKids(String parentLct);

	BpLCP findLcpFromPtUst(String pt, String ust, String lcl);

	List<BpLCP> findLCPSHByPTFromBP(String pt);

	BpLCP findLcpFromPtLck(String pt, String lck, String lcl);

	BpLCP findLcpSSByPtAndLct(String pt, String lctSS);

	BpLCP findLcpSSByPtAndGE(String pt);

	BpLCP findLcpshByPtAndLct(String pt, String lctShift);

	BpLCP findByParentLctAndLct(String lctParent, String lct);

	String findPtFromLcpAndLck(String lcp, String lck);

	List<BpLCP> findLCWK(String pt, String lcl);

	List<BpLCP> findByMylctparentToSchedule(String parentLct);

	List<BpLCP> findLCPKids(String parentLct, String lcl);

	BpLCP findLcpSsForOMByLCK(String lck);

	BpLCP findWcByPt(String pt);

	List<BpLCP> findWcByPts(List<String> pts);

	BpLCP findUlcSL(String pt, String lck);
}
