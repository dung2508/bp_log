package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "bp_cup_currperiod")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumPeriod extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	private String code;

	private String name;

	private String description;

	@Column(name = "mycrps")
	private String myCrps;

	@Column(name = "mycuptype")
	private String currPeriodType;

	@Column(name = "mycupno")
	private String mycupno;

	@Column(name = "mycap")
	private String myCap;

	@Column(name = "mydfdl")
	private String myDfdl;

	@Column(name = "mydfge")
	private String myDfge;

	@Column(name = "mylct")
	private String myLcType;

	@Column(name = "myparentcup")
	private String myParentCup;

	@Column(name = "mycti")
	private String myCti;

	private Boolean published;

	private Integer mynoaschild;
}
