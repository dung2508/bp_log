package vn.edu.clevai.bplog.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum;
import vn.edu.clevai.bplog.queue.RedisMessagePublisher;
import vn.edu.clevai.bplog.service.BpBppProcessService;

@Service
@RequiredArgsConstructor
public class BpBppProcessServiceImpl implements BpBppProcessService {


	private final LocalValueSaving localValueSaving;

	private final RedisMessagePublisher publisher;

	@Override
	public String generateBppCode(BppProcessTypeEnum bppProcessTypeEnum) {
		return bppProcessTypeEnum.getCode() + System.currentTimeMillis();
	}

	@Override
	public void createBppProcess(BppProcessTypeEnum type, String myparent) {
//		String code = generateBppCode(type);
//		localValueSaving.setBppCode(code, true);
//		BppProcessDTO bppProcess = BppProcessDTO.builder()
//				.code(code)
//				.name(code)
//				.bpptype(type.getCode())
//				.myparent(myparent)
//				.build();
//		publisher.publish(bppProcess);
	}

}