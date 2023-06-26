package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentEventType;

import java.util.List;
import java.util.Optional;

public interface BpLearningComponentEventTypeRepository extends JpaRepository<BpLearningComponentEventType, Integer> {

	List<BpLearningComponentEventType> findBpLearningComponentEventTypesByCode(String code);

	Optional<BpLearningComponentEventType> findFirstByCode(String code);

	Optional<BpLearningComponentEventType> findByCode(String code);

	/**
	 * @param listCode
	 * @return
	 */
	List<BpLearningComponentEventType> findAllByCodeIn(List<String> listCode);
}
