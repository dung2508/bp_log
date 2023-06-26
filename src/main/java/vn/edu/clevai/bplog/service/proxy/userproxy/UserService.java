package vn.edu.clevai.bplog.service.proxy.userproxy;

import org.springframework.http.ResponseEntity;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.util.List;

public interface UserService {
	ResponseEntity<UserAccountResponse> getStudentProfile(String username);

	UserAccountResponse getUserProfile(String username);

	GeneralPageResponse<UserAccountResponse> getWithFilter(String username,
														   String fullName,
														   List<Long> userAccountTypeIds,
														   Integer page,
														   Integer size);

}
