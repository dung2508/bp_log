package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.ULCMerge;

import java.util.Optional;

public interface ULCMergeRepository extends JpaRepository<ULCMerge, Long> {

	Optional<ULCMerge> findFirstByCode(String code);
}