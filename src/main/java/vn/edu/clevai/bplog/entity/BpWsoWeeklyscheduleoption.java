package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_wso_weeklyscheduleoption")
@SuperBuilder
@Getter
@Setter
public class BpWsoWeeklyscheduleoption extends BaseModel {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code")
	private String code;

	private Boolean monday;

	private Boolean tuesday;

	private Boolean wednesday;

	private Boolean thursday;

	private Boolean friday;

	private Boolean saturday;

	private Boolean sunday;
}
