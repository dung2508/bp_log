package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.CurriculumProgramSheet;

import java.sql.Timestamp;
import java.util.Optional;

public interface CurriculumProgramSheetRepository extends JpaRepository<CurriculumProgramSheet, Long>,
		JpaSpecificationExecutor<CurriculumProgramSheet> {
	Optional<CurriculumProgramSheet> findAllByMyCrppAndMyGGAndPublishedTrue(String crpp, String grade);

	@Query(nativeQuery = true, value =
			"SELECT crps.* " +
					"FROM bp_crps_curriculumprogramsheet crps " +
					"         INNER JOIN bp_crpp_curriculumprogrampackage crpp ON crps.mycrpp = crpp.code " +
					"    AND crps.mygg = :gg " +
					"    AND crpp.mypt = :pt " +
					"         INNER JOIN bp_accyear ac ON crpp.myaccyear = ac.code " +
					"    AND :timestamp BETWEEN ac.startdate AND ac.enddate " +
					"    AND crpp.myterm = 'MT' " +
					"WHERE crps.published " +
					"  AND crpp.published " +
					"LIMIT 1")
	Optional<CurriculumProgramSheet> getCrps(String pt, String gg, Timestamp timestamp);
}
