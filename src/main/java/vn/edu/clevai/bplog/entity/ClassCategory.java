package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bp_clc_classcategory")
public class ClassCategory extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	private String code;

	private String name;

	private String description;
	@Column(name = "myaccyear")
	private String myAccYear;
	@Column(name = "myterm")
	private String myTerm;
	@Column(name = "mypt")
	private String myPt;
	@Column(name = "mygg")
	private String myGg;
	@Column(name = "mywso")
	private String myWso;
	@Column(name = "mydfdl")
	private String myDfdl;
	@Column(name = "mydfge")
	private String myDfge;
	private Boolean published;

	@Column(name = "mycashsta")
	private String myCashSta;

	@Column(name = "clctype")
	private String clcType;

}
