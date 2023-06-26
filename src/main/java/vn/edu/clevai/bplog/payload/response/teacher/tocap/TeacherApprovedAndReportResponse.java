package vn.edu.clevai.bplog.payload.response.teacher.tocap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherApprovedAndReportResponse {

	@Data
	@Builder
	public static class RequestApprovedAndScheduledInfo {
		@JsonProperty("product_id")
		private Long productId;

		@JsonProperty("grade_id")
		private Long gradeId;

		@JsonProperty("subject_id")
		private Long subjectId;

		@JsonProperty("teaching_date")
		@JsonFormat(pattern = "yyyy-MM-dd")
		private Date teachingDate;

		@JsonProperty("id")
		private Long teachingScheduleAssigneeId;

		@JsonProperty("class_level_id")
		private Long classLevelId;

		@JsonProperty("teaching_position_type")
		private Long teachingPositionType;

		@JsonProperty("status")
		private String status;

		private String category;
	}

	@JsonProperty("teacher_id")
	private Long teacherId;

	@JsonProperty("teacher_username")
	private String teacherUsername;

	@JsonProperty("teacher_fullname")
	private String teacherFullname;

	@JsonProperty("average_rating")
	private BigDecimal averageRating;

	@JsonProperty("canceled_rate")
	private BigDecimal canceledRate;

	@JsonProperty("total_math_basic_registered_schedules")
	private Long totalScheduleRegisterBasic;

	@JsonProperty("total_math_plus_registered_schedules")
	private Long totalScheduleRegisterPlus;

	@JsonProperty("total_arranged_is_main")
	private Long totalArrangedIsMain;

	@JsonProperty("total_arranged_is_backup")
	private Long totalArrangedIsBackup;

	@JsonProperty("teacher_rank")
	private Long teacherRank;

	@JsonProperty("teaching_schedules_in_period")
	private List<RequestApprovedAndScheduledInfo> teachingSchedulesInPeriod;

}