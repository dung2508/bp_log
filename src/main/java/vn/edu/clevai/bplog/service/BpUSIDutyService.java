package vn.edu.clevai.bplog.service;


import vn.edu.clevai.bplog.dto.sheet.UsidRegisterSO5DTO;
import vn.edu.clevai.bplog.entity.BpUsiDuty;
import vn.edu.clevai.bplog.entity.UsiDutyClassCategory;
import vn.edu.clevai.bplog.payload.request.RegisterEMRequest;
import vn.edu.clevai.bplog.repository.projection.UsidDistinctInfoProjection;
import vn.edu.clevai.common.proxy.bplog.payload.request.SetUsiDutyFromXRequest;

import java.util.List;

public interface BpUSIDutyService {

	BpUsiDuty setUsiDFromX(
			String lcet, String lcp, String ust, String cap, String chrt, String usi, String gg, String dfdl, String dfge);

	void registerTeToCap(SetUsiDutyFromXRequest request);

	List<String> findUsi(String lcet, String ust, String cap, String chrt, String lcp, String excludeUsi, String gg, String dfdl, String dfge);

	BpUsiDuty setUsiDutyFromX(String lcp, String cap, String ust, String usi, String lcet, String chrt);

	UsiDutyClassCategory setUsiDutyClc(String clcCode, String usiDutyCode, Boolean isEnable);

	String getWSOFromListDay(List<String> listDay);

	List<String> findUsiFromBp(String pt, String cady, String ust, String gg, String dfdl,
							   String dfge, String lcp, String position);

	List<String> findRegister5Users(String pt, List<String> cap, String ust, String gg, String dfdl,
									String dfge, String lcp, String position);

	List<String> findUsiBackupByCady(String cady);

	List<String> findEmFromCady(String cady, String lcet, String ust, String lcp);

	void registerEMDiLive(RegisterEMRequest registerEMRequest);

	void registerEMGET(RegisterEMRequest registerRequest);

	List<UsidDistinctInfoProjection> findDistinctInfo(String ay);

	List<BpUsiDuty> findBy(String pt, String gg, String dfdl, String cap, String lcp, String ust);

	BpUsiDuty findCashStart(String ay, String term, String pt, String gg, String dfdl);

	void doImport(List<UsidRegisterSO5DTO> listData);

	List<BpUsiDuty> findAllConfigurationsByPtAndAccYearAndTerm(
			String pt,
			String ay,
			String term
	);
}
