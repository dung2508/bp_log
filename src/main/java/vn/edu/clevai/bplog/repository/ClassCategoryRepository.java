package vn.edu.clevai.bplog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.ClassCategory;

import java.util.List;
import java.util.Optional;

public interface ClassCategoryRepository extends JpaRepository<ClassCategory, Long> {
	Optional<ClassCategory> findFirstByMyAccYearAndMyPtAndMyGgAndMyDfdlAndMyTermAndPublishedAndMyWso(
			String myAccYear, String myPt, String myGg, String myDfdl, String myTerm, Boolean published, String myWso);

	@Query(value = "" +
			"select * " +
			"from bp_clc_classcategory clc  " +
			"where ifnull(myaccyear,'null') = ifnull(:accYear,'null')   " +
			"and  ifnull(myterm,'null') = ifnull(:mt,'null')  " +
			"and  ifnull(mygg,'null') = ifnull(:gg,'null')  " +
			"and  ifnull(mypt,'null') = ifnull(:pt,'null')  " +
			"and  ifnull(mywso,'null') = ifnull(:wso,'null')  " +
			"and ifnull(mydfdl,'null') = ifnull(:dfdl,'null')  " +
			"and ifnull(mydfge,'null') = ifnull(:dfge,'null')  " +
			"and clctype = :clcType " +
			"and published = :published " +
			"limit 1", nativeQuery = true)
	Optional<ClassCategory> findClc(
			String accYear, String mt, String pt, String gg, String wso, String dfdl, String dfge, Boolean published, String clcType);

	Optional<ClassCategory> findFirstByMyAccYearAndMyGgAndMyTermAndClcTypeAndPublished(
			String myAccYear, String myGg, String myTerm, String clcType, Boolean published);


	Optional<ClassCategory> findFirstByCode(String code);

	List<ClassCategory> findAllByMyAccYearAndMyTermAndPublished(String ay, String mt, Boolean published);

	List<ClassCategory> findAllByClcTypeAndMyAccYearAndPublishedTrue(String clcType, String myAccYear);
}
