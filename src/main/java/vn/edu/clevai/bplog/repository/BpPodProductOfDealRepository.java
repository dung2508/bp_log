package vn.edu.clevai.bplog.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BpPodProductOfDealRepository extends JpaRepository<BpPodProductOfDeal, Long> {

	Optional<BpPodProductOfDeal> findByCode(String code);


	Page<BpPodProductOfDeal> findByIdBetween(Long fromId, Long toId, Pageable pageable);

	@Query(value = "" +
			"select  * " +
			"from bp_pod_productofdeal " +
			"where code in :listCode ", nativeQuery = true)
	List<BpPodProductOfDeal> findALlByListCode(List<String> listCode);

	@Query(value = "" +
			"select a.* " +
			"    from bp_pod_productofdeal a " +
			"join bp_pod_clag b on a.code = b.mypod " +
			"where b.myclag IN :clag and " +
			"      :date between b.assigned_at and b.unassigned_at", nativeQuery = true)
	List<BpPodProductOfDeal> findByClagInAndDate(List<String> clag, Date date);

	@Query(value = "" +
			"select distinct pod.*  " +
			"from bp_pod_productofdeal pod  " +
			"         join bp_pod_clag bpc on pod.code = bpc.mypod  " +
			"         join bp_clag_classgroup bcc on bcc.code = bpc.myclag  " +
			"         join bp_usi_useritem buu on pod.myst = buu.code  " +
			"where bcc.code in (:clagCode)  " +
			"  and bpc.active  " +
			"  and bcc.active  " +
			"  and :date BETWEEN bpc.assigned_at AND bpc.unassigned_at " +
			"  and buu.myust =:ust", nativeQuery = true)
	List<BpPodProductOfDeal> findSTByClagAndDate(List<String> clagCode, Date date, String ust);

	@Query(value = "" +
			"select distinct pod.*  " +
			"from bp_pod_productofdeal pod  " +
			"         join bp_pod_clag bpc on pod.code = bpc.mypod  " +
			"         join bp_clag_classgroup bcc on bcc.code = bpc.myclag  " +
			"         join bp_usi_useritem buu on pod.myst = buu.code  " +
			"where bcc.code IN :clagCode  " +
			"  and bpc.active  " +
			"  and bcc.active  " +
			"  and now() BETWEEN bpc.assigned_at AND bpc.unassigned_at " +
			"  and buu.myust IN :ust", nativeQuery = true)
	List<BpPodProductOfDeal> findByClagCode(List<String> clagCode, List<String> ust);

	@Query(value = "" +
			"select distinct pod.* " +
			"from bp_pod_productofdeal pod " +
			"         join bp_pod_clag bpc on pod.code = bpc.mypod " +
			"         join bp_clag_classgroup bcc on bcc.code = bpc.myclag " +
			"         join bp_usi_useritem buu on pod.myst = buu.code " +
			"where bcc.code IN :clagCode " +
			"  and bpc.active " +
			"  and bcc.active " +
			"  and now() BETWEEN bpc.assigned_at AND bpc.unassigned_at " +
			"  and buu.myust IN :ust " +
			"  and bpc.assigned_at = :capStart " +
			"  and bpc.unassigned_at = :capEnd", nativeQuery = true)
	List<BpPodProductOfDeal> findByClagAndCadyAndUst(List<String> clagCode, List<String> ust, Timestamp capStart, Timestamp capEnd);

	@Query(value = "" +
			"select *    " +
			"from bp_pod_productofdeal    " +
			"where mypt = :pt    " +
			"and myst = :usi    " +
			"and Date(NOW()) between fromdate and todate    " +
			"order by created_at DESC     " +
			"limit 1 ", nativeQuery = true)
	Optional<BpPodProductOfDeal> findByUsiAndPt(String usi, String pt);

	@Query(value = "" +
			"select *    " +
			"from bp_pod_productofdeal    " +
			"where mypt = :pt    " +
			"and myst = :usi    " +
			"and :date between fromdate and todate    " +
			"order by created_at DESC     " +
			"limit 1 ", nativeQuery = true)
	Optional<BpPodProductOfDeal> findByUsiAndPtAndDate(String usi, String pt, java.sql.Date date);

	@Query(
			value = "SELECT bpp.* " +
					"FROM bp_usi_useritem us " +
					"         INNER JOIN bp_usid_usiduty ud ON ud.myusi = us.code " +
					"    AND ud.mylcet = :lcetCode " +
					"    AND ud.myust = :myust " +
					"    AND ud.mycap = :capCode " +
					"    AND ud.mychrt = :chrtCode " +
					"    AND ud.mylcp = :lcpCode " +
					"    AND ud.myusi NOT IN :excludeUSI " +
					"         INNER JOIN bp_usid_cashsta buc on ud.code = buc.myusid " +
					"         INNER JOIN bp_cashsta_calshiftstart bcc on buc.mycashsta = bcc.code " +
					"    AND bcc.mygg = :gg " +
					"    AND bcc.mydfdl = :dfdl " +
					"    AND bcc.mydfge = :dfge " +
					"         INNER JOIN bp_pod_productofdeal bpp on us.code = bpp.myst " +
					"LIMIT 1",
			nativeQuery = true)
	Optional<BpPodProductOfDeal> findFirstByUSIDuty(String lcetCode, String myust, String capCode, String chrtCode, String lcpCode, Collection<?> excludeUSI, String gg, String dfdl, String dfge);

	@Query(
			value = "SELECT DISTINCT bpp.* " +
					"FROM bp_usi_useritem us " +
					"         INNER JOIN bp_usid_usiduty ud ON ud.myusi = us.code " +
					"    AND ud.mylcet = :lcetCode " +
					"    AND ud.myust = :myust " +
					"    AND ud.mycap = :capCode " +
					"    AND ud.mychrt = :chrtCode " +
					"    AND ud.mylcp = :lcpCode " +
					"    AND ud.myusi NOT IN :excludeUSI " +
					"         INNER JOIN bp_usid_cashsta buc on ud.code = buc.myusid " +
					"         INNER JOIN bp_cashsta_calshiftstart bcc on buc.mycashsta = bcc.code " +
					"    AND bcc.mygg = :gg " +
					"    AND bcc.mydfdl = :dfdl " +
					"    AND bcc.mydfge = :dfge " +
					"         INNER JOIN bp_pod_productofdeal bpp on us.code = bpp.myst ",
			nativeQuery = true)
	List<BpPodProductOfDeal> findBy(String lcetCode, String myust, String capCode, String chrtCode, String lcpCode, Collection<?> excludeUSI, String gg, String dfdl, String dfge);

	@Query(value = "" +
			"select pod.mypt " +
			"from bp_pod_productofdeal pod " +
			"join bp_usi_useritem buu on buu.code = pod.myst " +
			"where :endCap between fromdate and todate " +
			"and buu.myust = :ust " +
			"group by pod.mypt ", nativeQuery = true)
	List<String> getPtFromCadyAndUst(Timestamp endCap, String ust);


	@Query(nativeQuery = true, value =
			"select *    " +
					"from bp_pod_productofdeal    " +
					"where myst = :usi    " +
					"and :time between fromdate and todate    " +
					"order by created_at DESC     ")
	List<BpPodProductOfDeal> findByUsi(String usi, Timestamp time);

	List<BpPodProductOfDeal> findAllByMystIn(Collection<String> usis);

	@Query(value = "" +
			"select distinct pod.*  " +
			"from bp_pod_productofdeal pod  " +
			"         join bp_pod_clag bpc on pod.code = bpc.mypod  " +
			"         join bp_clag_classgroup bcc on bcc.code = bpc.myclag  " +
			"         join bp_usi_useritem buu on pod.myst = buu.code  " +
			"where bcc.code IN :clagCode  " +
			"  and bpc.active  " +
			"  and bcc.active  " +
			"  and :start BETWEEN bpc.assigned_at AND bpc.unassigned_at " +
			"  and buu.myust IN :ust", nativeQuery = true)
	List<BpPodProductOfDeal> findPodCodeByClag(List<String> clagCode, List<String> ust, Timestamp start);

	@Query(
			nativeQuery = true,
			value = "SELECT " +
					"    bpp.* " +
					"FROM " +
					"    bp_cap_calendarperiod AS bcc " +
					"INNER JOIN bp_pod_clag AS bpc ON " +
					"    bcc.published " +
					"    AND bcc.code            = :cap " +
					"    AND bpc.myclag          = :clag " +
					"    AND bpc.unassigned_at   >= bcc.startperiod " +
					"    AND bpc.assigned_at     <= bcc.endperiod " +
					"    AND bpc.active " +
					"INNER JOIN bp_clag_classgroup AS bcc2 ON " +
					"    bcc2.code = bpc.myclag " +
					"    AND bcc2.active " +
					"INNER JOIN bp_pod_productofdeal AS bpp ON " +
					"    bpp.code = bpc.mypod " +
					"INNER JOIN bp_usi_useritem AS buu ON " +
					"    bpp.myst = buu.code " +
					"    AND buu.myust IN :usts"
	)
	List<BpPodProductOfDeal> findActivePodsClagAndCap(String clag, String cap, List<String> usts);
	
	Optional<BpPodProductOfDeal> findFirstByXdealAndMyst(Long xdeal, String myst);
	
}