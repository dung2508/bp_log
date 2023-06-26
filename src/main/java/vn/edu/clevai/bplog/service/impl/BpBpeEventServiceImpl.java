package vn.edu.clevai.bplog.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.BpeEventTypeEnum;
import vn.edu.clevai.bplog.queue.RedisMessagePublisher;
import vn.edu.clevai.bplog.service.BpBpeEventService;
import vn.edu.clevai.common.api.util.StrUtils;

@Service
@RequiredArgsConstructor
public class BpBpeEventServiceImpl implements BpBpeEventService {

	private final LocalValueSaving localValueSaving;

	private final RedisMessagePublisher publisher;

	@Override
	public String generateBpeCode(BpeEventTypeEnum bpeEventTypeEnum) {
		return StrUtils.generateTimestampCode(bpeEventTypeEnum.getCode());
	}

	@Override
	public void createBpeEvent(BpeEventTypeEnum type) {
//		String code = generateBpeCode(type);
//		localValueSaving.setBpeCode(code, false);
//		BpeEventDTO bpsStep = BpeEventDTO.builder()
//				.code(code)
//				.name(code)
//				.bpetype(type.getCode())
//				.mybps(localValueSaving.getBpsCode())
//				.build();
//		publisher.publish(bpsStep);
	}

}