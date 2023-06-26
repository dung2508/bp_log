package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BpPODWSO;

import java.util.Optional;

public interface BpPODWSORepository extends JpaRepository<BpPODWSO, Long> {

	Optional<BpPODWSO> findByCode(String code);
}
