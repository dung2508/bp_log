package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.CalendarPeriod;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "bp_ulc_uniquelearningcomponent")
@Getter
@Setter
public class BpUniqueLearningComponent extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 3956922799557814187L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code")
	@EqualsAndHashCode.Include
	private String code;

	private String name;

	@Column(name = "myjointulc")
	private String myJoinUlc;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mylct", referencedColumnName = "code", nullable = false)
	private BpLearningComponentType myLct;

	@Column(name = "mylct", insertable = false, updatable = false)
	private String myLctCode;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mylcp", referencedColumnName = "code", nullable = false)
	private BpLCP myLcp;

	@Column(name = "mygg")
	private String myGg;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mycap", referencedColumnName = "code", nullable = false)
	private CalendarPeriod myCap;

	@Column(name = "mydfdl")
	private String myDfdl;

	@Column(name = "mydfge")
	private String myDfge;

	private String description;

	@Column(name = "myparentulc")
	private String myParent;

	private Boolean published;

	private String xdsc;

	@Column(name = "mybps")
	private String mybps;

	@Column(name = "publishbps")
	private String publishbps;

	@Column(name = "unpublishbps")
	private String unpublishbps;

	@Column(name = "mypt")
	private String myPt;

	@Column(name = "mydfqc")
	private String myDfqc;

	@Column(name = "ulc_no")
	private Integer ulcNo;

}
