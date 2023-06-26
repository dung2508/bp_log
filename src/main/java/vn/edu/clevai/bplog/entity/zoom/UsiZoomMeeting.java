package vn.edu.clevai.bplog.entity.zoom;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "usi_zoom_meeting")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsiZoomMeeting extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	private String code;
	@Column(name = "zoom_meeting_code")
	private String zoomMeetingCode;
	@Column(name = "ulc_code")
	private String ulcCode;
	private String usi;
	private String position;
	private Boolean published;
}
