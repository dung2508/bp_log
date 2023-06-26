package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpWsoWeeklyscheduleoption;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODWSOResponse;

public interface BpWsoWeeklyscheduleoptionService {
	BpWsoWeeklyscheduleoption findByCode(String code);

	/**
	 * @param wsoCode:          25 - Schedule Monday and Thursday
	 * @param currPeriodNumber: 2 - Shift 2
	 * @return 5 - Thursday
	 */
	String findDayOfWeek(String wsoCode, String currPeriodNumber);

	/**
	 * @param wsoCode:       25 - Schedule Monday and Thursday
	 * @param numberAsChild: 5 - Thursday
	 * @return 2 - Shift 2
	 */
	String getCurriculumDayNo(String wsoCode, String numberAsChild);

	BpWsoWeeklyscheduleoption getPOD_WSO(String podCode);

	BpWsoWeeklyscheduleoption getWSOFromX(String xwso);

	String findXWSO(Long xdeal);

	BpPODWSOResponse setPOD_WSO(String podCode, String wsoCode);

	String classDayToWSO(String classDay);

}
