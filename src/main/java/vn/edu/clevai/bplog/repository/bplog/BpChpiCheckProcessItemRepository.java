package vn.edu.clevai.bplog.repository.bplog;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.logDb.BpChpiCheckProcessItem;

import java.util.List;
import java.util.Optional;

public interface BpChpiCheckProcessItemRepository extends JpaRepository<BpChpiCheckProcessItem, Integer> {

	@Query(value = "SELECT * FROM bp_chpi_checkprocessitem "
			+ "WHERE bp_chpi_checkprocessitem.mychpt = :CHPTCode "
			+ "ORDER BY CAST(SUBSTRING_INDEX(code, '-',-1) AS UNSIGNED) DESC, ID DESC " + "limit 1", nativeQuery = true)
	Optional<BpChpiCheckProcessItem> findLastByCHPT(String CHPTCode);

	List<BpChpiCheckProcessItem> findByCode(String code);

	Optional<BpChpiCheckProcessItem> findFirstByCode(String code);

	List<BpChpiCheckProcessItem> findAllByMyCuiEventCode(String cuiEventCOde);

	@Query(
			value = "SELECT * " +
					"FROM bp_chpi_checkprocessitem " +
					"WHERE mycuievent = :mycuievent ",
			nativeQuery = true)
	Optional<BpChpiCheckProcessItem> findFirstByCuiEvent(String mycuievent);

}
