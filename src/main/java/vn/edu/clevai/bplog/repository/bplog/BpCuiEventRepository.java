package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.config.RegionCacheSupporter;
import vn.edu.clevai.bplog.entity.logDb.BpCuiEvent;
import vn.edu.clevai.bplog.repository.projection.SessionOperatorAndCuiProjection;
import vn.edu.clevai.bplog.service.RedisLockService;
import vn.edu.clevai.common.api.exception.ConflictException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static vn.edu.clevai.bplog.config.RegionCacheSupporter.TEN_SECONDS_IN_MILLISECONDS;
import static vn.edu.clevai.bplog.utils.RLockUtils.getRedisLockService;

public interface BpCuiEventRepository extends JpaRepository<BpCuiEvent, Integer> {

	List<BpCuiEvent> findBpCuiEventsByCode(String code);

	Optional<BpCuiEvent> findFirstByCode(String code);

	List<BpCuiEvent> findByMyCuiCode(String myCui_code);

	boolean existsByCode(String code);

	@Query(value = "" +
			"select buu.code  " +
			"from bp_cuie_cuievent bcc " +
			"   join bp_cui_content_user_ulc_instance bccuui on bcc.mycui = bccuui.code " +
			"   join bp_usi_useritem buu on bccuui.myusi = buu.code " +
			"where bcc.code = :code " +
			"limit 1", nativeQuery = true)
	String findUSICodeByCUIEvent(String code);

	@Query(value = " " +
			"select  *  " +
			"from bp_cuie_cuievent cuie  " +
			"where cuie.mycui = :cuiCode  " +
			"and cuie.mylcet_lceventtype in :lcetList", nativeQuery = true)
	List<BpCuiEvent> findByCuiAndLcet(String cuiCode, List<String> lcetList);

	default <S extends BpCuiEvent> S createOrUpdate(S entity) {
		RedisLockService lockService = getRedisLockService();
		String key = lockService.generateCacheLockKey(RegionCacheSupporter.BP_CUIE_CUIEVENT, String.valueOf(entity.getCode()));
		try {
			if (!lockService.tryLock(key, TEN_SECONDS_IN_MILLISECONDS, Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
				throw new ConflictException(String.format("Save BpCuiEvent failed with code %s", entity.getCode()));
			}
			if (entity.getId() == null) {
				findFirstByCode(entity.getCode())
						.ifPresent(existed -> {
							entity.setId(existed.getId());
							entity.setPlanbpe(existed.getPlanbpe());
							entity.setActualbpe(existed.getActualbpe());
							entity.setPublishbpe(existed.getPublishbpe());
							entity.setUnpublishbpe(existed.getUnpublishbpe());
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

	Optional<BpCuiEvent> findByMyUsiAndMyLcetCodeAndEventPlanTimeBetween
			(String usi, String lcetCode, Date from, Date to);

	@Query(
			value = "WITH o_ulc AS " +
					"         (SELECT ulc.code, " +
					"                 cap.startperiod " +
					"          FROM bp_ulc_uniquelearningcomponent ulc " +
					"                   JOIN bp_cui_content_user_ulc_instance cui ON cui.myulc = ulc.code " +
					"                   JOIN bp_cap_calendarperiod cap ON ulc.mycap = cap.code " +
					"          WHERE cui.myusi = :username " +
					"            AND DATE(cap.startperiod + INTERVAL 7 HOUR) = :date " +
					"            AND cui.published) " +
					"SELECT cui.code           AS cuiCode, " +
					"       ulc.mylcp          AS lcpCode, " +
					"       o_ulc.startperiod AS startTime, " +
					"       ulc.mygg           AS ggCode, " +
					"       ulc.mydfdl         AS dfdlCode, " +
					"       ulc.mydfge         AS dfgeCode, " +
					"       usi.code           AS usiCode, " +
					"       usi.fullname       AS usiFullname, " +
					"       usi.phone          AS usiPhone " +
					"FROM o_ulc " +
					"         JOIN bp_ulc_uniquelearningcomponent ulc ON ulc.code = o_ulc.code " +
					"         JOIN bp_cui_content_user_ulc_instance cui ON cui.myulc = ulc.code " +
					"         JOIN bp_usi_useritem usi ON cui.myusi = usi.code " +
					"WHERE usi.myust = 'TE'",
			nativeQuery = true)
	Page<SessionOperatorAndCuiProjection> findSessionOperatorAndCui(String username, LocalDate date, Pageable pageable);


	@Query(value = "select a.*  " +
			"  from  bp_cuie_cuievent a  " +
			"           join  bp_chpi_checkprocessitem b on a.code = b.mycuievent  " +
			"           join  bp_chsi_checkstepitem c on b.code = c.mychpi  " +
			"           join  bp_chri_checkeritem d on c.mychri = d.code  " +
			"  where " +
			"    eventplantime between (NOW() - interval ? minute) and NOW() " +
			"    and d.myusi <> 'AU' " +
			"    and a.mylcet_lceventtype not in ('DR-JN-JRQ') " +
			"    and published = true;", nativeQuery = true)
	List<BpCuiEvent> findAllByEventPlanTimeNearly(Integer minutes);

	@Query(
			value = "SELECT *" +
					"FROM bp_cuie_cuievent " +
					"WHERE mycui = :mycui " +
					"  AND mylcet_lceventtype = :mylcet " +
					"LIMIT 1",
			nativeQuery = true)
	Optional<BpCuiEvent> findFirstByCuiAndLcet(String mycui, String mylcet);

	List<BpCuiEvent> findByMycuiIn(Collection<String> cuiCodes);

	@Query(nativeQuery = true, value =
			"SELECT * " +
					"FROM bp_cuie_cuievent " +
					"WHERE myusi = :code " +
					"  AND DATE(:now) = DATE(eventplantime) " +
					"  AND published " +
					"ORDER BY eventplantime DESC, id DESC " +
					"LIMIT 1"
	)
	Optional<BpCuiEvent> bppJoinDL(String code, Date now);

	@Modifying
	@Transactional
	@Query(value = " " +
			"with future as ( select cuie.code as cuieCode " +
			"                 from bp_cui_content_user_ulc_instance cui " +
			"                          join bp_ulc_uniquelearningcomponent ulc on ulc.code =  cui.myulc " +
			"                          join bp_cap_calendarperiod cap on cap.code = ulc.mycap " +
			"                          join bp_cuie_cuievent cuie on cuie.mycui = cui.code " +
			"                 where cui.published " +
			"                   and cap.published " +
			"                   and ulc.published " +
			"                   and cui.myusi = :usi " +
			"                   and cuie.published " +
			"                   and cap.startperiod >= ifnull(:from,cap.startperiod) " +
			"                   and ifnull(:to,cap.endperiod) >= cap.endperiod) " +
			"update bp_cuie_cuievent cuie " +
			"set cuie.published = :published " +
			"where cuie.code in (select cuieCode from future)"
			, nativeQuery = true)
	void unpublishedCuieUnnecessaryOfModifyStudent(String usi, Timestamp from, Timestamp to, Boolean published);

	// @TODO
	//  need refactor
	@Modifying
	@Transactional
	@Query(value = " " +
			"with unnecessaryn as ( select cuie.code as code  " +
			"                       from bp_cuie_cuievent cuie  " +
			"                       join bp_cui_content_user_ulc_instance cui on cui.code = cuie.mycui  " +
			"                       join bp_ulc_uniquelearningcomponent ulc on ulc.code = cui.myulc  " +
			"                       where cui.myusi = :usi  " +
			"                       and cui.published  " +
			"                       and cuie.published  " +
			"                       and ulc.published  " +
			"                       and ulc.ulc_no is not null  " +
			"                       and ulc.mycap is null   " +
			"                       group by cuie.code)  " +
			"update bp_cuie_cuievent  " +
			"set published = :published  " +
			"where code in (select code from unnecessaryn)", nativeQuery = true)
	void unpublishedCuieUnnecessaryForScheduleMPForOM(String usi, Boolean published);

	@Query(value = "" +
			"select cuie.*    " +
			"from bp_cuie_cuievent cuie     " +
			"join bp_cui_content_user_ulc_instance cui on cui.code = cuie.mycui    " +
			"join bp_ulc_uniquelearningcomponent ulc on ulc.code = cui.myulc    " +
			"join bp_clag_ulc clagUlc on clagUlc.myulc = ulc.code    " +
			"join bp_clag_classgroup clag on clag.code = clagUlc.myclag    " +
			"join bp_cap_calendarperiod cap on cap.code = ulc.mycap    " +
			"where cap.published    " +
			"and cuie.published    " +
			"and cui.published    " +
			"and ulc.published    " +
			"and clag.active    " +
			"and clag.code = :clag    " +
			"and cui.myusi = :usi    " +
			"and cap.startperiod >= :from    " +
			"and cap.endperiod <= :to    " +
			"group by cuie.id", nativeQuery = true)
	List<BpCuiEvent> findCuieByCapAndClagAndUsi(String usi, String clag, Timestamp from, Timestamp to);
}
