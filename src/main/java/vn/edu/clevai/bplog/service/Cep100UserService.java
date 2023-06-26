package vn.edu.clevai.bplog.service;

import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

public interface Cep100UserService {
	UserAccountResponse getByUsername(String username);

	Long getStudentId(String username);

	GeneralPageResponse<UserAccountResponse> findTeachers(
			String username,
			Integer page,
			Integer size
	);

	UserAccountResponse findTeacherFromX(String username);
}
