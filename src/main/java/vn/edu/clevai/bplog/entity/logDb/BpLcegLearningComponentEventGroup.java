package vn.edu.clevai.bplog.entity.logDb;

import java.io.Serializable;

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
import vn.edu.clevai.bplog.entity.BaseModel;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_cuie_cuievent")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpLcegLearningComponentEventGroup extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3029300434690913092L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	@Column(name = "name")
	private String name;
}
