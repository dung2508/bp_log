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
@Table(name = "unit_test_log")
@SuperBuilder
public class UnitTestLog extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer type;

	private String unitFunction;

	@Column(name = "my_time")
	private Timestamp myTime;

	@Column(name = "collect_time")
	private Timestamp collectTime;

	@Column(name = "time")
	private Timestamp time;

	@Column(name = "ay_code")
	private String ayCode;

	@Column(name = "trm_code")
	private String trmCode;

	@Column(name = "pt_code")
	private String ptCode;

	@Column(name = "gg_code")
	private String ggCode;

	@Column(name = "cap_code")
	private String capCode;

	@Column(name = "cap_type")
	private String capType;

	@Column(name = "crpp_code")
	private String crppCode;

	@Column(name = "crps_code")
	private String crpsCode;

	@Column(name = "cup_code")
	private String cupCode;

	@Column(name = "cup_type")
	private String cupType;

	@Column(name = "input_cup_code")
	private String inputCupCode;

	@Column(name = "cup_no")
	private String cupNo;

	@Column(name = "return_value")
	private String returnValue;

	@Column(name = "input_value")
	private String inputValue;

	private String code;

	private String funcname;

	private String processId;

	private Timestamp mytimestamp;

	private String podCode;

	@Column(name = "dfdl_code")
	private String dfdlCode;

	@Column(name = "lct_code")
	private String lctCode;

	@Column(name = "dfge_code")
	private String dfgeCode;

	@Column(name = "wso_code")
	private String wsoCode;

	private String outputCode;

	private String clagCode;

	private String clagTypeCode;

	@Column(name = "chpt_code")
	private String chptCode;

	@Column(name = "cti1_code")
	private String cti1Code;

	@Column(name = "cti2_code")
	private String cti2Code;

	@Column(name = "cti3_code")
	private String cti3Code;

	@Column(name = "cui_event_code")
	private String cuiEventCode;

	@Column(name = "input_cap_code")
	private String inputCapCode;

	@Column(name = "trigger_user_type")
	private String triggerUserType;

	@Column(name = "checker_user_type")
	private String checkerUserType;

	@Column(name = "chpt_type")
	private String chptType;

	@Column(name = "lcet_code")
	private String lcetCode;

	private String xDfdl;

	private String xSt;

	private String xdeal;

	@Column(name = "st_code")
	private String stCode;

	private String xwso;

	private String xGg;

	private String xDfge;

	private String xclass;

	private String xdsc;

	private String xsessiongroup;

	private String xcash;

	private String clagdynCode;

	private String ulcCode;

	private String chstCode;

	private String chpiCode;

	private String chsiCode;

	private String chltCode;

	private String chliCode;

	private String chriCode;

	private String lastname;

	private String firstname;

	private String myust;

	private String username;

	private String clagtype;

	private String mygg;

	private String mywso;

	private String mydfdl;

	private String mypt;

	@Column(name = "assigned_at")
	private String assignedAt;

	@Column(name = "unassigned_at")
	private String unassignedAt;

	private String membertype;

	private String page;

	private String size;

	private String fromDate;

	private String toDate;

	private String prdCode;

	private Long xptId;

	private String maxtotalstudents;

	private String lcpCode;

	private String usiCode;

	private String lcetList;

	private String chrtCode;

	private String excludeUsi;

	private String cuiCode;

	private String eventPlanTime;

	private String cassCode;

}

