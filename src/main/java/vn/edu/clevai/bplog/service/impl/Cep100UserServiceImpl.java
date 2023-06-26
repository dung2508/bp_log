package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.Cep100UserService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.util.Arrays;

@Service
public class Cep100UserServiceImpl extends BaseProxyService implements Cep100UserService {

	private final UserService userService;

	public Cep100UserServiceImpl(UserService userService) {
		this.userService = userService;
	}

	@Override
	public UserAccountResponse getByUsername(String username) {
		return userService.getUserProfile(username);
	}

	@Override
	public Long getStudentId(String username) {
		return getByUsername(username).getId();
	}
	@Override
	public GeneralPageResponse<UserAccountResponse> findTeachers(String username, Integer page, Integer size) {
		return userService
				.getWithFilter(
						username,
						null,
						Arrays.asList(
								3L, 4L, 15L
						),
						page,
						size
				);
	}

	@Override
	public UserAccountResponse findTeacherFromX(String username){
		return getByUsername(username);
	}
}
