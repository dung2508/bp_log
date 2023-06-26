package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentType;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "bp_lcp_lcperiod")
public class BpLCP extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -1357658043135943439L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String code;

	private String lcperiodno;

	private String mylct;

	private String mylctparent;

	private String myprd;

	private String scheduling;

	private String description;

	private String mypt;

	private String mystructure;

	private String cgbr;

	@Column(name = "nolcp")
	private Integer nolcp;

	private Boolean published;

	@ManyToOne
	@JoinColumn(name = "mylct", referencedColumnName = "code", insertable = false, updatable = false)
	private BpLearningComponentType lct;

}
