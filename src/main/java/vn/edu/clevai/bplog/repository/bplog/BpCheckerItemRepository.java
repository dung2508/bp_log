package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.logDb.BpCheckerItem;
import vn.edu.clevai.bplog.repository.projection.ChriInfoProjection;

import java.util.List;
import java.util.Optional;

public interface BpCheckerItemRepository extends JpaRepository<BpCheckerItem, Integer> {


	@Query("SELECT a FROM BpCheckerItem a WHERE a.myUsi = :usiCode AND a.myChrt.code = :chrtCode")
	List<BpCheckerItem> findBpCheckerItemsByMyUsiAndMyChrt(String usiCode, String chrtCode);

	@Query(value = "SELECT bcc.name AS name, buu.email AS email, bcc.mychrt FROM bp_chri_checkeritem bcc "
			+ "JOIN bp_usi_useritem buu on  bcc.myusi = buu.code WHERE bcc.code = :code "
			+ "LIMIT 1 ", nativeQuery = true)
	Optional<ChriInfoProjection> findEmailByChriCode(String code);

	@Query(value = "SELECT * FROM bp_chri_checkeritem AS a "
			+ "WHERE a.code LIKE (:code%) "
			+ "ORDER BY CAST(SUBSTRING_INDEX(code, '-',-1) AS UNSIGNED) DESC, ID DESC limit 1", nativeQuery = true)
	Optional<BpCheckerItem> findLastByCode(String code);

	Optional<BpCheckerItem> findFirstByCode(String code);
}
