package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bp_lcpm_lcpmerge")
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LcpMerge extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String mylcp;

	private String mylctpk;

	private Boolean isudlm;

	private Boolean isugem;

	private Boolean ismain;

	private String description;

	private Boolean published;

}