package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "bp_cap_calendarperiod")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarPeriod extends BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7721665320891487976L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	private String code;

	private String description;

	@Column(name = "myparentcap")
	private String myParent;

	@Column(name = "mygrandparentcap")
	private String myGrandParent;

	@Column(name = "myprd")
	private String myPrd;

	@Column(name = "captype")
	private String capType;

	@Column(name = "startperiod")
	private Timestamp startTime;

	@Column(name = "endperiod")
	private Timestamp endTime;

	@Column(name = "mynoaschild")
	private String numberAsChild;

	@Column(name = "mynoasgrandchild")
	private String numberAsGrandChild;

	@Column(name = "mycashsta")
	private String cashStart;

	@Column(name = "mylct")
	private String myLct;

	private String mystructure;

	private Boolean published;
}
