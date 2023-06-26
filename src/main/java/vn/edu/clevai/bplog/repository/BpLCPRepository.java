package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.projection.LcpCodePJ;

import java.util.List;
import java.util.Optional;

public interface BpLCPRepository extends JpaRepository<BpLCP, Integer> {
	Optional<BpLCP> findByCode(String code);

	Optional<BpLCP> findFirstByMyptAndMylct(String mypt, String mylct);

	Optional<BpLCP> findByMyptAndMylctAndLcperiodnoLike(String mypt, String mylct, String lcperiodBeginning);

	@Query(nativeQuery = true, value =
			"SELECT lcp.* " +
					"FROM bp_lcp_lcperiod lcp " +
					"         INNER JOIN bp_lct_learningcomponenttype lct ON lcp.mylct = lct.code " +
					"    AND lcp.published " +
					"    AND lct.published " +
					"    AND lct.need_schedule " +
					"    AND lcp.mylctparent = :parentLct ")
	List<BpLCP> findLCPKids(String parentLct);

	@Query(value = "select lcp.* from bp_lcp_lcperiod lcp " + "join bp_lct_learningcomponenttype lct1 on lcp.mylct = lct1.code and lct1.mylcl = ?1 and lct1.mylck = ?2 "
			+ "join bp_lct_learningcomponenttype lct2 on lcp.mylctparent = lct2.code and lct2.mylcl = ?3 "
			+ "and lcp.published and lct1.published and lct2.published", nativeQuery = true)
	List<BpLCP> findAllByMyLctAndMyLckAndMyParentLct(String myLcl, String myLck, String myParentLcl);

	@Query(value = "select lcpss.* from bp_lcp_lcperiod lcpss "
			+ "join bp_lcp_lcperiod lcpsh on lcpss.mylctparent = lcpsh.mylct "
			+ "join bp_lcp_lcperiod lcpmn on lcpmn.mylct = lcpsh.mylctparent "
			+ "join bp_lct_learningcomponenttype lctpk on lctpk.code = lcpmn.mylctparent "
			+ "join bp_lct_learningcomponenttype lctss on lctss.code = lcpss.mylct " + "where lctpk.published "
			+ "and lcpss.published " + "and lcpmn.published " + "and lcpsh.published " + "and lctss.published "
			+ "and lctss.mylck = :lck  " + "and lctpk.mypt = :pt " +
			" and lctss.mylcl = 'SS' " + "limit 1 ", nativeQuery = true)
	Optional<BpLCP> findLcpSsFromPtLck(String pt, String lck);

	@Query(nativeQuery = true, value = "SELECT DISTINCT lcp2.* " + "FROM bp_lcp_lcperiod lcp "
			+ "         INNER JOIN bp_lct_learningcomponenttype lctP "
			+ "                    ON lcp.mylctparent = lctP.code "
			+ "         INNER JOIN bp_lct_learningcomponenttype lct ON lcp.mylct = lct.code "
			+ "         INNER JOIN bp_lcp_lcperiod lcp2 ON lct.code = lcp2.mylctparent " + "    AND lcp2.published "
			+ "         INNER JOIN bp_lct_learningcomponenttype lctC ON lcp2.mylct = lctC.code "
			+ "    AND lctC.published AND lctC.need_schedule " + "    AND lctC.mylcl = 'SH' " + "WHERE lctP.mypt = :pt "
			+ "  AND lct.published " + "  AND lctP.published " + "  AND lcp.published "
			+ "  AND ((lct.mylcl = 'MN' AND lct.mylck LIKE 'MP%'))")
	List<BpLCP> findLCPSHByPTFromBP(String pt);

	@Query(value = "select lcpsh.* from  bp_lcp_lcperiod lcpsh "
			+ "                           join bp_lcp_lcperiod lcpmn on lcpmn.mylct = lcpsh.mylctparent "
			+ "                           join bp_lct_learningcomponenttype lctpk on lctpk.code = lcpmn.mylctparent "
			+ "                           join bp_lct_learningcomponenttype lctsh on lctsh.code = lcpsh.mylct "
			+ "where lctpk.published " + "  and lcpmn.published " + "  and lcpsh.published " + "  and lctsh.published "
			+ "  and lctpk.mypt = :pt " + "  and lctsh.code like CONCAT('%',:lck,'%')  " +
			"and lctsh.mylcl = 'SH' "
			+ "limit 1 ", nativeQuery = true)
	Optional<BpLCP> findLcpShByPt(String pt, String lck);

	@Query(value = "select lcpsh.* from  bp_lcp_lcperiod lcpsh " +
			"    join bp_lct_learningcomponenttype lctsh on lctsh.code = lcpsh.mylct " +
			"    join bp_lcp_lcperiod lcpmn on lcpmn.mylct = lcpsh.mylctparent " +
			"    join bp_lct_learningcomponenttype lctpk on lctpk.code = lcpmn.mylctparent " +
			"where lctpk.published " +
			"  and lcpmn.published " +
			"  and lcpsh.published " +
			"  and lctsh.published " +
			"  and lctpk.mypt = :pt " +
			"  and lctsh.code = :lct " +
			"  and lctsh.mylcl = 'SH' limit 1", nativeQuery = true)
	Optional<BpLCP> findLcpshByPtAndLct(String pt, String lct);

	@Query(value = "select lcpss.* from bp_lcp_lcperiod lcpss    " +
			"   join bp_lcp_lcperiod lcpsh on lcpss.mylctparent = lcpsh.mylct    " +
			"   join bp_lcp_lcperiod lcpmn on lcpmn.mylct = lcpsh.mylctparent    " +
			"   join bp_lct_learningcomponenttype lctpk on lctpk.code = lcpmn.mylctparent    " +
			"   join bp_lct_learningcomponenttype lctss on lctss.code = lcpss.mylct  " +
			"where lctpk.published  " +
			"    and lcpss.published  " +
			"    and lcpmn.published  " +
			"    and lcpsh.published  " +
			"    and lctss.published  " +
			"    and lctpk.mypt = :pt  " +
			"    and lctss.code = :lct  " +
			"    and lctss.mylcl = 'SS' limit 1", nativeQuery = true)
	Optional<BpLCP> findLcpSSByPtAndLct(String pt, String lct);

	@Query(value = "select lcpss.* from bp_lcp_lcperiod lcpss    " +
			"   join bp_lcp_lcperiod lcpsh on lcpss.mylctparent = lcpsh.mylct    " +
			"   join bp_lcp_lcperiod lcpmn on lcpmn.mylct = lcpsh.mylctparent    " +
			"   join bp_lct_learningcomponenttype lctpk on lctpk.code = lcpmn.mylctparent    " +
			"   join bp_lct_learningcomponenttype lctss on lctss.code = lcpss.mylct  " +
			"where lctpk.published  " +
			"    and lcpss.published  " +
			"    and lcpmn.published  " +
			"    and lcpsh.published  " +
			"    and lctss.published  " +
			"    and lctpk.mypt = :pt  " +
			"    and lctss.code like '%GE%'  " +
			"    and lctss.mylcl = 'SS' limit 1", nativeQuery = true)
	Optional<BpLCP> findLcpSSByPtAndGE(String pt);

	@Query(value = "select lctpk.mypt from bp_lcp_lcperiod lcpss   "
			+ "                           join bp_lcp_lcperiod lcpsh on lcpss.mylctparent = lcpsh.mylct   "
			+ "                           join bp_lcp_lcperiod lcpmn on lcpmn.mylct = lcpsh.mylctparent   "
			+ "                           join bp_lct_learningcomponenttype lctpk on lctpk.code = lcpmn.mylctparent   "
			+ "                           join bp_lct_learningcomponenttype lctss on lctss.code = lcpss.mylct   "
			+ "where lctpk.published   " + "  and lcpss.published   " + "  and lcpmn.published   "
			+ "  and lcpsh.published   " + "  and lctss.published   " + "  and lctss.mylck = :lck   "
			+ "  and lcpss.code = :lcp  " + "limit 1", nativeQuery = true)
	Optional<String> findPtFromLcpLck(String lcp, String lck);

	@Query(nativeQuery = true, value = "SELECT DISTINCT lcp.* " + "FROM bp_lcp_lcperiod lcp "
			+ "         INNER JOIN bp_lct_learningcomponenttype lctP "
			+ "                    ON lcp.mylctparent = lctP.code "
			+ "         INNER JOIN bp_lct_learningcomponenttype lct ON lcp.mylct = lct.code " + "WHERE lctP.mypt = :pt "
			+ "  AND lct.mylcl = :lcl " + "  AND lct.published " + "  AND lctP.published " + "  AND lcp.published")
	List<BpLCP> findLCWK(String pt, String lcl);

	@Query(value = "WITH lcp_myss AS (SELECT code, myprd , mylctparent, lcperiodno "
			+ "FROM bp_lcp_lcperiod WHERE lcperiodno like 'FD%' "
			+ "AND code = :code), lcp_child_exclude AS (SELECT code, myprd , mylctparent, lcperiodno "
			+ "FROM bp_lcp_lcperiod WHERE lcperiodno like 'FD%' "
			+ "AND code <> :code AND mylctparent = (SELECT mylctparent FROM lcp_myss)) "
			+ "SELECT SUM(REGEXP_SUBSTR(a.myprd ,'[0-9]+')) AS before_minute "
			+ "FROM lcp_child_exclude a, lcp_myss b WHERE a.mylctparent = b.mylctparent "
			+ "AND a.lcperiodno < b.lcperiodno", nativeQuery = true)
	Optional<Integer> calculateAfterMinuteByCode(String code);

	@Query("SELECT a FROM BpLCP a LEFT JOIN FETCH a.lct b WHERE a.mylctparent = :parentLct " +
			"AND a.published = TRUE AND b.published = TRUE and b.needSchedule = TRUE")
	List<BpLCP> findByMylctparentToSchedule(String parentLct);

	@Query(nativeQuery = true, value =
			"SELECT lcp.* " +
					"FROM bp_lcp_lcperiod lcp " +
					"         INNER JOIN bp_lct_learningcomponenttype lct ON lcp.mylct = lct.code " +
					"    AND lcp.published " +
					"    AND lct.published " +
					"    AND lct.need_schedule " +
					"    AND lcp.mylctparent = :parentLct " +
					"    AND lct.mylcl = :lcl")
	List<BpLCP> findLCPKids(String parentLct, String lcl);

	Optional<BpLCP> findByMylctAndMylctparentAndPublishedTrue(String pt, String lct);

	@Query(value = "select lcpss.*   " +
			"from bp_lct_learningcomponenttype lctpk   " +
			"    join bp_lcp_lcperiod lcpmn on lcpmn.mylctparent = lctpk.code   " +
			"    join bp_lcp_lcperiod lcpss on lcpss.mylctparent = lcpmn.mylct   " +
			"    join bp_lct_learningcomponenttype lctss on lctss.code=lcpss.mylct   " +
			"where lctpk.published   " +
			"and lcpmn.published   " +
			"and lcpss.published   " +
			"and lctpk.mypt = 'OM'   " +
			"and lctss.mylcl = 'SS'   " +
			"and lctss.mylck like concat('%',:lck,'%')   " +
			"limit  1", nativeQuery = true)
	Optional<BpLCP> findLcpSsForOMByLck(String lck);

	@Query(value = "WITH RECURSIVE lcp_cte AS ( " +
			"    SELECT lcpmn.* " +
			"    FROM bp_lct_learningcomponenttype lctpk " +
			"             JOIN bp_lcp_lcperiod lcpmn ON lctpk.code = lcpmn.mylctparent " +
			"             JOIN bp_lct_learningcomponenttype lctmn on lctmn.code = lcpmn.mylct " +
			"    WHERE lctpk.mypt IN :mypts " +
			"      AND lctpk.published " +
			"      AND lctmn.mylcl = :myLcl " +
			"      AND lctmn.mylck = :myLck " +
			"    UNION " +
			"    SELECT lcp1.* FROM bp_lcp_lcperiod lcp1 " +
			"    JOIN lcp_cte lcp ON lcp1.mylctparent = lcp.mylct " +
			") select * from lcp_cte", nativeQuery = true)
	List<BpLCP> findMCByPT(String myLcl, String myLck, List<String> mypts);


	@Query(value =
			"SELECT lcpsh.code as lcpsh, lcpss.code as lcpss, lcpsc.code as lcpsc, lcpsl.code as lcpsl " +
					"from bp_lct_learningcomponenttype lctpk " +
					"         join bp_lcp_lcperiod lcpwk on lctpk.code = lcpwk.mylctparent " +
					"         join bp_lct_learningcomponenttype lctwk on lcpwk.mylct = lctwk.code " +
					"         join bp_lcp_lcperiod lcpsh on lcpwk.mylct = lcpsh.mylctparent " +
					"         left join bp_lcp_lcperiod lcpss on lcpsh.mylct = lcpss.mylctparent " +
					"         left join bp_lcp_lcperiod lcpsc on lcpss.mylct = lcpsc.mylctparent " +
					"         left join bp_lcp_lcperiod lcpsl on lcpsc.mylct = lcpsl.mylctparent " +
					"WHERE lctpk.mypt IN (:mypts) " +
					"  and lctwk.mylck IN (:myLck) "
			, nativeQuery = true)
	List<LcpCodePJ> findMCByPT2(String myLck, List<String> mypts);


	@Query(
			nativeQuery = true,
			value = "WITH package AS( " +
					"    SELECT * FROM bp_lct_learningcomponenttype AS bll WHERE mypt = :pt AND published " +
					") " +
					"SELECT * FROM bp_lcp_lcperiod AS lcp " +
					"WHERE " +
					"    lcp.mylctparent = (SELECT code FROM package) " +
					"    AND lcp.mylct = 'WC-1WK' "
	)
	Optional<BpLCP> findWcByPt(String pt);

	@Query(nativeQuery = true,
			value = "SELECT lcpsl.* " +
					"FROM bp_lct_learningcomponenttype lctpk " +
					"         join bp_lcp_lcperiod lcpmn on lctpk.code = lcpmn.mylctparent " +
					"         join bp_lct_learningcomponenttype lctmn on lcpmn.mylct = lctmn.code " +
					"         join bp_lcp_lcperiod lcpss on lcpss.mylctparent = lctmn.code " +
					"         join bp_lct_learningcomponenttype lctss on lctss.code = lcpss.mylct " +
					"         join bp_lcp_lcperiod lcpsc on lcpsc.mylctparent = lcpss.mylct " +
					"         join bp_lcp_lcperiod lcpsl on lcpsl.mylctparent = lcpsc.mylct " +
					"where lctpk.mypt = :pt" +
					"  AND lctpk.published " +
					"and lctss.mylck = :lck"
	)
	BpLCP findUlcSL(String pt, String lck);

	@Query(
			nativeQuery = true,
			value = "WITH package AS( " +
					"    SELECT * FROM bp_lct_learningcomponenttype AS bll WHERE mypt IN :pts AND published " +
					") " +
					"SELECT * FROM bp_lcp_lcperiod AS lcp " +
					"WHERE " +
					"    lcp.mylctparent = (SELECT code FROM package) " +
					"    AND lcp.mylct = 'WC-1WK' "
	)
	List<BpLCP> findWcByPts(List<String> pts);
}
