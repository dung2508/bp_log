package vn.edu.clevai.bplog.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.clevai.bplog.entity.CalendarPeriod;

public interface BpCalendarPeriodRepository extends JpaRepository<CalendarPeriod, Long> {

	@Query(value = "WITH lcp_sh AS (SELECT b.* FROM bp_lcp_lcperiod a JOIN "
			+ "(SELECT a.* FROM bp_lcp_lcperiod a JOIN "
			+ "(SELECT b.* FROM bp_lct_learningcomponenttype a "
			+ "JOIN bp_lcp_lcperiod b ON a.code = b.mylctparent "
			+ "WHERE a.mypt = :mypt AND b.published  = 1 AND b.myprd = '1MN') b ON a.mylctparent  = b.mylct) b "
			+ "ON a.mylctparent  = b.mylct AND a.code = :lcpCode AND a.published = TRUE), "
			+ "cap AS (SELECT * FROM bp_cap_calendarperiod WHERE DATE(startperiod) = DATE(:date) "
			+ "AND captype = 'CASH' AND published = 1 AND mycashsta = :cashStaCode) "
			+ "SELECT a.* FROM cap a JOIN lcp_sh b ON a.myprd  = b.myprd", nativeQuery = true)
	Optional<CalendarPeriod> step4FindCapByLcpCodeAndCashStaCode(String lcpCode, String cashStaCode, String mypt, LocalDate date);

	@Query(
			value = "WITH lcp_session AS " +
					"         (SELECT mylctparent, myprd, lcperiodno " +
					"          FROM bp_lcp_lcperiod " +
					"          WHERE code = :lcpCode), " +
					"     lcp_structure AS " +
					"         (SELECT GROUP_CONCAT(myprd SEPARATOR '-') AS mystructure, " +
					"                 COUNT(*) AS children_count " +
					"          FROM bp_lcp_lcperiod " +
					"          WHERE mylctparent IN " +
					"                (SELECT mylctparent FROM lcp_session) " +
					"            AND published = 1 " +
					"            AND lcperiodno LIKE 'FD%' " +
					"          ORDER BY lcperiodno) " +
					"SELECT a.* " +
					"FROM bp_cap_calendarperiod a " +
					"         JOIN lcp_session b " +
					"              ON a.myprd = b.myprd " +
					"                  AND REGEXP_SUBSTR(a.mynoaschild, '[0-9]+') = REGEXP_SUBSTR(b.lcperiodno, '[0-9]+') " +
					"         JOIN lcp_structure c " +
					"              ON a.mystructure = " +
					"                 CASE c.children_count " +
					"                     WHEN 1 THEN CONCAT(c.mystructure, '-0MI-0MI') " +
					"                     WHEN 2 THEN CONCAT(c.mystructure, '-0MI') " +
					"                     ELSE c.mystructure " +
					"                     END " +
					"WHERE a.myparentcap = :parentCap " +
					"  AND a.published = 1",
			nativeQuery = true)
	Optional<CalendarPeriod> step5FindCapByLcpParentCapAndLcpCode(String parentCap, String lcpCode);
}
