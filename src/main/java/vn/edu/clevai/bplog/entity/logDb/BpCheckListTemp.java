package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "bp_chlt_checklisttemp")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpCheckListTemp extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -3815145381343391446L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	private String name;

	@Column(name = "mychst")
	@EqualsAndHashCode.Include
	private String myChst;

	@Column(name = "subcode")
	private String subCode;

	@Column(name = "donot")
	private String doNot;

	@Column(name = "correctexample")
	private String correctExample;

	@Column(name = "incorrectexample")
	private String incorrectExample;

	@Column(name = "scoretype")
	private String score1Type;

	@Column(name = "scoretype2")
	private String score2Type;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "myparentchlt", referencedColumnName = "code", nullable = false)
	private BpCheckListTemp myParentChlt;

	@OneToMany(mappedBy = "myParentChlt", cascade = CascadeType.ALL)
	private Set<BpCheckListTemp> subChlts;

}
