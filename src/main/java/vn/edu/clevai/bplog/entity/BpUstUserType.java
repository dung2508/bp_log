package vn.edu.clevai.bplog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_ust_usertype")
@SuperBuilder
@Getter
@Setter
public class BpUstUserType extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "myusl")
	private String myusl;

	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "myparentust")
	private String myparentust;
	
	@Column(name = "description")
	private String description;
}
