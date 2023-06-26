package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BpPODDFDL;

import java.util.Optional;

public interface BpPODDFDLRepository extends JpaRepository<BpPODDFDL, String> {

	Optional<BpPODDFDL> findByCode(String code);
}
