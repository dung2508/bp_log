package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.payload.request.bp.BPGetContentsRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPScheduleUssRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPUlcRequest;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.payload.response.ULCDetailInfoResponse;
import vn.edu.clevai.bplog.payload.response.ulc.ULCResponse;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.api.model.MessageResponseDTO;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCLAGULCResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpULCResponse;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface BpULCService {

	List<BpULCResponse> getGESFromX(String xdsc);

	List<BpULCResponse> getUSHFromX(String xdsc);

	List<BpULCResponse> getUDLFromX(String xdsc);

	String findXDSC(String xclass);

	String findXDSC(
			String xsessiongroup,
			String xcash
	);

	@WriteUnitTestLog
	@UnitFunctionName("getCLAGPERM_USH")
	BpULCResponse getCLAGPERM_USH(String clagCode);

	BpULCResponse getCLAGDYN_GES(String clagCode);

	BpULCResponse getCLAGPERM_UDL(String clagCode);

	BpUniqueLearningComponent findByCode(String ulcCode);

	BpCLAGULCResponse setCLAGPERM_USH(String clagCode, String ulcCode);

	BpCLAGULCResponse setCLAGDYN_GES(String clagCode, String ulcCode);

	BpCLAGULCResponse setCLAGPERM_UDL(String clagCode, String ulcCode);

	List<BpULCResponse> getUGEFromX(String xdsc);

	BpULCResponse getCLAGDYN_UGE(String clagdyn);

	BpCLAGULCResponse setCLAGDYN_UGE(String clagdyn, String uge);

	BpCLAGULCResponse setCLAGDYN_UGE_GEDLG(String clagdyn, String uge);

	BpCLAGULCResponse setCLAGDYN_UGE_GEGES(String clagdyn, String uge);

	BpULCResponse setULC(
			String myParent,
			String code,
			String name,
			String myJoinUlc,
			String mylct,
			String mygg,
			String mypt,
			String mycap,
			String mydfdl,
			String mydfge,
			String mylcp,
			String xdsc,
			Integer mynoaschild,
			Boolean published
	);

	void bppScheduleUDLC(BPUlcRequest ulcRequest, BPGetContentsRequest contentsRequest, BPScheduleUssRequest sussReq);

	void scheduleUDLGUDL(BPUlcRequest ulcRequest, BPGetContentsRequest contentsRequest, BPScheduleUssRequest sussReq);

	void scheduleUDLGUGE(BPUlcRequest ulcRequest, BPGetContentsRequest contentsRequest, BPScheduleUssRequest sussReq);

	String generateUlcCode(
			String lcp, String ca, String gg, String dfdl, String dfge,
			String pt, Integer parentIndex, Integer index
	);

	BpULCResponse getUlc(String clagCode, String capCode, String lcpCode);

	BpCLAGULCResponse createOrUpdateClagUlc(String clagCode, String UlcCode);

	List<BpCLAGULC> getByULC(String ulcCode);

	void CreateOrUpdateClagUlc(String clagCode, String UlcCode);

	void collectUDLCParameters(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq);

	void createUDLC(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq);

	void createUGES(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq);

	void collectUDLParameters1(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq);

	void collectUDLParameters2(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq);

	void bppScheduleUSS(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq, String clagType);

	void collectCLAGListDLC(BPScheduleUssRequest sussReq);

	void collectCLAGListDLG(BPScheduleUssRequest sussReq);

	void collectUDLParameters(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq);

	void collectUGEParameters(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq);

	void collectUGEParameters1(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq);

	void collectUGEParameters2(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest
			sussReq);

	void bppScheduleUGES(BPUlcRequest ulcRequest, BPGetContentsRequest contentsRequest, BPScheduleUssRequest
			sussReq);

	void suggestCLAGUGE(BPUlcRequest ulcReq, BPScheduleUssRequest sussReq, String dfgeCode);

	void createUDLG(BPUlcRequest ulcReq, BPGetContentsRequest contentsReq, BPScheduleUssRequest sussReq);

	void setMyJointUdl1(BPUlcRequest ulcReq, BpULCResponse uDLC);

	void setMyJointUdl2(BPUlcRequest ulcReq, BpULCResponse uDLC);

	void bppScheduleUDLC1(BPUlcRequest bpUlcRequest, BPGetContentsRequest
			bpGetContentsRequest, BPScheduleUssRequest bpScheduleUssRequest);

	void setPublishedUSH(String xdsc, boolean published);

	List<String> setPublishedUSS(List<String> ushCodes, boolean published);

	void changeCLAGTEBPPJoinRequestGETE(String xst, Long xpt, String xcash, String xsessionggroup, Timestamp cuieActualtimeFet, boolean published);

	void changeCLAGTEBppChangeClag(String xst, Long xpt, String xcash, String xsessionggroup, Timestamp cuieActualtimeFet, boolean published);

	void changeCLAGTEBppChangeTE(String xst, Long xpt, String xcash, String xsessionggroup, Timestamp cuieActualtimeFet, boolean published);

	void SUIScheduleCAWK(Timestamp date) throws ParseException;

	void scheduleULC(String ulcParentCode, BpLCP lcp, CalendarPeriod cap, String gg, String dfdl, List<BpClagClassgroup> clags,
					 String pt, boolean needCreateKids, List<BpPodProductOfDeal> clagPods,
					 String dfge, Integer parentIndex, CurriculumPeriod parentCup);

	List<BpUniqueLearningComponent> findUlcFromParentUlcAndLcp(String ush, String lcp);

	List<BpUniqueLearningComponent> findUlcFromParentUlcAndLct(String ush, String lct);

	BpUniqueLearningComponent findUBWFromBP(String cady, String lcp, String lct);

	GeneralPageResponse<ULCResponse> getULCSHs(int page, int size, List<String> lctCodes, List<String> ggCodes, List<String> dfdlCodes, String from, String to);

	void publishULCSH(List<Long> ids);

	ULCDetailInfoResponse getUlcDetail(Integer id);

	List<BpUniqueLearningComponent> findUlcByCapFromBp(String capCode, String lcpCode);

	void createCuiULC(String lcp, String cap, String ust);

	void createCuiMainOfULC(String lcp, String cap);

	ULCDetailInfoResponse convertToDetailResponse(BpUniqueLearningComponent u);

	List<BpUniqueLearningComponent> findUlcByCapLcpGgDfdlDfge(String cap, String lcp, String gg, String dfdl, String dfge);

	List<BpUniqueLearningComponent> findUlcByCap(String cap);

	void mergeULC(String gg, String dfdl, CalendarPeriod cap, Boolean isudlm, Boolean isugem);

	void scheduleMPForOM(String clagCode, String podCode) throws Exception;

	void scheduleShift(String pt, String gg, String dfdl, Timestamp timestamp, String lcp);

	List<BpUniqueLearningComponent> findAllUlcFromParentUlc(String ulc);

	void convertBpToX(String ulcCode);

	Boolean convertBpToX(ScheduleRequest scheduleRequest);

	Boolean convertBpToXMonth(ScheduleRequest scheduleRequest);

	Boolean convertBpToXWeek(ScheduleRequest scheduleRequest);

	List<BpUniqueLearningComponent> findAllUgesByCapLcpGgDfdl(String cass, String lcp, String gg, String dfdl);

	BpULCResponse createUBW(CalendarPeriod c) throws Exception;

	MessageResponseDTO scheduleShiftLock(ScheduleRequest scheduleRequest);

	void removeCacheScheduleShift(ScheduleRequest scheduleRequest);

	void removeCacheScheduleMonth(ScheduleRequest scheduleRequest);

	BpUniqueLearningComponent createUlc(String pt, String lck, String claGCode, String currentUsi);

	void scheduleShiftForPodClag(String pt, String gg, String dfdl, Timestamp timestamp, String lcp, List<BpClagClassgroup> clags,
								 Map<String, List<BpPodProductOfDeal>> pods);

	MessageResponseDTO scheduleWeekLock(ScheduleRequest scheduleRequest);

	List<BpUniqueLearningComponent> findAllWcUlces(
			List<String> lcps,
			String cap,
			List<String> ggs,
			List<String> dfdls
	);
}