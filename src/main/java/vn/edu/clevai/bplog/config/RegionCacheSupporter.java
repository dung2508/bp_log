package vn.edu.clevai.bplog.config;

import java.util.concurrent.TimeUnit;

public interface RegionCacheSupporter {

	String BP_CLAG_ULC = "bp-clag-ulc";
	String BP_CTI_CONTENT_ITEM = "bp-cti-contentitem";
	String BP_CUI_CONTENT_USER_ULC_INSTANCE = "bp-cui-content-user-ulc-instance";
	String BP_CUIE_CUIEVENT = "bp-cuie-cuievent";
	String BP_POD_CLAG = "bp-pod-clag";
	String BP_CLAG_CLASSGROUP = "bp-clag-classgroup";
	String BP_TASK_CACHE_REGION = "bp-task-cache-region";

	Integer BP_CLAG_ULC_INFO_DURATION = 30 * 60;
	Integer BP_CTI_CONTENT_ITEM_INFO_DURATION = 30 * 60;
	Integer BP_CUI_CONTENT_USER_ULC_INSTANCE_INFO_DURATION = 30 * 60;
	Integer BP_CUIE_CUIEVENT_INFO_DURATION = 30 * 60;
	Integer BP_POD_CLAG_INFO_DURATION = 30 * 60;
	Integer BP_TASK_CACHE_DURATION = 8 * 60 * 60;


	Integer TEN_SECONDS_IN_MILLISECONDS = 10 * 1000;

	TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
}
