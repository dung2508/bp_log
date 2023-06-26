package vn.edu.clevai.bplog.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_clcr_clcregistration")
@SuperBuilder
@Getter
@Setter
public class BpClcrRegistration extends BaseModel {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@EqualsAndHashCode.Include
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "time_session_id")
	private String timeSessionId;

	@Column(name = "myusi")
	private String myusi;

	@Column(name = "mypt")
	private String mypt;

	@Column(name = "mygg")
	private String mygg;

	@Column(name = "mywso")
	private String mywso;

	@Column(name = "mycasssta")
	private String mycasssta;

	@Column(name = "submited_type")
	private String submitedType;

	@Column(name = "submited_at")
	private Timestamp submitedAt;
	
	@Column(name = "approved_at")
	private Timestamp approvedAt;
	
	@Column(name = "submited_cancel_at")
	private Timestamp submitedCancelAt;
	
	@Column(name = "approved_cancel_at")
	private Timestamp approvedCancelAt;

	@Column(name = "start_time")
	private Timestamp startTime;

	@Column(name = "end_time")
	private Timestamp endTime;

	@Column(name = "published_at")
	private Timestamp publishedAt;

	@Column(name = "effective_date")
	private Date effectiveDate;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
	
	@Column(name = "last_action_myusi")
	private String lastActionMyusi;

}
