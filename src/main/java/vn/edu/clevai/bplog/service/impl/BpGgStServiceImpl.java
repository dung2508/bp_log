package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.BpStGg;
import vn.edu.clevai.bplog.repository.BpGgStRepository;
import vn.edu.clevai.bplog.service.BpGgStService;
import vn.edu.clevai.bplog.service.BpPodProductOfDealService;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpGgStResponse;

@Service
public class BpGgStServiceImpl implements BpGgStService {
	@Autowired
	private BpPodProductOfDealService bpPodProductOfDealService;

	@Autowired
	private BpGgStRepository bpGgStRepository;

	@Override
	@WriteUnitTestLog
	public BpGgStResponse BPSetGG(String podCode, String ggCode) {
		BpPodProductOfDeal bpPodProductOfDeal = bpPodProductOfDealService.findByCode(podCode);

		return setST_GG(bpPodProductOfDeal.getMyst(), ggCode);
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_ST_GG
	)
	public BpGgStResponse setST_GG(String stCode, String stMygg) {
		String code = String.format("%s-%s", stMygg, stCode);
		BpStGg bpStGg = bpGgStRepository.findByCode(code).orElseGet(() -> bpGgStRepository.save(
				BpStGg
						.builder()
						.code(code)
						.mygg(stMygg)
						.myst(stCode)
						.build()));

		return BpGgStResponse
				.builder()
				.id(bpStGg.getId())
				.code(bpStGg.getCode())
				.mygg(bpStGg.getMygg())
				.myst(bpStGg.getMyst())
				.createdAt(bpStGg.getCreatedAt())
				.updatedAt(bpStGg.getUpdatedAt())
				.build();
	}
}
