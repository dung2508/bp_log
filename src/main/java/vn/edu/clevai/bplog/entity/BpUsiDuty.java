package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_usid_usiduty")
@SuperBuilder
@Getter
@Setter
public class BpUsiDuty extends BaseModel implements Serializable {
	private static final long serialVersionUID = -706885895895708611L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "mybpp")
	private String mybpp;

	@Column(name = "mybps")
	private String mybps;

	@Column(name = "mybpe")
	private String mybpe;

	@Column(name = "code")
	private String code;

	@Column(name = "myusi")
	private String myUsi;

	@Column(name = "isapproved")
	private Boolean isApproved;

	@Column(name = "mypreviouscode")
	private String mypreviouscode;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
	// Master data
	@Column(name = "mylcet")
	private String myLcet;

	@Column(name = "myust")
	private String myUst;

	@Column(name = "mycap")
	private String myCap;

	@Column(name = "mychrt")
	private String myChrt;

	@Column(name = "mylcp")
	private String myLcp;

	@Column(name = "myaccyear")
	private String myaccyear;

	@Column(name = "myterm")
	private String myterm;

	@Column(name = "mypt")
	private String mypt;

	@Column(name = "mygg")
	private String mygg;

	@Column(name = "mywso")
	private String mywso;

	@Column(name = "mydfdl")
	private String mydfdl;

	@Column(name = "mydfge")
	private String mydfge;

	@Column(name = "mycashstr")
	private String mycashstr;

	@Column(name = "mycashsta")
	private String mycashsta;

	@Column(name = "mycassstr")
	private String mycassstr;

	// STEP 3
	@Column(name = "submited_at")
	private Timestamp submitedAt;

	@Column(name = "submited_cancel_at")
	private Timestamp submitedCancelAt;

	@Column(name = "start_time")
	private Timestamp startTime;

	@Column(name = "end_time")
	private Timestamp endTime;

	@Column(name = "effective_date")
	private Date effectiveDate;

	private String timeSessionId;

	@Column(name = "approved_at")
	private Timestamp approvedAt;

	@Column(name = "approved_cancel_at")
	private Timestamp approvedCancelAt;

	@Column(name = "published_at")
	private Timestamp publishedAt;

	@Column(name = "approved_myusi")
	private String approvedMyusi;

	@Column(name = "cancel_myusi")
	private String cancelMyusi;

	@Column(name = "allocated_at")
	private Timestamp allocatedAt;

	@Column(name = "unallocated_at")
	private Timestamp unallocatedAt;

	@Column(name = "allocated_myusi")
	private String allocatedMyusi;

	@Column(name = "unallocated_myusi")
	private String unallocatedMyusi;

	@Column(name = "position")
	private String position;

	@Column(name = "reason")
	private String teacherCancelReason;

	@Column(name = "published")
	private Boolean published;
	
	@Column(name = "publishbpe")
	private String publishbpe;
	
	@Column(name = "unpublishbpe")
	private String unpublishbpe;
	
	@Column(name = "publishbps")
	private String publishbps;
	
	@Column(name = "unpublishbps")
	private String unpublishbps;
}
