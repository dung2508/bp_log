package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_dfdl_difficultygrade")
@SuperBuilder
@Getter
@Setter
public class BpDfdlDifficultygrade extends BaseModel {

	@Id
	@EqualsAndHashCode.Include
	private Integer id;

	@EqualsAndHashCode.Include
	private String code;
	private String name;
	private String description;
	private Boolean published;
}
