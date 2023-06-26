package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpPRD;

import java.util.Optional;

public interface BpPRDRepository extends JpaRepository<BpPRD, Long> {

	@Query(nativeQuery = true, value =
			"SELECT * FROM bp_prd_periodduration WHERE periodlength = :length AND LOWER(periodunit) = LOWER(:unit) ORDER BY id LIMIT 1")
	Optional<BpPRD> findByPeriodAndLength(Integer length, String unit);
}