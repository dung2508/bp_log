package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@SuperBuilder
@Table(name = "bp_st_gg")
public class BpStGg extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String code;

	private String mygg;

	private String myst;
}
