package vn.edu.clevai.bplog.entity.logDb;

import java.io.Serializable;

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
import vn.edu.clevai.bplog.entity.BaseModel;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_chrt_checkertype")
@Getter
@Setter
public class BpCheckerType extends BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1892204699884562520L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String code;

	private String name;

}
