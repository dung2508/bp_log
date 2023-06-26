package vn.edu.clevai.bplog.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ssl_sheet_config")
public class SSLSheetConfig {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	private String name;

	private String dfdl;

	@Column(name = "lct_shift_type")
	private String lctShiftType;

	@Column(name = "session_type")
	private String sessionType;

	@Column(name = "session_no")
	private String sessionNo;

	@Column(name = "prod_type")
	private String prodType;

	private String dfge;

	@Column(name = "grade_code")
	private String ggCode;

	@Column(name = "bl3_q_group_name")
	private String bl3QGroupName;
}
