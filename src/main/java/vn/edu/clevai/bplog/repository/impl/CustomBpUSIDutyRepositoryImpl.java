package vn.edu.clevai.bplog.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.edu.clevai.bplog.common.enumtype.BppRegisterEnum;
import vn.edu.clevai.bplog.common.enumtype.PositionAssignEnum;
import vn.edu.clevai.bplog.repository.CustomBpUSIDutyRepository;
import vn.edu.clevai.bplog.repository.projection.DteAssignedProjection;
import vn.edu.clevai.bplog.repository.projection.GetAssignedProjection;
import vn.edu.clevai.bplog.repository.projection.RegistedProjection;
import vn.edu.clevai.bplog.utils.Utils;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class CustomBpUSIDutyRepositoryImpl implements CustomBpUSIDutyRepository {

	@Autowired
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistedProjection> dteRegistedQuantity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId) {
		String myTerm = getMyTerm(startDate);
		String dateRange = "WITH RECURSIVE date_range AS (SELECT '" + startDate + "' AS date, "
				+ "WEEKDAY('" + startDate + "') + 2 AS wso UNION ALL SELECT ADDDATE(date_range.date, 1) AS date,"
				+ "WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso FROM date_range WHERE date_range.date < '" + endDate + "'), ";
		String registed = "    registed AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"         WHERE a.approved_at IS NOT NULL " +
				"           AND a.published = TRUE " +
				"           AND a.mybpp LIKE '%" + BppRegisterEnum.DTE3_CONFIRM.getLikeStatement() +"%' " +
				"           AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND a.myterm = '" + myTerm + "' ";
		String assignedMain = "    assigned AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"         WHERE mybpp LIKE CONCAT('%" + BppRegisterEnum.DTE4_ALLOCATE.getLikeStatement() +"%') " +
				" 			AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"           AND unallocated_at IS NULL " +
				"           AND a.myterm = '" + myTerm + "' " +
				"           AND position = '" + PositionAssignEnum.MAIN.getName() + "' " +
				"           AND a.published ";
		
		String assignedBackup = "    assigned_backup AS (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"                        FROM bp_usid_usiduty a " +
				"                                 JOIN date_range b ON a.mywso = b.wso " +
				"                                 JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                                 JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"                                 JOIN bp_dfdl_difficultygrade e ON a.mydfdl = e.code " +
				"                                 JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"                        WHERE a.approved_at IS NOT NULL " +
				"                          AND a.published = TRUE " +
				"                          AND a.mybpp LIKE CONCAT('%" + BppRegisterEnum.DTE4_ALLOCATE.getLikeStatement() + "%') " +
				"                          AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"                          AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"                          AND a.myterm = '" + myTerm + "'" +
				"                          AND a.position = '" + PositionAssignEnum.BACKUP.getName() + "' ";
		String select = "SELECT a.date, (a.quantity - IFNULL(b.quantity, 0) - IFNULL(c.quantity, 0))" +
				"FROM registed a LEFT JOIN assigned b ON a.date = b.date" +
				"         LEFT JOIN assigned_backup c ON a.date = c.date;";

		String groupByDate = "         GROUP BY b.date) ";
		if (Objects.nonNull(productId)) {
			registed += " AND c.id = " + productId;
			assignedMain += " AND c.id = " + productId;
			assignedBackup += " AND c.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			registed += " AND d.id = " + gradeId;
			assignedBackup += " AND d.id = " + gradeId;
		}

		assignedBackup += " AND e.id = " + classLevelId;

		registed += groupByDate + ", ";
		assignedMain += groupByDate + ", ";
		assignedBackup += groupByDate;
		String fullQuery = dateRange.concat(registed).concat(assignedMain).concat(assignedBackup).concat(select);
		log.debug("Execute Query: {}", fullQuery);
		return toRegisted(em.createNativeQuery(fullQuery).getResultList());
	}

	private String getMyTerm(String startDate) {
		String pattern = "yyyy-MM-dd";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		Date date = Date.from(LocalDate.parse(startDate, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant());
		String myTerm = Utils.getMyTermFromTime(date);
		return myTerm;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistedProjection> lteRegistedQuantity(Integer productId, Integer gradeId, Integer classLevelId,
														String startDate, String endDate, Integer subjectId) {
		String myTerm = getMyTerm(startDate);
		String dateRange = "WITH RECURSIVE date_range AS (SELECT '" + startDate + "' AS date, "
				+ "WEEKDAY('" + startDate + "') + 2 AS wso UNION ALL SELECT ADDDATE(date_range.date, 1) AS date,"
				+ "WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso FROM date_range WHERE date_range.date < '" + endDate + "'), ";
		String registed = "    registed AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"         WHERE a.approved_at IS NOT NULL " +
				"           AND a.published = TRUE " +
				"           AND a.mybpp LIKE '%" + BppRegisterEnum.LTE3_CONFIRM.getLikeStatement() +"%' " +
				"           AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND a.myterm = '" + myTerm + "' ";
		String assignedMain = "    assigned AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"         WHERE mybpp LIKE CONCAT('%" + BppRegisterEnum.LTE4_ALLOCATE.getLikeStatement() +"%') " +
				" 			AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"           AND unallocated_at IS NULL " +
				"           AND a.myterm = '" + myTerm + "' " +
				"           AND position = '" + PositionAssignEnum.MAIN.getName() + "' " +
				"           AND a.published ";

		String assignedBackup = "    assigned_backup AS (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"                        FROM bp_usid_usiduty a " +
				"                                 JOIN date_range b ON a.mywso = b.wso " +
				"                                 JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                                 JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"                                 JOIN bp_dfdl_difficultygrade e ON a.mydfdl = e.code " +
				"                                 JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"                        WHERE a.approved_at IS NOT NULL " +
				"                          AND a.published = TRUE " +
				"                          AND a.mybpp LIKE CONCAT('%" + BppRegisterEnum.LTE4_ALLOCATE.getLikeStatement() + "%') " +
				"                          AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"                          AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"                          AND a.myterm = '" + myTerm + "'" +
				"                          AND a.position = '" + PositionAssignEnum.BACKUP.getName() + "' ";
		String select = "SELECT a.date, (a.quantity - IFNULL(b.quantity, 0) - IFNULL(c.quantity, 0))" +
				"FROM registed a LEFT JOIN assigned b ON a.date = b.date" +
				"         LEFT JOIN assigned_backup c ON a.date = c.date;";

		String groupByDate = "         GROUP BY b.date) ";
		if (Objects.nonNull(productId)) {
			registed += " AND c.id = " + productId;
			assignedMain += " AND c.id = " + productId;
			assignedBackup += " AND c.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			registed += " AND d.id = " + gradeId;
			assignedBackup += " AND d.id = " + gradeId;
		}

		assignedBackup += " AND e.id = " + classLevelId;

		registed += groupByDate + ", ";
		assignedMain += groupByDate + ", ";
		assignedBackup += groupByDate;
		String fullQuery = dateRange.concat(registed).concat(assignedMain).concat(assignedBackup).concat(select);
		log.debug("Execute Query: {}", fullQuery);
		return toRegisted(em.createNativeQuery(fullQuery).getResultList());
	}

	private List<RegistedProjection> toRegisted(List<Object[]> inputs) {
		List<RegistedProjection> results = new ArrayList<>();
		for (Object[] arr : inputs) {
			RegistedProjection projection = RegistedProjection.builder()
					.dateInWeek(Objects.nonNull(arr[0]) ? (String) arr[0] : null)
					.totalTeacher(Objects.nonNull(arr[1]) ? ((BigInteger) arr[1]).intValue() : null).build();
			results.add(projection);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RegistedProjection> gteRegistedQuantity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId) {
		String myTerm = getMyTerm(startDate);
		String dateRange = "WITH RECURSIVE date_range AS (SELECT '" + startDate + "' AS date, "
				+ "WEEKDAY('" + startDate + "') + 2 AS wso UNION ALL SELECT ADDDATE(date_range.date, 1) AS date,"
				+ "WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso FROM date_range WHERE date_range.date < '" + endDate + "'), ";
		String registed = "    registed AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"         WHERE a.approved_at IS NOT NULL " +
				"           AND a.published = TRUE " +
				"           AND a.mybpp LIKE '%" + BppRegisterEnum.GTE3_CONFIRM.getLikeStatement() +"%' " +
				"           AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND a.myterm = '" + myTerm + "' ";
		String assignedMain = "    assigned AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"         WHERE mybpp LIKE CONCAT('%" + BppRegisterEnum.GTE4_ALLOCATE.getLikeStatement() +"%') " +
				" 			AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"           AND unallocated_at IS NULL " +
				"           AND a.myterm = '" + myTerm + "' " +
				"           AND position = '" + PositionAssignEnum.MAIN.getName() + "' " +
				"           AND a.published ";

		String assignedBackup = "    assigned_backup AS (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"                        FROM bp_usid_usiduty a " +
				"                                 JOIN date_range b ON a.mywso = b.wso " +
				"                                 JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                                 JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"                                 JOIN bp_dfdl_difficultygrade e ON a.mydfdl = e.code " +
				"                                 JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"                        WHERE a.approved_at IS NOT NULL " +
				"                          AND a.published = TRUE " +
				"                          AND a.mybpp LIKE CONCAT('%" + BppRegisterEnum.GTE4_ALLOCATE.getLikeStatement() + "%') " +
				"                          AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"                          AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"                          AND a.myterm = '" + myTerm + "'" +
				"                          AND a.position = '" + PositionAssignEnum.BACKUP.getName() + "' ";
		String select = "SELECT a.date, (a.quantity - IFNULL(b.quantity, 0) - IFNULL(c.quantity, 0))" +
				"FROM registed a LEFT JOIN assigned b ON a.date = b.date" +
				"         LEFT JOIN assigned_backup c ON a.date = c.date;";

		String groupByDate = "         GROUP BY b.date) ";
		if (Objects.nonNull(productId)) {
			registed += " AND c.id = " + productId;
			assignedMain += " AND c.id = " + productId;
			assignedBackup += " AND c.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			registed += " AND d.id = " + gradeId;
			assignedBackup += " AND d.id = " + gradeId;
		}

		assignedBackup += " AND e.id = " + classLevelId;

		registed += groupByDate + ", ";
		assignedMain += groupByDate + ", ";
		assignedBackup += groupByDate;
		String fullQuery = dateRange.concat(registed).concat(assignedMain).concat(assignedBackup).concat(select);
		log.debug("Execute Query: {}", fullQuery);
		return toRegisted(em.createNativeQuery(fullQuery).getResultList());
	}

	@Override
	public List<RegistedProjection> qoRegistedQuantity(Integer productId, Integer gradeId, Integer classLevelId, String startDate, String endDate, Integer subjectId) {
		String myTerm = getMyTerm(startDate);
		String dateRange = "WITH RECURSIVE date_range AS (SELECT '" + startDate + "' AS date, "
				+ "WEEKDAY('" + startDate + "') + 2 AS wso UNION ALL SELECT ADDDATE(date_range.date, 1) AS date,"
				+ "WEEKDAY(ADDDATE(date_range.date, 1)) + 2 AS wso FROM date_range WHERE date_range.date < '" + endDate + "'), ";
		String registed = "    registed AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"         WHERE a.approved_at IS NOT NULL " +
				"           AND a.published = TRUE " +
				"           AND a.mybpp LIKE '%" + BppRegisterEnum.QO3_CONFIRM.getLikeStatement() +"%' " +
				"           AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND a.myterm = '" + myTerm + "' ";
		String assignedMain = "    assigned AS " +
				"        (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"         FROM bp_usid_usiduty a " +
				"                  JOIN date_range b ON a.mywso = b.wso " +
				"                  JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                  JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"         WHERE mybpp LIKE CONCAT('%" + BppRegisterEnum.QO4_ALLOCATE.getLikeStatement() +"%') " +
				" 			AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"           AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"           AND unallocated_at IS NULL " +
				"           AND a.myterm = '" + myTerm + "' " +
				"           AND position = '" + PositionAssignEnum.MAIN.getName() + "' " +
				"           AND a.published ";

		String assignedBackup = "    assigned_backup AS (SELECT b.date, COUNT(DISTINCT myusi) AS quantity " +
				"                        FROM bp_usid_usiduty a " +
				"                                 JOIN date_range b ON a.mywso = b.wso " +
				"                                 JOIN bp_pt_producttype c ON a.mypt = c.code " +
				"                                 JOIN bp_gg_gradegroup d ON a.mygg = d.code " +
				"                                 JOIN bp_dfdl_difficultygrade e ON a.mydfdl = e.code " +
				"                                 JOIN bp_cap_calendarperiod f ON a.mycap = f.code " +
				"                        WHERE a.approved_at IS NOT NULL " +
				"                          AND a.published = TRUE " +
				"                          AND a.mybpp LIKE CONCAT('%" + BppRegisterEnum.QO4_ALLOCATE.getLikeStatement() + "%') " +
				"                          AND (DATE(a.start_time) <= b.date AND (a.end_time IS NULL OR DATE(a.end_time) >= b.date)) " +
				"                          AND DATE(f.startperiod + INTERVAL 7 HOUR) = b.date " +
				"                          AND a.myterm = '" + myTerm + "'" +
				"                          AND a.position = '" + PositionAssignEnum.BACKUP.getName() + "' ";
		String select = "SELECT a.date, (a.quantity - IFNULL(b.quantity, 0) - IFNULL(c.quantity, 0))" +
				"FROM registed a LEFT JOIN assigned b ON a.date = b.date" +
				"         LEFT JOIN assigned_backup c ON a.date = c.date;";

		String groupByDate = "         GROUP BY b.date) ";
		if (Objects.nonNull(productId)) {
			registed += " AND c.id = " + productId;
			assignedMain += " AND c.id = " + productId;
			assignedBackup += " AND c.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			registed += " AND d.id = " + gradeId;
			assignedBackup += " AND d.id = " + gradeId;
		}

		assignedBackup += " AND e.id = " + classLevelId;

		registed += groupByDate + ", ";
		assignedMain += groupByDate + ", ";
		assignedBackup += groupByDate;
		String fullQuery = dateRange.concat(registed).concat(assignedMain).concat(assignedBackup).concat(select);
		log.debug("Execute Query: {}", fullQuery);
		return toRegisted(em.createNativeQuery(fullQuery).getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GetAssignedProjection> gteAssignedQuatity(Integer productId, Integer gradeId, Integer classLevelId, String startDate,
			String endDate, Integer subjectId) {
		String select = "SELECT DATE_FORMAT(b.startperiod, '%Y-%m-%d') AS fromDay, a.mydfdl, a.position, COUNT(DISTINCT myusi) AS totalTeacher "
				+ "FROM bp_usid_usiduty a JOIN bp_cap_calendarperiod b ON a.mycap = b.code "
				+ "JOIN bp_dfdl_difficultygrade c ON a.mydfdl  = c.code JOIN bp_pt_producttype d ON a.mypt = d.code "
				+ "JOIN bp_gg_gradegroup e ON a.mygg = e.code JOIN bp_wso_weeklyscheduleoption f ON a.mywso = f.code ";
		String where = "WHERE a.mybpp LIKE '%" + BppRegisterEnum.GTE4_ALLOCATE.getLikeStatement() + "%' "
				+ "AND (a.is_deleted IS NULL OR a.is_deleted = FALSE) AND a.unallocated_at IS NULL AND a.published "
				+ "AND (CASE DAYOFWEEK(b.startperiod) WHEN 2 THEN f.monday "
				+ "WHEN 3 THEN f.tuesday WHEN 4 THEN f.wednesday WHEN 5 THEN f.thursday "
				+ "WHEN 6 THEN f.friday WHEN 7 THEN f.saturday WHEN 8 THEN f.sunday ELSE FALSE END) = TRUE ";
		if (Objects.nonNull(productId)) {
			where += " AND d.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			where += " AND e.id = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND c.id = " + classLevelId;
		}
		if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
			if (StringUtils.isBlank(startDate)) {
				where += " AND DATE(b.startperiod) <= '" + endDate + "'";
			} else if (StringUtils.isBlank(endDate)) {
				where += " AND DATE(b.startperiod) >= '" + startDate + "'";
			} else {
				where += " AND DATE(b.startperiod) BETWEEN '" + startDate + "' AND '" + endDate + "' ";
			}
		}
		String groupBy = "GROUP BY DATE_FORMAT(b.startperiod, '%Y-%m-%d'), a.position";
		String fullQuery = select.concat(where).concat(groupBy);
		log.debug("Execute Assigned Query: {}", fullQuery);
		return toGteAssigned(em.createNativeQuery(fullQuery).getResultList());
	}

	private List<GetAssignedProjection> toGteAssigned(List<Object[]> inputs) {
		List<GetAssignedProjection> results = new ArrayList<>();
		for (Object[] arr : inputs) {
			GetAssignedProjection projection = GetAssignedProjection.builder()
					.fromDay(Objects.nonNull(arr[0]) ? (String) arr[0] : null)
					.mydfge(Objects.nonNull(arr[1]) ? (String) arr[1] : null)
					.position(Objects.nonNull(arr[2]) ? (String) arr[2] : null)
					.totalTeacher(Objects.nonNull(arr[3]) ? ((BigInteger) arr[3]).intValue() : null).build();
			results.add(projection);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DteAssignedProjection> dteAssignedQuantity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId) {
		String select = "SELECT DATE_FORMAT(b.startperiod, '%Y-%m-%d') AS fromDay, a.position, COUNT(DISTINCT myusi) AS totalTeacher "
				+ "FROM bp_usid_usiduty a JOIN bp_cap_calendarperiod b ON a.mycap = b.code "
				+ "JOIN bp_dfdl_difficultygrade c ON a.mydfdl  = c.code JOIN bp_pt_producttype d ON a.mypt = d.code "
				+ "JOIN bp_gg_gradegroup e ON a.mygg = e.code JOIN bp_wso_weeklyscheduleoption f ON a.mywso = f.code ";
		String where = "WHERE a.mybpp LIKE '%" + BppRegisterEnum.DTE4_ALLOCATE.getLikeStatement() + "%' "
				+ "AND (a.is_deleted IS NULL OR a.is_deleted = FALSE) AND a.unallocated_at IS NULL AND a.published  "
				+ "AND (CASE DAYOFWEEK(b.startperiod) WHEN 2 THEN f.monday "
				+ "WHEN 3 THEN f.tuesday WHEN 4 THEN f.wednesday WHEN 5 THEN f.thursday "
				+ "WHEN 6 THEN f.friday WHEN 7 THEN f.saturday WHEN 8 THEN f.sunday ELSE FALSE END) = TRUE ";
		if (Objects.nonNull(productId)) {
			where += " AND d.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			where += " AND e.id = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND c.id = " + classLevelId;
		}
		if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
			if (StringUtils.isBlank(startDate)) {
				where += " AND DATE(b.startperiod) <= '" + endDate + "'";
			} else if (StringUtils.isBlank(endDate)) {
				where += " AND DATE(b.startperiod) >= '" + startDate + "'";
			} else {
				where += " AND DATE(b.startperiod) BETWEEN '" + startDate + "' AND '" + endDate + "' ";
			}
		}
		String groupBy = " GROUP BY DATE_FORMAT(b.startperiod, '%Y-%m-%d'), a.position";
		String fullQuery = select.concat(where).concat(groupBy);
		log.info("Execute Assigned Query: {}", fullQuery);
		return toDteAssigned(em.createNativeQuery(fullQuery).getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DteAssignedProjection> qoAssignedQuantity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId) {
		String select = "SELECT DATE_FORMAT(b.startperiod, '%Y-%m-%d') AS fromDay, a.position, COUNT(DISTINCT myusi) AS totalTeacher "
				+ "FROM bp_usid_usiduty a JOIN bp_cap_calendarperiod b ON a.mycap = b.code "
				+ "JOIN bp_dfdl_difficultygrade c ON a.mydfdl  = c.code JOIN bp_pt_producttype d ON a.mypt = d.code "
				+ "JOIN bp_gg_gradegroup e ON a.mygg = e.code JOIN bp_wso_weeklyscheduleoption f ON a.mywso = f.code ";
		String where = "WHERE a.mybpp LIKE '%" + BppRegisterEnum.QO4_ALLOCATE.getLikeStatement() + "%' "
				+ "AND (a.is_deleted IS NULL OR a.is_deleted = FALSE) AND a.unallocated_at IS NULL AND a.published "
				+ "AND (CASE DAYOFWEEK(b.startperiod) WHEN 2 THEN f.monday "
				+ "WHEN 3 THEN f.tuesday WHEN 4 THEN f.wednesday WHEN 5 THEN f.thursday "
				+ "WHEN 6 THEN f.friday WHEN 7 THEN f.saturday WHEN 8 THEN f.sunday ELSE FALSE END) = TRUE ";
		if (Objects.nonNull(productId)) {
			where += " AND d.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			where += " AND e.id = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND c.id = " + classLevelId;
		}
		if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
			if (StringUtils.isBlank(startDate)) {
				where += " AND DATE(b.startperiod) <= '" + endDate + "'";
			} else if (StringUtils.isBlank(endDate)) {
				where += " AND DATE(b.startperiod) >= '" + startDate + "'";
			} else {
				where += " AND DATE(b.startperiod) BETWEEN '" + startDate + "' AND '" + endDate + "' ";
			}
		}
		String groupBy = " GROUP BY DATE_FORMAT(b.startperiod, '%Y-%m-%d'), a.position";
		String fullQuery = select.concat(where).concat(groupBy);
		log.info("Execute Assigned Query: {}", fullQuery);
		return toDteAssigned(em.createNativeQuery(fullQuery).getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DteAssignedProjection> lteAssignedQuantity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId) {
		String select = "SELECT DATE_FORMAT(b.startperiod, '%Y-%m-%d') AS fromDay, a.position, COUNT(DISTINCT myusi) AS totalTeacher "
				+ "FROM bp_usid_usiduty a JOIN bp_cap_calendarperiod b ON a.mycap = b.code "
				+ "JOIN bp_dfdl_difficultygrade c ON a.mydfdl  = c.code JOIN bp_pt_producttype d ON a.mypt = d.code "
				+ "JOIN bp_gg_gradegroup e ON a.mygg = e.code JOIN bp_wso_weeklyscheduleoption f ON a.mywso = f.code ";
		String where = "WHERE a.mybpp LIKE '%" + BppRegisterEnum.LTE4_ALLOCATE.getLikeStatement() + "%' "
				+ "AND (a.is_deleted IS NULL OR a.is_deleted = FALSE) AND a.unallocated_at IS NULL AND a.published "
				+ "AND (CASE DAYOFWEEK(b.startperiod) WHEN 2 THEN f.monday "
				+ "WHEN 3 THEN f.tuesday WHEN 4 THEN f.wednesday WHEN 5 THEN f.thursday "
				+ "WHEN 6 THEN f.friday WHEN 7 THEN f.saturday WHEN 8 THEN f.sunday ELSE FALSE END) = TRUE ";
		if (Objects.nonNull(productId)) {
			where += " AND d.id = " + productId;
		}
		if (Objects.nonNull(gradeId)) {
			where += " AND e.id = " + gradeId;
		}
		if (Objects.nonNull(classLevelId)) {
			where += " AND c.id = " + classLevelId;
		}
		if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
			if (StringUtils.isBlank(startDate)) {
				where += " AND DATE(b.startperiod) <= '" + endDate + "'";
			} else if (StringUtils.isBlank(endDate)) {
				where += " AND DATE(b.startperiod) >= '" + startDate + "'";
			} else {
				where += " AND DATE(b.startperiod) BETWEEN '" + startDate + "' AND '" + endDate + "' ";
			}
		}
		String groupBy = " GROUP BY DATE_FORMAT(b.startperiod, '%Y-%m-%d'), a.position";
		String fullQuery = select.concat(where).concat(groupBy);
		log.info("Execute Assigned Query: {}", fullQuery);
		return toDteAssigned(em.createNativeQuery(fullQuery).getResultList());
	}

	private List<DteAssignedProjection> toDteAssigned(List<Object[]> inputs) {
		List<DteAssignedProjection> results = new ArrayList<>();
		for (Object[] arr : inputs) {
			DteAssignedProjection projection = DteAssignedProjection.builder()
					.fromDay(Objects.nonNull(arr[0]) ? (String) arr[0] : null)
					.position(Objects.nonNull(arr[1]) ? (String) arr[1] : null)
					.totalTeacher(Objects.nonNull(arr[2]) ? ((BigInteger) arr[2]).intValue() : null).build();
			results.add(projection);
		}
		return results;
	}

}
