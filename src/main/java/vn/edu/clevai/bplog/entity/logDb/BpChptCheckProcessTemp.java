package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;
import vn.edu.clevai.bplog.entity.BpLCP;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bp_chpt_checkprocesstemp")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class BpChptCheckProcessTemp extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5521230251690356481L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@EqualsAndHashCode.Include
	@Column(name = "code", unique = true)
	private String code;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mylcp", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
	private BpLCP myLcp;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "mylct", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
//	private BpLearningComponentType myLct;

//	@Column(name = "mychpttype")
//	private String myChptType;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "mylceg", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
//	private BpLcegLearningComponentEventGroup myLceg;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mylcet", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
	private BpLearningComponentEventType myLcet;

//	@Column(name = "mylctfilter")
//	private String myLctFilter;

	@Column(name = "triggerusertype")
	private String triggerUserType;

	@Column(name = "supust")
	private String checkerUserType;

	@Column(name = "timebase")
	private String timeBase;

	@Column(name = "timeoffsetunit")
	private String timeOffSetUnit;

	@Column(name = "timeoffsetvalue")
	private Integer timeOffSetValue;

	private String note;

}
