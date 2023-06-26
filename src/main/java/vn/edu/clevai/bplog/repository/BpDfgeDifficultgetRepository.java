package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BpDfgeDifficultget;

import java.util.List;
import java.util.Optional;

public interface BpDfgeDifficultgetRepository extends JpaRepository<BpDfgeDifficultget, String> {
	Optional<BpDfgeDifficultget> findByCode(String code);

	List<BpDfgeDifficultget> findByCodeIsNot(String code);
}
