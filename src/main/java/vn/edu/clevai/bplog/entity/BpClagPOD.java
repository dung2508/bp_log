package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_clag_pod")
@SuperBuilder
@Getter
@Setter
public class BpClagPOD extends BaseModel {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "mypod")
	private String mypod;

	@Column(name = "myclag")
	private String myclag;

	@Column(name = "membertype")
	private String memberType;

	@Column(name = "assigned_at")
	private Timestamp assignedAt;

	@Column(name = "unassigned_at")
	private Timestamp unAssignedAt;

	@Column(name = "active")
	private Boolean active;
}
