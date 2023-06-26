package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.logDb.BpChsiCheckStepItem;

import java.util.List;
import java.util.Optional;

public interface BpChsiCheckStepItemRepository extends JpaRepository<BpChsiCheckStepItem, Integer> {
	boolean existsByCode(String code);

	@Query(value = "SELECT * FROM bp_chsi_checkstepitem WHERE mychst = :CHSTCode "
			+ "ORDER BY CAST(SUBSTRING_INDEX(code, '-',-1) AS UNSIGNED) DESC, ID DESC LIMIT 1 ", nativeQuery = true)
	Optional<BpChsiCheckStepItem> findLastByCHST(String CHSTCode);

	Optional<BpChsiCheckStepItem> findFirstByCode(String code);

	/**
	 * @param chpiCode
	 * @return
	 */
	@Query("SELECT a FROM BpChsiCheckStepItem a WHERE a.myChpi.code = :chpiCode")
	List<BpChsiCheckStepItem> findAllByChpiCode(String chpiCode);
}
