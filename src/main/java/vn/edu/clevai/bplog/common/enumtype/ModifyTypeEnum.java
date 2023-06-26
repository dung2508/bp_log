package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModifyTypeEnum {
	PURCHASE("Purchase"),
	RENEW_REPEAT("Repeat"),
	RENEW_CROSSSELL("Crosssell"),
	RENEW_TRANFER("Tranfer"),
	RENEW_TOPUP("Topup"),
	PASS("Pass"),
	DEFER_BEFORE_SIGNUP("Before signup"),
	DEFER_AFTER_SIGNUP("After signup"),
	DEFER_CHANGE("Change"),
	SUSPEND("Suspend"),
	UNSUSPEND("Unsuspend"),
	REFUND("Refund"),

	CHANGE_CIB("ChangeCIB");

	private final String name;
}
