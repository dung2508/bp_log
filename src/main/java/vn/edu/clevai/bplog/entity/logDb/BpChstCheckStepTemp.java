package vn.edu.clevai.bplog.entity.logDb;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "bp_chst_checksteptemp")
@SuperBuilder
@Getter
@Setter
public class BpChstCheckStepTemp extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5871742672188441020L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "mychpt")
	private String myChpt;

	@Column(name = "code", unique = true)
	private String code;

	@ManyToOne
	@JoinColumn(name = "mychlt", referencedColumnName = "code", nullable = false, insertable = false, updatable = false)
	private BpCheckListTemp myChlt;

	@Column(name = "checksample")
	private Integer checkSample;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mychrt", referencedColumnName = "code")
	private BpCheckerType myChrt;

	private String name;
}
