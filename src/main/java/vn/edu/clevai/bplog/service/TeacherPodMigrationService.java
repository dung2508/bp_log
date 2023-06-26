package vn.edu.clevai.bplog.service;

import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.util.List;

public interface TeacherPodMigrationService {
	List<BpPODResponse> migrateSingleTeacher(String username, String ust);

	void migrateTeacherPods(List<UserAccountResponse> teachers);

	void migrateTeacherPods(
			Integer page,
			Integer size
	);

	void migrateAllTeacherPods();
}
