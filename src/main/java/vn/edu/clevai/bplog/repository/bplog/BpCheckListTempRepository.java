package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.logDb.BpCheckListTemp;

import java.util.List;
import java.util.Optional;

public interface BpCheckListTempRepository extends JpaRepository<BpCheckListTemp, Integer> {

	Optional<BpCheckListTemp> findByCode(String code);

	List<BpCheckListTemp> findByMyParentChltCode(String myParentChltCode);

}
