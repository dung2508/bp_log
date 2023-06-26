package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.logDb.BpChptCheckProcessTemp;
import vn.edu.clevai.bplog.repository.projection.CheckProcessTempProjection;

import java.util.List;
import java.util.Optional;

public interface BpChptCheckProcessTempRepository extends JpaRepository<BpChptCheckProcessTemp, Integer> {

	@Query(value = "SELECT *, bcc.created_at as createAt, bcc.updated_at AS updateAt "
			+ "FROM bp_chpt_checkprocesstemp bcc " + "WHERE mylct = :myLct AND mylcet = :myLcEt "
			+ "AND ifnull(triggerusertype,'null') = IFNULL(:USTTrigger,'null') "
			+ "AND ifnull(checkerusertype,'null') = IFNULL(:USTChecker,'null') "
			+ "AND ifnull(mychpttype,'null') = IFNULL(:myChptType,'null') "
			+ "ORDER BY created_at DESC LIMIT 1  ", nativeQuery = true)
	Optional<CheckProcessTempProjection> findCHPT(String myLct, String myLcEt, String USTTrigger, String USTChecker,
												  String myChptType);

	//List<BpChptCheckProcessTemp> findBpChptCheckProcessTempsByCode(String code);

	Optional<BpChptCheckProcessTemp> findFirstByCode(String code);

	boolean existsByCode(String code);

	@Query(value = "SELECT b.* FROM bp_chpi_checkprocessitem a, bp_chpt_checkprocesstemp b "
			+ "WHERE a.mychpt  = b.code AND a.mycuievent = :cuiEventCode AND b.mylct = :lctCode  AND b.mylcet = :lcetCode "
			+ "ORDER BY b.id DESC LIMIT 1", nativeQuery = true)
	Optional<BpChptCheckProcessTemp> findByCuiEeventCodeAndLctCodeAndLcetCode(String cuiEventCode, String lctCode,
																			  String lcetCode);

	Optional<BpChptCheckProcessTemp> findFirstByMyLcpCodeAndMyLcetCode(String myLcp_code, String myLcet_code);


	@Query(value = "" +
			"select * " +
			"from bp_chpt_checkprocesstemp " +
			"where mylcp = :lcpCode", nativeQuery = true)
	List<BpChptCheckProcessTemp> findAllByMyLcpCode(String lcpCode);

}
