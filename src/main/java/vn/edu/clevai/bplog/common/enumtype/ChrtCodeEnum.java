package vn.edu.clevai.bplog.common.enumtype;

public enum ChrtCodeEnum {
	SELF("Self"), SUP("Sup"), SELF_MGR("SelfMgr"), MGR("Mgr"), PEER("Peer"), SUP_PEER("SupPeer"),
	SUP_MGR("SupMgr");

	final String code;

	ChrtCodeEnum(String code) {
		this.code = code;
	}

	public static ChrtCodeEnum findByCode(String name) {
		for (ChrtCodeEnum c : values()) {
			if (c.getCode().equalsIgnoreCase(name))
				return c;
		}
		return null;
	}

	public String getCode() {
		return code;
	}

}
