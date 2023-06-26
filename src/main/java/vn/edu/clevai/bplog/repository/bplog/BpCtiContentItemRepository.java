package vn.edu.clevai.bplog.repository.bplog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.ContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.repository.projection.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BpCtiContentItemRepository extends JpaRepository<BpContentItem, Integer> {
	List<BpContentItem> findByCodeIn(List<String> code);

	boolean existsByCode(String code);

	Optional<BpContentItem> findFirstByCode(String code);

	Optional<BpContentItem> findFirstByCodeAndPublishedTrue(String code);

	default <S extends BpContentItem> S createOrUpdate(S entity) {
		if (entity.getId() == null) {
			findFirstByCode(entity.getCode())
					.ifPresent(existed -> entity.setId(existed.getId()));
		}

		saveAndFlush(entity);

		return entity;
	}

	@Query(nativeQuery = true, value =
			"WITH t AS (SELECT cui.* " +
					"           FROM bp_cui_content_user_ulc_instance cui " +
					"                    INNER JOIN bp_usi_useritem usi on cui.myusi = usi.code " +
					"               AND usi.myust = 'ST' " +
					"               AND cui.published " +
					"           WHERE cui.myulc IN :ulcCodes " +
					"           LIMIT 1), " +
					"     t2 AS ( " +
					"         SELECT cm.mycti, RANK() over (ORDER BY c.myulc) AS ordering " +
					"         FROM bp_cui_content_user_ulc_instance c " +
					"                  INNER JOIN t ON c.myusi = t.myusi " +
					"             AND c.published " +
					"                  INNER JOIN bp_cui_content_user_ulc_instance cm ON c.myulc = cm.myulc " +
					"             AND cm.myusi = 'AU' " +
					"         WHERE c.myulc IN :ulcCodes " +
					"     ) " +
					"SELECT cti.mybl5qp, ordering " +
					"FROM bp_cti_contentitem cti " +
					"         INNER JOIN t2 ON cti.code = t2.mycti AND cti.published ")
	List<RQSProjection> getRQSInfo(Collection<String> ulcCodes);

	@Query(nativeQuery = true, value =
			"WITH t AS (SELECT cui.* " +
					"           FROM bp_cui_content_user_ulc_instance cui " +
					"                    INNER JOIN bp_usi_useritem usi on cui.myusi = usi.code " +
					"               AND usi.myust = 'ST' " +
					"               AND cui.published " +
					"           WHERE cui.myulc IN :ulcCodes " +
					"           LIMIT 1), " +
					"     t2 AS ( " +
					"         SELECT cm.mycti, RANK() over (ORDER BY c.myulc) AS ordering " +
					"         FROM bp_cui_content_user_ulc_instance c " +
					"                  INNER JOIN t ON c.myusi = t.myusi " +
					"             AND c.published " +
					"                  INNER JOIN bp_cui_content_user_ulc_instance cm ON c.myulc = cm.myulc " +
					"             AND cm.myusi = 'AU' " +
					"         WHERE c.myulc IN :ulcCodes " +
					"     ) " +
					"SELECT " +
					"   cti.id AS id, " +
					"   cti.mybl5qp, " +
					"   ordering " +
					"FROM bp_cti_contentitem cti " +
					"         INNER JOIN t2 ON cti.code = t2.mycti AND cti.published ")
	List<DQSProjection> getDqsInfo(Collection<String> ulcCodes);


	@Query(nativeQuery = true, value =
			"SELECT bcc.* " +
					"FROM bp_cui_content_user_ulc_instance cui " +
					"         INNER JOIN bp_cti_contentitem bcc on cui.mycti = bcc.code " +
					"    AND cui.published " +
					"    AND bcc.published " +
					"    AND cui.myulc = :ulc " +
					"    AND cui.myusi = 'AU'")
	BpContentItem findUlcCti(String ulc);

	@Query(nativeQuery = true, value =
			"WITH t AS (SELECT cui.* " +
					"           FROM bp_cui_content_user_ulc_instance cui " +
					"                    INNER JOIN bp_usi_useritem usi on cui.myusi = usi.code " +
					"               AND usi.myust = 'ST' " +
					"               AND cui.published " +
					"           WHERE cui.myulc IN :ulcCodes " +
					"           LIMIT 1), " +
					"     t2 AS ( " +
					"         SELECT DISTINCT cm.mycti " +
					"         FROM bp_cui_content_user_ulc_instance c " +
					"                  INNER JOIN t ON c.myusi = t.myusi " +
					"             AND c.published " +
					"                  INNER JOIN bp_cui_content_user_ulc_instance cm ON c.myulc = cm.myulc " +
					"             AND cm.myusi = 'AU' " +
					"         WHERE c.myulc IN :ulcCodes " +
					"     ) " +
					"SELECT DISTINCT cti.mylo " +
					"FROM bp_cti_contentitem cti " +
					"         INNER JOIN t2 ON cti.code = t2.mycti " +
					"WHERE cti.mylo IS NOT NULL")
	List<String> getAQR1OrCQRCti(Collection<String> ulcCodes);

	@Query(
			nativeQuery = true,
			value = "WITH t AS (SELECT cui.* " +
					"           FROM bp_cui_content_user_ulc_instance cui " +
					"                    INNER JOIN bp_usi_useritem usi on cui.myusi = usi.code " +
					"               AND usi.myust = 'ST' " +
					"               AND cui.published " +
					"           WHERE cui.myulc IN :ulcCodes " +
					"           LIMIT 1), " +
					"     t2 AS ( " +
					"         SELECT DISTINCT cm.mycti, ulcC.myparentulc " +
					"         FROM bp_cui_content_user_ulc_instance c " +
					"                  INNER JOIN t ON c.myusi = t.myusi " +
					"             AND c.published " +
					"                  INNER JOIN bp_cui_content_user_ulc_instance cm ON c.myulc = cm.myulc " +
					"             AND cm.myusi = 'AU' " +
					"                  INNER JOIN bp_ulc_uniquelearningcomponent ulcC ON c.myulc = ulcC.code " +
					"         WHERE c.myulc IN :ulcCodes " +
					"     ) " +
					"SELECT cti.mylo, RANK() over (ORDER BY myparentulc) AS ordering " +
					"FROM bp_cti_contentitem cti " +
					"         INNER JOIN t2 ON cti.code = t2.mycti")
	List<AQR2InfoProjection> getAQR2Cti(Collection<String> ulcCodes);

	@Query(nativeQuery = true, value =
			"SELECT DISTINCT ctiC.myvalueset AS slideUrl, ctiC.code AS ctiCode, ctiC.name " +
					"FROM bp_cui_content_user_ulc_instance cui " +
					"         INNER JOIN bp_cti_contentitem cti ON cui.mycti = cti.code " +
					"    AND cti.published " +
					"    AND cui.myulc = :ulcCode " +
					"    AND cui.published " +
					"    AND cui.myusi = 'AU' " +
					"         INNER JOIN bp_cti_contentitem ctiC ON cti.code = ctiC.myparentcti " +
					"    AND ctiC.published " +
					"    AND ctiC.myctt = 'CTI_SSST'")
	List<CtiSlideInfoProjection> getUlcSlideUrls(String ulcCode);

	@Query(nativeQuery = true, value =
			"SELECT ctiC.starturl " +
					"FROM bp_cui_content_user_ulc_instance cui " +
					"         INNER JOIN bp_cti_contentitem cti ON cui.mycti = cti.code " +
					"    AND cti.published " +
					"    AND cui.published " +
					"    AND cui.myulc = :ulcCode " +
					"    AND cui.myusi = 'AU' " +
					"         INNER JOIN bp_cti_contentitem ctiC ON cti.code = ctiC.myparentcti " +
					"    AND ctiC.published " +
					"    AND ctiC.myctt = 'CTI_VDL' " +
					"LIMIT 1")
	String getUlcVideoUrl(String ulcCode);

	List<BpContentItem> findAllByMyParentAndPublishedTrueAndMyCtt(String myParent, String myCtt);

	@Query(nativeQuery = true, value =
			"SELECT cti.name, cti.duration " +
					"FROM bp_cui_content_user_ulc_instance cui " +
					"         INNER JOIN bp_cti_contentitem cti ON cui.mycti = cti.code " +
					"    AND cui.published " +
					"    AND cti.published " +
					"    AND cui.myulc = :ulc " +
					"    AND cui.myusi = 'AU' " +
					"LIMIT 1")
	PC40MIInfoProjection getPC40MICti(String ulc);


	@Query(value = "FROM ContentItem cti WHERE cti.parentContentItem = :rootCtiCode")
	List<ContentItem> findAllByParent(String rootCtiCode);

	@Query(
			nativeQuery = true,
			value = "WITH mainCuis AS ( " +
					"    SELECT " +
					"        cui.* " +
					"    FROM bp_cui_content_user_ulc_instance AS cui " +
					"    WHERE cui.myulc IN :ulcCodes  " +
					"        AND cui.myusi = \"AU\" " +
					"        AND cui.mycti IS NOT NULL " +
					") " +
					"SELECT " +
					"    mc.myulc                                        AS myulc, " +
					"    cti.mylo                                        AS mylo, " +
					"    IF(buu.ulc_no BETWEEN 1 AND 3, buu.ulc_no, 0)   AS `group` " +
					"FROM " +
					"    mainCuis AS mc " +
					"INNER JOIN bp_cti_contentitem AS cti ON " +
					"    cti.code = mc.mycti " +
					"    AND cti.mylo IS NOT NULL " +
					"INNER JOIN bp_ulc_uniquelearningcomponent AS buu ON " +
					"    buu.code = mc.myulc "
	)
	List<AQR1InfoProjection> getAQR1Cti(Collection<String> ulcCodes);
}