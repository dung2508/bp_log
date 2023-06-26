package vn.edu.clevai.bplog.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.entity.BpPODWSO;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.BpWsoWeeklyscheduleoption;
import vn.edu.clevai.bplog.repository.BpPODWSORepository;
import vn.edu.clevai.bplog.repository.BpWsoWeeklyscheduleoptionRepository;
import vn.edu.clevai.bplog.service.BpPodProductOfDealService;
import vn.edu.clevai.bplog.service.BpWsoWeeklyscheduleoptionService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODWSOResponse;

@Service
public class BpWsoWeeklyscheduleoptionServiceImpl extends BaseProxyService implements BpWsoWeeklyscheduleoptionService {

	@Autowired
	private BpPodProductOfDealService bpPodProductOfDealService;

	@Autowired
	private BpWsoWeeklyscheduleoptionRepository bpWsoWeeklyscheduleoptionRepository;

	@Autowired
	private BpPODWSORepository bpPODWSORepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private LmsService lmsService;

	@Override
	public BpWsoWeeklyscheduleoption findByCode(String code) {
		return bpWsoWeeklyscheduleoptionRepository.findByCode(code)
				.orElseThrow(
						() -> new NotFoundException("Could not find any BpWsoWeeklyscheduleoption using code = " + code)
				);
	}

	@Override
	public String findDayOfWeek(String wsoCode, String cupNo) { // cupNo = CurriculumDay no
		int number = Integer.parseInt(cupNo);
		return wsoCode.substring(number - 1, number);
	}

	@Override
	public String getCurriculumDayNo(String wsoCode, String numberAsChild) {
		return String.valueOf(wsoCode.indexOf(numberAsChild) + 1);
	}

	@Override
	@WriteUnitTestLog
	public BpWsoWeeklyscheduleoption getPOD_WSO(String podCode) {
		BpPodProductOfDeal bp = bpPodProductOfDealService.findByCode(podCode);
		return getWSOFromX(findXWSO(bp.getXdeal()));
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_WSO_FROM_X
	)
	public BpWsoWeeklyscheduleoption getWSOFromX(String xwso) {
		return findByCode(xwso);
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.FIND_XWSO
	)
	public String findXWSO(Long xXdealid) {
		return classDayToWSO(lmsService.getClassDayByXDEAL(xXdealid));
	}

	@Override
	@WriteUnitTestLog
	@Transactional
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_POD_WSO
	)
	public BpPODWSOResponse setPOD_WSO(String podPod_code, String podMywso) {
		String code = podPod_code + "-" + podMywso;
		return modelMapper.map(bpPODWSORepository.findByCode(code).orElseGet(() -> bpPODWSORepository.save(BpPODWSO.builder()
				.code(code)
				.mypod(podPod_code)
				.mywso(podMywso)
				.build())), BpPODWSOResponse.class);
	}

	@Override
	public String classDayToWSO(String classDay) {
		return classDay.replace(",", "").replace(" ", "");
	}
}
