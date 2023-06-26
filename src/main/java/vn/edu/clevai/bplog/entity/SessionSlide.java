package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session_slide")
public class SessionSlide extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "curriculum_shift_id")
	private Long curriculumShiftId;

	@Column(name = "curriculum_session_id")
	private Long curriculumSessionId;

	@Column(name = "bl3_q_group_name")
	private String bl3QGroupName;

	@Column(name = "ssl_link")
	private String sslLink;

}
