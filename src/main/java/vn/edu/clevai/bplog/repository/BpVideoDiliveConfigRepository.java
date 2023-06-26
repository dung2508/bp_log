package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BpVideoDiliveConfig;

import java.util.Optional;

public interface BpVideoDiliveConfigRepository extends JpaRepository<BpVideoDiliveConfig, Integer> {
	Optional<BpVideoDiliveConfig> findByMyptAndMyggAndMydfdl(String pt, String gg, String dfdl);
}
