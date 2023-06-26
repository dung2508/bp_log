package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.logDb.BpCheckListItem;

import java.util.List;
import java.util.Optional;

public interface BpCheckListItemRepository extends JpaRepository<BpCheckListItem, Integer> {
	@Query(value = "SELECT * FROM bp_chli_checklistitem WHERE myparentchlt = :chltCode "
			+ "ORDER BY CAST(SUBSTRING_INDEX(code, '-',-1) AS UNSIGNED) DESC, ID DESC "
			+ "LIMIT 1", nativeQuery = true)
	Optional<BpCheckListItem> findLastByParentCHLT(String chltCode);

	@Query(value = "SELECT * FROM bp_chli_checklistitem WHERE myparentchli = :chliCode "
			+ "ORDER BY CAST(SUBSTRING_INDEX(code, '-',-1) AS UNSIGNED) DESC, ID DESC LIMIT 1", nativeQuery = true)
	Optional<BpCheckListItem> findLastByParentCHLI(String chliCode);

	/**
	 * @param chliCode
	 * @return
	 */
	Optional<BpCheckListItem> findFirstByCode(String chliCode);

	@Query("SELECT a FROM BpCheckListItem a WHERE a.code IN (:chliCodes)")
	List<BpCheckListItem> findAllByListChliCode(List<String> chliCodes);

	@Query("SELECT a FROM BpCheckListItem a WHERE a.myChsi.code = :chsiCode")
	Page<BpCheckListItem> findFirstByChsiCode(String chsiCode, Pageable pageable);
}
