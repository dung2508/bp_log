package vn.edu.clevai.bplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Lct {
	DLC_75MI(67, "DLC-75MI", "DLC", null, "SH", true),
	DLG_90MI(68, "DLG-90MI", "DLG", null, "SH", true),
	GES_75MI(70, "GES-75MI", "GES", null, "SH", true),
	DL_40MI(80, "DL-40MI", "DL", null, "SS", true),
	CO_30MI(81, "CO-30MI", "CO", null, "SS", true),
	GE_45MI(82, "GE-45MI", "GE", null, "SS", true),
	DCT_40MI(94, "DCT-40MI", "DCT", null, "SS", false),
	DLG_120MI(116, "DLG-120MI", "DLG", null, "SH", true),
	GE_75MI(117, "GE-75MI", "GE", null, "SS", true),
	PKBC_1PK(123, "PKBC-1PK", "BCPK", "BC", "PK", true),
	PKPM_1PK(124, "PKPM-1PK", "PMPK", "PM", "PK", true),
	PKPO_1PK(125, "PKPO-1PK", "POPK", "PO", "PK", true),
	PKTH_1PK(126, "PKTH-1PK", "POPK", "TH", "PK", true),
	PKTU_1PK(127, "PKTU-1PK", "TUPK", "TU", "PK", true),
	PKOM_1PK(128, "PKOM-1PK", "PKOM", "OM", "PK", true),
	PKOE_1PK(129, "PKOE-1PK", "PKOE", "OE", "PK", true),
	MC_1MN(130, "MC-1MN", "MC", null, "MN", true),
	MP71_1MN(131, "MP71-1MN", "MP71", null, "MN", true),
	MP80_1MN(132, "MP80-1MN", "MP80", null, "MN", true),
	MP40_1MN(133, "MP40-1MN", "MP40", null, "MN", true),
	MP80T_1MN(134, "MP80T-1MN", "MP80T", null, "MN", true),
	MP40L_1MN(135, "MP40L-1MN", "MP40L", null, "MN", true),
	WC_1WK(136, "WC-1WK", "WC", null, "WK", true),
	PC_40MI(137, "PC-40MI", "PC", null, "SH", true),
	LI0_45MI(141, "LI0-45MI", "LI0", null, "SH", true),
	LI_45MI(146, "LI-45MI", "LI", null, "SS", true),
	AQR0_AA(147, "AQR0-AA", "AQR0", null, "SC", true),
	RC_5MI(148, "RC-5MI", "RC", null, "SS", true),
	AQR1_AA(152, "AQR1-AA", "AQR1", null, "SC", true),
	AQR2_AA(153, "AQR2-AA", "AQR2", null, "SC", true),
	AQRT_AA(154, "AQRT-AA", "AQRT", null, "SC", true),
	DSC_SS(155, "DSC-SS", "DSC", null, "SC", true),
	HORG_AA(157, "HORG-AA", "HORG", null, "SS", true),
	QA_AA(160, "QA-AA", "QA", null, "SS", true),
	QT_AA(161, "QT-AA", "QT", null, "SS", true),
	PKAL_1PK(162, "PKAL-1PK", "ALPK", null, "PK", true),
	HRV_EA(223, "HRV-EA", "HRV", null, "SH", true),
	CQR_AA(224, "CQR-AA", "CQR", null, "SC", true),
	HRG_EA(229, "HRG-EA", "HRG", null, "SS", true),
	HAV_EA(230, "HAV-EA", "HAV", null, "SS", true),
	HTRG_EA(231, "HTRG-EA", "HTRG", null, "SS", true),
	LCH_45MI(325, "LCH-45MI", "LCH", null, "SC", true),
	AQS_AA(326, "AQS-AA", "AQS", null, "SL", false),
	RQS_1MI(327, "RQS-1MI", "RQS", null, "SL", true),
	DQS_AA(328, "DQS-AA", "DQS", null, "SL", true),
	RL_45MI(329, "RL-45MI", "RL", null, "SS", true),
	QQ_AA(330, "QQ-AA", "QQ", null, "SL", true),
	BW_1DP(331, "BW-1DP", "BW", null, "DP", true),
	CQS_AA(332, "CQS-AA", "CQS", null, "SL", true),
	MP0_1MN(333, "MP0-1MN", "MP0", null, "MN", true),
	WP71_1WK(334, "WP71-1WK", "WP71", null, "WK", true),
	WP80_1WK(335, "WP80-1WK", "WP80", null, "WK", true),
	WP40_1WK(336, "WP40-1WK", "WP40", null, "WK", true),
	WP80T_1WK(337, "WP80T-1WK", "WP80T", null, "WK", true),
	WP0_1WK(338, "WP0-1WK", "WP0", null, "WK", true),
	WP40L_1WK(339, "WP40L-1WK", "WP40L", null, "WK", true);

	private Integer id;
	private String code;
	private String mylctk;
	private String mypt;
	private String mylcl;
	private Boolean needSchedule;

	public static Lct findByCode(String code) {
		return Arrays.stream(values()).filter(lct -> lct.code.equals(code)).findFirst().orElseThrow(
				() -> new NotFoundException("Could not find any lct using code = " + code)
		);
	}
}
