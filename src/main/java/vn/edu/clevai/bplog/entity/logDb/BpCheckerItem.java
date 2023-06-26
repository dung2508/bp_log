package vn.edu.clevai.bplog.entity.logDb;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_chri_checkeritem")
@Getter
@Setter
@SuperBuilder
public class BpCheckerItem extends BaseModel implements Serializable {

	private static final long serialVersionUID = -16791438777635L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mychrt", referencedColumnName = "code")
	private BpCheckerType myChrt;

	@Column(name = "myusi")
	private String myUsi;

	@Column(name = "mycap")
	private String myCap;

	private String description;
}
