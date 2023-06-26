package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "abli_sheet")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbliSheet extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "abli_code")
	private String abliCode;

	@Column(name = "bl3_code")
	private String bl3Code;

	@Column(name = "q_type")
	private String qType;

	@Column(name = "c1_bl4_code")
	private String c1Bl4Code;

	@Column(name = "c1_bl5_code")
	private String c1Bl5Code;

	@Column(name = "c2_bl4_code")
	private String c2Bl4Code;

	@Column(name = "c2_bl5_code")
	private String c2Bl5Code;
}
