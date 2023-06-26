package vn.edu.clevai.bplog.service;

import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODCLAGResponse;

import java.sql.Timestamp;
import java.text.ParseException;

public interface BPPService {
	void bppAssignGG(Long xdeal);

	void bppAssignCLAGPERM(Long xdeal, String modifyType);

	void bppAssignWSO(Long xdeal);

	void bppAssignDFDL(Long xdeal);

	void bppAssignStudent(Long xdeal);

	void bppChangeGG(Long xdeal);

	void bppChangeDFDL(Long xdeal);

	void bppChangeWSO(Long xdeal);

	void bppPurchase(Long xdeal);

	void bppRenewRepeat(Long xdeal);

	void bppRenewCrossSell(Long xdeal);

	void bppRenewTransfer(Long xdeal1, Timestamp startDateDeal1, Timestamp endDateDeal1,
						  Long xdeal2);

	void bppRenewTopUp(Long xdeal1, Timestamp startDateDeal1, Timestamp endDateDeal1,
					   Long xdeal2);

	void bppDeferAfterSignup(Long xdeal, Timestamp newStartDate, Timestamp newEndDate);

	void bppDeferBeforeSignup(Long xdeal, Timestamp newStartDate, Timestamp newEndDate);

	void bppSuspend(Long xdeal, Timestamp newStartDate, Timestamp newEndDate);

	void bppUnSuspend(Long xdeal, Timestamp newStartDate, Timestamp newEndDate);

	void bppRefund(Long xdeal, Timestamp newStartDate, Timestamp newEndDate);

	void bppExtend(Long xdeal, Timestamp newStartDate, Timestamp newEndDate);

	BpPODCLAGResponse bppAssignCLAGDYN(Long xdeal, String xcady) throws ParseException;

	void bppTransfer(Long xdeal1, Timestamp newStartDate, Timestamp newEndDate);

	void bppTopup(Long xdeal1, Timestamp newStartDate, Timestamp newEndDate);

	void bppUpdatePODCLAG(Long xdeal, Timestamp newStartDate, Timestamp newEndDate, String modifyType);
}
