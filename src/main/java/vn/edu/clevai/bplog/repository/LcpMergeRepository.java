package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.LcpMerge;

import java.util.Optional;

public interface LcpMergeRepository extends JpaRepository<LcpMerge, Long> {
	@Query(
			nativeQuery = true,
			value = "SELECT " +
					"    lcpmain.* " +
					"FROM " +
					"    bp_lcpm_lcpmerge AS lcpmain " +
					"WHERE " +
					"    lcpmain.ismain " +
					"    AND lcpmain.isudlm " +
					"    AND EXISTS ( " +
					"        SELECT " +
					"            * " +
					"        FROM " +
					"            bp_lcpm_lcpmerge AS lcpm " +
					"        JOIN bp_lcp_lcperiod AS lcp ON " +
					"            lcpm.mylcp = lcp.code " +
					"        WHERE " +
					"            lcpm.isudlm " +
					"            AND lcpm.published " +
					"            AND lcp.published " +
					"            AND lcp.mylctparent = :ushLct " +
					"    )"
	)
	Optional<LcpMerge> findMainUdlLcpMergeByUshLct(String ushLct);
}