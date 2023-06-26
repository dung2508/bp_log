package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import vn.edu.clevai.bplog.service.RedisLockService;
import vn.edu.clevai.common.api.exception.UnableToAcquireLockException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisLockServiceImpl implements RedisLockService {

	@Value("${cache.redis.lock.waiting-time}")
	private long configuredWaitingTime;

	@Value("${spring.application.name}")
	private String appName;

	private final RedissonClient redissonClient;

	private final Environment environment;


	public RedisLockServiceImpl(RedissonClient redissonClient,
                                Environment environment) {
		this.redissonClient = redissonClient;
		this.environment = environment;
	}

	@Override
	public void lockInterruptibly(String key, long releaseTime, TimeUnit timeUnit) throws InterruptedException {
		RLock rLock = redissonClient.getLock(key);
		rLock.lockInterruptibly(releaseTime, timeUnit);
	}

	@Override
	public boolean tryLock(String key, long waitingTime, long releaseTime, TimeUnit timeUnit) throws InterruptedException {
		RLock rLock = redissonClient.getLock(key);
		return rLock.tryLock(waitingTime, releaseTime, timeUnit);
	}

	@Override
	public void lock(String key, long releaseTime, TimeUnit timeUnit) {
		RLock rLock = redissonClient.getLock(key);
		rLock.lock(releaseTime, timeUnit);
	}

	@Override
	public boolean isLocked(String key) {
		RLock rLock = redissonClient.getLock(key);
		return rLock.isLocked();
	}

	@Override
	public void lock(String key) {
		RLock rLock = redissonClient.getLock(key);
		rLock.lock();
	}

	@Override
	public void unlock(String key) {
		RLock rLock = redissonClient.getLock(key);

		if (Objects.nonNull(rLock)
				&& rLock.isLocked()
				&& rLock.isHeldByCurrentThread()) {
			rLock.unlock();
		} else {
			log.warn("Unable to unlock with key: {} by current thread!!!", key);
		}
	}

	@Override
	public String generateCacheLockKey(String... strings) {
		return String.join("-", environment.getActiveProfiles()) + "-"
				+ appName + "-"
				+ String.join("-", strings);
	}

	@Override
	public void lockWithConfigured(String key) throws Exception {
		RLock rLock = redissonClient.getLock(key);

		if (!rLock.tryLock(configuredWaitingTime, TimeUnit.SECONDS)) {
			String message = "Thread: " + Thread.currentThread().getName()
					+ " unable to acquire lock with key: " + key
					+ " for " + configuredWaitingTime
					+ " " + TimeUnit.SECONDS.name();
//			slackService.notifySlack(message);
			throw new UnableToAcquireLockException(message);
		}
	}

	@Override
	public void forceUnlock(String key) {
		RLock rLock = redissonClient.getLock(key);
		if (rLock.forceUnlock()) {
			log.info("Success force unlock for key: {} using force unlock async", key);
		} else {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to force unlock for key: " + key);
		}
	}

	public boolean checkKeyWithPrefix(String prefix){
		RKeys keys = redissonClient.getKeys();
		Iterable<String> keysByPattern = keys.getKeysByPattern(prefix);
		return keysByPattern.iterator().hasNext();


	}

}
