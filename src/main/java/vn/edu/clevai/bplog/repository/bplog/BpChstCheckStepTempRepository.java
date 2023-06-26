package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.logDb.BpChstCheckStepTemp;

import java.util.List;
import java.util.Optional;

public interface BpChstCheckStepTempRepository extends JpaRepository<BpChstCheckStepTemp, Integer> {

	List<BpChstCheckStepTemp> findByCode(String code);

	List<BpChstCheckStepTemp> findByMyChpt(String chptCode);

	Optional<BpChstCheckStepTemp> findFirstByCode(String code);
}
