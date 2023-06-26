package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.UsiDutyClassCategory;

import java.util.Optional;

public interface BpUsiDutyClassCategoryRepository extends JpaRepository<UsiDutyClassCategory, Long> {
	Optional<UsiDutyClassCategory> findFirstByCode(String code);
}
