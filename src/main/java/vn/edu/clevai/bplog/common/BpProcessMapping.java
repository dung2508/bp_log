package vn.edu.clevai.bplog.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import vn.edu.clevai.bplog.common.BpProcessCommon.VariableConfig;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;

import javax.annotation.PostConstruct;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
@Component
public class BpProcessMapping {
	private static final String BPP_SCHEDULE_USS_CODE = "10152";
	private static final String COLLECT_CLAG_LIST_DLC_CODE = "1012";
	private static final String COLLECT_CLAG_LIST_DLG_CODE = "11121";
	private static final String COLLECT_UDLC_PARAMETERS_PROCESS_CODE = "10141";
	private static final String COLLECT_UDL_PARAMETERS_1_PROCESS_CODE = "10142";
	private static final String COLLECT_UDL_PARAMETERS_2_PROCESS_CODE = "10151";
	private static final String COLLECT_UDL_PARAMETERS_PROCESS_CODE = "1021";
	private static final String COLUMN_PREFIX = "col";
	private static final String CREATE_UDLC_PROCESS_CODE = "1011";
	private static final String CREATE_UDLG_PROCESS_CODE = "1111";
	private static final String CREATE_UGES_PROCESS_CODE = "1211";
	private static final String FIND_CUIEVENT_START_PROCESS_CODE = "5112";
	private static final String FIND_XDFDL_CODE = "332";
	private static final String FIND_XST_CODE = "291";
	private static final String FIND_XWSO_CODE = "342";
	private static final String GET_DFDL_FROM_X_CODE = "333";
	private static final String GET_GG_FROM_X_CODE = "314";
	private static final String GET_ST_FROM_X_CODE = "292";
	private static final String GET_WSO_FROM_X_CODE = "343";
	private static final String SET_POD_DFDL_CODE = "334";
	private static final String SET_POD_WSO_CODE = "344";
	private static final String SET_ST_CODE = "293";
	private static final String SET_ST_GG_CODE = "315";
	private static final String SIX_GROUPCODE = "6";
	private static final String SUGGEST_CLAG_UGE_PROCESS_CODE = "1152";
	private static final String WRITE_CUIEVENT_JOIN_REQUEST_ACTUAL_TIME_PROCESS_CODE = "5113";
	public static final String BPP_ASSIGN_CLAGPERM_CODE = "2117";
	public static final String BPP_ASSIGN_DFDL_CODE = "2115";
	public static final String BPP_ASSIGN_GG_CODE = "2114";
	public static final String BPP_ASSIGN_STUDENT_CODE = "2111";
	public static final String BPP_ASSIGN_WSO_CODE = "2116";
	public static final String BPP_PURCHASE_CODE = "211";
	public static final String FIND_XCLASS_CODE = "21151";
	public static final String FIND_XGG_CODE = "302";
	public static final String FIND_XSESSIONGROUP_CODE = "304";
	public static final String GET_CLAGDYN_FROM_X_CODE = "305";
	public static final String GET_CLAGPERM_FROM_X_CODE = "21152";
	public static final String GET_POD_CLAGPERM_CODE = "2412";
	public static final String GET_POD_FROM_X_CODE = "2112";
	public static final String GET_ST_GG_CODE = "21141";
	public static final String GET_XCASH_CODE = "303";
	public static final String SET_CLAGDYN_CODE = "306";
	public static final String SET_CLAGPERM_CODE = "21153";
	public static final String SET_POD_CLAGDYN_CODE = "308";
	public static final String SET_POD_CLAGPERM_CODE = "2513";
	public static final String SET_POD_CODE = "2113";
	public static final String DTE_TO_CLAG_CODE = "";
	public static final String GTE_TO_CLAG_CODE = "";
	public static final String CTE_TO_CLAG_CODE = "";

	private List<BpProcessCommon> listProcess;

	@PostConstruct
	public void doInitMapProcess() {
		if (Objects.isNull(listProcess))
			listProcess = new ArrayList<>();


		listProcess
				.add(buildBpProcess(BPLogProcessEnum.GET_GG_FROM_X, GET_GG_FROM_X_CODE, "20", "xGrade_id", "code"));

		listProcess
				.add(buildBpProcess(BPLogProcessEnum.FIND_XGG, FIND_XGG_CODE, "20", "xst", "xClass_level_id", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_XCASH, GET_XCASH_CODE, "20", "xGrade_id", "xcady", "xcash",
				"code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_XSESSIONGROUP, FIND_XSESSIONGROUP_CODE, "20",
				"xSession_group_id", "xXdealid", "xcash", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_CLAGDYN_FROM_X, GET_CLAGDYN_FROM_X_CODE, "20", "xcash",
				"xSession_group_id", "xcash", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_CLAGDYN, SET_CLAGDYN_CODE, "3, 20",
				"clagClagcode", "clagMypt", "clagMygg", "clagMydfdl", "clagMydfge", "membertype", "clagMaxtotalstudent", "xSession_group_id", "xcash", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_POD_CLAGDYN, SET_POD_CLAGDYN_CODE, "3, 12, 20",
				"podPod_code", "clagClagcode", "membertype", "assignedAt", "unassignedAt", "podclagCode", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_CUIEVENT_JOIN_REQUEST, FIND_CUIEVENT_START_PROCESS_CODE,
				SIX_GROUPCODE, "cuieCuiecode", "cuieMylcet", "cuiePlantime", "cuieActualtime", "cuieCuicode",
				"cuieMyusi", "cuieMycti", "cuieMylcp", "cuieMylct", "cuiePublished"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.WRITE_CUIEVENT_JOIN_REQUEST_ACTUAL_TIME, SIX_GROUPCODE,
				WRITE_CUIEVENT_JOIN_REQUEST_ACTUAL_TIME_PROCESS_CODE, "cuieCuiecode", "cuieMylcet", "cuiePlantime",
				"cuieActualtime", "cuieCuicode", "cuieMyusi", "cuieMycti", "cuieMylcp", "cuieMylct", "cuiePublished"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.BPP_PURCHASE, BPP_PURCHASE_CODE, "20", "xXdealid"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.BPP_ASSIGN_STUDENT, BPP_ASSIGN_STUDENT_CODE, "20", "xXdealid"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_POD_FROM_X, GET_POD_FROM_X_CODE, "20", "xXdealid", "code", "podPod_code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_POD, SET_POD_CODE, "8, 20",
				"podPod_code", "podMyst", "podMypt", "myprd", "fromdate", "todate", "xXdealid", "code"));

		listProcess
				.add(buildBpProcess(BPLogProcessEnum.BPP_ASSIGN_GG, BPP_ASSIGN_GG_CODE, "7, 20",
						"stCode", "xXdealid", "code"));

		listProcess.add(
				buildBpProcess(BPLogProcessEnum.BPP_ASSIGN_DFDL, BPP_ASSIGN_DFDL_CODE, "8, 20", "podMydfdl", "xXdealid", "code"));

		listProcess
				.add(buildBpProcess(BPLogProcessEnum.BPP_ASSIGN_WSO, BPP_ASSIGN_WSO_CODE, "8, 20", "podMywso", "xXdealid", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.BPP_ASSIGN_CLAGPERM, BPP_ASSIGN_CLAGPERM_CODE, "8, 20",
				"xXdealid", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_POD_CLAGPERM, GET_POD_CLAGPERM_CODE, "3, 8",
				"clagClagcode", "assignedAt", "unassignedAt", "podPod_code", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_XST, FIND_XST_CODE, "20", "xXdealid", "xst", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_ST_FROM_X, GET_ST_FROM_X_CODE, "7,20", "stCode", "xst", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_ST, SET_ST_CODE, "20", "code", "firstname", "lastname",
				"username"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_XWSO, FIND_XWSO_CODE, "20",
				"xXdealid", "xwso", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_WSO_FROM_X, GET_WSO_FROM_X_CODE, "8",
				"podMywso", "xwso", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_POD_WSO, SET_POD_WSO_CODE, "8, 14",
				"podPod_code", "podMywso", "podwsoCode", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_XDFDL, FIND_XDFDL_CODE, "20",
				"xStudent_id", "xXdealid", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_DFDL_FROM_X, GET_DFDL_FROM_X_CODE, "8, 20",
				"podMydfdl", "xStudent_id", "xdfdl", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_POD_DFDL, SET_POD_DFDL_CODE, "8, 13",
				"podPod_code", "podMydfdl", "poddfdlCode", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_ST_GG, GET_ST_GG_CODE, "20", "ST", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_ST_GG, SET_ST_GG_CODE, "7, 8, 20",
				"stCode", "stMygg", "stggCode", "class_level_id", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_XCLASS, FIND_XCLASS_CODE, "20", "xXdealid", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_CLAGPERM_FROM_X, GET_CLAGPERM_FROM_X_CODE, "20",
				"xClass_id", "code"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_CLAGPERM, SET_CLAGPERM_CODE, "3, 115, 20", "clagMypt",
				"clagMywso", "clagMydfdl", "gdgMygg", "xClass_id", "code", "maxtotalstudent", "clagtype"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SET_POD_CLAGPERM, SET_POD_CLAGPERM_CODE, "3, 8, 12",
				"clagClagcode", "membertype", "assignedAt", "unassignedAt", "podPod_code", "podMyclagperm", "stPodclag",
				"code"));

		listProcess.add(
				buildBpProcess(BPLogProcessEnum.SET_MY_JOINT_UDL_1, "1023", "1", "ulcMycap", "ulcLcpcode", "ulcMylct"));
		listProcess.add(
				buildBpProcess(BPLogProcessEnum.SET_MY_JOINT_UDL_2, "1024", "1", "ulcMycap", "ulcLcpcode", "ulcMylct"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.CREATE_UDLC, CREATE_UDLC_PROCESS_CODE, "1", "ulcMycap",
				"ulcLcpcode", "ulcMylct"));
		buildBpProcess(BPLogProcessEnum.CREATE_UDLC, CREATE_UDLC_PROCESS_CODE, "1", "ulcMycap", "ulcLcpcode",
				"ulcMylct");

		buildBpProcess(BPLogProcessEnum.CREATE_UDLG, CREATE_UDLG_PROCESS_CODE, "1", "ulcMycap", "ulcLcpcode",
				"ulcMylct");
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_UDLC_PARAMETERS, COLLECT_UDLC_PARAMETERS_PROCESS_CODE,
				"4", "ulcMycap", "ulcLcpcode", "ulcMylct"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_UDL_PARAMETERS_1, COLLECT_UDL_PARAMETERS_1_PROCESS_CODE,
				"4", "ulcMycap", "ulcLcpcode", "ulcMylct", "ulcMyjoinulc", "sussCass", "sussLcp", "sussClaglist",
				"sussTe", "sussTo"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_UDL_PARAMETERS_2, COLLECT_UDL_PARAMETERS_2_PROCESS_CODE,
				"4", "ulcMycap", "ulcLcpcode", "ulcMylct", "ulcMyjoinulc", "sussCass", "sussLcp", "sussClaglist",
				"sussTe", "sussTo"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.BPP_SCHEDULE_USS, BPP_SCHEDULE_USS_CODE, "5", "sussCass",
				"sussLcp", "sussClaglist", "sussTe", "sussTo", "sussCo", "sussSo", "sussCti"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_CLAG_LIST_DLC, COLLECT_CLAG_LIST_DLC_CODE, "3",
				"sussCass", "sussLcp", "sussClaglist", "sussTe", "sussTo", "sussCo", "sussSo", "sussCti"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_CLAG_LIST_DLG, COLLECT_CLAG_LIST_DLG_CODE, "3",
				"sussCass", "sussLcp", "sussClaglist", "sussTe", "sussTo", "sussCo", "sussSo", "sussCti"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_UDL_PARAMETERS, COLLECT_UDL_PARAMETERS_PROCESS_CODE,
				"1,2,3,4", "ulcMycap", "ulcLcpcode", "ulcMylct"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_UGE_PARAMETERS_1, "1151", "1,2", "ulcMycap",
				"ulcLcpcode", "ulcMylct", "ulcMyjoinulc", "sussCass", "sussLcp", "sussClaglist", "sussTe", "sussTo",
				"clagCode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_UGE_PARAMETERS, "1031", "1,2", "ulcMycap", "ulcLcpcode",
				"ulcMylct", "ulcMyjoinulc", "sussCass", "sussLcp", "sussClaglist", "sussTe", "sussTo", "clagCode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.COLLECT_UGE_PARAMETERS_2, "10331", "1,2,3,4", "ulcMycap",
				"ulcLcpcode", "ulcMylct", "ulcMyjoinulc", "sussCass", "sussLcp", "sussClaglist", "sussTe", "sussTo",
				"clagCode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.BPP_SCHEDULE_UDLG, "110", "1,2,3,4", "ulcMycap"));
		listProcess
				.add(buildBpProcess(BPLogProcessEnum.Create_Or_Update_CLag_Ulc, "11122", "3", "clagCode", "ulcCode"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_CHPT4, "940", "6,91", "cuieCuiecode", "chptCode"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.CREATE_CHPI, "900", "9,101", "cuieCuiecode", "chptCode",
				"cti1Code", "cti2Code", "cti3Code", "cuieCuiecode", "toSendEmail", "chpiCode"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_CHPT5, "930", "6,91", "lctCode", "lcetCode",
				"triggerUserType", "checkerUserType", "chptType", "chptCode"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SEND_EMAIL_CHSI, "920", "102", "chsiCode"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.SEND_EMAIL_CHPI, "990", "101", "chpiCode"));
		// UGES
		listProcess.add(buildBpProcess(BPLogProcessEnum.CREATE_UGES, CREATE_UGES_PROCESS_CODE, "1", "ulcMycap",
				"ulcLcpcode", "ulcMylct"));
		listProcess.add(
				buildBpProcess(BPLogProcessEnum.SUGGEST_CLAG_UGE, SUGGEST_CLAG_UGE_PROCESS_CODE, "4", "sussClagList"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.BPP_SCHEDULE_UGES, "120", "1", "ulcMyCap"));
		listProcess
				.add(buildBpProcess(BPLogProcessEnum.CREATE_UDLG, "1111", "1", "ulcUlcCode", "ulcMycap", "ulcLcpcode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.BPP_SCHEDULE_UDLG, "110", "1", "ulcMyCap"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_1A, "532", "6", "teacher_username",
				"session_group_code", "live_at", "students"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_1B, "532", "6", "teacher_username",
				"session_group_code", "live_at", "students"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_2, "532", "6", "teacher_username",
				"session_group_code", "live_at", "students"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.TEACHER_SUBMIT_REPORT_3, "532", "6", "teacher_username",
				"session_group_code", "live_at", "students"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.PLAN_CUI_EVENT, "10000", "cuieMyusi", "cuieCuiecode",
				"cuieMylcet", "cuiePlantime"));

		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_USI_TE, "621", "4,6",
				"sussTe", "cuieActualtime"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_ULC_TE, "622", "1,3",
				"clagClagcode", "ulcUlccode", "ulcMycap", "ulcLcpcode", "ulcMylct", "ulcMyjoinulc", "ulcPublished"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.WRITE_CUI_EVENT_TIMEUP_TE_ACTUAL_TIME, "622", "5,6",
				"cuiUlc__code", "cuiMy_lcp", "cuiUsi_code", "cuiUst", "cuiPublished", "cuieMylcet", "cuieActualtimefet",
				"cuieCuicode", "cuieMyusi", "cuieMylcp", "cuiCui_code", "cuieActualtimebet", "cuieCuiecode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.GET_USI, "8311", "6", "cuieActualtime"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_CUI_CUI_EVENT, "83112", "5,6",
				"cuiUlc__code", "cuiMy_lcp", "cuiUsi_code", "cuiUst", "cuieMylcet", "cuieCuicode", "cuiCui_code", "cuieCuiecode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.FIND_CHPI, "83113", "10",
				"chpiChpi_code", "chpiMycuie"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.CREATE_CTI, "83114", "10,21",
				"myContentType", "fileBeginURL", "ctiCode", "cti2Code"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.WRITE_CUI_EVENT_ASSIGN_VIDEO_TE_ACTUAL_TIME, "83115", "5,6",
				"cuiUlc__code", "cuiMy_lcp", "cuiUsi_code", "cuiUst", "cuiPublished", "cuieMylcet", "cuieActualtimefet",
				"cuieCuicode", "cuieMyusi", "cuieMylcp", "cuiCui_code", "cuieActualtimebet", "cuieCuiecode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.CREATE_CUI_EVENT, "10421", "5,6", "cuieCuiecode",
				"cuieMylcet", "cuieCuicode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.TRIGGER_PLANNED_CUI_EVENTS, "107", "1", ""));
		listProcess.add(buildBpProcess(BPLogProcessEnum.WRITE_EVENT_PLAN_TIME, "1074", "4,6", "cuiEventCode",
				"eventPlanTime", "cassCode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.WRITE_EVENT_ACTUAL_TIME, "1074", "1", "cuiEventCode"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.ASSIGN_TE_TO_CLAG, "", "", "date", "usts", "pts", "ggs", "dfdls"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.ASSIGN_DTE_TO_CLAG, "161110", "", "date", "usts", "pts", "ggs", "dfdls"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.ASSIGN_GTE_TO_CLAG, "161210", "", "date", "usts", "pts", "ggs", "dfdls"));
		listProcess.add(buildBpProcess(BPLogProcessEnum.ASSIGN_CTE_TO_CLAG, "161310", "", "date", "usts", "pts", "ggs", "dfdls"));

	}

	private BpProcessCommon buildBpProcess(BPLogProcessEnum processEnum, String processCode, String groupCode,
										   String... columns) {
		Assert.notNull(groupCode, "Group code must be not null");
		Assert.notNull(processEnum, "Process name must be not null");
		Assert.notNull(processCode, "Process code must be not null");
		Assert.notEmpty(columns, "List column mustbe not empty");
		return BpProcessCommon.builder().groupCode(groupCode).processCode(processCode).process(processEnum)
				.listConfig(buildVariableConfig(columns)).build();
	}

	private List<VariableConfig> buildVariableConfig(String... variables) {
		List<VariableConfig> listConfig = new ArrayList<BpProcessCommon.VariableConfig>();
		int index = 1;
		for (String v : variables) {
			listConfig.add(VariableConfig.builder()
					.columnName(BpProcessMapping.COLUMN_PREFIX.concat(String.valueOf(index))).variable(v).build());
			index++;
		}
		return listConfig;
	}

	public BpProcessCommon findByProcessEnum(BPLogProcessEnum processEnum) {
		return listProcess.stream().filter(k -> k.getProcess().equals(processEnum)).findAny().orElse(null);
	}

	public BpProcessCommon findByProcessEnumAndSignature(BPLogProcessEnum processEnum, MethodSignature signature, Object... args) {
		if (BPLogProcessEnum.ASSIGN_TE_TO_CLAG.equals(processEnum)) {
			BpProcessCommon processCommon = listProcess.stream().filter(k -> k.getProcess().equals(BPLogProcessEnum.ASSIGN_TE_TO_CLAG)).findAny().orElse(null);
			if (processCommon == null) return null;
			Parameter[] parameters = signature.getMethod().getParameters();
			List<?> valueOfUst = new ArrayList<>();
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i].getName().equals("usts")) {
					if (args[i] instanceof Collection)
						valueOfUst = (List<?>) args[i];
				}
			}
			if (valueOfUst.isEmpty()) {
				return null;
			}
			String ust = valueOfUst.get(0).toString();
			try {
				processEnum = BPLogProcessEnum.valueOf(BPLogProcessEnum.ASSIGN_TE_TO_CLAG.name().replace("TE", ust).toUpperCase());
			} catch (Exception e) {
				log.error("Error when find bp process {}", e.getMessage());
				return null;
			}
		}

		return findByProcessEnum(processEnum);
	}

}
