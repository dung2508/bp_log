package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum LCPLCTLCKEnum {
	ACQ("ACQ"),
	VALA("VALA"),
	CHURN("CHURN"),
	POSTP("POSTP"),
	STREN("STREN"),
	LCMN("LCMN"),
	LCWK("LCWK"),
	ONB_15MI("ONB-15MI"),
	DLC_75MI("DLC-75MI"),
	DLG_90MI("DLG-90MI"),
	DL_40MI("DL-40MI"),
	DCT_40MI("DCT-40MI"),
	DSC_SS("DSC-SS"),
	DQS_AA("DQS-AA"),
	RC_5MI("RC-5MI"),
	RQS_1MI("RQS-1MI"),
	CO_30MI("CO-30MI"),
	HRG_EA("HRG-EA"),
	AQR1_AA("AQR1-AA"),
	HAV_EA("HAV-EA"),
	AQR2_AA("AQR2-AA"),
	AQS_AA("AQS-AA"),

	DL0_75MI("DL0-75MI"),
	GES_75MI("GES-75MI"),
	ASM_AT("ASM-AT"),
	AST_AT("AST-AT"),
	MSN_AT("MSN-AT"),
	MEX_AT("MEX-AT"),
	PH_AT("PH-AT"),
	TET_AT("TET-AT"),
	CTD_AT("CTD-AT"),
	CASE_AT("CASE-AT"),
	QT_15MI("QT-15MI"),
	GE_45MI("GE-45MI"),
	MW_AT("MW-AT"),
	BW_AT("BW-AT"),
	ASQC_AT("ASQC-AT"),
	EP_AT("EP-AT"),
	RL_AT("RL-AT"),
	TS_AT("TS-AT"),
	RC_AT("RC-AT"),
	CA_AT("CA-AT"),
	TX_AT("TX-AT"),
	CSC_30MI("CSC-30MI"),
	CCT_30MI("CCT-30MI"),
	GSC_15MI("GSC-15MI"),
	GSC_25MI("GSC-25MI"),
	AQR("AQR"),
	TQR("TQR"),
	PLB_AT("PLB-AT"),
	TC("TC"),
	RPSC("RPSC"),
	DAS("DAS"),
	AQS("AQS"),

	RTS("RTS"),
	RPSL_5MI("RPSL-5MI"),
	BC_1MN("BC-1MN"),
	PM_1MN("PM-1MN"),
	MD_1MN("MD-1MN"),
	TP10_1MN("TP10-1MN"),
	TPU_1YE("TPU-1YE"),
	ADM_AT("ADM-AT"),
	GSC_75MI("GSC-75MI"),
	GSC_AT("GSC-AT"),
	DLG_120MI("DLG-120MI"),
	GE_75MI("GE-75MI"),
	GGT_75MI("GGT-75MI"),
	GGT("GGT"),

	LCT_BACKUP("UBW-1440MI"),
	LCK_LI0("LI0"),
	LCK_DL("DL"),
	LCK_GE("GE"),
	LCK_CO("CO"),
	LCK_LI("LI"),
	LCK_DCT("DCT"),
	LCK_DSC("DSC"),

	// OM
	MP40L_1MN("MP40L-1MN"),
	LI0_45MI("LI0-45MI"),
	LI_45MI("LI-45MI"),
	HORG_AA("HORG-AA"),
	RL_45MI("RL-45MI"),
	QA_AA("QA-AA"),

	// PC
	MC_1MN("MC-1MN"),
	WC_1WK("WC-1WK"),
	PC_40MI("PC-40MI"),
	CQR_AA("CQR-AA"),
	CQS_AA("CQS-AA");

	private final String code;

	public static LCPLCTLCKEnum findByCode(String code) {
		return Arrays.stream(values()).filter(v -> v.getCode().equals(code))
				.findFirst().orElse(null);
	}
}
