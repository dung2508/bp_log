package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.repository.projection.CLAGDetailInfoProjection;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODCLAGResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.XClassInfoResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.XSessionGroupInfoResponse;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface BpClagClassgroupService {
	BpClagClassgroup findByCode(String code);

	BpClagClassgroupResponse BPSetCLAG(
			String name,
			String mypt,
			String mygg,
			String dfdlCode,
			String wsoCode,
			String mydfge,
			String clagtype,
			Integer maxtotalstudents,
			String description,
			String xclag,
			Integer classIndex
	);

	BpClagClassgroupResponse getCLAGPERMFromX(String xclass);

	String findXCLASS(Long xdeal);

	XClassInfoResponse findXClassInfo(Long xdeal);

	BpClagClassgroupResponse getPOD_CLAGPERM(String pod);

	BpClagClassgroupResponse setCLAGDYN_DFGE(
			String clagdynCode,
			String dfgeCode
	);

	BpPODCLAGResponse setPOD_CLAGPERM(
			String pod,
			String clagperm,
			Date assignedAt,
			Date unassignedAt,
			String membertype,
			String modifyType
	);

	BpClagClassgroupResponse getCLAGDYNFromX(String xsessiongroup, String xcash);

	@WriteUnitTestLog
	@UnitFunctionName("setCLAGDYN")
	BpClagClassgroupResponse createOrUpdateDynamicClag(
			String code,
			String mypt,
			String mygg,
			String mydfdl,
			String mydfge,
			String mywso,
			Integer maxtotalstudents,
			String clagtype,
			String xsessiongroup,
			String xcash
	);

	BpClagClassgroupResponse createOrUpdatePermanentClag(
			String code,
			String mypt,
			String mygg,
			String mydfdl,
			String mywso,
			String clagtype,
			String xclass,
			Integer maxtotalstudents
	);

	BpPODCLAGResponse getPOD_CLAGDYN(String podCode, String capCode);

	XSessionGroupInfoResponse findXSESSIONGROUP(Long xdeal, String xcash);

	BpPODCLAGResponse setPOD_CLAGDYN(String podCode, String clagdyn, Date assignedAt, Date unassignedAt, String membertype);

	String getXCASH(String xcady, Long xgg) throws ParseException;

	BpPODCLAGResponse setPOD_CLAGDYN_TE(String podCode, String xsessionggroup, String xcash, boolean published);

	List<BpClagClassgroup> findBy(String pt, String gg, String dfdl, CalendarPeriod cady, String clagType);

	Long getMyPODSHNo(BpPodProductOfDeal pod, CalendarPeriod cash);

	String getPeriodNo(Long podSHNo);

	List<BpClagClassgroup> findClagFromULC(String ulc);

	void resetAssignToClag(BpPODCLAG podClag, String ulc);

	void bppAssignTEtoCLAG2(String cady, List<String> ust, List<String> pts, List<String> ggs, List<String> dfdls);

	void bppAssignEMToClag(String date, List<String> ust, List<String> pts, List<String> ggs, List<String> dfdls);

	void bppAssignTeToClagCAWK(Timestamp date, List<String> usts, List<String> pts, List<String> ggs, List<String> dfdls) throws ParseException;

	void bppAssignTeToClagCADY(Timestamp date, List<String> usts, List<String> pts, List<String> ggs, List<String> dfdls) throws ParseException;

	void bppAssignEmToClagCADY(Timestamp date, List<String> usts, List<String> pts, List<String> ggs, List<String> dfdls) throws ParseException;

	void bppAssignEmToClagCAWK(Timestamp date, List<String> usts, List<String> pts, List<String> ggs, List<String> dfdls) throws ParseException;


	List<BpClagClassgroup> findClagByUlcAndType(String ulcCode, String clagType);

	List<BpClagClassgroup> createClagDynDfge(
			String pt, String gg, String dfdl, CalendarPeriod cap, BpLCP lcp, List<BpPodProductOfDeal> pods
	);

	List<CLAGDetailInfoProjection> findClagDetailFromULC(String ulcCode);

	List<BpClagClassgroup> findClagByUlcAndPt(String ulcCode, String ptCode);

	List<BpClagClassgroup> findBy(String pt, String gg, String dfdl, String clagType);

	BpClagClassgroup findByXClass(String xclass);

}
