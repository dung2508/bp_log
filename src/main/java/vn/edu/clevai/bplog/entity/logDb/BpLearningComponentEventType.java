package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_lcet_learningcomponenteventtype")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpLearningComponentEventType extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 8930146143136160248L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	private String name;

	@Column(name = "mylceg")
	private String myLceg;

	private String description;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "myparent", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
//	private BpLearningComponentEventType myParent;

//	@OneToMany(mappedBy = "myParent", cascade = CascadeType.ALL)
//	private Set<BpLearningComponentEventType> subLcet;
}
