package vn.edu.clevai.bplog.common;

import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;

public class RegisterBpBppCommon {
	public static String step4FindBppTypeByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12400BPPRegister4-Allocate-GTE";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13400BPPRegister4-Allocate-DTE";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18400BPPRegister4-Allocate-CTE";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19400BPPRegister4-Allocate-LTE";
		} else if (USTEnum.SO.getName().equalsIgnoreCase(ustCode)) {
			return "14400BPPRegister4-Allocate-SO";
		} else if ("TO".equalsIgnoreCase(ustCode)) {
			return "15400BPPRegister4-Allocate-DO";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17400BPPRegister4-Allocate-QO";
		}
		return null;
	}

	public static String step4FindBpsInsertByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12433BPSRegister4-Allocate-USI";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13433BPSRegister4-Allocate-USI";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18433BPSRegister4-Allocate-USI";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19433BPSRegister4-Allocate-USI";
		} else if ("SO".equalsIgnoreCase(ustCode)) {
			return "14430BPSRegister4-Allocate-USI";
		} else if ("TO".equalsIgnoreCase(ustCode)) {
			return "15430BPSRegister4-Allocate-USI";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17430BPSRegister4-Allocate-USI";
		}
		return null;
	}
	
	public static String step4FindBpsUpdateByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12443BPSRegister4-Allocate-USI";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13443BPSRegister4-Allocate-USI";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18443BPSRegister4-Allocate-USI";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19443BPSRegister4-Allocate-USI";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17460BPSRegister4-Allocate-USI";
		}
		return null;
	}
	
	public static String step4FindBpeInsertByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12436BPERegister4-Allocate-USID";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13437BPERegister4-Allocate-USID";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18436BPERegister4-Allocate-USID";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19436BPERegister4-Allocate-USID";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17470BPERegister4-Allocate-USID";
		}
		return null;
	}
	
	public static String step4FindBpeUpdateByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12446BPERegister4-Allocate-USID";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13446BPERegister4-Allocate-USID";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18446BPERegister4-Allocate-USID";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19446BPERegister4-Allocate-USID";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17470BPERegister4-Allocate-USID";
		}
		return null;
	}
	
	// STEP 5 =====>
	
	public static String step5FindBppTypeByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12500BPPRegister5-Transform-GTE";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13500BPPRegister5-Transform-DTE";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18500BPPRegister5-Transform-CTE";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19500BPPRegister5-Transform-LTE";
		} else if (USTEnum.SO.getName().equalsIgnoreCase(ustCode)) {
			return "14500BPPRegister5-Transform-SO";
		} else if ("DO".equalsIgnoreCase(ustCode)) {
			return "17500BPPRegister5-Transform-QO";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17500BPPRegister5-Transform-QO";
		}
		return null;
	}
	
	public static String step5FindBpsInsertByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12533BPPRegister5-Transform-USI";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13533BPPRegister5-Transform-USI";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18533BPPRegister5-Transform-USI";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19533BPPRegister5-Transform-USI";
		} else if ("SO".equalsIgnoreCase(ustCode)) {
			return "14543BPPRegister5-Transform-USI";
		} else if ("TO".equalsIgnoreCase(ustCode)) {
			return "15543BPPRegister5-Transform-USI";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17543BPPRegister5-Transform-USI";
		}
		return null;
	}
	
	public static String step5FindBpeInsertByUserType(String ustCode) {
		if (USTEnum.GTE.getName().equalsIgnoreCase(ustCode)) {
			return "12534BPERegister5-Transform-USID";
		} else if (USTEnum.DTE.getName().equalsIgnoreCase(ustCode)) {
			return "13534BPERegister5-Transform-USID";
		} else if ("CTE".equalsIgnoreCase(ustCode)) {
			return "18534BPERegister5-Transform-USID";
		} else if ("LTE".equalsIgnoreCase(ustCode)) {
			return "19534BPERegister5-Transform-USID";
		} else if ("SO".equalsIgnoreCase(ustCode)) {
			return "14544BPERegister5-Transform-USID";
		} else if ("TO".equalsIgnoreCase(ustCode)) {
			return "15544BPERegister5-Transform-USID";
		} else if ("QO".equalsIgnoreCase(ustCode)) {
			return "17544BPERegister5-Transform-USID";
		}
		return null;
	}
	
}
