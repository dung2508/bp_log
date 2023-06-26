package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.Extracurricular;

import java.sql.Timestamp;
import java.util.List;

public interface ExtracurricularRepository extends JpaRepository<Extracurricular, Long> {

	@Query(
			value = "SELECT * " +
					"FROM extracurricular " +
					"WHERE (:gradeId IS NULL OR grade_id = :gradeId) " +
					"  AND start_time BETWEEN :startTime AND :endTime ",
			nativeQuery = true)
	List<Extracurricular> findByGradeIdAndTimeRange(Long gradeId, Timestamp startTime, Timestamp endTime);

}