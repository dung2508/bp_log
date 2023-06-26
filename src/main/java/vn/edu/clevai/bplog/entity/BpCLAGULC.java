package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_clag_ulc")
@SuperBuilder
@Data
public class BpCLAGULC extends BaseModel {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "myulc")
	private String myulc;

	@Column(name = "myclag")
	private String myclag;

	@Column(name = "mybps")
	private String mybps;

	@Column(name = "publishbps")
	private String publishbps;

	@Column(name = "unpublishbps")
	private String unpublishbps;

}
