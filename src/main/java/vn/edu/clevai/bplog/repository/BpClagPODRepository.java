package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BpClagPOD;

import java.util.Optional;

public interface BpClagPODRepository extends JpaRepository<BpClagPOD, Integer> {
	Optional<BpClagPOD> findByCode(String code);
}
