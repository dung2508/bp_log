package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpDfdlDifficultygrade;

import java.util.List;
import java.util.Optional;

public interface BpDfdlDifficultygradeRepository extends JpaRepository<BpDfdlDifficultygrade, Integer> {
	Optional<BpDfdlDifficultygrade> findByCode(String code);
	Optional<BpDfdlDifficultygrade> findByCodeAndPublishedTrue(String code);
	List<BpDfdlDifficultygrade> findAllByPublishedTrue();

	@Query("SELECT code FROM BpDfdlDifficultygrade WHERE published = :isPublished ")
	List<String> findOnlyCode(boolean isPublished);
}
