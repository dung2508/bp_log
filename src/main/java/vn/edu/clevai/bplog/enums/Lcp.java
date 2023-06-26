package vn.edu.clevai.bplog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Lcp {
	MC_1MN_ED1_PC_40MI(77, "MC-1MN-ED1-PC-40MI", "ED1", "PC-40MI", "MC-1MN", "40MI", "EPOD", null, null),
	MP71_1MN_CD17_DLC_75MI(78, "MP71-1MN-CD17-DLC-75MI", "CD17", "DLC-75MI", "MP71-1MN", "75MI", "ACG", null, null),
	MP80_1MN_CD18_DLG_90MI(79, "MP80-1MN-CD18-DLG-90MI", "CD18", "DLG-90MI", "MP80-1MN", "90MI", "ACG", null, null),
	MP40_1MN_CD14_DLG_90MI(80, "MP40-1MN-CD14-DLG-90MI", "CD14", "DLG-90MI", "MP40-1MN", "90MI", "ACG", null, null),
	MP80T_1MN_CD18_DLG_120MI(81, "MP80T-1MN-CD18-DLG-120MI", "CD18", "DLG-120MI", "MP80T-1MN", "120MI", "ACG", null, null),
	MP40L_1MN_CD14_LI0_45MI(82, "MP40L-1MN-CD14-LI0-45MI", "CD14", "LI0-45MI", "MP40L-1MN", "45MI", "ACG", null, null),
	WC_1WK_ED1_PC_40MI(83, "WC-1WK-ED1-PC-40MI", "ED1", "PC-40MI", "WC-1WK", "40MI", "EPOD", null, null),
	PC_40MI_AAX_CQR_AA(84, "PC-40MI-AAX-CQR-AA", "AAX", "CQR-AA", "PC-40MI", null, "EPOD", null, 10),
	DLC_75MI_FD1_DL_40MI(85, "DLC-75MI-FD1-DL-40MI", "FD1", "DL-40MI", "DLC-75MI", "40MI", "ACG", null, null),
	DLG_90MI_FD1_DL_40MI(86, "DLG-90MI-FD1-DL-40MI", "FD1", "DL-40MI", "DLG-90MI", "40MI", "ACG", null, null),
	DLG_120MI_FD1_DL_40MI(87, "DLG-120MI-FD1-DL-40MI", "FD1", "DL-40MI", "DLG-120MI", "40MI", "ACG", null, null),
	LI0_45MI_FD1_LI_45MI(88, "LI0-45MI-FD1-LI-45MI", "FD1", "LI-45MI", "LI0-45MI", "40MI", "ACG", null, null),
	GES_75MI_FD1_GE_75MI(89, "GES-75MI-FD1-GE-75MI", "FD1", "GE-75MI", "GES-75MI", "75MI", "ECG", null, null),
	DL_40MI_SSX_DSC_SS(92, "DL-40MI-SSX-DSC-SS", "SSX", "DSC-SS", "DL-40MI", null, "ACG", null, 3),
	LI_45MI_FD1_LCH_45MI(93, "LI-45MI-FD1-LCH-45MI", "FD1", "LCH-45MI", "LI-45MI", "45MI", "ECG", null, null),
	AQR0_AA_AAX_AQS_AA(94, "AQR0-AA-AAX-AQS-AA", "AAX", "AQS-AA", "AQR0-AA", null, "EPOD", null, 12),
	RC_5MI_FDX_RQS_1MI(95, "RC-5MI-FDX-RQS-1MI", "FDX", "RQS-1MI", "RC-5MI", null, "EPOD", null, 6),
	AQR1_AA_AAX_AQS_AA(99, "AQR1-AA-AAX-AQS-AA", "AAX", "AQS-AA", "AQR1-AA", null, "EPOD", null, 12),
	AQR2_AA_AAX_AQS_AA(100, "AQR2-AA-AAX-AQS-AA", "AAX", "AQS-AA", "AQR2-AA", null, "EPOD", null, 12),
	AQRT_AA_AAX_AQS_AA(101, "AQRT-AA-AAX-AQS-AA", "AAX", "AQS-AA", "AQRT-AA", null, "EPOD", null, 12),
	DSC_SS_AA1_DQS_AA(102, "DSC-SS-AA1-DQS-AA", "AA1", "DQS-AA", "DSC-SS", null, "EPOD", null, null),
	MP40L_1MN_AAX_HORG_AA(103, "MP40L-1MN-AAX-HORG-AA", "AAX", "HORG-AA", "MP40L-1MN", null, "EPOD", "REG", 120),
	HORG_AA_AAX_AQR1_AA(104, "HORG-AA-AAX-AQR1-AA", "AAX", "AQR1-AA", "HORG-AA", null, "EPOD", null, 12),
	MP40L_1MN_ADX_RL_45MI(105, "MP40L-1MN-ADX-RL-45MI", "ADX", "RL-45MI", "MP40L-1MN", "45MI", "EPOD", null, 120),
	MP40L_1MN_AA1_QA_AA(106, "MP40L-1MN-AA1-QA-AA", "AA1", "QA-AA", "MP40L-1MN", null, "EPOD", null, null),
	QA_AA_AAX_QT_AA(107, "QA-AA-AAX-QT-AA", "AAX", "QT-AA", "QA-AA", null, "EPOD", null, null),
	QT_AA_AAX_QQ_AA(108, "QT-AA-AAX-QQ-AA", "AAX", "QQ-AA", "QT-AA", null, "EPOD", null, null),
	PKAL_1PK_FD1_BW_1DP(109, "PKAL-1PK-FD1-BW-1DP", "FD1", "BW-1DP", "PKAL-1PK", "1DP", "ACG", null, null),
	MP71_1MN_CD21_GES_75MI(117, "MP71-1MN-CD21-GES-75MI", "CD21", "GES-75MI", "MP71-1MN", "75MI", "ACG", null, null),
	WC_1WK_EA1_HRV_EA(118, "WC-1WK-EA1-HRV-EA", "EA1", "HRV-EA", "WC-1WK", null, "EPOD", "REV", 4),
	DLC_75MI_FD2_RC_5MI(119, "DLC-75MI-FD2-RC-5MI", "FD2", "RC-5MI", "DLC-75MI", "5MI", "ECG", null, null),
	DLG_90MI_FD2_RC_5MI(120, "DLG-90MI-FD2-RC-5MI", "FD2", "RC-5MI", "DLG-90MI", "5MI", "ECG", null, null),
	DLG_120MI_FD2_RC_5MI(121, "DLG-120MI-FD2-RC-5MI", "FD2", "RC-5MI", "DLG-120MI", "5MI", "ECG", null, null),
	GES_75MI_EA1_HRG_EA(122, "GES-75MI-EA1-HRG-EA", "EA1", "HRG-EA", "GES-75MI", null, "EPOD", "REG", null),
	DLC_75MI_FD3_CO_30MI(130, "DLC-75MI-FD3-CO-30MI", "FD3", "CO-30MI", "DLC-75MI", "30MI", "ECG", null, null),
	DLG_90MI_FD3_GE_45MI(131, "DLG-90MI-FD3-GE-45MI", "FD3", "GE-45MI", "DLG-90MI", "45MI", "ECG", null, null),
	DLG_120MI_FD3_GE_75MI(132, "DLG-120MI-FD3-GE-75MI", "FD3", "GE-75MI", "DLG-120MI", "75MI", "ECG", null, null),
	GES_75MI_EA2_HAV_EA(133, "GES-75MI-EA2-HAV-EA", "EA2", "HAV-EA", "GES-75MI", null, "EPOD", "ADV", null),
	DLC_75MI_EA1_HRG_EA(141, "DLC-75MI-EA1-HRG-EA", "EA1", "HRG-EA", "DLC-75MI", null, "EPOD", "REG", null),
	DLG_90MI_EA1_HRG_EA(142, "DLG-90MI-EA1-HRG-EA", "EA1", "HRG-EA", "DLG-90MI", null, "EPOD", "REG", null),
	DLG_120MI_EA1_HTRG_EA(143, "DLG-120MI-EA1-HTRG-EA", "EA1", "HTRG-EA", "DLG-120MI", null, "EPOD", "REG", null),
	DL_40MI_FD1_DCT_40MI(144, "DL-40MI-FD1-DCT-40MI", "FD1", "DCT-40MI", "DL-40MI", "40MI", "ECG", null, null),
	DLC_75MI_EA2_HAV_EA(145, "DLC-75MI-EA2-HAV-EA", "EA2", "HAV-EA", "DLC-75MI", null, "EPOD", "ADV", null),
	DLG_90MI_EA2_HAV_EA(146, "DLG-90MI-EA2-HAV-EA", "EA2", "HAV-EA", "DLG-90MI", null, "EPOD", "ADV", null),
	PKBC_1PK_FDX_MC_1MN(147, "PKBC-1PK-FDX-MC-1MN", "FDX", "MC-1MN", "PKBC-1PK", "1MN", "ACG", null, null),
	PKPM_1PK_FDX_MC_1MN(148, "PKPM-1PK-FDX-MC-1MN", "FDX", "MC-1MN", "PKPM-1PK", "1MN", "ACG", null, null),
	PKPO_1PK_FDX_MC_1MN(149, "PKPO-1PK-FDX-MC-1MN", "FDX", "MC-1MN", "PKPO-1PK", "1MN", "ACG", null, null),
	PKTH_1PK_FDX_MC_1MN(150, "PKTH-1PK-FDX-MC-1MN", "FDX", "MC-1MN", "PKTH-1PK", "1MN", "ACG", null, null),
	PKTU_1PK_FDX_MC_1MN(151, "PKTU-1PK-FDX-MC-1MN", "FDX", "MC-1MN", "PKTU-1PK", "1MN", "ACG", null, null),
	PKOM_1PK_FDX_MC_1MN(152, "PKOM-1PK-FDX-MC-1MN", "FDX", "MC-1MN", "PKOM-1PK", "1MN", "ACG", null, null),
	PKOE_1PK_FDX_MC_1MN(153, "PKOE-1PK-FDX-MC-1MN", "FDX", "MC-1MN", "PKOE-1PK", "1MN", "ACG", null, null),
	HRV_EA_AAX_AQR0_AA(167, "HRV-EA-AAX-AQR0-AA", "AAX", "AQR0-AA", "HRV-EA", null, "EPOD", null, 4),
	CQR_AA_AAX_CQS_AA(168, "CQR-AA-AAX-CQS-AA", "AAX", "CQS-AA", "CQR-AA", null, "EPOD", null, 5),
	HRG_EA_AAX_AQR1_AA(173, "HRG-EA-AAX-AQR1-AA", "AAX", "AQR1-AA", "HRG-EA", null, "EPOD", null, 6),
	HAV_EA_AAX_AQR2_AA(174, "HAV-EA-AAX-AQR2-AA", "AAX", "AQR2-AA", "HAV-EA", null, "EPOD", null, 2),
	HTRG_EA_AAX_AQRT_AA(175, "HTRG-EA-AAX-AQRT-AA", "AAX", "AQRT-AA", "HTRG-EA", null, "EPOD", null, 10),
	PKBC_1PK_FDX_MP71_1MN(187, "PKBC-1PK-FDX-MP71-1MN", "FDX", "MP71-1MN", "PKBC-1PK", "1MN", "ACG", null, null),
	PKPM_1PK_FDX_MP80_1MN(188, "PKPM-1PK-FDX-MP80-1MN", "FDX", "MP80-1MN", "PKPM-1PK", "1MN", "ACG", null, null),
	PKPO_1PK_FDX_MP40_1MN(189, "PKPO-1PK-FDX-MP40-1MN", "FDX", "MP40-1MN", "PKPO-1PK", "1MN", "ACG", null, null),
	PKTH_1PK_FDX_MP80T_1MN(190, "PKTH-1PK-FDX-MP80T-1MN", "FDX", "MP80T-1MN", "PKTH-1PK", "1MN", "ACG", null, null),
	PKTU_1PK_FDX_MP0_1MN(191, "PKTU-1PK-FDX-MP0-1MN", "FDX", "MP0-1MN", "PKTU-1PK", "1MN", "ACG", null, null),
	PKOM_1PK_FDX_MP40L_1MN(192, "PKOM-1PK-FDX-MP40L-1MN", "FDX", "MP40L-1MN", "PKOM-1PK", "1MN", "ACG", null, null),
	PKOE_1PK_FDX_MP40L_1MN(193, "PKOE-1PK-FDX-MP40L-1MN", "FDX", "MP40L-1MN", "PKOE-1PK", "1MN", "ACG", null, null),
	PKBC_1PK_FDX_WC_1WK(200, "PKBC-1PK-FDX-WC-1WK", "FDX", "WC-1WK", "PKBC-1PK", "1WK", "ACG", null, null),
	PKPM_1PK_FDX_WC_1WK(201, "PKPM-1PK-FDX-WC-1WK", "FDX", "WC-1WK", "PKPM-1PK", "1WK", "ACG", null, null),
	PKPO_1PK_FDX_WC_1WK(202, "PKPO-1PK-FDX-WC-1WK", "FDX", "WC-1WK", "PKPO-1PK", "1WK", "ACG", null, null),
	PKTH_1PK_FDX_WC_1WK(203, "PKTH-1PK-FDX-WC-1WK", "FDX", "WC-1WK", "PKTH-1PK", "1WK", "ACG", null, null),
	PKTU_1PK_FDX_WC_1WK(204, "PKTU-1PK-FDX-WC-1WK", "FDX", "WC-1WK", "PKTU-1PK", "1WK", "ACG", null, null),
	PKOM_1PK_FDX_WC_1WK(205, "PKOM-1PK-FDX-WC-1WK", "FDX", "WC-1WK", "PKOM-1PK", "1WK", "ACG", null, null),
	PKOE_1PK_FDX_WC_1WK(206, "PKOE-1PK-FDX-WC-1WK", "FDX", "WC-1WK", "PKOE-1PK", "1WK", "ACG", null, null),
	PKBC_1PK_FDX_WP71_1WK(211, "PKBC-1PK-FDX-WP71-1WK", "FDX", "WP71-1WK", "PKBC-1PK", "1WK", "ACG", null, null),
	PKPM_1PK_FDX_WP80_1WK(212, "PKPM-1PK-FDX-WP80-1WK", "FDX", "WP80-1WK", "PKPM-1PK", "1WK", "ACG", null, null),
	PKPO_1PK_FDX_WP40_1WK(213, "PKPO-1PK-FDX-WP40-1WK", "FDX", "WP40-1WK", "PKPO-1PK", "1WK", "ACG", null, null),
	PKTH_1PK_FDX_WP80T_1WK(214, "PKTH-1PK-FDX-WP80T-1WK", "FDX", "WP80T-1WK", "PKTH-1PK", "1WK", "ACG", null, null),
	PKTU_1PK_FDX_WP0_1WK(215, "PKTU-1PK-FDX-WP0-1WK", "FDX", "WP0-1WK", "PKTU-1PK", "1WK", "ACG", null, null),
	PKOM_1PK_FDX_WP40L_1WK(216, "PKOM-1PK-FDX-WP40L-1WK", "FDX", "WP40L-1WK", "PKOM-1PK", "1WK", "ACG", null, null),
	PKOE_1PK_FDX_WP40L_1WK(217, "PKOE-1PK-FDX-WP40L-1WK", "FDX", "WP40L-1WK", "PKOE-1PK", "1WK", "ACG", null, null),
	;

	private Integer id;

	private String code;

	private String lcperiodno;

	private String mylct;

	private String mylctparent;

	private String myprd;

	private String cgbr;

	private String dfqc;

	private Integer nolcp;

	public static Lcp findByCode(String code) {
		return Arrays.stream(values()).filter(lcp -> lcp.code.equals(code)).findFirst().orElseThrow(
				() -> new NotFoundException("Could not find any lcp using code = " + code)
		);
	}
}
