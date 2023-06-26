package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_lct_learningcomponenttype")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpLearningComponentType extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -6813293002493395500L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	private String name;

	@Column(name = "mylcl")
	private String myLcl;

	@Column(name = "mylck")
	private String myLck;

	private String description;

	private Boolean published;

	@Column(name = "need_schedule")
	private Boolean needSchedule;

	@Column(name = "mypt")
	private String myPt;
}
