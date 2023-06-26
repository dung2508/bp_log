package vn.edu.clevai.bplog.payload.request.teacher;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisToCapPublishRequest {
	@JsonProperty("product_id")
	private Integer productId;

	@JsonProperty("class_level_id")
	private Integer classLevelId;

	@JsonProperty("grade_id")
	private Integer gradeId;

	@JsonProperty("user_account_type_id")
	@NotNull
	private Integer userAccountTypeId;

	@JsonProperty("start_date")
	private String startDate;

	@JsonProperty("end_date")
	private String endDate;

	@JsonProperty("subject_id")
	private Integer subjectId;

	private List<Assign> assigns;

	@Data
	@Builder
	public static class Assign {
		
		@JsonProperty("id")
		private Long id;
		
		@JsonProperty("teacher_id")
		private Integer teacherId;

		@JsonProperty("teaching_position_type")
		private Integer teachingPositionType;

		@JsonProperty("teaching_date")
		private String teachingDate;

		@JsonProperty("category")
		private String category;

		@JsonProperty("teaching_cancel_reason_id")
		private Integer teachingCancelReasonId;

		@JsonProperty("teaching_cancel_reason")
		private String teacherCancelReason;

		@JsonProperty("teaching_cancel_reason_display_period_id")
		private Integer teachingCancelReasonDisplayPeriodId;
	}
}
