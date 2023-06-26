package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_cuie_cuievent")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpCuiEvent extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 7126701370411421545L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	private String name;

	@Column(name = "myusi")
	private String myUsi;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mycui", referencedColumnName = "code", nullable = false, updatable = false)
	private BpCuiContentUserUlc myCui;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mylcet_lceventtype", referencedColumnName = "code", nullable = false, updatable = false)
	@EqualsAndHashCode.Include
	private BpLearningComponentEventType myLcet;

	@Column(name = "eventplantime")
	private Timestamp eventPlanTime;

	@Column(name = "eventactualtime_fet")
	private Timestamp eventActualTimeFet;

	@Column(name = "eventactualtime_bet")
	private Timestamp eventActualTimeBet;

	@Column(name = "trigger_at")
	private Timestamp triggerAt;

	private Boolean published;

	@Column(name = "mycui", insertable = false, updatable = false)
	private String mycui;

	@Column(name = "value1")
	private String value1;

	@Column(name = "planbpe")
	private String planbpe;

	@Column(name = "actualbpe")
	private String actualbpe;

	@Column(name = "publishbpe")
	private String publishbpe;

	@Column(name = "unpublishbpe")
	private String unpublishbpe;

}
