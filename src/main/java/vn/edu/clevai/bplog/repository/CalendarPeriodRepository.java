package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.CalendarPeriod;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CalendarPeriodRepository extends JpaRepository<CalendarPeriod, Long> {
	@Query(
			value = "SELECT p FROM CalendarPeriod p WHERE :date BETWEEN p.startTime AND p.endTime " +
					" AND p.capType = :capType")
	Optional<CalendarPeriod> findByTimeAndCapType(Date date, String capType);

	Optional<CalendarPeriod> findByCode(String code);

	Optional<CalendarPeriod> findByCodeAndCapType(String code, String capType);

	Optional<CalendarPeriod> findByMyParentAndNumberAsChildAndPublishedTrue
			(String inputCap, String numberAsChild);

	Optional<CalendarPeriod> findByMyGrandParentAndCapTypeAndNumberAsGrandChildAndPublishedTrue
			(String inputCap, String capType, String numberAsGrandChild);

	List<CalendarPeriod> findByMyGrandParentAndCapTypeAndPublishedTrue
			(String inputCap, String capType);

	Optional<CalendarPeriod> findByMyParentAndMyPrdAndCashStartAndCapTypeAndPublishedTrue
			(String inputCap, String prd, String cashStart, String capType);

	Optional<CalendarPeriod> findByMyParentAndNumberAsChildAndAndMystructureAndCapTypeAndPublishedTrue
			(String inputCap, String numberAsChild, String myStructure, String capType);

	@Query(nativeQuery = true, value =
			"SELECT cady.* " +
					"FROM bp_cap_calendarperiod cawk " +
					"         INNER JOIN bp_cap_calendarperiod cady " +
					"                    ON cawk.code = cady.myparentcap " +
					"                        AND cawk.captype = 'CAWK' " +
					"                        AND cady.captype = 'CADY' " +
					"                        AND :date BETWEEN cawk.startperiod AND cawk.endperiod")
	List<CalendarPeriod> findCadyByDateBetweenCawk(String date);

	List<CalendarPeriod> findByMyParent(String myparent);

	List<CalendarPeriod> findByMyParentAndMystructureAndMyLctIsNull(String capCode, String capStructure);

	List<CalendarPeriod> findAllByMyParentAndCapTypeAndPublishedTrueOrderByNumberAsChildDesc(String parent, String type);

	@Query(value = "FROM CalendarPeriod c WHERE week(now()) <= week(c.startTime) AND year(now()) = year(c.startTime) AND c.capType IN (:list)")
	List<CalendarPeriod> findAllByType(List<String> list);

	@Query(value = "FROM CalendarPeriod c WHERE week(now()) <= week(c.endTime) AND year(now()) = year(c.endTime) AND c.capType =:capType")
	List<CalendarPeriod> findAllByTypeMonth(String capType);

	@Query(value = "" +
			"select cap.*   " +
			"from bp_cap_calendarperiod cap   " +
			"where cap.published   " +
			"and cap.startperiod between :from and :to   " +
			"and cap.captype = 'CADY'   " +
			"and if(DAYOFWEEK(cap.endperiod)=1,8,DAYOFWEEK(cap.endperiod)) in :wso " +
			"group by cap.code", nativeQuery = true)
	List<CalendarPeriod> getCadyFromWsoAndCawk(Timestamp from, Timestamp to, List<Integer> wso);

	@Query(value = "" +
			"with s as ( select cap.startperiod as start, cap.endperiod as end " +
			"                from bp_ulc_uniquelearningcomponent ulc " +
			"                         join bp_clag_ulc bcu on ulc.code = bcu.myulc " +
			"                         join bp_clag_classgroup clag on clag.code = bcu.myclag " +
			"                         join bp_cap_calendarperiod cap on cap.code = ulc.mycap " +
			"                where cap.published " +
			"                  and ulc.published " +
			"                  and clag.active " +
			"                  and cap.startperiod >= NOW() " +
			"                  and clag.code = :clag " +
			"                group by cap.startperiod, cap.endperiod ) " +
			"select cap.* from bp_cap_calendarperiod cap " +
			"                      join s on s.start >= cap.startperiod " +
			"                            and s.end <= cap.endperiod " +
			"    and cap.captype = 'CADY'", nativeQuery = true)
	List<CalendarPeriod> getCadyListScheduledOfClag(String clag);

	@Query(value = "" +
			"select cap2.* " +
			"from bp_cui_content_user_ulc_instance cui " +
			"         join bp_ulc_uniquelearningcomponent ulc on cui.myulc = ulc.code " +
			"         join bp_cap_calendarperiod cap1 on cap1.code = ulc.mycap " +
			"         join bp_cap_calendarperiod cap2 on cap1.startperiod = cap2.startperiod " +
			"                                         and cap1.endperiod = cap2.endperiod " +
			"where cui.myusi = :usi " +
			"  and ulc.published " +
			"  and cap1.published " +
			"  and cap2.published " +
			"  and ulc.mypt = ifnull(:pt,ulc.mypt) " +
			"  and ulc.mygg = ifnull(:gg,ulc.mygg) " +
			"  and ulc.mydfdl = ifnull(:dfdl,ulc.mydfdl) " +
			"  and cap1.startperiod >= ifnull(:from,cap1.startperiod) " +
			"  and cap1.endperiod <= ifnull(:to,cap1.endperiod) " +
			"  and cap2.captype = :parentCapType " +
			"group by cap2.id", nativeQuery = true)
	List<CalendarPeriod> findCapListScheduledForEPOD(
			String usi, Timestamp from, Timestamp to, String pt, String gg, String dfdl, String parentCapType);

}