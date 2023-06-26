package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.common.enumtype.CurriculumPeriodEnum;
import vn.edu.clevai.bplog.dto.sheet.*;
import vn.edu.clevai.bplog.entity.CurriculumPeriod;
import vn.edu.clevai.bplog.payload.request.CombineCodeRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.CombineCodeResponse;

import java.sql.Timestamp;
import java.util.List;

public interface CurriculumPeriodService {
	CurriculumPeriod getCUPByCAP(String crpsCode, String capCode, String cupType);

	CurriculumPeriod getCUWK(String crpsCode, String cuwkNo, String cupType);

	CurriculumPeriod getCUDY(String inputCupCode, String cupNo, String cupType);

	CurriculumPeriod getCUDY2(Timestamp time, String crpp, String gg, String cudyNo);

	CurriculumPeriod getCUSH(String inputCupCode, String dfdlCode, String lctCode, String cupType);

	CurriculumPeriod getCup(String parentCup, String dfdl, String cupno, Integer mynoaschild);

	void save(CurriculumPeriod currSession);

	String combineCurrCode(CombineCodeRequest r);

	CombineCodeResponse breakCurrCode(String code);

	CurriculumPeriod findByCodeAndCurrPeriodType(String code, String periodType);

	void saveAbliName(String abliCode, String shiftName);

	void saveCurriculumAbli(String abliCode, String shiftName, List<AbliSheetDTO> abliSheetDTOS);

	void saveCurriculumCoq(String coqCode, String shiftName, List<CoqSheetDTO> listData);

	void saveCurriculumPC(String om, String shiftName, List<CtiPCSheetDTO> listData);

	void saveCurriculumSsl(List<SSLSheetDTO> listData);

	void saveCurriculumHWOm(String spreadsheetCode, String name, List<OnRampProductCRPPDTO> listData);

	CurriculumPeriod findAllByMyParentCupAndLCPPeriod(String code, String lcperiodno, Integer no);

	CurriculumPeriod getOrCreateCup(String pt, String crps, String parentCup, String dfdl, String lct,
									String dfge, String cupNo, Integer noAsChild, CurriculumPeriodEnum cupType);

	CurriculumPeriod getCup(String pt, String crps, String parentCup, String dfdl, String lct,
							String dfge, String cupNo, Integer noAsChild, CurriculumPeriodEnum cupType);

	CurriculumPeriod getCUP(String crps, String cupno, String cupType);

	CurriculumPeriod getCUP(String crps, String cupno, int mynoaschild, String cupType);
}
