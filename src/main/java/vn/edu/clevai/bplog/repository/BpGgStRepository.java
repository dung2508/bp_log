package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.BpStGg;

import java.util.Optional;

public interface BpGgStRepository extends JpaRepository<BpStGg, Integer> {
	Optional<BpStGg> findByCode(String code);
}
