package vn.edu.clevai.bplog.service;

import vn.edu.clevai.common.proxy.lms.payload.response.ClassInfoResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;

import java.util.List;

public interface PermanentClagMigrationService {
	BpClagClassgroupResponse migrateClag(String xclass);

	void migrateClagPage(Integer page, Integer size);

	List<BpClagClassgroupResponse> migrateClags(List<ClassInfoResponse> xclasses);

	void migrateAllClags();
}
