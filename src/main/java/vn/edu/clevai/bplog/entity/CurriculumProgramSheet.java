package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "bp_crps_curriculumprogramsheet")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumProgramSheet extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "mycrpp")
	private String myCrpp;

	@Column(name = "mygg")
	private String myGG;

	private String name;

	private String code;

	@Column(name = "filelocationurl")
	private String fileLocationUrl;

	private Boolean published;
}
