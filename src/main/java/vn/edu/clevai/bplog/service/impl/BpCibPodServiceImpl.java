package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.BpCibPod;
import vn.edu.clevai.bplog.entity.BpDfdlDifficultygrade;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.BpWsoWeeklyscheduleoption;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCipPodResponse;
import vn.edu.clevai.bplog.repository.BpCibPodRepository;
import vn.edu.clevai.bplog.service.BpCibPodService;
import vn.edu.clevai.bplog.service.BpDfdlDifficultygradeService;
import vn.edu.clevai.bplog.service.BpPodProductOfDealService;
import vn.edu.clevai.bplog.service.BpWsoWeeklyscheduleoptionService;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BpCibPodServiceImpl implements BpCibPodService {
	@Autowired
	private BpCibPodRepository bpCibPodRepository;

	@Autowired
	private BpPodProductOfDealService bpPodProductOfDealService;

	@Autowired
	private BpDfdlDifficultygradeService bpDfdlDifficultygradeService;

	@Autowired
	private BpWsoWeeklyscheduleoptionService bpWsoWeeklyscheduleoptionService;

	@Override
	public BpCibPod findLastByMypod(String mypod) {
		return bpCibPodRepository.findFirstByMypodOrderByUpdatedAtDesc(mypod)
				.orElseThrow(
						() -> new NotFoundException("Could not find BpCibPod using mypod = " + mypod)
				);
	}

	@Override
	public BpCibPod createWithMydfdl(String mypod, String mydfdl) {
		BpCibPod toCreate = BpCibPod
				.builder()
				.mypod(mypod)
				.mydfdl(mydfdl)
				.build();

		try {
			BpCibPod last = findLastByMypod(mypod);

			toCreate.setMywso(last.getMywso());
		} catch (NotFoundException ignored) {
		}

		String code = Stream.of(toCreate.getMypod(), toCreate.getMywso(), toCreate.getMydfdl()).filter(Objects::nonNull).collect(Collectors.joining("-"));
		toCreate.setCode(code);

		return bpCibPodRepository.save(toCreate);
	}

	@Override
	public BpCibPod createWithMywso(String mypod, String mywso) {
		BpCibPod toCreate = BpCibPod
				.builder()
				.mypod(mypod)
				.mywso(mywso)
				.build();

		try {
			BpCibPod last = findLastByMypod(mypod);

			toCreate.setMydfdl(last.getMydfdl());
		} catch (NotFoundException ignored) {
		}

		String code = Stream.of(toCreate.getMypod(), toCreate.getMywso(), toCreate.getMydfdl()).filter(Objects::nonNull).collect(Collectors.joining("-"));
		toCreate.setCode(code);

		return bpCibPodRepository.save(toCreate);
	}

	@Override
	@WriteUnitTestLog
	public BpCipPodResponse BPSetWSO(String podCode, String wsoCode) {
		BpPodProductOfDeal bpPodProductOfDeal = bpPodProductOfDealService.findByCode(podCode);
		BpWsoWeeklyscheduleoption bpWsoWeeklyscheduleoption = bpWsoWeeklyscheduleoptionService.findByCode(wsoCode);

		BpCibPod bpCibPod = createWithMywso(bpPodProductOfDeal.getCode(), bpWsoWeeklyscheduleoption.getCode());

		return BpCipPodResponse
				.builder()
				.id(bpCibPod.getId())
				.code(bpCibPod.getCode())
				.mydfdl(bpCibPod.getMydfdl())
				.mypod(bpCibPod.getMypod())
				.mywso(bpCibPod.getMywso())
				.createdAt(bpCibPod.getCreatedAt())
				.updatedAt(bpCibPod.getUpdatedAt())
				.build();
	}

	@Override
	@WriteUnitTestLog
	public BpCipPodResponse BPSetDFDL(String podCode, String dfdlCode) {
		BpPodProductOfDeal bpPodProductOfDeal = bpPodProductOfDealService.findByCode(podCode);
		BpDfdlDifficultygrade bpDfdlDifficultygrade = bpDfdlDifficultygradeService.findByCode(dfdlCode);

		BpCibPod bpCibPod = createWithMydfdl(bpPodProductOfDeal.getCode(), bpDfdlDifficultygrade.getCode());

		return BpCipPodResponse
				.builder()
				.id(bpCibPod.getId())
				.code(bpCibPod.getCode())
				.mydfdl(bpCibPod.getMydfdl())
				.mypod(bpCibPod.getMypod())
				.mywso(bpCibPod.getMywso())
				.createdAt(bpCibPod.getCreatedAt())
				.updatedAt(bpCibPod.getUpdatedAt())
				.build();
	}
}
