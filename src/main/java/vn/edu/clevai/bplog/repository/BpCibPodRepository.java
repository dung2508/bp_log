package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BpCibPod;

import java.util.Optional;

public interface BpCibPodRepository extends JpaRepository<BpCibPod, Integer> {
	Optional<BpCibPod> findFirstByMypodOrderByUpdatedAtDesc(String mypod);
}
