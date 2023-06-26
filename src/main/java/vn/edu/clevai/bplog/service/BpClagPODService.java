package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.BpPODCLAG;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagPODResponse;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface BpClagPODService {
	@WriteUnitTestLog
	BpClagPODResponse BPSetCLAGPOD(String podCode, String clagCode, String clagTypeCode);

	BpPODCLAG setPODClag(String podCode, String clagCode, String cap, String ust);

	void unpublishedPodClag(BpPODCLAG podclag);

	void updateStatusByCadyAndUst(CalendarPeriod cap, String ust, boolean active);

	void setPodListClagList(List<BpPodProductOfDeal> podList, List<String> clagList, String ust, Map<String, String> mapClagUlc);

	List<BpPODCLAG> findByPodAndCap(String pod, Timestamp from, Timestamp to, Boolean published);

	List<BpPODCLAG> findByCadyAndUstAndClag(String cady, String ust, String clag);

	List<BpPODCLAG> findByUstAndClag(String ust, String clag);

	BpPODCLAG findByCode(String code);

	List<BpPODCLAG> findByPodAndClagAndPublishedAndCady(String pod, String clag, Boolean published, CalendarPeriod cady);

	void save(BpPODCLAG podclag);

	String getPodClagCode(String pod, String clag, Timestamp start, Timestamp end);

}
