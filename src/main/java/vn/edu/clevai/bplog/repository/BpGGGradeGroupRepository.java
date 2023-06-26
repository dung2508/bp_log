package vn.edu.clevai.bplog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpGGGradeGroup;

public interface BpGGGradeGroupRepository extends JpaRepository<BpGGGradeGroup, Integer> {
	Optional<BpGGGradeGroup> findByCep100GradeId(String cep100GradeId);

	Optional<BpGGGradeGroup> findByCode(String code);

	List<BpGGGradeGroup> findAllByCode(String code);

	Optional<BpGGGradeGroup> findByCodeAndPublishedTrue(String code);

	@Query(value = "SELECT * FROM bp_gg_gradegroup gg" +
			" join bp_usid_usiduty usid on gg.code = usid.mygg and usid.published" +
			" WHERE usid.mybpp like concat('%', :mybpp, '%') and usid.myaccyear = :myAccYear and gg.published", nativeQuery = true)
	List<BpGGGradeGroup> findAllGGByUSID(String mybpp, String myAccYear);
	List<BpGGGradeGroup> findAllByPublishedTrue();


	@Query("SELECT code FROM BpGGGradeGroup WHERE published = :isPublished")
	List<String> findOnlyCode(boolean isPublished);

}
