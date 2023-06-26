package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.enumtype.BpTaskStatusEnum;
import vn.edu.clevai.bplog.config.RegionCacheSupporter;
import vn.edu.clevai.bplog.dto.TaskInfoDTO;
import vn.edu.clevai.bplog.service.BpTaskInfoService;

import java.util.Objects;

@Service
@Slf4j
public class BpTaskInfoServiceImpl implements BpTaskInfoService {

	private final CacheManager cacheManager;

	public BpTaskInfoServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public String getTaskInfo(String name) {
		Cache cache = cacheManager.getCache(RegionCacheSupporter.BP_TASK_CACHE_REGION);

		if (Objects.isNull(cache)) {
			log.warn("Couldn't find cache with name: {}", RegionCacheSupporter.BP_TASK_CACHE_REGION);
			return null;
		}

		TaskInfoDTO dto = cache.get(name, TaskInfoDTO.class);
		return Objects.nonNull(dto) ? dto.getStatus() : null;
	}

	@Override
	public void create(String taskName, BpTaskStatusEnum statusEnum, String errorMsg) {
		putToCache(taskName, TaskInfoDTO.builder()
				.status(statusEnum.name())
				.name(taskName)
				.errMgs(errorMsg)
				.build());
	}

	@Override
	public void delete(String taskName) {
		deleteFromCache(taskName);
	}

	private void deleteFromCache(String taskName) {
		Cache cache = cacheManager.getCache(RegionCacheSupporter.BP_TASK_CACHE_REGION);
		if (Objects.isNull(cache)) {
			log.warn("Couldn't find cache with name: {}", RegionCacheSupporter.BP_TASK_CACHE_REGION);
			return;
		}
		cache.evictIfPresent(taskName);
	}

	private void putToCache(String taskName, TaskInfoDTO dto) {
		Cache cache = cacheManager.getCache(RegionCacheSupporter.BP_TASK_CACHE_REGION);

		if (Objects.isNull(cache)) {
			log.warn("Couldn't find cache with name: {}", RegionCacheSupporter.BP_TASK_CACHE_REGION);
			return;
		}

		cache.put(taskName, dto);
	}
}
