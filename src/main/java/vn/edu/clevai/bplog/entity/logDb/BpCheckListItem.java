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
@Table(name = "bp_chli_checklistitem")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpCheckListItem extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2193433796492555849L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	@Column(name = "subcode")
	private String subCode;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mychsi", referencedColumnName = "code")
	private BpChsiCheckStepItem myChsi;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "myparentchlt", referencedColumnName = "code")
	private BpCheckListTemp myParentChlt;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mysubchlt", referencedColumnName = "code")
	private BpCheckListTemp mysubchlt;

	@Column(name = "do")
	private String chltDo;

	@Column(name = "donot")
	private String doNot;

	@Column(name = "correctexample")
	private String correctExample;

	@Column(name = "incorrectexample")
	private String incorrectExample;

	@Column(name = "scoretype1")
	private String score1Type;

	@Column(name = "score1")
	private String score1;

	@Column(name = "scoretype2")
	private String score2Type;

	private String score2;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "myparentchli", referencedColumnName = "code")
	private BpCheckListItem myParentChli;

	@OneToMany(mappedBy = "myParentChli", cascade = CascadeType.ALL)
	@OrderBy(value = "id")
	private Set<BpCheckListItem> subChli;
}
