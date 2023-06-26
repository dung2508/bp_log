package vn.edu.clevai.bplog.repository;

import static vn.edu.clevai.bplog.config.RegionCacheSupporter.TEN_SECONDS_IN_MILLISECONDS;
import static vn.edu.clevai.bplog.utils.RLockUtils.getRedisLockService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.clevai.bplog.config.RegionCacheSupporter;
import vn.edu.clevai.bplog.entity.BpCLAGULC;
import vn.edu.clevai.bplog.service.RedisLockService;
import vn.edu.clevai.common.api.exception.ConflictException;

public interface BpCLAGULCRepository extends JpaRepository<BpCLAGULC, Long> {

	Optional<BpCLAGULC> findFirstByCode(String code);

	default <S extends BpCLAGULC> S createOrUpdate(S entity) {
		RedisLockService lockService = getRedisLockService();
		String key = lockService.generateCacheLockKey(RegionCacheSupporter.BP_CLAG_ULC, String.valueOf(entity.getCode()));
		try {
			if (!lockService.tryLock(key, TEN_SECONDS_IN_MILLISECONDS, Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
				throw new ConflictException(String.format("Save BpCLAGULC failed with code %s", entity.getCode()));
			}
			if (entity.getId() == null) {
				findFirstByCode(entity.getCode())
						.ifPresent(existed -> {
							entity.setId(existed.getId());
							entity.setMybps(existed.getMybps());
							entity.setPublishbps(existed.getPublishbps());
							entity.setUnpublishbps(existed.getUnpublishbps());
						});
			}
			saveAndFlush(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lockService.unlock(key);
		}
		return entity;
	}


	List<BpCLAGULC> findByMyulc(String ulcCode);

	List<BpCLAGULC> findByMyulcIn(Collection<String> ulcCodes);

}
