package vn.edu.clevai.bplog.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.BpsStepTypeEnum;
import vn.edu.clevai.bplog.queue.RedisMessagePublisher;
import vn.edu.clevai.bplog.service.BpBpsStepService;
import vn.edu.clevai.common.api.util.StrUtils;

@Service
@RequiredArgsConstructor
public class BpBpsStepServiceImpl implements BpBpsStepService {

	private final LocalValueSaving localValueSaving;

	private final RedisMessagePublisher publisher;

	@Override
	public String generateBpsCode(BpsStepTypeEnum bpsStepTypeEnum) {
		return StrUtils.generateTimestampCode(bpsStepTypeEnum.getCode());
	}

	@Override
	public void createBpsStep(BpsStepTypeEnum type) {
//		String code = generateBpsCode(type);
//		localValueSaving.setBpsCode(code, false);
//		BpsStepDTO bpsStep = BpsStepDTO.builder()
//				.code(code)
//				.name(code)
//				.bpstype(type.getCode())
//				.myprocess(localValueSaving.getBppCode())
//				.build();
//		publisher.publish(bpsStep);
	}

}
