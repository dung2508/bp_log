package vn.edu.clevai.bplog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.repository.projection.AssignDoerProjection;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BpUsiUserItemRepository extends JpaRepository<BpUsiUserItem, Long> {
	Optional<BpUsiUserItem> findByUsername(String username);

	Optional<BpUsiUserItem> findByCode(String code);

	List<BpUsiUserItem> findAllByCodeIn(List<String> codes);

	Page<BpUsiUserItem> findByMyust(String ust, Pageable pageable);

	Page<BpUsiUserItem> findByIdBetweenAndMyust(Long fromId, Long toId, String myust, Pageable pageable);

	boolean existsByCode(String code);

	@Query(
			value = "SELECT us.* " +
					"FROM bp_usi_useritem us " +
					"         JOIN bp_usid_usiduty ud ON ud.myusi = us.code " +
					"WHERE ud.mylcet = :lcet " +
					"  AND ud.myust = :ust " +
					"  AND ud.mycap = :cap " +
					"  AND ud.mychrt = :chrt " +
					"  AND ud.mylcp = :lcp " +
					"  AND ud.myusi NOT IN :excludeUSI " +
					"ORDER BY RAND() " +
					"LIMIT 1",
			nativeQuery = true)
	Optional<BpUsiUserItem> findFirstUSIByUSIDuty(
			String lcet,
			String ust,
			String cap,
			String chrt,
			String lcp,
			Collection<String> excludeUSI
	);

	@Query(value = "SELECT us.code, " +
			"       us.username, " +
			"       us.phone, " +
			"       ud.mylcp, " +
			"       ud.mycap, " +
			"       ud.mylcet, " +
			"       ud.mychrt " +
			"FROM bp_usi_useritem us " +
			"         JOIN bp_usid_usiduty ud ON ud.myusi = us.code " +
			"WHERE ud.mylcet = :lcet " +
			"  AND ud.myust = :ust " +
			"  AND ud.mycap IN :caps " +
			"  AND ud.mychrt = :chrt " +
			"ORDER BY ud.code  ",
			nativeQuery = true)
	List<AssignDoerProjection> findListDoer(
			String lcet,
			String ust,
			Collection<String> caps,
			String chrt
	);

	List<BpUsiUserItem> findByMyust(String myust);


	@Query(value = "SELECT * " +
			"FROM bp_usi_useritem " +
			"WHERE myust IN :ustList " +
			"  AND (:name IS NULL OR LOWER(username) like LOWER(CONCAT('%', :name,'%')))  " +
			"  OR (:name IS NULL OR LOWER(fullname) LIKE LOWER(CONCAT('%', :name,'%'))) ",
			nativeQuery = true)
	Page<BpUsiUserItem> findByNameAndCode(List<String> ustList, String name, Pageable pageable);

	Optional<BpUsiUserItem> findFirstByMyustAndMyparentNull(String myust);

	Optional<BpUsiUserItem> findFirstByMyustAndMyparent(String myust, String myparent);

}
