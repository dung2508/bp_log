package vn.edu.clevai.bplog.payload.response.cti;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectWithRewardResponse;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaningObjectResponse {
	@JsonProperty("index")
	private Long index;

	@JsonProperty("dsc_id")
	@EqualsAndHashCode.Include
	private Long dscId;

	@JsonProperty("class_name")
	private String className;

	@JsonProperty("class_code")
	private String classCode;

	@JsonProperty("thumbnail_url")
	private String thumbnailUrl;

	@JsonProperty("video_url")
	private String videoUrl;

	@JsonProperty("video_backup_url")
	private String videoBackupUrl;

	@JsonProperty("video_second_backup_url")
	private String videoSecondBackupUrl;

	@JsonProperty("video_replay_url")
	private String videoReplayUrl;

	@JsonProperty("learning_object_type")
	private String learningObjectType;

	@JsonProperty("grade_id")
	private Long gradeId;

	@JsonProperty("live_at")
	private Timestamp liveAt;

	@JsonProperty("category")
	private String category;

	@JsonProperty("learning_objects")
	private List<LearningObjectWithRewardResponse> learningObjectWithRewardResponseList;

	@JsonProperty("total_complete_learning_objects")
	private Long totalCompleteLearningObjects;

	@JsonProperty("total_learning_objects")
	private Long totalLearningObjects;

	@JsonProperty("status")
	private String status;

	@JsonProperty("training_type_id")
	private Long trainingTypeId;
}
