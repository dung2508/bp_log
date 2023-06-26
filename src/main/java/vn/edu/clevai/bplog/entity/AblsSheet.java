package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "ablp_sheet")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AblsSheet extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "column_1")
	private String column1;

	@Column(name = "column_2")
	private String column2;

	@Column(name = "column_3")
	private String column3;

	@Column(name = "curr_shift_g3")
	private String currShiftG3;

	@Column(name = "abli_link_g3")
	private String abliLinkG3;

	@Column(name = "curr_shift_g4")
	private String currShiftG4;

	@Column(name = "abli_link_g4")
	private String abliLinkG4;

	@Column(name = "curr_shift_g5")
	private String currShiftG5;

	@Column(name = "abli_link_g5")
	private String abliLinkG5;

	@Column(name = "curr_shift_g6")
	private String currShiftG6;

	@Column(name = "abli_link_g6")
	private String abliLinkG6;

	@Column(name = "curr_shift_g7")
	private String currShiftG7;

	@Column(name = "abli_link_g7")
	private String abliLinkG7;

	@Column(name = "curr_shift_g8")
	private String currShiftG8;

	@Column(name = "abli_link_g8")
	private String abliLinkG8;

	@Column(name = "curr_shift_g9")
	private String currShiftG9;

	@Column(name = "abli_link_g9")
	private String abliLinkG9;
}
