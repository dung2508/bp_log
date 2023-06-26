package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentType;

import java.util.List;
import java.util.Optional;

public interface BpLearningComponentTypeRepository extends JpaRepository<BpLearningComponentType, Integer> {
	List<BpLearningComponentType> findAllByCode(String code);

	List<BpLearningComponentType> findAllByMyLclAndPublishedTrue(String myLcl);

	Optional<BpLearningComponentType> findByCode(String code);

	@Query(
			value = "SELECT lct.* " +
					"FROM bp_lcpm_lcpmerge lcpmain " +
					"JOIN bp_lct_learningcomponenttype lct ON lcpmain.mylctpk = lct.code " +
					"WHERE lcpmain.ismain " +
					"  AND lcpmain.isudlm " +
					"  AND EXISTS " +
					"    (SELECT * " +
					"     FROM bp_lcpm_lcpmerge lcpm " +
					"              JOIN bp_lcp_lcperiod lcp ON lcpm.mylcp = lcp.code " +
					"     WHERE lcpm.isudlm " +
					"       AND lcpm.published " +
					"       AND lcp.mylctparent = :lct) " +
					"LIMIT 1",
			nativeQuery = true
	)
	Optional<BpLearningComponentType> findMainLctByMergedUdl(String lct);

}
