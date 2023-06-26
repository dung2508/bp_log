package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_chsi_checkstepitem")
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpChsiCheckStepItem extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -2489635428155817290L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mychpi", referencedColumnName = "code")
	private BpChpiCheckProcessItem myChpi;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	@Column(name = "checksample")
	private Integer checkSample;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mychri", referencedColumnName = "code")
	private BpCheckerItem myChri;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mychst", referencedColumnName = "code")
	private BpChstCheckStepTemp myChst;

	private String description;
}
