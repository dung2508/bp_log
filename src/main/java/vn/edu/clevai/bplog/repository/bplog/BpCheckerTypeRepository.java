package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.logDb.BpCheckerType;

import java.util.List;

public interface BpCheckerTypeRepository extends JpaRepository<BpCheckerType, Integer> {
	List<BpCheckerType> findByCode(String code);
}
