package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_chpi_checkprocessitem")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpChpiCheckProcessItem extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1213111952980923589L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mychpt", referencedColumnName = "code")
	private BpChptCheckProcessTemp myChpt;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	private String name;

	@Column(name = "mylcet")
	private String myLcet;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mycuievent", referencedColumnName = "code")
	private BpCuiEvent myCuiEvent;

	@Column(name = "mytrigger")
	private String myTrigger;

	@Column(name = "mychecker")
	private String myChecker;

	@Column(name = "mycti1")
	private String myCti1;

	@Column(name = "mycti2")
	private String myCti2;

	@Column(name = "mycti3")
	private String myCti3;

	@Column(name = "description")
	private String description;

	@OneToMany(mappedBy = "myChpi", cascade = CascadeType.PERSIST)
	private Set<BpChsiCheckStepItem> listChsi;
}
