package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.Curriculum;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

	@Query(
			value = "SELECT * " +
					"FROM curriculum " +
					"WHERE subject_id IN :subjectIds " +
					"  AND grade_id IN :gradeIds " +
					"  AND class_level_id IN :classLevelIds " +
					"  AND training_type_id IN :trainingTypeIds " +
					"  AND start_date <= :endDate " +
					"  AND end_date >= :startDate",
			nativeQuery = true)
	List<Curriculum> findCurriculums(
			List<Long> subjectIds,
			List<Long> gradeIds,
			List<Long> classLevelIds,
			List<Long> trainingTypeIds,
			Date startDate,
			Date endDate
	);

	@Query(
			value = "SELECT * " +
					"FROM curriculum " +
					"WHERE subject_id = :subjectId " +
					"  AND grade_id = :gradeId " +
					"  AND class_level_id = :classLevelId " +
					"  AND training_type_id = :trainingTypeId " +
					"  AND start_date = :startDate " +
					"  AND end_date = :endDate " +
					"  AND ordering = :ordering",
			nativeQuery = true)
	Optional<Curriculum> findCurriculum(
			Long subjectId,
			Long gradeId,
			Long classLevelId,
			Long trainingTypeId,
			Date startDate,
			Date endDate,
			Integer ordering
	);

}