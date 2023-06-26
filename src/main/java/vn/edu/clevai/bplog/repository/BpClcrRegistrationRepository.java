package vn.edu.clevai.bplog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.clevai.bplog.entity.BpClcrRegistration;
import vn.edu.clevai.bplog.repository.projection.ClassCategoryProjection;

public interface BpClcrRegistrationRepository extends JpaRepository<BpClcrRegistration, Integer> {

	@Query(value = "SELECT clc.id as id, pt.id as productId, gg.id as gradeId, wso.code as dayOfWeek, "
			+ "cashsta.id as timeSlotId FROM bp_clcr_clcregistration clc JOIN "
			+ "bp_pt_producttype pt ON clc.mypt = pt.code JOIN bp_gg_gradegroup gg ON clc.mygg = gg.code "
			+ "JOIN bp_wso_weeklyscheduleoption wso ON clc.mywso = wso.code "
			+ "JOIN bp_cashsta_calshiftstart cashsta ON clc.mycashsta = cashsta.code "
			+ "WHERE clc.is_deleted = FALSE AND clc.myusi = :myusi AND clc.time_session_id = :timeSessionId", nativeQuery = true)
	List<ClassCategoryProjection> getAllByMyusiAndTimeSessionId(String myusi, String timeSessionId);
	
	List<BpClcrRegistration> findAllByMyusiAndTimeSessionId(String myusi, String timeSessionId);
}
