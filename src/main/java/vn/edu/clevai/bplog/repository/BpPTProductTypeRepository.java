package vn.edu.clevai.bplog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpPTProductType;

public interface BpPTProductTypeRepository extends JpaRepository<BpPTProductType, Integer>{
	Optional<BpPTProductType> findByCode(String code);
	List<BpPTProductType> findAllByCode(String code);
	
	@Query(value = "select * from bp_pt_producttype pt " +
			"join bp_lct_learningcomponenttype lct on lct.mypt = pt.code " +
			"and lct.code = :code and pt.published and lct.published", nativeQuery = true)
	Optional<BpPTProductType> findByLctParent(String code);


	List<BpPTProductType> findByCodeInAndPublishedTrue(List<String> codes);

	List<BpPTProductType> findAllByPublishedTrueOrderByCode();


	@Query(value = "FROM BpPTProductType where (:codes IS NULL OR code IN (:codes)) ")
    List<BpPTProductType> findAllByCodeIn(List<String> codes);

}
