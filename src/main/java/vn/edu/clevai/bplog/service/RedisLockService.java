package vn.edu.clevai.bplog.service;

import java.util.concurrent.TimeUnit;

public interface RedisLockService {

	/**
	 * @param releaseTime release time
	 * @param timeUnit    time unit
	 * @throws InterruptedException when locked thread got Interrupted
	 */
	void lockInterruptibly(String key, long releaseTime, TimeUnit timeUnit) throws InterruptedException;

	/**
	 * @param waitingTime waiting time
	 * @param releaseTime release time
	 * @param timeUnit    time unit
	 * @return true if success acquired lock
	 * @throws InterruptedException when locked thread got Interrupted
	 */
	boolean tryLock(String key, long waitingTime, long releaseTime, TimeUnit timeUnit) throws InterruptedException;

	/**
	 * @param releaseTime release time
	 * @param timeUnit    time unit
	 */
	void lock(String key, long releaseTime, TimeUnit timeUnit);

	boolean isLocked(String key);

	void lock(String key);

	void unlock(String key);

	String generateCacheLockKey(String... strings);

	void lockWithConfigured(String key) throws Exception;

	void forceUnlock(String key) throws InterruptedException;

	boolean checkKeyWithPrefix(String prefix);
}
