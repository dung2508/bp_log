package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_sagr_sagrate")
@SuperBuilder
@Getter
@Setter
public class BpSagrRate extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "mypt")
	private String mypt;

	@Column(name = "mygg")
	private String mygg;

	@Column(name = "mydfdl")
	private String mydfdl;

	@Column(name = "mydfge")
	private String mydfge;

	@Column(name = "rate")
	private Double rate;

	@Column(name = "published")
	private Boolean published;
}
