package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.CurriculumProgramPackage;

import java.sql.Timestamp;
import java.util.Optional;

public interface CurriculumProgramPackageRepository extends JpaRepository<CurriculumProgramPackage, Long>,
		JpaSpecificationExecutor<CurriculumProgramPackage> {

	@Query(value = "select *  " +
			"from bp_crpp_curriculumprogrampackage  " +
			"where myaccyear = :academicYear " +
			" and :time BETWEEN startdate and enddate" +
			" and mypt = :productType " +
			" and published = 1 " +
			" limit 1", nativeQuery = true)
	Optional<CurriculumProgramPackage> findByMyAccYearAndTimeAndMyPt
			(String academicYear, Timestamp time, String productType);

	Optional<CurriculumProgramPackage> findByMyAccYearAndMyTermAndMyPtAndPublishedTrue
			(String ayCode, String term, String ptCode);

	@Query(
			nativeQuery = true,
			value = "SELECT " +
					"    * " +
					"FROM " +
					"    bp_crpp_curriculumprogrampackage AS bcc " +
					"WHERE " +
					"    :time BETWEEN startdate AND enddate " +
					"    AND mypt = :pt " +
					"    AND published"
	)
	Optional<CurriculumProgramPackage> findByPtAndTime(String pt, Timestamp time);
}
