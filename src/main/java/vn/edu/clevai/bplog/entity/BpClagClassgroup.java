package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@Table(name = "bp_clag_classgroup")
public class BpClagClassgroup extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String code;

	private String name;

	private String mypt;

	private String mygg;

	private String mydfdl;

	private String mywso;

	private String mydfge;

	private String clagtype;

	private Boolean active;

	private Integer maxtotalstudents;

	private String description;

	private String xclass;

	private String xsessiongroup;

	private String xcash;
}
