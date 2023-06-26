package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.config.RegionCacheSupporter;
import vn.edu.clevai.bplog.entity.logDb.BpCuiContentUserUlc;
import vn.edu.clevai.bplog.entity.projection.AnswerAndQuestionPJ;
import vn.edu.clevai.bplog.entity.projection.ScheduleMonthCalendarPJ;
import vn.edu.clevai.bplog.payload.request.bp.GetQuestionAnswerRequest;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.repository.projection.BpCuiContentULCProjection;
import vn.edu.clevai.bplog.service.RedisLockService;
import vn.edu.clevai.common.api.exception.ConflictException;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static vn.edu.clevai.bplog.config.RegionCacheSupporter.TEN_SECONDS_IN_MILLISECONDS;
import static vn.edu.clevai.bplog.utils.RLockUtils.getRedisLockService;

public interface BpCuiContentUserUlcRepository extends JpaRepository<BpCuiContentUserUlc, Integer> {

	List<BpCuiContentUserUlc> findBpCuiContentUserUlcsByCode(String code);

	boolean existsByCode(String code);

	@Query("SELECT a FROM BpCuiContentUserUlc a LEFT JOIN FETCH a.myUsi b WHERE a.code = :code")
	Optional<BpCuiContentUserUlc> findByCode(String code);

	@Query(value = "SELECT a.id, a.code ,a.name ,a.mycti ,a.myulc FROM bp_cui_content_user_ulc_instance a "
			+ "WHERE a.code = (SELECT mycui FROM bp_cuie_cuievent bcc WHERE code = :cuiEventCode)", nativeQuery = true)
	Optional<BpCuiContentULCProjection> findByCUIEventCode(String cuiEventCode);


	@Query(value = "select *    " +
			"from bp_cui_content_user_ulc_instance    " +
			"where published    " +
			"and myulc = :ulc    " +
			"and myusi = 'AU' " +
			"and mycti is not null   " +
			"order by created_at     " +
			"limit  1", nativeQuery = true)
	Optional<BpCuiContentUserUlc> findCuiMainFromUlc(String ulc);

	List<BpCuiContentUserUlc> findByMyulcIn(Collection<String> ulcCodes);

	@Query(value = "select cui.*  " +
			"from bp_cui_content_user_ulc_instance cui  " +
			"         join bp_ulc_uniquelearningcomponent ulc on cui.myulc = ulc.code  " +
			"where ulc.published  " +
			"  and cui.published  " +
			"  and ulc.mylcp = :lcp  " +
			"  and cui.myusi = :usi  " +
			"  and  ulc.ulc_no is not null  " +
			"order by ulc.ulc_no   " +
			"limit :limit offset :offset", nativeQuery = true)
	List<BpCuiContentUserUlc> findCuiFromUsiLcp(String usi, String lcp, int limit, int offset);

	@Query(value = "select COUNT(cuiMain.id) " +
			"from bp_cui_content_user_ulc_instance cui " +
			"         join bp_ulc_uniquelearningcomponent ulc on cui.myulc = ulc.code " +
			"         join bp_cui_content_user_ulc_instance cuiMain on cuiMain.myulc = ulc.code " +
			"                                                    and cuiMain.mycti is not null " +
			"                                                    and cuiMain.myusi = 'AU' " +
			"where ulc.published " +
			"  and cui.published " +
			"  and ulc.mylcp = :lcp " +
			"  and cui.myusi = :usi " +
			"  and  ulc.ulc_no is not null " +
			"  and cuiMain.published " +
			"order by ulc.ulc_no", nativeQuery = true)
	Integer getCountCuiFromUsiLcp(String usi, String lcp);

	@Query(value = " " +
			"select cui.*   " +
			"from bp_cui_content_user_ulc_instance cui   " +
			"         join bp_ulc_uniquelearningcomponent ulc on cui.myulc = ulc.code   " +
			"where ulc.published   " +
			"  and cui.published   " +
			"  and ulc.mylcp = :lcp   " +
			"  and cui.myusi = :usi   " +
			"  and  ulc.ulc_no is not null   " +
			"order by ulc.ulc_no ", nativeQuery = true)
	List<BpCuiContentUserUlc> findAllByUsiLcp(String usi, String lcp);


	@Query(value = "select cui.* " +
			"from bp_cui_content_user_ulc_instance cui " +
			"         join bp_ulc_uniquelearningcomponent ulc on cui.myulc = ulc.code " +
			"where ulc.published " +
			"  and cui.published " +
			"  and ulc.mylcp = :lcp " +
			"  and cui.myusi = :usi " +
			"  and ulc_no = :ulcNo " +
			"limit  1 ", nativeQuery = true)
	Optional<BpCuiContentUserUlc> findCuiByUsiLcpUlcNo(String usi, String lcp, Integer ulcNo);

	default <S extends BpCuiContentUserUlc> S createOrUpdate(S entity) {
		RedisLockService lockService = getRedisLockService();
		String key = lockService.generateCacheLockKey(RegionCacheSupporter.BP_CUI_CONTENT_USER_ULC_INSTANCE, String.valueOf(entity.getCode()));
		try {
			if (!lockService.tryLock(key, TEN_SECONDS_IN_MILLISECONDS, Integer.MAX_VALUE, TimeUnit.MILLISECONDS)) {
				throw new ConflictException(String.format("Save BpCuiContentUserUlc failed with code %s", entity.getCode()));
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

	Optional<BpCuiContentUserUlc> findFirstByCode(String code);

	Optional<BpCuiContentUserUlc> findByMyulcAndMyusi(String ulc, String usi);

	@Query(nativeQuery = true, value =
			"SELECT cui.myusi " +
					"FROM bp_cui_content_user_ulc_instance cui " +
					"         INNER JOIN bp_usi_useritem usi ON cui.myusi = usi.code " +
					"    AND usi.myust = :ust " +
					"    AND cui.myulc = :ulc " +
					"    AND cui.published " +
					"LIMIT 1")
	String findFirstCuiUsi(String ulc, String ust);


	@Query(value =
			"WITH RECURSIVE cap(id, code, captype) AS " +
					"                         (SELECT id, code, captype " +
					"                          FROM bp_cap_calendarperiod a " +
					"                          WHERE code = :#{#request.capCode} " +
					"                          UNION ALL " +
					"                          SELECT b.id, b.code, b.captype " +
					"                          FROM bp_cap_calendarperiod b " +
					"                                   JOIN cap ca ON b.myparentcap = ca.code) " +
					", bp_pod_dfdl_cte(mypod, mydfdl, mydfdl_rank) AS (" +
					" 			select mypod, mydfdl, ROW_NUMBER() OVER (PARTITION BY mypod ORDER BY  updated_at DESC) AS mydfdl_rank " +
					"        	from bp_pod_dfdl ) " +
					", bp_st_gg_cte(myst, mygg, mygg_rank) AS (" +
					" 		SELECT myst, mygg, ROW_NUMBER() OVER (PARTITION BY myst ORDER BY  updated_at DESC) AS mygg_rank FROM bp_st_gg ) " +
					"      SELECT cui.`code`      as code " +
					"           , cap.captype     as captype " +
					"           , ulc.mylcp       as mylcp " +
					"           , pod.mypt        as mypt " +
					"           , st.mygg         as mygg " +
					"           , pod_dfdl.mydfdl as mydfdl " +
					"           , cui.myulc       as myulc " +
					"           , cui.mycti       as mycti " +
					"           , cui.myusi       as myusi " +
					"      FROM bp_cui_content_user_ulc_instance as cui " +
					"               JOIN bp_pod_productofdeal as pod on (cui.myusi = pod.myst) " +
					"               JOIN bp_st_gg_cte as st on cui.myusi = st.myst " +
					"               JOIN bp_pod_dfdl_cte as pod_dfdl on pod.`code` = pod_dfdl.mypod " +
					"               JOIN bp_ulc_uniquelearningcomponent as ulc on ulc.`code` = cui.myulc " +
					"               JOIN cap on cap.`code` = ulc.mycap " +
					"      WHERE st.mygg_rank = 1 AND  pod_dfdl.mydfdl_rank = 1 " +
					"		 AND (:#{#request.ggsIsNull} = TRUE OR st.mygg IN (:#{#request.ggs})) " +
					"        AND (:#{#request.dfdlsIsNull} = TRUE OR pod_dfdl.mydfdl IN (:#{#request.dfdls})) " +
					"        AND ulc.mypt IN (:#{#request.pt}) " +
					"        AND pod.mypt IN (:#{#request.pt}) " +
					"        AND ulc.mylcp IN (:#{#request.lcps}) " +
					" " +
					"      UNION " +
					" " +
					"      SELECT cui.`code`     as code " +
					"           , cap.captype    as captype " +
					"           , ulc.mylcp      as mylcp " +
					"           , ulc.mypt       as mypt " +
					"           , ulc.mygg       as mygg " +
					"           , ulc.mydfdl     as mydfdl " +
					"           , cui.myulc      as myulc " +
					"           , cui.mycti      as mycti " +
					"           , cui.myusi      as myusi " +
					"      FROM bp_cui_content_user_ulc_instance as cui " +
					"               JOIN bp_ulc_uniquelearningcomponent as ulc on ulc.`code` = cui.myulc " +
					"               JOIN cap on cap.`code` = ulc.mycap " +
					"      WHERE (:#{#request.ggsIsNull} = TRUE OR ulc.mygg IN (:#{#request.ggs})) " +
					"        AND (:#{#request.dfdlsIsNull} = TRUE OR ulc.mydfdl IN (:#{#request.dfdls})) " +
					"        AND ulc.mypt IN (:#{#request.pt}) " +
					"        AND cui.myusi = 'AU' " +
					"        AND ulc.mylcp IN (:#{#request.lcps}) "

			, countQuery =
			"WITH RECURSIVE cap(id, code, captype) AS " +
					"                         (SELECT id, code, captype " +
					"                          FROM bp_cap_calendarperiod a " +
					"                          WHERE code = :#{#request.capCode} " +
					"                          UNION ALL " +
					"                          SELECT b.id, b.code, b.captype " +
					"                          FROM bp_cap_calendarperiod b " +
					"                                   JOIN cap ca ON b.myparentcap = ca.code) " +
					", bp_pod_dfdl_cte(mypod, mydfdl, mydfdl_rank) AS (" +
					" 			select mypod, mydfdl, ROW_NUMBER() OVER (PARTITION BY mypod ORDER BY  updated_at DESC) AS mydfdl_rank " +
					"        	from bp_pod_dfdl ) " +
					", bp_st_gg_cte(myst, mygg, mygg_rank) AS (" +
					" 		SELECT myst, mygg, ROW_NUMBER() OVER (PARTITION BY myst ORDER BY  updated_at DESC) AS mygg_rank FROM bp_st_gg ) " +
					",  cui_cte AS ( " +
					"      SELECT cui.`code`      as code " +
					"           , cap.captype     as captype " +
					"           , ulc.mylcp       as mylcp " +
					"           , pod.mypt        as mypt " +
					"           , st.mygg         as mygg " +
					"           , pod_dfdl.mydfdl as mydfdl " +
					"           , cui.myulc       as myulc " +
					"           , cui.mycti       as mycti " +
					"           , cui.myusi       as myusi " +
					"      FROM bp_cui_content_user_ulc_instance as cui " +
					"               JOIN bp_pod_productofdeal as pod on (cui.myusi = pod.myst) " +
					"               JOIN bp_st_gg_cte as st on cui.myusi = st.myst " +
					"               JOIN bp_pod_dfdl_cte as pod_dfdl on pod.`code` = pod_dfdl.mypod " +
					"               JOIN bp_ulc_uniquelearningcomponent as ulc on ulc.`code` = cui.myulc " +
					"               JOIN cap on cap.`code` = ulc.mycap " +
					"      WHERE st.mygg_rank = 1 AND  pod_dfdl.mydfdl_rank = 1 " +
					"		 AND (:#{#request.ggsIsNull} = TRUE OR st.mygg IN (:#{#request.ggs})) " +
					"        AND (:#{#request.dfdlsIsNull} = TRUE OR pod_dfdl.mydfdl IN (:#{#request.dfdls})) " +
					"        AND ulc.mypt IN (:#{#request.pt}) " +
					"        AND pod.mypt IN (:#{#request.pt}) " +
					"        AND ulc.mylcp IN (:#{#request.lcps}) " +
					" " +
					"      UNION " +
					" " +
					"      SELECT cui.`code`     as code " +
					"           , cap.captype    as captype " +
					"           , ulc.mylcp      as mylcp " +
					"           , ulc.mypt       as mypt " +
					"           , ulc.mygg       as mygg " +
					"           , ulc.mydfdl     as mydfdl " +
					"           , cui.myulc      as myulc " +
					"           , cui.mycti      as mycti " +
					"           , cui.myusi      as myusi " +
					"      FROM bp_cui_content_user_ulc_instance as cui " +
					"               JOIN bp_ulc_uniquelearningcomponent as ulc on ulc.`code` = cui.myulc " +
					"               JOIN cap on cap.`code` = ulc.mycap " +
					"      WHERE (:#{#request.ggsIsNull} = TRUE OR ulc.mygg IN (:#{#request.ggs})) " +
					"        AND (:#{#request.dfdlsIsNull} = TRUE OR ulc.mydfdl IN (:#{#request.dfdls})) " +
					"        AND ulc.mypt IN (:#{#request.pt}) " +
					"        AND cui.myusi = 'AU' " +
					"        AND ulc.mylcp IN (:#{#request.lcps}) " +
					" ) SELECT COUNT(*) FROM cui_cte "
			, nativeQuery = true
	)
	Page<ScheduleMonthCalendarPJ> findALlByCondition(ScheduleRequest request, Pageable pageable);

	@Query(nativeQuery = true, value =
			"SELECT cui.* " +
					"FROM bp_cui_content_user_ulc_instance cui " +
					"         INNER JOIN bp_usi_useritem usi ON cui.myusi = usi.code " +
					"    AND usi.myust = :ust " +
					"    AND cui.myulc IN :ulcs " +
					"    AND cui.published ")
	List<BpCuiContentUserUlc> findCuiByUlcInAndUst(Collection<String> ulcs, String ust);

	@Query(value = "" +
			"select ctiAqr.mylo " +
			"from bp_cui_content_user_ulc_instance cuiHw " +
			"         join bp_ulc_uniquelearningcomponent ulcHw on cuiHw.myulc = ulcHw.code " +
			"    and cuiHw.myusi = :usi " +
			"    and ulcHw.mylcp = :lcpHw " +
			"         join bp_ulc_uniquelearningcomponent ulcAQR on ulcAQR.myparentulc = ulcHw.code " +
			"         join bp_cui_content_user_ulc_instance cuiMainAqr on cuiMainAqr.myulc = ulcAQR.code " +
			"    and cuiMainAqr.myusi = 'AU' " +
			"    and cuiMainAqr.mycti is not null " +
			"         join bp_cti_contentitem ctiAqr on ctiAqr.code = cuiMainAqr.mycti " +
			"    and ctiAqr.mylo is not null " +
			"where cuiHw.published " +
			"  and ulcHw.published " +
			"  and ulcAQR.published " +
			"  and cuiMainAqr.published " +
			"  and ctiAqr.published " +
			"order by ulcHw.ulc_no ", nativeQuery = true)
	List<String> findAssignLo(String usi, String lcpHw);

	@Query(
			nativeQuery = true,
			/* This works with an assumption: An user has less than 1000 HRG-EA-AAX-AQR1-AA in a year. */
			value = "SELECT bccuui2.mycti FROM bp_ulc_uniquelearningcomponent AS buu " +
					"INNER JOIN bp_cui_content_user_ulc_instance AS bccuui ON " +
					"    bccuui.myusi            = :usi " +
					"    AND bccuui.myulc        = buu.code " +
					"    AND buu.mylcp           = 'HRG-EA-AAX-AQR1-AA' " +
					"    AND buu.mygg            = :gg " +
					"    AND buu.mydfdl          = :dfdl " +
					"    AND buu.mypt            = :pt " +
					"    AND buu.published " +
					"    AND (buu.mycap IS NULL OR buu.mycap < :cap) " +
					"    AND bccuui.published " +
					"INNER JOIN bp_cui_content_user_ulc_instance AS bccuui2 ON " +
					"    bccuui2.myusi           = 'AU' " +
					"    AND bccuui2.myulc       = buu.code " +
					"    AND bccuui2.mycti IS NOT NULL " +
					"ORDER BY RAND() " +
					"LIMIT 4"
	)
	List<String> findHrvCtis(String pt, String gg, String dfdl, String cap, String usi);

	@Query(value =
			"WITH cti_question AS (SELECT * " +
					"                      FROM bp_cti_contentitem " +
					"                      WHERE myctt = 'CTI_QTS') " +
					"   , cti_answer AS (SELECT * " +
					"                    FROM bp_cti_contentitem " +
					"                    WHERE myctt = 'CTI_AWS') " +
					"   , cte_cti_count AS (SELECT cti_parent.code 			as code " +
					"                            , cti_parent.id   			as id " +
					"                            , cti_parent.created_at   	as created_at " +
					"                            , count(cti.code) 			as num_of_child " +
					"                       FROM bp_cti_contentitem cti " +
					"                                JOIN bp_cti_contentitem cti_parent on cti.myparentcti = cti_parent.code " +
					"                       WHERE cti.myctt IN ('CTI_QTS', 'CTI_AWS') " +
					"                       GROUP BY cti.myparentcti) " +
					"   , cte_cti AS (SELECT  cti_root.code 			as code " +
					"						, cti_root.id 				as id " +
					"						, cti_root.num_of_child 	as num_of_child " +
					"						, cti_root.created_at 		as created_at " +
					"                       , question.created_at 		as question_reated_at " +
					"                       , question.myvalueset 		as question_myvalueset " +
					"                       , answer.created_at 		as answer_created_at " +
					"                       , answer.myvalueset 		as answer_myvalueset " +
					"                 FROM cte_cti_count cti_root " +
					"                          LEFT JOIN cti_question question ON cti_root.code = question.myparentcti " +
					"                          LEFT JOIN cti_answer answer ON cti_root.code = answer.myparentcti) " +
					"SELECT ulc.code       				as ulcCode " +
					"     , ulc.id         				as ulcId " +
					"     , ulc.created_at 				as createdAt " +
					"     , ulc.mygg       				as mygg " +
					"     , cti.id         				as rootCtiId " +
					"     , cti.code       				as rootCtiCode " +
					"	  , cti.question_reated_at 		as questionCreatedAt " +
					"	  , cti.question_myvalueset 	as questionMyvalueset " +
					"	  , cti.answer_created_at 		as answerCreatedAt " +
					"	  , cti.answer_myvalueset 		as answerMyvalueset " +
					"     , cui.code       as cuiCode " +
					"     , cui.id         as cuiId " +
					"     , cui.myusi      as myUsiCui " +
					"     , usi.fullname   as myUsiFullName " +
					"     , usi.myust      as myUst " +
					" 	  ,	(CASE WHEN cti.num_of_child = 1 THEN " +
					"            (CASE WHEN (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(cui.created_at)) <= :seconds THEN '0' " +
					"                  ELSE '1' END) " +
					"            ELSE " +
					"                (CASE WHEN (UNIX_TIMESTAMP(cti.answer_created_at) - UNIX_TIMESTAMP(cui.created_at)) <= :seconds THEN '0' " +
					"                       ELSE '1' END) " +
					"       END) AS isExpired " +
					"FROM bp_ulc_uniquelearningcomponent ulc " +
					"         JOIN bp_cui_content_user_ulc_instance cui ON ulc.code = cui.myulc " +
					"         JOIN bp_usi_useritem usi ON usi.code = cui.myusi " +
					"         JOIN cte_cti cti ON cti.code = cui.mycti " +
					"WHERE ulc.published = TRUE AND ulc.code LIKE CONCAT('%', :#{#request.lcpLike}, '%') " +
					"AND (:#{#request.currentUsi} IS NULL OR cui.myusi = :#{#request.currentUsi}) " +
					"AND (:#{#request.gg} IS NULL OR ulc.mygg = :#{#request.gg}) " +
					"AND " +
					"    CASE WHEN (:#{#request.status}) = 'RESOLVED'  THEN cti.num_of_child > 1 " +
					"         WHEN (:#{#request.status}) = 'SUBMITTED' THEN cti.num_of_child = 1 " +
					"    ELSE TRUE " +
					"    END " +
					"AND " +
					"    CASE WHEN (:isExpired) = FALSE THEN " +
					"            (CASE WHEN cti.num_of_child = 1 THEN " +
					"                    (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(cui.created_at)) <= :seconds " +
					"                  WHEN cti.num_of_child > 1 THEN " +
					"                    (UNIX_TIMESTAMP(cti.answer_created_at) - UNIX_TIMESTAMP(cui.created_at)) <= :seconds " +
					"                  ELSE TRUE " +
					"                  END " +
					"            ) " +
					"         WHEN (:isExpired) = TRUE THEN " +
					"            (CASE WHEN cti.num_of_child = 1 THEN " +
					"                    (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(cui.created_at)) > :seconds " +
					"                  WHEN cti.num_of_child > 1 THEN " +
					"                    (UNIX_TIMESTAMP(cti.answer_created_at) - UNIX_TIMESTAMP(cui.created_at)) > :seconds " +
					"                  ELSE TRUE " +
					"                  END " +
					"            ) " +
					"    ELSE TRUE " +
					"    END " +
					"AND ( :#{#request.filterByTime} = FALSE " +
					"OR ( " +
					" DATE(ulc.created_at + INTERVAL 7 HOUR) BETWEEN DATE(:#{#request.fromTime}) AND DATE(:#{#request.toTime}) " +
					") " +
					") "

			, countQuery =
			"WITH cti_question AS (SELECT * " +
					"                      FROM bp_cti_contentitem " +
					"                      WHERE myctt = 'CTI_QTS') " +
					"   , cti_answer AS (SELECT * " +
					"                    FROM bp_cti_contentitem " +
					"                    WHERE myctt = 'CTI_AWS') " +
					"   , cte_cti_count AS (SELECT cti_parent.code 			as code " +
					"                            , cti_parent.id   			as id " +
					"                            , cti_parent.created_at   	as created_at " +
					"                            , count(cti.code) 			as num_of_child " +
					"                       FROM bp_cti_contentitem cti " +
					"                                JOIN bp_cti_contentitem cti_parent on cti.myparentcti = cti_parent.code " +
					"                       WHERE cti.myctt IN ('CTI_QTS', 'CTI_AWS') " +
					"                       GROUP BY cti.myparentcti) " +
					"   , cte_cti AS (SELECT  cti_root.code 			as code " +
					"						, cti_root.id 				as id " +
					"						, cti_root.num_of_child 	as num_of_child " +
					"						, cti_root.created_at 		as created_at " +
					"                       , question.created_at 		as question_reated_at " +
					"                       , question.myvalueset 		as question_myvalueset " +
					"                       , answer.created_at 		as answer_created_at " +
					"                       , answer.myvalueset 		as answer_myvalueset " +
					"                 FROM cte_cti_count cti_root " +
					"                          LEFT JOIN cti_question question ON cti_root.code = question.myparentcti " +
					"                          LEFT JOIN cti_answer answer ON cti_root.code = answer.myparentcti) " +
					"SELECT count(ulc.code) " +
					"FROM bp_ulc_uniquelearningcomponent ulc " +
					"         JOIN bp_cui_content_user_ulc_instance cui ON ulc.code = cui.myulc " +
					"         JOIN bp_usi_useritem usi ON usi.code = cui.myusi " +
					"         JOIN cte_cti cti ON cti.code = cui.mycti " +
					"WHERE ulc.published = TRUE AND ulc.code LIKE CONCAT('%', :#{#request.lcpLike}, '%') " +
					"AND (:#{#request.currentUsi} IS NULL OR cui.myusi = :#{#request.currentUsi}) " +
					"AND (:#{#request.gg} IS NULL OR ulc.mygg = :#{#request.gg}) " +
					"AND " +
					"    CASE WHEN (:#{#request.status}) = 'RESOLVED'  THEN cti.num_of_child > 1 " +
					"         WHEN (:#{#request.status}) = 'SUBMITTED' THEN cti.num_of_child = 1 " +
					"    ELSE TRUE " +
					"    END " +
					"AND " +
					"    CASE WHEN (:isExpired) = FALSE THEN " +
					"            (CASE WHEN cti.num_of_child = 1 THEN " +
					"                    (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(cui.created_at)) <= :seconds " +
					"                  WHEN cti.num_of_child > 1 THEN " +
					"                    (UNIX_TIMESTAMP(cti.answer_created_at) - UNIX_TIMESTAMP(cui.created_at)) <= :seconds " +
					"                  ELSE TRUE " +
					"                  END " +
					"            ) " +
					"         WHEN (:isExpired) = TRUE THEN " +
					"            (CASE WHEN cti.num_of_child = 1 THEN " +
					"                    (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(cui.created_at)) > :seconds " +
					"                  WHEN cti.num_of_child > 1 THEN " +
					"                    (UNIX_TIMESTAMP(cti.answer_created_at) - UNIX_TIMESTAMP(cui.created_at)) > :seconds " +
					"                  ELSE TRUE " +
					"                  END " +
					"            ) " +
					"    ELSE TRUE " +
					"    END " +
					"AND ( :#{#request.filterByTime} = FALSE " +
					"OR ( " +
					" DATE(ulc.created_at + INTERVAL 7 HOUR) BETWEEN DATE(:#{#request.fromTime}) AND DATE(:#{#request.toTime}) " +
					") " +
					") "
			, nativeQuery = true)
	Page<AnswerAndQuestionPJ> findCuiQuestion(@Param("request") GetQuestionAnswerRequest request,
											  @Param("isExpired") Boolean isExpired,
											  @Param("seconds") Integer seconds,
											  @Param("pageable") Pageable pageable);

	@Query(value =
			"SELECT cui FROM BpCuiContentUserUlc cui " +
					"JOIN ContentItem cti ON cui.myCtiCode = cti.code " +
					"WHERE cui.myUlcCode = :ulcCode AND cti.code = :ctiCode AND cui.published = :isActive ")
	BpCuiContentUserUlc getCuiByUlcAndCti(String ulcCode, String ctiCode, boolean isActive);


	@Modifying
	@Transactional
	@Query(value = "" +
			"with future as ( select cui.code as cuiCode  " +
			"                 from bp_cui_content_user_ulc_instance cui  " +
			"                          join bp_ulc_uniquelearningcomponent ulc on ulc.code =  cui.myulc  " +
			"                          join bp_cap_calendarperiod cap on cap.code = ulc.mycap  " +
			"                 where cui.published  " +
			"                   and cap.published  " +
			"                   and ulc.published  " +
			"                   and cui.myusi = :usi  " +
			"                   and cap.startperiod >= ifnull(:from,cap.startperiod)  " +
			"                   and ifnull(:to,cap.endperiod) >= cap.endperiod)  " +
			"update bp_cui_content_user_ulc_instance cui  " +
			"set cui.published = :published  " +
			"where cui.code in (select cuiCode from future)"
			, nativeQuery = true)
	void unpublishedCuiUnnecessaryOfModifyStudent(String usi, Timestamp from, Timestamp to, Boolean published);

	// @TODO
	// need refactor
	@Modifying
	@Transactional
	@Query(value = "" +
			"with unnecessaryn as ( select cui.code as code   " +
			"                       from  bp_cui_content_user_ulc_instance cui   " +
			"                       join bp_ulc_uniquelearningcomponent ulc on ulc.code = cui.myulc   " +
			"                       where cui.myusi = :usi   " +
			"                       and cui.published   " +
			"                       and ulc.published   " +
			"                       and ulc.ulc_no is not null   " +
			"                       and ulc.mycap is null   " +
			"                       group by cui.code)   " +
			"update bp_cui_content_user_ulc_instance   " +
			"set published = :published   " +
			"where code in (select code from unnecessaryn)", nativeQuery = true)
	void unpublishedCuiUnnecessaryForScheduleMPForOM(String usi, Boolean published);

	@Query(value = "" +
			"select cui.*     " +
			"from bp_cui_content_user_ulc_instance cui      " +
			"join bp_ulc_uniquelearningcomponent ulc on ulc.code = cui.myulc     " +
			"join bp_clag_ulc clagUlc on clagUlc.myulc = ulc.code     " +
			"join bp_clag_classgroup clag on clag.code = clagUlc.myclag     " +
			"join bp_cap_calendarperiod cap on cap.code = ulc.mycap     " +
			"where cap.published     " +
			"and cui.published     " +
			"and ulc.published     " +
			"and clag.active     " +
			"and clag.code = :clag     " +
			"and cui.myusi = :usi     " +
			"and cap.startperiod >= :from     " +
			"and cap.endperiod <= :to     " +
			"group by cui.id", nativeQuery = true)
	List<BpCuiContentUserUlc> findCuiByCapAndClagAndUsi(String usi, String clag, Timestamp from, Timestamp to);


	@Query(value =
			"SELECT ulc.code 			as ulcCode " +
					", ulc.id 			as ulcId " +
					", ulc.createdAt 	as createdAt " +
					", ulc.myGg 		as mygg " +
					", cti.id 			as rootCtiId " +
					", cti.code 		as rootCtiCode " +
					", cui.code 		as cuiCode " +
					", cui.id 			as cuiId " +
					", cui.myusi 		as myUsiCui " +
					", usi.fullname 	as myUsiFullName " +
					", usi.myust 		as myUst " +
					"FROM BpUniqueLearningComponent ulc " +
					"JOIN BpCuiContentUserUlc cui ON ulc.code = cui.myUlcCode " +
					"JOIN ContentItem cti ON cti.code = cui.myCtiCode " +
					"JOIN BpUsiUserItem usi ON usi.code = cui.myusi " +
					"WHERE cui.code = :cuiCode "
	)
	AnswerAndQuestionPJ findDetailsQuestion(String cuiCode);
	
	@Modifying
	@Query(value = "UPDATE bp_cui_content_user_ulc_instance SET published = FALSE "
			+ "WHERE myusi = :myusi AND myulc IN (:ulcCodes)", nativeQuery = true)
	void inactiveCuiByMyusiAndUlcCodeIn(String myusi, List<String> ulcCodes);
}