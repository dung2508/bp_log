package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bp_crpp_curriculumprogrampackage")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumProgramPackage extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "myaccyear")
	private String myAccYear;

	@Column(name = "myterm")
	private String myTerm;

	@Column(name = "mypt")
	private String myPt;

	private String name;

	private String code;

	private String description;

	@Column(name = "startdate")
	private Timestamp startDate;

	@Column(name = "enddate")
	private Timestamp endDate;

	@Column(name = "filelocationurl")
	private String fileLocationUrl;

	private Boolean published;
}
