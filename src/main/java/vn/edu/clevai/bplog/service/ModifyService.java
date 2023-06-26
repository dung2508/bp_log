package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.CalendarPeriod;

public interface ModifyService {
	void bppSyncST(String oldPodClag, String newPodClag, String modifyType);

	void bppSyncTE(String clag, String oldUsi, String newUsi, String cady);

	void bppSyncTE(String oldUsi, String newUsi, CalendarPeriod cady, String pt);
}
