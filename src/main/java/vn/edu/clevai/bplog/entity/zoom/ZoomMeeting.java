package vn.edu.clevai.bplog.entity.zoom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "zoom_meeting")
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoomMeeting extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@Column(name = "id")
	private Long id;
	@Column(name = "uuid")
	private String uuid;
	private String code;
	@Column(name = "host_id")
	private String hostId;
	@Column(name = "host_email")
	private String hostEmail;
	private String topic;
	private Long type;
	private String assistantId;
	private String status;
	@Column(name = "start_time")
	private Timestamp startTime;
	private Long duration;
	private String timezone;
	@Column(name = "zoom_created_at")
	private Timestamp zoomCreatedAt;
	@Column(name = "start_url")
	private String startUrl;
	@Column(name = "join_url")
	private String joinUrl;
	private String password;
	@Column(name = "h323_password")
	private String h323Password;
	@Column(name = "pstn_password")
	private String pstnPassword;
	@Column(name = "encrypted_password")
	private String encryptedPassword;
	private String agenda;
	@Column(name = "pre_schedule")
	private Boolean preSchedule;
	@Column(name = "host_video")
	private Boolean hostVideo;
	@Column(name = "participant_video")
	private Boolean participantVideo;
	@Column(name = "join_before_host")
	private Boolean joinBeforeHost;
	@Column(name = "jbh_time")
	private Long jbhTime;
	@Column(name = "auto_recording")
	private String autoRecording;
	@Column(name = "alternative_hosts")
	private String alternativeHosts;
	@Column(name = "show_share_button")
	private Boolean showShareButton;
	@Column(name = "request_permission_to_unmute_participants")
	private Boolean requestPermissionToUnmuteParticipants;
	private String audio;
	private Boolean published;
}
