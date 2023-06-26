package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_gg_gradegroup")
@SuperBuilder
@Getter
@Setter
public class BpGGGradeGroup extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "summername")
	private String summerName;

	@Column(name = "cep100_grade_id")
	private String cep100GradeId;

	@Column(name = "description")
	private String description;

	@Column(name = "mycashsta")
	private String cashStart;

	private Boolean published;

}
