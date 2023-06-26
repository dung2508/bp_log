package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Table(name = "curriculum")
public class Curriculum extends Auditable<String> {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "subject_id")
	private Long subjectId;

	@Column(name = "grade_id")
	private Long gradeId;

	@Column(name = "class_level_id")
	private Long classLevelId;

	@Column(name = "training_type_id")
	private Long trainingTypeId;

	@Column(name = "lesson_code")
	private String lessonCode;

	@Column(name = "lesson_name")
	private String lessonName;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "ordering")
	private Integer ordering;

}
