package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "bp_dtc_duty_classes")
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UsiDutyClassCategory extends BaseModel implements Serializable {
	private static final long serialVersionUID = -706885895895708611L;
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "myusid")
	private String myUsiD;

	@Column(name = "myclc")
	private String myClassCategory;

	@Column(name = "is_enabled")
	private Boolean isEnable;

}
