package vn.edu.clevai.bplog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpUsiDuty;
import vn.edu.clevai.bplog.entity.projection.UsiDutyPJ;
import vn.edu.clevai.bplog.repository.projection.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BpUSIDutyRepository extends JpaRepository<BpUsiDuty, Long> {

	@Query(value = "select usid.myusi  " +
			"from bp_usid_usiduty usid  " +
			"join bp_usid_cashsta buc on usid.code = buc.myusid  " +
			"join bp_cashsta_calshiftstart bcc on buc.mycashsta = bcc.code  " +
			"where bcc.published  " +
			"and buc.is_enable  " +
			"and usid.mycap = :cap  " +
			"and usid.mylcp = :lcp  " +
			"and usid.myust = :ust  " +
			"and usid.mylcet = :lcet  " +
			"and ifnull(bcc.mygg,'null') = ifnull(:gg,'null')  " +
			"and ifnull(bcc.mydfdl,'null') = ifnull(:dfdl,null)  " +
			"and ifnull(bcc.mydfge,'null') = ifnull(:dfge,null)  " +
			"and usid.mychrt = :chrt  " +
			"and  ifnull(:excludeUsi,'null') = ifnull(:excludeUsi,null)  " +
			"group by usid.myusi", nativeQuery = true)
	List<String> findByUlc(String lcet, String ust, String cap, String chrt, String lcp, String excludeUsi, String gg, String dfdl, String dfge);

	Optional<BpUsiDuty> findFirstByCode(String code);

	@Query(value = "select usid.myusi  " +
			"from bp_usid_usiduty usid  " +
			"where is_deleted is not true  " +
			"and mycap = :cap  " +
			"and ifnull(:pt,'null') = ifnull(mypt,'null')  " +
			"and ifnull(:gg,'null') = ifnull(mygg,'null')  " +
			"and ifnull(:dfdl,'null') = ifnull(mydfdl,'null')  " +
			"and ifnull(:dfge,'null') = ifnull(mydfge,'null')  " +
			"and myust = :ust  " +
			"and ifnull(:lcp,mylcp) = mylcp  " +
			"and ifnull(:position,'null') = ifnull(position,'null')   " +
			"and published " +
			"group by usid.myusi", nativeQuery = true)
	List<String> findUsiFromBp(String cap, String ust, String pt, String gg, String dfdl, String dfge,
							   String lcp, String position);

	@Query(value = "select usid.myusi  " +
			"from bp_usid_usiduty usid  " +
			"where is_deleted is not true  " +
			"and mycap in (:cap)  " +
			"and ifnull(:pt,'null') = ifnull(mypt,'null')  " +
			"and ifnull(:gg,'null') = ifnull(mygg,'null')  " +
			"and ifnull(:dfdl,'null') = ifnull(mydfdl,'null')  " +
			"and ifnull(:dfge,'null') = ifnull(mydfge,'null')  " +
			"and myust = :ust  " +
			"and ifnull(:lcp,mylcp) = mylcp  " +
			"and ifnull(:position,'null') = ifnull(position,'null')   " +
			"and mybpp LIKE '%BPPRegister5%' " +
			"group by usid.myusi", nativeQuery = true)
	List<String> findRegister5Users(
			List<String> cap, String ust, String pt, String gg, String dfdl, String dfge,
			String lcp, String position
	);

	@Query(value = "select usid.myusi     " +
			"from bp_usid_usiduty usid     " +
			"         join bp_dtc_duty_classes bddc on usid.code = bddc.myusid     " +
			"         join bp_clc_classcategory bcc on bddc.myclc = bcc.code     " +
			"where usid.mycap = :cap     " +
			"  and usid.mylcet = 'BF-RD-AB'     " +
			"  and bddc.is_enabled     " +
			"group by usid.myusi ", nativeQuery = true)
	List<String> findUsiBackupByCady(String cap);

	@Query(value = "select usid.myusi        " +
			"from bp_usid_usiduty usid        " +
			"where usid.mycap = :cap        " +
			"  and usid.mylcet = :lcet " +
			"and usid.myust = :ust " +
			"and usid.mylcp = :lcp " +
			"and is_deleted is not true  " +
			"group by usid.myusi ", nativeQuery = true)
	List<String> findEmFromCady(String cap, String lcet, String ust, String lcp);
	
	/*
	@Query(value = "" +
			"select usid.myusi        " +
			"from bp_usid_usiduty usid        " +
			"where usid.mycap = :cap        " +
			"  and usid.mylcet = :lcet " +
			"and usid.myust = :ust " +
			"and usid.mylcp = :lcp   " +
			"group by usid.myusi ", nativeQuery = true)
	List<GetRegistedQuantityProjection> getSteRegistedQuantity(String type, Integer productId, Integer gradeId,
			Integer classLevelId, Date startDate, Date endDate, Integer subjectId);

	 */


	@Query(value = "SELECT DISTINCT mypt, mygg, mydfdl FROM bp_usid_usiduty WHERE mybpp like concat('%', :bpp, '%') and published", nativeQuery = true)
	Set<AvailableSlotsProjection> findAvailableSlots(String bpp);

	@Query(value = "WITH GroupByPTAndGGAndDFDL AS (SELECT mywso, mypt, mygg, mydfdl " +
			"                               FROM bp_usid_usiduty " +
			"                               WHERE bpp = :bpp " +
			"                                 AND DATE(start_time + INTERVAL 7 HOUR) >= :startDate " +
			"                                 AND myust = IFNULL(:myust, myust) " +
			"                                 AND mypt = IFNULL(:mypt, mypt) " +
			"                                 AND mygg = IFNULL(:mygg, mygg) " +
			"                                 AND mydfdl = IFNULL(:mydfdl, mydfdl) " +
			"                                 AND DATE(end_time + INTERVAL 7 HOUR) <= :endDate " +
			"                                 AND mywso IS NOT NULL " +
			"                               GROUP BY mywso, mypt, mygg, mydfdl) " +
			"SELECT mywso, COUNT(*) AS requiredQuantity " +
			"FROM GroupByPTAndGGAndDFDL " +
			"GROUP BY mywso ", nativeQuery = true)
	List<UsidDTERequiredProjection> findDTERequired(Timestamp startDate, Timestamp endDate, String bpp, String myust, String mypt, String mygg, String mydfdl);


	@Query(value = "SELECT mywso, COUNT(*) AS registeredQuantity " +
			"FROM bp_usid_usiduty " +
			"WHERE bpp = :bpp " +
			"  AND DATE(start_time + INTERVAL 7 HOUR) >= :startDate " +
			"  AND (DATE(end_time + INTERVAL 7 HOUR) <= :endDate OR end_time IS NULL) " +
			"  AND myust = IFNULL(:myust, myust) " +
			"  AND mypt = IFNULL(:mypt, mypt) " +
			"  AND mygg = IFNULL(:mygg, mygg) " +
			"  AND mydfdl = IFNULL(:mydfdl, mydfdl) " +
			"GROUP BY mywso", nativeQuery = true)
	List<UsidDTERegisterProjection> findDTERegistered(Timestamp startDate, Timestamp endDate, String bpp, String myust, String mypt, String mygg, String mydfdl);

	@Query(value = "SELECT mycap, position, COUNT(*) AS quantity " +
			"FROM bp_usid_usiduty " +
			"WHERE mybpp = :bpp " +
			"  AND DATE(start_time + INTERVAL 7 HOUR) >= :startDate " +
			"  AND (DATE(end_time + INTERVAL 7 HOUR) <= :endDate OR end_time IS NULL) " +
			"  AND myust = IFNULL(:myust, myust) " +
			"  AND mypt = IFNULL(:mypt, mypt) " +
			"  AND mygg = IFNULL(:mygg, mygg) " +
			"  AND mydfdl = IFNULL(:mydfdl, mydfdl) " +
			"  AND unallocated_at IS NULL " +
			"GROUP BY mycap, position", nativeQuery = true)
	List<UsidDTEAssignProjection> findDTEAssign(Timestamp startDate, Timestamp endDate, String bpp, String myust, String mypt, String mygg, String mydfdl);


	@Query(value = "WITH RECURSIVE " +
			"    date_range AS " +
			"        (SELECT :startDate              AS date, " +
			"                WEEKDAY(:startDate) + 2 AS wso " +
			"         UNION ALL " +
			"         SELECT ADDDATE(date_range.date, 1)              AS date, " +
			"                WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso " +
			"         FROM date_range " +
			"         WHERE date_range.date < :endDate), " +
			"    approved AS " +
			"        (SELECT NULL AS id, " +
			"                usid.myusi, " +
			"                date_range.date, " +
			"                usid.mypt, " +
			"                usid.mygg, " +
			"                NULL AS mydfdl, " +
			"                NULL AS mydfge, " +
			"                NULL AS position, " +
			"                3    AS step " +
			"         FROM bp_usid_usiduty usid " +
			"                  JOIN date_range " +
			"                       ON usid.mywso = date_range.wso " +
			"                              AND (date_range.date >= DATE(usid.start_time + INTERVAL 7 HOUR) " +
			"                              		AND date_range.date < IFNULL(DATE(usid.end_time + INTERVAL 7 HOUR), '2100-01-01'))" +
			"                  LEFT JOIN bp_usi_useritem usi ON usid.myusi = usi.code " +
			"                  LEFT JOIN bp_pt_producttype pt ON usid.mypt = pt.code " +
			"                  LEFT JOIN bp_gg_gradegroup gg ON usid.mygg = gg.code " +
			"         WHERE mybpp LIKE CONCAT('%BPPRegister3-Confirm-', :ust, '%') " +
			"           AND usid.myterm = :myTerm " +
			"           AND (:usiIds IS NULL OR usi.id IN (:usiIds)) " +
			"           AND (:ptIds IS NULL OR pt.id IN (:ptIds)) " +
			"           AND (:ggIds IS NULL OR gg.id IN (:ggIds)) AND usid.published = TRUE), " +
			"    approved_stats AS " +
			"        (SELECT myusi, COUNT(*) AS sum_approved " +
			"         FROM approved " +
			"         GROUP BY myusi), " +
			"    assigned AS " +
			"        (SELECT usid.id, " +
			"                usid.myusi, " +
			"                date_range.date, " +
			"                usid.mypt, " +
			"                usid.mygg, " +
			"                usid.mydfdl, " +
			"                usid.mydfge, " +
			"                usid.position, " +
			"                4 AS step " +
			"         FROM bp_usid_usiduty usid " +
			"                  JOIN bp_cap_calendarperiod cap ON usid.mycap = cap.code " +
			"                  JOIN date_range ON DATE(cap.startperiod + INTERVAL 7 HOUR) = date_range.date " +
			"         WHERE mybpp LIKE CONCAT('%BPPRegister4-Allocate-', :ust, '%') " +
			"           AND unallocated_at IS NULL " +
			"           AND usid.published = TRUE " +
			"         GROUP BY usid.id), " +
			"    assigned_stats AS " +
			"        (SELECT myusi, " +
			"                SUM(IF(position = 'MAIN', 1, 0))   AS sum_main_assigned, " +
			"                SUM(IF(position = 'BACKUP', 1, 0)) AS sum_backup_assigned " +
			"         FROM assigned " +
			"         GROUP BY myusi) " +
			"SELECT usi.id                                        AS usiId, " +
			"       usi.code                                      AS usiCode, " +
			"       usi.fullname                                  AS usiFullname, " +
			"       IFNULL(approved_stats.sum_approved, 0)        AS sumApproved, " +
			"       IFNULL(assigned_stats.sum_main_assigned, 0)   AS sumMainAssigned, " +
			"       IFNULL(assigned_stats.sum_backup_assigned, 0) AS sumBackupAssigned " +
			"FROM approved " +
			"         LEFT JOIN bp_usi_useritem usi ON approved.myusi = usi.code " +
			"         LEFT JOIN approved_stats ON approved.myusi = approved_stats.myusi " +
			"         LEFT JOIN assigned_stats ON approved.myusi = assigned_stats.myusi " +
			"GROUP BY usi.code " +
			"ORDER BY usi.code",
			countQuery = "WITH RECURSIVE " +
					"    date_range AS " +
					"        (SELECT :startDate              AS date, " +
					"                WEEKDAY(:startDate) + 2 AS wso " +
					"         UNION ALL " +
					"         SELECT ADDDATE(date_range.date, 1)              AS date, " +
					"                WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso " +
					"         FROM date_range " +
					"         WHERE date_range.date < :endDate), " +
					"    approved AS " +
					"        (SELECT NULL AS id, " +
					"                usid.myusi, " +
					"                date_range.date, " +
					"                usid.mypt, " +
					"                usid.mygg, " +
					"                NULL AS mydfdl, " +
					"                NULL AS mydfge, " +
					"                NULL AS position, " +
					"                3    AS step " +
					"         FROM bp_usid_usiduty usid " +
					"                  JOIN date_range " +
					"                       ON usid.mywso = date_range.wso " +
					"                              AND (date_range.date >= DATE(usid.start_time + INTERVAL 7 HOUR) " +
					"                              		AND date_range.date < IFNULL(DATE(usid.end_time + INTERVAL 7 HOUR), '2100-01-01'))" +
					"                  LEFT JOIN bp_usi_useritem usi ON usid.myusi = usi.code " +
					"                  LEFT JOIN bp_pt_producttype pt ON usid.mypt = pt.code " +
					"                  LEFT JOIN bp_gg_gradegroup gg ON usid.mygg = gg.code " +
					"         WHERE mybpp LIKE CONCAT('%BPPRegister3-Confirm-', :ust, '%') " +
					"           AND usid.myterm = :myTerm " +
					"           AND usid.published = TRUE " +
					"           AND (:usiIds IS NULL OR usi.id IN (:usiIds)) " +
					"           AND (:ptIds IS NULL OR pt.id IN (:ptIds)) " +
					"           AND (:ggIds IS NULL OR gg.id IN (:ggIds))) " +
					"SELECT COUNT(DISTINCT approved.myusi) " +
					"FROM approved",
			nativeQuery = true)
	Page<TeacherAssigneeProjection> getTeacherAssignee(
			String ust,
			Collection<Long> usiIds,
			Collection<Long> ptIds,
			Collection<Long> ggIds,
			LocalDate startDate,
			LocalDate endDate,
			Pageable pageable,
			String myTerm
	);

	@Query(value = "WITH RECURSIVE " +
			"    date_range AS " +
			"        (SELECT :startDate              AS date, " +
			"                WEEKDAY(:startDate) + 2 AS wso " +
			"         UNION ALL " +
			"         SELECT ADDDATE(date_range.date, 1)              AS date, " +
			"                WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso " +
			"         FROM date_range " +
			"         WHERE date_range.date < :endDate), " +
			"    approved AS " +
			"        (SELECT NULL AS id, " +
			"                usid.myusi, " +
			"                date_range.date, " +
			"                usid.mypt, " +
			"                usid.mygg, " +
			"                NULL AS mydfdl, " +
			"                NULL AS mydfge, " +
			"                NULL AS position, " +
			"                3    AS step " +
			"         FROM bp_usid_usiduty usid " +
			"                  JOIN date_range " +
			"                       ON usid.mywso = date_range.wso " +
			"                              AND (date_range.date >= DATE(usid.start_time + INTERVAL 7 HOUR) " +
			"                              		AND date_range.date < IFNULL(DATE(usid.end_time + INTERVAL 7 HOUR), '2100-01-01'))" +
			"                  LEFT JOIN bp_usi_useritem usi ON usid.myusi = usi.code " +
			"                  LEFT JOIN bp_pt_producttype pt ON usid.mypt = pt.code " +
			"                  LEFT JOIN bp_gg_gradegroup gg ON usid.mygg = gg.code " +
			"         WHERE mybpp LIKE CONCAT('%BPPRegister3-Confirm-', :ust, '%') " +
			"           AND usid.myterm = :myTerm " +
			"           AND (:usiIds IS NULL OR usi.id IN (:usiIds)) " +
			"           AND (:ptIds IS NULL OR pt.id IN (:ptIds)) " +
			"           AND (:ggIds IS NULL OR gg.id IN (:ggIds)) AND usid.published = TRUE), " +
			"    assigned AS " +
			"        (SELECT usid.id, " +
			"                usid.myusi, " +
			"                date_range.date, " +
			"                usid.mypt, " +
			"                usid.mygg, " +
			"                usid.mydfdl, " +
			"                usid.mydfge, " +
			"                usid.position, " +
			"                4 AS step " +
			"         FROM bp_usid_usiduty usid " +
			"                  JOIN bp_cap_calendarperiod cap ON usid.mycap = cap.code " +
			"                  JOIN date_range ON DATE(cap.startperiod + INTERVAL 7 HOUR) = date_range.date " +
			"         WHERE mybpp LIKE CONCAT('%BPPRegister4-Allocate-', :ust, '%') " +
			"           AND unallocated_at IS NULL " +
			"         GROUP BY usid.id), " +
			"    total AS " +
			"        (SELECT * " +
			"         FROM approved " +
			"         UNION " +
			"         SELECT * " +
			"         FROM assigned) " +
			"SELECT total.date, " +
			"       usi.id       AS usiId, " +
			"       usi.code     AS usiCode, " +
			"       usi.fullname AS usiFullname, " +
			"       total.id     AS usidId, " +
			"       pt.id        AS ptId, " +
			"       gg.id        AS ggId, " +
			"       dfdl.id      AS dfdlId, " +
			"       total.mydfge AS dfgeCode, " +
			"       total.position AS position " +
			"FROM total " +
			"         LEFT JOIN bp_usi_useritem usi ON total.myusi = usi.code " +
			"         LEFT JOIN bp_pt_producttype pt ON total.mypt = pt.code " +
			"         LEFT JOIN bp_gg_gradegroup gg ON total.mygg = gg.code " +
			"         LEFT JOIN bp_dfdl_difficultygrade dfdl ON total.mydfdl = dfdl.code",
			nativeQuery = true)
	List<TeacherAssigneeProjection> getTeacherAssigneeDetails(
			String ust,
			Collection<Long> usiIds,
			Collection<Long> ptIds,
			Collection<Long> ggIds,
			LocalDate startDate,
			LocalDate endDate,
			String myTerm
	);

	@Query(value = "SELECT a.* FROM bp_usid_usiduty a JOIN bp_pt_producttype b ON a.mypt = b.code JOIN bp_gg_gradegroup c "
			+ "ON a.mygg  = c.code JOIN bp_usi_useritem d ON a.myusi  = d.code JOIN bp_wso_weeklyscheduleoption e ON a.mywso = e.code "
			+ "WHERE a.mybpp LIKE CONCAT('%', :bpp, '%') AND b.id = :pt AND c.id = :gg AND d.id = :teacherid "
			+ "AND e.code = DAYOFWEEK(:regisDate) "
			+ "AND (a.is_deleted IS NULL OR a.is_deleted = FALSE) AND a.approved_at IS NOT NULL "
			+ "AND a.myaccyear = :accYear AND a.myterm = :myTerm AND a.published = TRUE LIMIT 1", nativeQuery = true)
	Optional<BpUsiDuty> findFirstByPtGgTeacherIdAccTermAndRegisDate(String bpp, Integer pt, Integer gg,
																	Integer teacherid, String accYear, String myTerm, String regisDate);

	@Query(nativeQuery = true, value =
			"SELECT DISTINCT mygg, mypt, mydfdl, mywso, mycashsta " +
					"FROM bp_usid_usiduty " +
					"WHERE mygg IS NOT NULL " +
					"  AND mypt IS NOT NULL " +
					"  AND mydfdl IS NOT NULL " +
					"  AND mywso IS NOT NULL " +
					"  AND mycashsta IS NOT NULL " +
					"  AND myaccyear = :ay ")
	List<UsidDistinctInfoProjection> findDistinctInfo(String ay);

	List<BpUsiDuty> findAllByMyptAndMyggAndMydfdlAndMyCapAndMyLcpAndMyUstAndPublishedTrue
			(String pt, String gg, String dfdl, String cap, String lcp, String ust);

	Optional<BpUsiDuty> findFirstByMyaccyearAndMytermAndMyptAndMyggAndMydfdlAndPublishedTrueOrderByCreatedAtDesc
			(String ay, String term, String pt, String gg, String dfdl);

	@Query(" UPDATE BpUsiDuty " +
			"SET unallocatedAt       = current_date, " +
			"    published           = FALSE, " +
			"    unpublishbps        =:unpublishbps, " +
			"    unpublishbpe        =:unpublishbpe, " +
			"    isDeleted           = TRUE, " +
			"    teacherCancelReason = :teacherCancelReason " +
			"WHERE mypreviouscode = :previousCode")
	@Modifying
	void updateUsiDutyChild(String previousCode, String teacherCancelReason, String unpublishbps, String unpublishbpe);

	@Query("SELECT COUNT(u) FROM BpUsiDuty u WHERE (isDeleted IS NULL OR isDeleted = FALSE) "
			+ "AND (unallocatedAt IS NULL) AND mypreviouscode = :previousCode")
	Long countByPreviousCode(String previousCode);

	//SELECT id, code, mygg, mydfdl, mypt FROM bp_usid_usiduty  where mypt = 'OM'  and mybpp like '%Register1-Setting%' order by mypt

//	@Query(value =
//			"WITH usid AS ( " +
//					"    SELECT * " +
//					"    FROM bp_usid_usiduty " +
//					"    where mypt = :mypt " +
//					"      and mybpp like :mybpp and published = 1 " +
//					") " +
//					"SELECT DISTINCT mygg as mygg, mypt as mypt, GROUP_CONCAT(mydfdl) as mydfdl FROM usid GROUP BY mypt, mygg ",
//			nativeQuery = true)

	@Query(nativeQuery = true,
			value = "WITH " +
					"    crpp AS (SELECT * " +
					"              FROM bp_crpp_curriculumprogrampackage " +
					"              WHERE " +
					"                  date(:date) BETWEEN date(startdate) and date(enddate) " +
					"                and published = 1 " +
					"                and (:mypt is null or mypt = :mypt)), " +
					"     usid AS (SELECT u.* " +
					"              FROM bp_usid_usiduty u " +
					"                   JOIN crpp ON (crpp.myterm = u.myterm AND u.myaccyear = crpp.myaccyear) " +
					"              where " +
					"                (:mypt is null or u.mypt = :mypt) " +
					"                and u.mybpp like :mybpp " +
					"                and u.published = 1) " +
					"SELECT DISTINCT mygg as mygg, mypt as mypt, GROUP_CONCAT(mydfdl) as mydfdl FROM usid GROUP BY mypt, mygg "
	)
	List<UsiDutyPJ> findAllByPT(Timestamp date, String mypt, String mybpp);

	List<BpUsiDuty> findByMyptAndMyaccyearAndMytermAndMybppLikeAndPublishedTrue(
			String pt, String ay, String term, String bpp
	);

	Optional<BpUsiDuty> findByMypreviouscode(String previouscode);
}
