package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bp_ulcm_ulcmerge")
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ULCMerge extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;

	private String mainulc;

	private Boolean isudlm;

	private Boolean isugem;

	private String description;

	private Boolean published;

}