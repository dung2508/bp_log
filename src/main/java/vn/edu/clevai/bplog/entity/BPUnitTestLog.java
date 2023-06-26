package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "bp_unit_test_log")
@SuperBuilder
public class BPUnitTestLog extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "row_type")
	private String rowType;

	@Column(name = "func_name")
	private String funcName;

	@Column(name = "process_id")
	private String processId;

	@Column(name = "process_code")
	private String processCode;

	@Column(name = "mytimestamp")
	private Timestamp myTimestamp;

	@Column(name = "param_group_no")
	private String paramGroupNo;

	@Column(name = "col1")
	private String col1;

	@Column(name = "col2")
	private String col2;

	@Column(name = "col3")
	private String col3;

	@Column(name = "col4")
	private String col4;

	@Column(name = "col5")
	private String col5;

	@Column(name = "col6")
	private String col6;

	@Column(name = "col7")
	private String col7;

	@Column(name = "col8")
	private String col8;

	@Column(name = "col9")
	private String col9;

	@Column(name = "col10")
	private String col10;
}
