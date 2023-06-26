package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.CurriculumPeriod;

import java.util.List;
import java.util.Optional;

public interface CurriculumPeriodRepository extends JpaRepository<CurriculumPeriod, Integer> {
	Optional<CurriculumPeriod> findByMyCrpsAndMyCapAndCurrPeriodTypeAndPublishedTrue
			(String crps, String calendarPeriod, String periodType);

	List<CurriculumPeriod> findByMyParentCupAndMycupnoAndCurrPeriodTypeAndPublishedTrue
			(String parent, String periodNumber, String periodType);

	Optional<CurriculumPeriod> findByMyParentCupAndMyDfdlAndMyLcTypeAndCurrPeriodTypeAndPublishedTrue
			(String parent, String dfdl, String lcType, String periodType);

	Optional<CurriculumPeriod> findByMyCrpsAndMyDfdlAndMyLcTypeAndMynoaschildAndCurrPeriodTypeAndPublishedTrue
			(String crps, String dfdl, String lcType, Integer myNoAsChild, String periodType);

	Optional<CurriculumPeriod> findByMyParentCupAndMycupnoAndMyDfgeAndMynoaschildAndCurrPeriodTypeAndPublishedTrue
			(String parent, String periodNumber, String dfge, Integer myNoAsChild, String periodType);

	Optional<CurriculumPeriod> findByCodeAndCurrPeriodType(String code, String periodType);


	Optional<CurriculumPeriod> findByMyParentCupAndMycupnoAndMynoaschildAndPublishedTrue
			(String parent, String periodNumber, Integer myNoAsChild);

	Optional<CurriculumPeriod> findByMyParentCupAndMycupnoAndMynoaschildAndPublishedTrueAndMyDfdl
			(String parent, String periodNumber, Integer myNoAsChild, String dfdl);

	@Query(nativeQuery = true, value =
			"SELECT cup.* " +
					"FROM bp_cup_currperiod cup " +
					"         INNER JOIN bp_crps_curriculumprogramsheet b on cup.mycrps = b.code " +
					"    AND b.mygg = :gg " +
					"    AND b.published " +
					"    AND cup.published " +
					"         INNER JOIN bp_crpp_curriculumprogrampackage c on b.mycrpp = c.code " +
					"    AND c.myaccyear = :ay " +
					"    AND c.myterm = :mt " +
					"    AND c.mypt = :pt " +
					"    AND c.published " +
					"         INNER JOIN bp_cap_calendarperiod bcc on cup.mycap = bcc.code " +
					"    AND bcc.code = :cawk " +
					"    AND bcc.published")
	Optional<CurriculumPeriod> getCUWK(String ay, String mt, String pt, String gg, String cawk);

	List<CurriculumPeriod> findAllByMyParentCupAndPublishedTrue(String parent);

	Optional<CurriculumPeriod> findByMyCrpsAndMycupnoAndCurrPeriodTypeAndPublishedTrue
			(String crpsCode, String cuwkNo, String cupType);

	CurriculumPeriod findFirstByMyCrpsAndMycupnoAndPublishedTrueAndCurrPeriodType(String crps, String cupno, String cupType);

	CurriculumPeriod findFirstByMyCrpsAndMycupnoAndMynoaschildAndPublishedTrueAndCurrPeriodType(String crps, String cupno, int mynoaschild, String cupType);
}
