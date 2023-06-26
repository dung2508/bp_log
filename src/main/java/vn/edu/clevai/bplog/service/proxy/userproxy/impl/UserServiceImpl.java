package vn.edu.clevai.bplog.service.proxy.userproxy.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.api.eureka.EurekaDiscoveryClientService;
import vn.edu.clevai.common.api.eureka.LookupStrategyEnum;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;
import vn.edu.clevai.common.proxy.user.proxy.UserServiceProxy;

@Service
public class UserServiceImpl implements UserService {

	@Value(value = "${internal.apigate.services.user.name}")
	private String userServiceName;
	private final UserServiceProxy userServiceProxy;
	private final DiscoveryClient discoveryClient;

	public UserServiceImpl(UserServiceProxy userServiceProxy, DiscoveryClient discoveryClient) {
		this.userServiceProxy = userServiceProxy;
		this.discoveryClient = discoveryClient;
	}

	public URI buildUserServiceUri() {
		return EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient,
				userServiceName, LookupStrategyEnum.RANDOM);
	}

	@Override
	public ResponseEntity<UserAccountResponse> getStudentProfile(String username) {
		return userServiceProxy.getStudentProfile(buildUserServiceUri(), username);
	}

	@Override
	public UserAccountResponse getUserProfile(String username) {
		return userServiceProxy.getUserProfile(buildUserServiceUri(), username).getBody();
	}

	@Override
	public GeneralPageResponse<UserAccountResponse> getWithFilter(String username, String fullName, List<Long> userAccountTypeIds, Integer page, Integer size) {
		return userServiceProxy
				.getWithFilter(
						buildUserServiceUri(),
						username,
						null,
						Arrays.asList(
								3L, 4L, 15L
						),
						page,
						size
				).getBody();
	}
}
