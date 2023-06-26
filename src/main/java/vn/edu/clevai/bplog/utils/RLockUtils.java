package vn.edu.clevai.bplog.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.service.RedisLockService;

@Component
public class RLockUtils {

	private static RedisLockService redisLockService;

	public RLockUtils(@Qualifier("redisLockServiceImpl") RedisLockService redisLockService) {
		RLockUtils.redisLockService = redisLockService;
	}

	public static RedisLockService getRedisLockService() {
		return redisLockService;
	}

}
