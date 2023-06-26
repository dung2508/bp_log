package vn.edu.clevai.bplog.payload.response.teacher;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YourselfResponse {
	@JsonProperty("teaching_schedule_slot_ids")
	private List<Long> teacherScheduleShifts;

	@JsonProperty("version_id")
	private Long versionId;

	@JsonProperty("start_at")
	private Integer startAt;

	@JsonProperty("end_at")
	private Integer endAt;
	
	@JsonProperty("waiting_approval_requests")
	private Boolean waitingApprovalRequests;
}
