package vn.edu.clevai.bplog.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.repository.BpPTProductTypeRepository;
import vn.edu.clevai.bplog.repository.CustomClagRepository;
import vn.edu.clevai.bplog.repository.projection.DteRequiredProjection;
import vn.edu.clevai.bplog.repository.projection.GteRequiredProjection;
import vn.edu.clevai.bplog.service.BpGGGradeGroupService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.common.api.model.DebuggingDTO;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class CustomClagRepositoryImpl implements CustomClagRepository {

	@Autowired
	private EntityManager em;

	@Autowired
	private BpPTProductTypeRepository ptRepo;

	@Autowired
	private LmsService lmsService;

	@Autowired
	private BpGGGradeGroupService gradeGroupService;

	@Override
	public List<GteRequiredProjection> gteRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
														  String startDate, String endDate, Integer subjectId) {

		ArrayList<GteRequiredProjection> results = new ArrayList<>();
		if (Objects.nonNull(productId)) {
			BpPTProductType pt = ptRepo.findById(productId).orElse(null);
			if (pt.getCode().equalsIgnoreCase("BC")) {
				return basicGteCalculate(gradeId, classLevelId, startDate, endDate, subjectId);
			}
			return nonBasicGteCalculate(productId, gradeId, classLevelId, startDate, endDate, subjectId);
		} else {
			results.addAll(basicGteCalculate(gradeId, classLevelId, startDate, endDate, subjectId));
			results.addAll(nonBasicGteCalculate(null, gradeId, classLevelId, startDate, endDate, subjectId));
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private List<GteRequiredProjection> nonBasicGteCalculate(Integer productId, Integer gradeId, Integer classLevelId,
															 String startDate, String endDate, Integer subjectId) {
		String query = "WITH RECURSIVE date_range AS (SELECT :startDate AS date, WEEKDAY(:startDate) + 2 AS wso UNION ALL "
				+ "SELECT ADDDATE(date_range.date, 1) AS date, WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso FROM date_range "
				+ "WHERE date_range.date < :endDate), twso AS (SELECT code, 2 AS wso FROM bp_wso_weeklyscheduleoption WHERE "
				+ "monday = TRUE UNION SELECT code, 3 AS wso FROM bp_wso_weeklyscheduleoption WHERE tuesday = TRUE "
				+ "UNION SELECT code, 4 AS wso FROM bp_wso_weeklyscheduleoption WHERE wednesday = TRUE UNION "
				+ "SELECT code, 5 AS wso FROM bp_wso_weeklyscheduleoption WHERE thursday = TRUE UNION SELECT code, 6 AS wso "
				+ "FROM bp_wso_weeklyscheduleoption WHERE friday = TRUE UNION SELECT code, 7 AS wso FROM bp_wso_weeklyscheduleoption "
				+ "WHERE saturday = TRUE UNION SELECT code, 8 AS wso FROM bp_wso_weeklyscheduleoption WHERE sunday = true), "
				+ "pod AS (SELECT d.date AS podDate, b.mypt, e.id AS ptid, b.mygg, f.id AS ggid, b.mydfdl, g.id AS dfdlid, "
				+ "COUNT(DISTINCT a.mypod) AS totalStudent FROM bp_pod_clag a JOIN bp_clag_classgroup b ON a.myclag  = b.code "
				+ "JOIN twso c ON b.mywso  = c.code JOIN date_range d ON c.wso = d.wso JOIN bp_pt_producttype e ON b.mypt  = e.code "
				+ "JOIN bp_gg_gradegroup f ON b.mygg  = f.code JOIN bp_dfdl_difficultygrade g ON b.mydfdl = g.code "
				+ "WHERE b.clagtype = 'PERM' AND b.active = TRUE AND (DATE(CONVERT_TZ(a.assigned_at, '+00:00', '+07:00')) <= d.date "
				+ "AND DATE(CONVERT_TZ(a.unassigned_at, '+00:00', '+07:00')) >= d.date) "
				+ "GROUP BY d.date, b.mypt, ptid, ggid, b.mygg, b.mydfdl), rate AS (SELECT a.ptid, a.mypt, a.mygg, a.dfdlid, a.mydfge, a.rate, a.date "
				+ "FROM (SELECT b.id AS ptid, mypt, mygg, mydfge, rate, d.date, e.id AS dfdlid, ROW_NUMBER() "
				+ "OVER(PARTITION BY mypt, mygg, e.id, mydfge, d.date "
				+ "ORDER BY effectivedate DESC) AS rownum FROM bp_sagr_sagrate a JOIN bp_pt_producttype b ON a.mypt = b.code "
				+ "JOIN twso c ON a.mywso = c.code JOIN date_range d ON c.wso = d.wso JOIN bp_dfdl_difficultygrade e "
				+ "ON a.mydfdl = e.code) a WHERE rownum = 1), "
				+ "ptids AS (SELECT :productId AS mptid UNION SELECT b.id AS mptid "
				+ "FROM (SELECT DISTINCT :productId AS ptid, c.id FROM bp_lcpm_lcpmerge a JOIN bp_lct_learningcomponenttype b "
				+ "ON a.mylctpk  = b.code JOIN bp_pt_producttype c ON b.mypt  = c.code WHERE a.isugem  = TRUE AND c.id = :productId) a "
				+ "JOIN (SELECT :productId AS ptid, c.id FROM bp_lcpm_lcpmerge a JOIN bp_lct_learningcomponenttype b "
				+ "ON a.mylctpk  = b.code JOIN bp_pt_producttype c ON b.mypt  = c.code WHERE a.isugem  = "
				+ "TRUE AND c.id <> :productId) b ON a.ptid = b.ptid) "
				+ "SELECT b.mydfge, CEILING(a.totalStudent*b.rate/12) AS totalTeacher, a.date_cal, a.totalStudent FROM "
				+ "(SELECT :productId AS myptid, a.mygg, a.mydfdl, DATE_FORMAT(a.podDate, '%Y-%m-%d') AS date_cal, "
				+ "SUM(a.totalStudent) AS totalStudent FROM pod a ";

		String where = buildWhereQuery(gradeId, classLevelId, subjectId);
		String groupBy = "GROUP BY a.mydfdl, a.podDate";
		String joinTail = ") a  JOIN rate b ON a.myptid = b.ptid AND a.mygg = b.mygg AND a.date_cal = DATE_FORMAT(b.date, '%Y-%m-%d') ";
		if (Objects.nonNull(classLevelId)) {
			joinTail += "AND b.dfdlid = " + classLevelId;
		}
		String fullQuery = query.concat(where).concat(groupBy).concat(joinTail);
		log.info("Query is {}", fullQuery);
		return toGteRequire(em.createNativeQuery(fullQuery).setParameter("startDate", startDate)
				.setParameter("endDate", endDate).setParameter("productId", productId).getResultList());
	}

	private List<GteRequiredProjection> toGteRequire(List<Object[]> inputs) {
		List<GteRequiredProjection> results = new ArrayList<>();
		for (Object[] arr : inputs) {
			GteRequiredProjection projection = GteRequiredProjection.builder()
					.fromDay(Objects.nonNull(arr[2]) ? (String) arr[2] : null)
					.mydfge(Objects.nonNull(arr[0]) ? (String) arr[0] : null)
					.totalTeacher(Objects.nonNull(arr[1]) ? ((BigDecimal) arr[1]).intValue() : null)
					.totalStudent(Objects.nonNull(arr[3]) ? ((BigDecimal) arr[3]).intValue() : null).build();
			results.add(projection);
		}
		return results;
	}

	private String buildWhereQuery(Integer gradeId, Integer classLevelId, Integer subjectId) {
		String where = "WHERE a.ptid IN (SELECT mptid FROM ptids) ";
		if (Objects.nonNull(gradeId)) {
			where += " AND a.ggid = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND a.dfdlid = " + classLevelId;
		}
		return where + " ";
	}

	private List<GteRequiredProjection> basicGteCalculate(Integer gradeId, Integer classLevelId, String startDate,
														  String endDate, Integer subjectId) {
		try {
			Integer cep100GradeId = gradeId != null
					? Integer.valueOf(gradeGroupService.findById(gradeId).getCep100GradeId())
					: null;
			return lmsService
					.getGteQuantity(15, 2, cep100GradeId, subjectId, classLevelId, LocalDate.parse(startDate),
							LocalDate.parse(endDate))
					.stream()
					.map(item -> Arrays.asList(
							GteRequiredProjection.builder().fromDay(item.getDate().toString()).mydfge("A")
									.totalTeacher(item.getRequiredQuantity().getMainA())
									.totalStudent(item.getRequiredQuantity().getTotalStudent()).build(),
							GteRequiredProjection.builder().fromDay(item.getDate().toString()).mydfge("B")
									.totalTeacher(item.getRequiredQuantity().getMainB())
									.totalStudent(item.getRequiredQuantity().getTotalStudent()).build(),
							GteRequiredProjection.builder().fromDay(item.getDate().toString()).mydfge("C")
									.totalTeacher(item.getRequiredQuantity().getMainC())
									.totalStudent(item.getRequiredQuantity().getTotalStudent()).build(),
							GteRequiredProjection.builder().fromDay(item.getDate().toString()).mydfge("D")
									.totalTeacher(item.getRequiredQuantity().getMainD())
									.totalStudent(item.getRequiredQuantity().getTotalStudent()).build()))
					.flatMap(Collection::stream).collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Error when get basicGteCalculate {}", DebuggingDTO.build(e));
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DteRequiredProjection> dteRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
														  String startDate, String endDate, Integer subjectId) {
		String select = "WITH RECURSIVE date_range AS  "
				+ "(SELECT :startDate AS date,  WEEKDAY(:startDate) + 2 AS wso UNION ALL "
				+ "SELECT ADDDATE(date_range.date, 1) AS date, WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso "
				+ "FROM date_range WHERE date_range.date < :endDate), "
				+ "twso AS (SELECT code, 2 AS wso FROM bp_wso_weeklyscheduleoption WHERE monday = TRUE UNION "
				+ "SELECT code, 3 AS wso FROM bp_wso_weeklyscheduleoption WHERE tuesday = TRUE UNION "
				+ "SELECT code, 4 AS wso FROM bp_wso_weeklyscheduleoption WHERE wednesday = TRUE UNION "
				+ "SELECT code, 5 AS wso FROM bp_wso_weeklyscheduleoption WHERE thursday = TRUE UNION "
				+ "SELECT code, 6 AS wso FROM bp_wso_weeklyscheduleoption WHERE friday = TRUE UNION "
				+ "SELECT code, 7 AS wso FROM bp_wso_weeklyscheduleoption WHERE saturday = TRUE UNION "
				+ "SELECT code, 8 AS wso FROM bp_wso_weeklyscheduleoption WHERE sunday = TRUE), "
				+ "ptids AS (SELECT :productId AS mptid UNION SELECT b.id AS mptid FROM "
				+ "(SELECT DISTINCT :productId AS ptid, c.id FROM bp_lcpm_lcpmerge a "
				+ "JOIN bp_lct_learningcomponenttype b ON a.mylctpk  = b.code JOIN bp_pt_producttype c ON b.mypt  = c.code "
				+ "WHERE a.isudlm  = TRUE AND c.id = :productId) a JOIN (SELECT :productId AS ptid, c.id FROM bp_lcpm_lcpmerge a "
				+ "JOIN bp_lct_learningcomponenttype b ON a.mylctpk  = b.code JOIN bp_pt_producttype c ON b.mypt  = c.code "
				+ "WHERE a.isudlm = TRUE AND c.id <> :productId) b ON a.ptid = b.ptid) "
				+ "SELECT c.date, COUNT(DISTINCT a.mygg, a.mydfdl) "
				+ "FROM bp_clag_classgroup a JOIN twso b ON a.mywso  = b.code "
				+ "JOIN date_range c ON b.wso = c.wso JOIN bp_pod_clag d ON a.code  = d.myclag "
				+ "JOIN bp_pt_producttype e ON a.mypt  = e.code JOIN bp_gg_gradegroup f ON a.mygg  = f.code "
				+ "JOIN bp_dfdl_difficultygrade g ON a.mydfdl = g.code ";

		String where = "WHERE a.clagtype  = 'PERM' AND a.active = TRUE AND (DATE(CONVERT_TZ(d.assigned_at, '+00:00', '+07:00')) <= c.date AND "
				+ "DATE(CONVERT_TZ(d.unassigned_at, '+00:00', '+07:00')) >= c.date) AND e.id IN (SELECT mptid FROM ptids) ";
		String groupBy = " GROUP BY c.date";

		if (Objects.nonNull(gradeId)) {
			where += " AND f.id = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND g.id = " + classLevelId;
		}
		String fullQuery = select.concat(where).concat(groupBy);
		log.info("Required DTE query {}", fullQuery);
		return toDteRequire(
				em.createNativeQuery(select.concat(where).concat(groupBy)).setParameter("startDate", startDate)
						.setParameter("endDate", endDate).setParameter("productId", productId).getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DteRequiredProjection> qoRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
														 String startDate, String endDate, Integer subjectId) {
		String select = "WITH RECURSIVE date_range AS  "
				+ "(SELECT :startDate AS date,  WEEKDAY(:startDate) + 2 AS wso UNION ALL "
				+ "SELECT ADDDATE(date_range.date, 1) AS date, WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso "
				+ "FROM date_range WHERE date_range.date < :endDate), "
				+ "twso AS (SELECT code, 2 AS wso FROM bp_wso_weeklyscheduleoption WHERE monday = TRUE UNION "
				+ "SELECT code, 3 AS wso FROM bp_wso_weeklyscheduleoption WHERE tuesday = TRUE UNION "
				+ "SELECT code, 4 AS wso FROM bp_wso_weeklyscheduleoption WHERE wednesday = TRUE UNION "
				+ "SELECT code, 5 AS wso FROM bp_wso_weeklyscheduleoption WHERE thursday = TRUE UNION "
				+ "SELECT code, 6 AS wso FROM bp_wso_weeklyscheduleoption WHERE friday = TRUE UNION "
				+ "SELECT code, 7 AS wso FROM bp_wso_weeklyscheduleoption WHERE saturday = TRUE UNION "
				+ "SELECT code, 8 AS wso FROM bp_wso_weeklyscheduleoption WHERE sunday = TRUE), "
				+ "ptids AS (SELECT :productId AS mptid UNION SELECT b.id AS mptid FROM "
				+ "(SELECT DISTINCT :productId AS ptid, c.id FROM bp_lcpm_lcpmerge a "
				+ "JOIN bp_lct_learningcomponenttype b ON a.mylctpk  = b.code JOIN bp_pt_producttype c ON b.mypt  = c.code "
				+ "WHERE a.isudlm  = TRUE AND c.id = :productId) a JOIN (SELECT :productId AS ptid, c.id FROM bp_lcpm_lcpmerge a "
				+ "JOIN bp_lct_learningcomponenttype b ON a.mylctpk  = b.code JOIN bp_pt_producttype c ON b.mypt  = c.code "
				+ "WHERE a.isudlm = TRUE AND c.id <> :productId) b ON a.ptid = b.ptid) "
				+ "SELECT c.date, COUNT(DISTINCT a.mygg, a.mydfdl) "
				+ "FROM bp_clag_classgroup a JOIN twso b ON a.mywso  = b.code "
				+ "JOIN date_range c ON b.wso = c.wso JOIN bp_pod_clag d ON a.code  = d.myclag "
				+ "JOIN bp_pt_producttype e ON a.mypt  = e.code JOIN bp_gg_gradegroup f ON a.mygg  = f.code "
				+ "JOIN bp_dfdl_difficultygrade g ON a.mydfdl = g.code ";

		String where = "WHERE a.clagtype  = 'PERM' AND a.active = TRUE AND (DATE(CONVERT_TZ(d.assigned_at, '+00:00', '+07:00')) <= c.date AND "
				+ "DATE(CONVERT_TZ(d.unassigned_at, '+00:00', '+07:00')) >= c.date) AND e.id IN (SELECT mptid FROM ptids) ";
		String groupBy = " GROUP BY c.date";

		if (Objects.nonNull(gradeId)) {
			where += " AND f.id = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND g.id = " + classLevelId;
		}
		String fullQuery = select.concat(where).concat(groupBy);
		log.info("Required QO query {}", fullQuery);
		return toDteRequire(
				em.createNativeQuery(select.concat(where).concat(groupBy)).setParameter("startDate", startDate)
						.setParameter("endDate", endDate).setParameter("productId", productId).getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DteRequiredProjection> lteRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
														  String startDate, String endDate, Integer subjectId) {
		String select = "WITH RECURSIVE date_range AS  "
				+ "(SELECT :startDate AS date,  WEEKDAY(:startDate) + 2 AS wso UNION ALL "
				+ "SELECT ADDDATE(date_range.date, 1) AS date, WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso "
				+ "FROM date_range WHERE date_range.date < :endDate), "
				+ "twso AS (SELECT code, 2 AS wso FROM bp_wso_weeklyscheduleoption WHERE monday = TRUE UNION "
				+ "SELECT code, 3 AS wso FROM bp_wso_weeklyscheduleoption WHERE tuesday = TRUE UNION "
				+ "SELECT code, 4 AS wso FROM bp_wso_weeklyscheduleoption WHERE wednesday = TRUE UNION "
				+ "SELECT code, 5 AS wso FROM bp_wso_weeklyscheduleoption WHERE thursday = TRUE UNION "
				+ "SELECT code, 6 AS wso FROM bp_wso_weeklyscheduleoption WHERE friday = TRUE UNION "
				+ "SELECT code, 7 AS wso FROM bp_wso_weeklyscheduleoption WHERE saturday = TRUE UNION "
				+ "SELECT code, 8 AS wso FROM bp_wso_weeklyscheduleoption WHERE sunday = TRUE) "
				+ "SELECT c.date, COUNT(DISTINCT a.mypt, a.mygg, a.mydfdl) "
				+ "FROM bp_clag_classgroup a JOIN twso b ON a.mywso  = b.code "
				+ "JOIN date_range c ON b.wso = c.wso JOIN bp_pod_clag d ON a.code  = d.myclag "
				+ "JOIN bp_pt_producttype e ON a.mypt  = e.code JOIN bp_gg_gradegroup f ON a.mygg  = f.code "
				+ "JOIN bp_dfdl_difficultygrade g ON a.mydfdl = g.code ";

		String where = "WHERE a.clagtype  = 'PERM' AND a.active = TRUE AND (DATE(CONVERT_TZ(d.assigned_at, '+00:00', '+07:00')) <= :endDate AND "
				+ "DATE(CONVERT_TZ(d.unassigned_at, '+00:00', '+07:00')) >= :startDate) ";
		String groupBy = " GROUP BY c.date";

		if (Objects.nonNull(productId)) {
			where += " AND e.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			where += " AND f.id = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND g.id = " + classLevelId;
		}
		String fullQuery = select.concat(where).concat(groupBy);
		log.info("Required LTE query {}", fullQuery);
		return toDteRequire(em.createNativeQuery(select.concat(where).concat(groupBy))
				.setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList());
	}

	private List<DteRequiredProjection> toDteRequire(List<Object[]> inputs) {
		List<DteRequiredProjection> results = new ArrayList<>();
		for (Object[] arr : inputs) {
			DteRequiredProjection projection = DteRequiredProjection.builder()
					.fromDay(Objects.nonNull(arr[0]) ? (String) arr[0] : null)
					.totalTeacher(Objects.nonNull(arr[1]) ? ((BigInteger) arr[1]).intValue() : null).build();
			results.add(projection);
		}
		return results;
	}

}
