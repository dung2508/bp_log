package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.config.RegionCacheSupporter;
import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.repository.projection.CLAGDetailInfoProjection;
import vn.edu.clevai.bplog.service.RedisLockService;
import vn.edu.clevai.common.api.exception.ConflictException;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static vn.edu.clevai.bplog.config.RegionCacheSupporter.TEN_SECONDS_IN_MILLISECONDS;
import static vn.edu.clevai.bplog.utils.RLockUtils.getRedisLockService;

public interface BpClagClassgroupRepository extends JpaRepository<BpClagClassgroup, Integer> {
	Optional<BpClagClassgroup> findByCode(String code);

	Optional<BpClagClassgroup> findFirstByCode(String code);

	boolean existsByCode(String code);

	default <S extends BpClagClassgroup> S createOrUpdate(S entity) {
		RedisLockService lockService = getRedisLockService();
		String key = lockService.generateCacheLockKey(RegionCacheSupporter.BP_CLAG_CLASSGROUP, String.valueOf(entity.getCode()));
		try {
			if (!lockService.tryLock(key, TEN_SECONDS_IN_MILLISECONDS, Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
				throw new ConflictException(String.format("Save BpClagClassgroup failed with code %s", entity.getCode()));
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
			"select clag.*  " +
			"from bp_clag_classgroup  clag " +
			"join bp_clag_ulc bcu on clag.code = bcu.myclag " +
			"join bp_ulc_uniquelearningcomponent buu on bcu.myulc = buu.code " +
			"where clag.active " +
			"and buu.code = :ulcCode ", nativeQuery = true)
	List<BpClagClassgroup> findByUlc(String ulcCode);

	@Query(nativeQuery = true, value =
			"WITH temp AS (SELECT bpc.mypod, b.code, b.startperiod, MAX(c.id) AS isJoin " +
					"              FROM bp_pod_clag bpc " +
					"                       INNER JOIN bp_clag_classgroup bcc on bpc.myclag = bcc.code AND bpc.mypod = :podCode AND bcc.active AND bpc.active " +
					"                       INNER JOIN bp_clag_ulc bcu on bcc.code = bcu.myclag " +
					"                       INNER JOIN bp_ulc_uniquelearningcomponent buu on bcu.myulc = buu.code AND buu.published " +
					"                       INNER JOIN bp_cap_calendarperiod b on buu.mycap = b.code " +
					"                       INNER JOIN bp_cap_calendarperiod b2 ON b2.code = :capCode AND b.startperiod <= b2.startperiod " +
					"                       LEFT JOIN bp_cui_content_user_ulc_instance bccuui on buu.code = bccuui.myulc AND bccuui.published " +
					"                       LEFT JOIN bp_cuie_cuievent c " +
					"                                 on bccuui.code = c.mycui AND c.mylcet_lceventtype = 'DR-JN-JRQ' AND c.published " +
					"              GROUP BY bpc.mypod, b.code, b.startperiod), " +
					"     temp2 AS ( " +
					"         SELECT *, ROW_NUMBER() over (PARTITION BY mypod ORDER BY startperiod) AS shNo " +
					"         FROM temp " +
					"         WHERE isJoin " +
					"     ) " +
					"SELECT shNo " +
					"FROM temp2 " +
					"WHERE code = :capCode")
	Long getMyPODSHNo(String podCode, String capCode);

	@Query(nativeQuery = true, value =
			"SELECT DISTINCT bcc.* " +
					"FROM bp_clag_classgroup bcc " +
					"         INNER JOIN bp_pod_clag bpc on bcc.code = bpc.myclag " +
					"    AND bpc.active " +
					"    AND bcc.active " +
					"WHERE bcc.mypt = :pt " +
					"  AND bcc.mygg = :gg " +
					"  AND bcc.mydfdl = :dfdl " +
					"  AND bcc.clagtype = :clagType " +
					"  AND bcc.mywso LIKE CONCAT('%', :partOfWso, '%') " +
					"  AND :start BETWEEN bpc.assigned_at AND bpc.unassigned_at " +
					"  AND bcc.maxtotalstudents > 0 ")
	List<BpClagClassgroup> findBy(String pt, String gg, String dfdl, String partOfWso, String clagType, Timestamp start);

	@Query(nativeQuery = true, value =
			"SELECT bcc.* " +
					"FROM bp_clag_ulc bcu " +
					"         INNER JOIN bp_clag_classgroup bcc on bcu.myclag = bcc.code " +
					"    AND bcu.myulc = :ulcCode " +
					"    AND bcc.clagtype = :clagType " +
					"    AND bcc.active")
	List<BpClagClassgroup> findClagByUlcAndType(String ulcCode, String clagType);

	@Query(value = "" +
			"select clag.*   " +
			"from bp_clag_classgroup clag   " +
			"join bp_clag_ulc bcu on clag.code = bcu.myclag   " +
			"join bp_ulc_uniquelearningcomponent buu on bcu.myulc = buu.code   " +
			"where clag.active   " +
			"and buu.published   " +
			"and clag.mypt = :ptCode   " +
			"and buu.code = :ulcCode", nativeQuery = true)
	List<BpClagClassgroup> findClagByULcAndPt(String ulcCode, String ptCode);

	List<BpClagClassgroup> findByCodeInAndActiveTrue(Collection<String> codes);

	List<BpClagClassgroup> findByClagtypeAndActiveTrueAndMywso(String clagType, String mywso);

	List<BpClagClassgroup> findByCodeIn(Collection<String> codes);

	@Query(nativeQuery = true, value =
			"WITH clag AS (SELECT bcc.*, " +
					"                     IFNULL(SUM(IF(buu.myust = 'ST', 1, 0)), 0) AS totalActiveStudents, " +
					"                     MAX(IF(buu.myust = 'TE', buu.id, null))    AS teId " +
					"              FROM bp_clag_classgroup bcc " +
					"                       INNER JOIN bp_clag_ulc bcu ON bcc.code = bcu.myclag " +
					"                       INNER JOIN bp_ulc_uniquelearningcomponent u on bcu.myulc = u.code " +
					"                       INNER JOIN bp_cap_calendarperiod b on u.mycap = b.code " +
					"                       LEFT JOIN bp_pod_clag bpc ON bcc.code = bpc.myclag " +
					"                  AND b.startperiod BETWEEN bpc.assigned_at AND bpc.unassigned_at " +
					"                  AND bpc.active " +
					"                       LEFT JOIN bp_pod_productofdeal bpp ON bpc.mypod = bpp.code " +
					"                       LEFT JOIN bp_usi_useritem buu ON bpp.myst = buu.code " +
					"                  AND buu.myust IN ('ST', 'TE') " +
					"              WHERE bcu.myulc = :ulcCode " +
					"              GROUP BY bcc.id) " +
					"SELECT clag.*, " +
					"       te.username AS teUsername, " +
					"       te.fullname AS teFullName, " +
					"       te.avatar   AS teAvatar " +
					"FROM clag " +
					"         LEFT JOIN bp_usi_useritem te ON clag.teId = te.id")
	List<CLAGDetailInfoProjection> findClagDetailFromULC(String ulcCode);

	List<BpClagClassgroup> findAllByMyptAndMyggAndMydfdlAndClagtypeAndActiveTrue(String pt, String gg, String dfdl, String clagType);

	@Query("FROM BpClagClassgroup clag WHERE clag.xclass = :xclass")
	BpClagClassgroup findByXCLass(String xclass);
}