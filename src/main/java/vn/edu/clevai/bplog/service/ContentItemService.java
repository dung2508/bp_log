package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.dto.curriculums.ContentItemDTO;
import vn.edu.clevai.bplog.entity.ContentItem;
import vn.edu.clevai.bplog.entity.CurriculumPeriod;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.payload.request.cti.CTIULCRequest;
import vn.edu.clevai.bplog.repository.projection.*;

import java.util.Collection;
import java.util.List;

public interface ContentItemService {
	void createOrUpdate(List<ContentItemDTO> dtos);

	BpContentItem createOrUpdate(BpContentItem contentItem);

	BpContentItem createOrUpdateCTT_ULC(CurriculumPeriod cup, CTIULCRequest ctiRequest, String lct,
										String originLct, Integer parentNo, Integer mynoaschild);

	BpContentItem findByCode(String code);

	List<RQSProjection> getRQSOrCQSInfo(Collection<String> ulcCodes);

	BpContentItem findUlcCti(String ulc);

	List<String> getAQRCti(Collection<String> ulcCodes);

	List<AQR1InfoProjection> getAQR1Cti(Collection<String> ulcCodes);

	List<AQR2InfoProjection> getAQR2Cti(Collection<String> ulcCodes);

	List<CtiSlideInfoProjection> getUlcSlideUrls(String ulcCode);

	List<DQSProjection> getDqsInfo(List<String> ulcCodes);

	String getUlcVideoUrl(String ulcCode);

	List<BpContentItem> findByCtiParentAndCtt(String cti, String ctt);

	PC40MIInfoProjection getPC40MICti(String ulc);

	List<String> getCQR(Collection<String> ulcCodes);

	List<ContentItem> findByParent(String rootCtiCode);
}
