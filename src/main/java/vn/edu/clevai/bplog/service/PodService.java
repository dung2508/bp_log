package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.payload.request.MigrationRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;

import java.util.List;


public interface PodService {
	BpPTProductType getPTFromX(Long xptId);

	BpPTProductType getPOD_PT(String podCode);

	BpPodProductOfDeal setPOD_PT(String podCode, String ptCode);

	Integer findXPT(Integer xdeal);

	PODResponse setPOD_ST(String podCode, String stCode);

	BpUsiUserItemResponse getPOD_ST(String podCode);

	String findXST(Long xdeal);

	BpUsiUserItemResponse getSTFromX(String xst);

	BpPODResponse setPOD
			(String podCode, String ptCode, String stCode, String prdCode, java.sql.Date fromDate, java.sql.Date endDate, Long xdeal);

	void migratePod(Long dealIdLt);

	void migratePodDfdl(MigrationRequest request);

	void migratePodDfdl(String podCode);

	void migratePodWso(Long fromId, Long toId, Integer size);

	void migratePodClagperm(MigrationRequest request);

	void migratePodClagperm(String podCode);

	List<BpPodProductOfDeal> getPod(Long fromId, Long toId, Integer size);

}
