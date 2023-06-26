package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.entity.projection.UlcScheduleShiftPJ;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.repository.projection.ULCMergeInfoProjection;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BpUniqueLearningComponentRepository extends JpaRepository<BpUniqueLearningComponent, Integer> {

	List<BpUniqueLearningComponent> findBpUniqueLearningComponentsByCode(String code);

	Optional<BpUniqueLearningComponent> findFirstByCode(String code);

	Optional<BpUniqueLearningComponent> findFirstByCodeAndPublishedTrue(String code);

	@Query(value = "select ulc.* " +
			"from bp_ulc_uniquelearningcomponent ulc " +
			"inner join bp_clag_ulc bcu on ulc.code = bcu.myulc " +
			"where bcu.myclag = :clagCode " +
			"and  ulc.mycap = :capCode " +
			"and ulc.mylcp = :lcpCode " +
			"limit 1", nativeQuery = true)
	Optional<BpUniqueLearningComponent> findByClagAndLcpAndCap(String clagCode, String capCode, String lcpCode);

	/**
	 * @param capCode
	 * @param lctCode
	 * @param lcpCode
	 * @return
	 */
	@Query(value =
			"SELECT a.*  FROM bp_ulc_uniquelearningcomponent a , bp_cap_calendarperiod b, bp_lct_learningcomponenttype c, bp_lcp_lcperiod d "
					+ "WHERE a.mycap = b.code AND a.mylct = c.code AND a.mylcp = d.code AND a.mycap = :capCode AND a.mylct = :lctCode "
					+ "AND a.mylcp = :lcpCode ORDER BY a.created_at DESC LIMIT 1", nativeQuery = true)
	Optional<BpUniqueLearningComponent> findFirstByCapLctLcpCode(String capCode, String lctCode, String lcpCode);

	List<BpUniqueLearningComponent> findByXdsc(String xdsc);

	List<BpUniqueLearningComponent> findByMyParentInAndPublishedTrue(Collection<String> codes);

	List<BpUniqueLearningComponent> findByMyParentInAndMyLctCodeAndPublishedTrue(
			Collection<String> parentUlcCodes,
			String lctCode
	);

	@Query(nativeQuery = true, value =
			"SELECT ulcC.* " +
					"FROM bp_ulc_uniquelearningcomponent ulcC " +
					"         INNER JOIN bp_ulc_uniquelearningcomponent ulcP ON ulcC.myparentulc = ulcP.code " +
					"    AND ulcC.published " +
					"         INNER JOIN bp_ulc_uniquelearningcomponent ulcGP ON ulcP.myparentulc = ulcGP.code " +
					"    AND ulcP.published " +
					"WHERE ulcGP.code IN :codes")
	List<BpUniqueLearningComponent> findByMyGrandParentInAndPublishedTrue(Collection<String> codes);

	@Query(nativeQuery = true, value =
			"SELECT ulcC.* " +
					"FROM bp_ulc_uniquelearningcomponent ulcC " +
					"         INNER JOIN bp_ulc_uniquelearningcomponent ulcP ON ulcC.myparentulc = ulcP.code " +
					"    AND ulcC.published " +
					"         INNER JOIN bp_ulc_uniquelearningcomponent ulcGP ON ulcP.myparentulc = ulcGP.code " +
					"    AND ulcP.published " +
					"         INNER JOIN bp_ulc_uniquelearningcomponent ulcGGP ON ulcGP.myparentulc = ulcGGP.code " +
					"    AND ulcGP.published " +
					"WHERE ulcGGP.code IN :codes")
	List<BpUniqueLearningComponent> findByMyGrandGrandParentInAndPublishedTrue(Collection<String> codes);

	List<BpUniqueLearningComponent> findByMyCapCodeAndPublished(String capCode, Boolean published);

	@Query(nativeQuery = true, value =
			"SELECT buu.* " +
					"FROM bp_cap_calendarperiod cady " +
					"         INNER JOIN bp_cap_calendarperiod cash " +
					"                    ON cady.code = cash.myparentcap AND cady.captype = 'CADY' AND cash.captype = 'CASH' AND cady.code = :cady " +
					"         INNER JOIN bp_cap_calendarperiod cass ON cash.code = cass.myparentcap AND cash.captype = 'CASS' " +
					"         INNER JOIN bp_ulc_uniquelearningcomponent buu " +
					"                    on cass.code = buu.mycap " +
					"                        AND buu.mygg = :gg " +
					"                        AND buu.mydfdl = :dfdl " +
					"                        AND buu.mydfge = :dfge " +
					"                        AND CASE " +
					"                                WHEN :isUDL IS NULL THEN true " +
					"                                WHEN :isUDL IS NULL THEN buu.code LIKE 'DL-%' " +
					"                                ELSE " +
					"                                    buu.code LIKE 'GE-%' END")
	List<BpUniqueLearningComponent> findULC(String cady, String gg, String dfdl, String dfge, Boolean isUDL);

	List<BpUniqueLearningComponent> findByMyCapCodeAndMyLcpCode(String myCap_code, String myLcp_code);

	@Query(value = "select ulc.* " +
			"from bp_ulc_uniquelearningcomponent ulc " +
			"where ulc.mycap in (select cap.code " +
			"                    from bp_cap_calendarperiod cap " +
			"                    where cap.captype = 'CASH' " +
			"                    and cap.myparentcap = :cady)", nativeQuery = true)
	List<BpUniqueLearningComponent> findUlcSHByCady(String cady);

	@Query(value = "select ulc.* " +
			"from bp_ulc_uniquelearningcomponent ulc " +
			"where ulc.myparentulc = :ulc " +
			" and ulc.mylcp = :lcp " +
			"  and ulc.published", nativeQuery = true)
	List<BpUniqueLearningComponent> findUlcByParentUlcAndLcp(String ulc, String lcp);

	List<BpUniqueLearningComponent> findAllByMyParentAndMyLctAndPublishedTrue(String ulc, String lct);

	@Query(value = "select ulc.* " +
			"from bp_ulc_uniquelearningcomponent ulc " +
			"where ifnull(ulc.mycap,'null') = ifnull(:cap,'null') " +
			"and ifnull(ulc.mygg,'null') = ifnull(:gg,'null') " +
			"and ifnull(ulc.mydfdl,'null') = ifnull(:dfdl,'null') " +
			"and ifnull(ulc.mydfge,'null') = ifnull(:dfge,'null')" +
			"and ifnull(ulc.mylcp, 'null') = ifnull(:lcp,'null') " +
			"and ulc.published ", nativeQuery = true)
	List<BpUniqueLearningComponent> findUlcByCapLcpGgDfdlDfge(String cap, String lcp, String gg, String dfdl, String dfge);

	Optional<BpUniqueLearningComponent> findFirstByMyCapCodeAndMyLcpCodeAndMyLctCode(String myCap_code, String myLcp_code, String myLct_code);

	List<BpUniqueLearningComponent> findAllByMyCapCodeAndPublished(String myCap_code, Boolean published);

	@Query(value = "select * from bp_ulc_uniquelearningcomponent ulc " +
			"join bp_lcp_lcperiod lcp on ulc.mylcp = lcp.code and lcp.published " +
			"join bp_lct_learningcomponenttype lct on lcp.mylct = lct.code and lct.published " +
			"and ulc.code = :ulcParent and lcp.mylct = :myLct and lct.mylck = :myLck and ulc.published", nativeQuery = true)
	List<BpUniqueLearningComponent> findAllByUlcParentAndMyLctAndMyLck(String ulcParent, String myLct, String myLck);

	@Query(nativeQuery = true, value =
			"WITH t AS (SELECT lcpm.mylcp, lct.mypt, lcpm.ismain, lcpm.mylctpk " +
					"           FROM bp_lcpm_lcpmerge lcpm " +
					"                    INNER JOIN bp_lct_learningcomponenttype lct ON lcpm.mylctpk = lct.code " +
					"               AND lcpm.published " +
					"               AND lct.published " +
					"               AND lcpm.isudlm = :isudlm " +
					"               AND lcpm.isugem = :isugem) " +
					"SELECT DISTINCT ulc.code, ulc.mydfge, ulc.mygg, ulc.mydfdl, t.* " +
					"FROM bp_ulc_uniquelearningcomponent ulc " +
					"         INNER JOIN t ON ulc.mylcp = t.mylcp " +
					"    AND ulc.published " +
					"    AND ulc.mygg = :gg " +
					"    AND ulc.mydfdl = :dfdl " +
					"         INNER JOIN bp_cap_calendarperiod cap ON ulc.mycap = cap.code " +
					"    AND cap.startperiod BETWEEN :start AND :end " +
					"         INNER JOIN bp_clag_ulc bcu ON ulc.code = bcu.myulc " +
					"         INNER JOIN bp_clag_classgroup clag ON bcu.myclag = clag.code " +
					"    AND clag.mypt = t.mypt")
	List<ULCMergeInfoProjection> findUDLMInfo(String gg, String dfdl, Timestamp start, Timestamp end, Boolean isudlm, Boolean isugem);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value =
			"UPDATE bp_ulc_uniquelearningcomponent " +
					"SET myjointulc = :myjoinulc, " +
					"    is_mainulc = IF(code = :myjoinulc, true, false) " +
					"WHERE code IN :ulcCodes " +
					"  AND published")
	void updateMyjoinulc(Set<String> ulcCodes, String myjoinulc);


	List<BpUniqueLearningComponent> findAllByMyParentAndPublishedTrue(String myParent);

	List<BpUniqueLearningComponent> findAllByCodeInAndPublishedTrue(List<String> codes);

	@Query(
			nativeQuery = true,
			value = "SELECT " +
					"    ulc.* " +
					"FROM " +
					"    bp_ulc_uniquelearningcomponent AS ulc " +
					"WHERE " +
					"    mycap = :cass " +
					"    AND mylcp = :lcp " +
					"    AND mydfdl = :dfdl " +
					"    AND mygg = :gg"
	)
	List<BpUniqueLearningComponent> findAllUgesByCapLcpGgDfdl(String cass, String lcp, String gg, String dfdl);

	@Query(value =
			"SELECT ulc.code					as code, " +
					" ulc.myGg					as myGg, " +
					" ulc.myDfdl				as myDfdl, " +
					" ulc.myDfge				as myDfge, " +
					" ulc.myDfqc				as myDfqc, " +
					" ulc.myPt					as myPt, " +
					" lct.myLck					as myLck, " +
					" cap.cashStart				as cashStart, " +
					" date(cap.startTime) 		as cady, " +
					" dayofweek(cap.startTime) 	as wso, " +
					" lcp.myprd	 				as myprd " +
					"FROM BpUniqueLearningComponent ulc " +
					"JOIN BpLCP lcp on ulc.myLcp = lcp.code " +
					"JOIN BpLearningComponentType lct on lcp.mylct = lct.code " +
					"JOIN CalendarPeriod cap on ulc.myCap = cap.code " +
					"WHERE 1 = 1 " +
					"AND (:#{#request.cady}  IS NULL OR (ulc.code like :#{#request.cady} )) " +
					"AND (:#{#request.ggsIsNull} = TRUE OR ulc.myGg 	IN (:#{#request.ggs})) " +
					"AND (:#{#request.dfdlsIsNull} = TRUE OR ulc.myDfdl 	IN (:#{#request.dfdls})) " +
					"AND ulc.myPt IN :#{#request.pt} "

	)
	Page<UlcScheduleShiftPJ> findAllByCondition(ScheduleRequest request, Pageable pageable);

	@Query(value =
			"SELECT ulc.* FROM bp_ulc_uniquelearningcomponent ulc " +
					"JOIN bp_cap_calendarperiod cap on ulc.mycap = cap.code " +
					"WHERE ulc.mypt IN :#{#request.pt} " +
					"  AND (:#{#request.cady} IS NULL OR cap.code LIKE concat('%',:#{#request.cady}, '%')) " +
					"  AND (:#{#request.ggsIsNull} = TRUE OR ulc.mygg IN (:#{#request.ggs})) " +
					"  AND (:#{#request.dfdlsIsNull} = TRUE OR ulc.mydfdl IN (:#{#request.dfdls})) " +
					"  AND ulc.published = TRUE AND ulc.myparentulc is null", nativeQuery = true)
	List<BpUniqueLearningComponent> findUlcByCapPtGgDfdlShift(ScheduleRequest request);

	@Query(value =
			"SELECT ulc.* FROM bp_ulc_uniquelearningcomponent ulc " +
					"JOIN bp_cap_calendarperiod cap on ulc.mycap = cap.code " +
					"WHERE ulc.mypt IN :#{#request.pt} " +
					"  AND (:#{#request.capCode} IS NULL OR cap.code LIKE concat('%',:#{#request.capCode}, '%')) " +
					"  AND (:#{#request.ggsIsNull} = TRUE OR ulc.mygg IN (:#{#request.ggs})) " +
					"  AND (:#{#request.dfdlsIsNull} = TRUE OR ulc.mydfdl IN (:#{#request.dfdls})) " +
					"  AND ulc.published = TRUE AND ulc.myparentulc is null", nativeQuery = true)
	List<BpUniqueLearningComponent> findUlcByCapPtGgDfdlMonth(ScheduleRequest request);

	/**
	 * @param clagCode
	 * @param startAt
	 * @return
	 */
	@Query(value = "SELECT b.* FROM bp_clag_ulc a JOIN bp_ulc_uniquelearningcomponent b "
			+ "ON a.myulc  = b.code JOIN bp_cap_calendarperiod c ON b.mycap  = b.code WHERE myclag = :clagCode "
			+ "AND c.startperiod >= :startAt AND b.published = TRUE", nativeQuery = true)
	List<BpUniqueLearningComponent> findAllByClagAndCapTime(String clagCode, Timestamp startAt);

	@Query(value = "select max(cap.startperiod) " +
			"from bp_cui_content_user_ulc_instance cui " +
			"         join bp_ulc_uniquelearningcomponent ulc on ulc.code =  cui.myulc " +
			"         join bp_cap_calendarperiod cap on cap.code = ulc.mycap " +
			"         join bp_clag_ulc cu on cu.myulc = ulc.code " +
			"where cap.published " +
			"  and ulc.published " +
			"  and cui.myusi = :usi " +
			"  and cap.startperiod >= NOW()", nativeQuery = true)
	Optional<Timestamp> findLastScheduleByUsi(String usi);


	@Query(
			nativeQuery = true,
			value = "SELECT " +
					"    * " +
					"FROM " +
					"    bp_ulc_uniquelearningcomponent AS ulc " +
					"WHERE " +
					"    ulc.mylcp IN :lcps " +
					"    AND mycap = :cap " +
					"    AND (:#{#ggs.size() == 0} " +
					"        OR ulc.mygg IN (:#{#ggs.size() == 0 ? NULL : #ggs}))" +
					"    AND (:#{#dfdls.size() == 0} " +
					"        OR ulc.mydfdl IN (:#{#dfdls.size() == 0 ? NULL : #dfdls}))"
	)
	List<BpUniqueLearningComponent> findAllWcUlces(
			List<String> lcps,
			String cap,
			List<String> ggs,
			List<String> dfdls
	);

}
