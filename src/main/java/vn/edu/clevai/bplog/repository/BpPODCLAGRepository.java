package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.config.RegionCacheSupporter;
import vn.edu.clevai.bplog.entity.BpPODCLAG;
import vn.edu.clevai.bplog.service.RedisLockService;
import vn.edu.clevai.common.api.exception.ConflictException;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static vn.edu.clevai.bplog.config.RegionCacheSupporter.TEN_SECONDS_IN_MILLISECONDS;
import static vn.edu.clevai.bplog.utils.RLockUtils.getRedisLockService;

public interface BpPODCLAGRepository extends JpaRepository<BpPODCLAG, Long> {

	Optional<BpPODCLAG> findFirstByCode(String code);

	default <S extends BpPODCLAG> S createOrUpdate(S entity) {
		RedisLockService lockService = getRedisLockService();
		String key = lockService.generateCacheLockKey(RegionCacheSupporter.BP_POD_CLAG, String.valueOf(entity.getCode()));
		try {
			if (!lockService.tryLock(key, TEN_SECONDS_IN_MILLISECONDS, Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
				throw new ConflictException(String.format("Save BpPODCLAG failed with code %s", entity.getCode()));
			}
			if (entity.getId() == null) {
				findFirstByCode(entity.getCode())
						.ifPresent(existed -> entity.setId(existed.getId()));
			}
			saveAndFlush(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lockService.unlock(key);
		}
		return entity;
	}

	@Query(value = "" +
			"select *    " +
			"from bp_pod_clag podClag    " +
			"where mypod = :podCode    " +
			"  and myclag = :clagCode    " +
			"  and assigned_at = :start    " +
			"  and unassigned_at = :end    " +
			"  and myust = :ust    " +
			"limit 1", nativeQuery = true)
	Optional<BpPODCLAG> findByPodClag(String podCode, String clagCode, String ust, Timestamp start, Timestamp end);


	@Query(value = "FROM BpPODCLAG b WHERE b.myUst = :ust AND b.assignedAt = :start AND b.unAssignedAt = :end")
	List<BpPODCLAG> findByUst(String ust, Timestamp start, Timestamp end);

	@Query(value = "" +
			"select podClag.* " +
			"from bp_pod_clag podClag " +
			"where mypod = :pod " +
			"and assigned_at >= ifnull(:from,assigned_at) " +
			"and unassigned_at <= ifnull(:to,unassigned_at) " +
			"and active = :published", nativeQuery = true)
	List<BpPODCLAG> findByPodAndCap(String pod, Timestamp from, Timestamp to, Boolean published);

	@Query(value = "" +
			"select bpc.* " +
			"from bp_pod_clag bpc " +
			"         join bp_cap_calendarperiod bcc on bpc.assigned_at = bcc.startperiod " +
			"    and bpc.unassigned_at = bcc.endperiod " +
			"where bcc.code = :cady " +
			"  and bpc.active " +
			"  and bpc.myust = :ust  " +
			"  and bpc.myclag = :clag", nativeQuery = true)
	List<BpPODCLAG> findByCapAndUstAnndClag(String cady, String ust, String clag);

	@Query(value = "" +
			"select bpc.* " +
			"from bp_pod_clag bpc " +
			"where bpc.active " +
			"  and bpc.myust = :ust  " +
			"  and bpc.myclag = :clag", nativeQuery = true)
	List<BpPODCLAG> findByUstAnndClag(String ust, String clag);

	@Query(value = "" +
			"select bpc.* " +
			"from bp_pod_clag bpc " +
			"where bpc.active " +
			"  and bpc.myust = :ust  " +
			"  and bpc.myclag IN :clags",
			nativeQuery = true)
	List<BpPODCLAG> findByUstAndClagIn(String ust, Collection<String> clags);

	@Query(value = "" +
			"select *  " +
			"from bp_pod_clag  " +
			"where mypod = :pod  " +
			"and membertype = :clagType  " +
			"order by created_at DESC  " +
			"limit 1", nativeQuery = true)
	Optional<BpPODCLAG> findLastByPodAndClagType(String pod, String clagType);

	Optional<BpPODCLAG> findFirstByMypodAndMyclag(String mypod, String myClag);

	@Query(value = "" +
			"select * " +
			"from bp_pod_clag " +
			"where active = :published " +
			"and myclag = :clag " +
			"and mypod = :pod " +
			"and assigned_at >= :assignAt " +
			"and unassigned_at <= :unAssignAt", nativeQuery = true)
	List<BpPODCLAG> findAllByPodAndClagAndCap(String pod, String clag, Timestamp assignAt, Timestamp unAssignAt, Boolean published);

	@Query(value = "" +
			"select podClag.* " +
			"from bp_pod_clag podClag " +
			"join bp_pod_productofdeal pod on pod.code = podClag.mypod " +
			"where pod.myst = :usi " +
			"  and podClag.membertype = :clagType " +
			"order by podClag.created_at DESC " +
			"limit 1", nativeQuery = true)
	Optional<BpPODCLAG> findLastByUsiAndClagType(String usi, String clagType);
}
