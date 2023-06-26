package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.BpeEventTypeEnum;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.BpClagPODRepository;
import vn.edu.clevai.bplog.repository.BpPODCLAGRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagPODResponse;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class BpClagPODServiceImpl implements BpClagPODService {
	private final BpClagPODRepository bpClagPODRepository;

	private final BpPODCLAGRepository bpPODCLAGRepository;

	private final BpPodProductOfDealService bpPodProductOfDealService;

	private final BpClagClassgroupService bpClagClassgroupService;

	private final CalendarPeriodService calendarPeriodService;

	private final BpULCService bpULCService;

	private final CuiEventService cuiEventService;

	private final CuiService cuiService;

	private final BpBpeEventService bpBpeEventService;

	private final LocalValueSaving valueSaving;


	public BpClagPODServiceImpl(BpClagPODRepository bpClagPODRepository,
								BpPODCLAGRepository bpPODCLAGRepository, BpPodProductOfDealService bpPodProductOfDealService,
								@Lazy BpClagClassgroupService bpClagClassgroupService, CalendarPeriodService calendarPeriodService,
								@Lazy BpULCService bpULCService, @Lazy CuiEventService cuiEventService, @Lazy CuiService cuiService, BpBpeEventService bpBpeEventService, LocalValueSaving valueSaving) {
		this.bpClagPODRepository = bpClagPODRepository;
		this.bpPODCLAGRepository = bpPODCLAGRepository;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.bpClagClassgroupService = bpClagClassgroupService;
		this.calendarPeriodService = calendarPeriodService;
		this.bpULCService = bpULCService;
		this.cuiEventService = cuiEventService;
		this.cuiService = cuiService;
		this.bpBpeEventService = bpBpeEventService;
		this.valueSaving = valueSaving;
	}

	@WriteUnitTestLog
	@Override
	public BpClagPODResponse BPSetCLAGPOD(String podCode, String clagCode, String clagTypeCode) {
		BpPodProductOfDeal bpPodProductOfDeal = bpPodProductOfDealService.findByCode(podCode);
		BpClagClassgroup bpClagClassgroup = bpClagClassgroupService.findByCode(clagCode);

		if (!bpClagClassgroup.getClagtype().equalsIgnoreCase(clagTypeCode)) {
			throw new BadRequestException(
					"clagtype of clag (code = " + clagCode + ") does not match the requested clagTypeCode = " + clagTypeCode
			);
		}

		if (ClagType.findByCode(clagTypeCode) == null) {
			throw new BadRequestException("clagTypeCode " + clagTypeCode + " is not supported");
		}

		BpClagPOD bpClagPOD = bpClagPODRepository.save(
				BpClagPOD
						.builder()
						.active(true)
						.assignedAt(DateUtils.now())
						.mypod(bpPodProductOfDeal.getCode())
						.memberType(clagTypeCode)
						.myclag(bpClagClassgroup.getCode())
						.code(String.format("%s-%s", clagCode, podCode))
						.build()
		);

		return BpClagPODResponse
				.builder()
				.id(bpClagPOD.getId())
				.mypod(bpClagPOD.getMypod())
				.myclag(bpClagPOD.getMyclag())
				.active(bpClagPOD.getActive())
				.assignedAt(bpClagPOD.getAssignedAt())
				.createdAt(bpClagPOD.getCreatedAt())
				.updatedAt(bpClagPOD.getUpdatedAt())
				.membertype(clagTypeCode)
				.unassignedAt(bpClagPOD.getUnAssignedAt())
				.code(bpClagPOD.getCode())
				.build();
	}

	@Override
	public BpPODCLAG setPODClag(String podCode, String clagCode, String capx, String ust) {
//		bpBpeEventService.createBpeEvent(BpeEventTypeEnum.ASSIGN_EM_ASSIGNPODCLAG_CLAG_POD);
		BpClagClassgroup clag = bpClagClassgroupService.findByCode(clagCode);
		CalendarPeriod cap = calendarPeriodService.findByCode(capx);
		String code = getPodClagCode(podCode, clagCode, cap.getStartTime(), cap.getEndTime());
		BpPODCLAG podclag = bpPODCLAGRepository.findByPodClag(podCode, clagCode, ust, cap.getStartTime(), cap.getEndTime())
				.orElseGet(() -> bpPODCLAGRepository.save(BpPODCLAG
						.builder()
						.active(true)
						.assignedAt(cap.getStartTime())
						.unAssignedAt(cap.getEndTime())
						.mypod(podCode)
						.memberType(clag.getClagtype())
						.myclag(clagCode)
						.code(code)
						.myUst(ust)
						.mybps(valueSaving.getBpsCode())
						.build()));
		podclag.setActive(true);
		podclag.setMybps(valueSaving.getBpsCode());
		return podclag;
	}

	@Override
	public void unpublishedPodClag(BpPODCLAG podclag) {
		BpPODCLAG podclag1 = bpPODCLAGRepository.findFirstByCode(podclag.getCode()).orElse(null);
		if (Objects.nonNull(podclag1)) podclag1.setActive(false);
		bpPODCLAGRepository.save(podclag1);
	}

	public void updateStatusByCadyAndUst(CalendarPeriod cap, String ust, boolean active) {
		log.info("Will set all pod_clag status to {} if cap.code = {}", active, cap.getCode());
		List<BpPODCLAG> podClags = bpPODCLAGRepository.findByUst(ust, cap.getStartTime(), cap.getEndTime());
		for (BpPODCLAG item : podClags) {
			item.setActive(active);
		}
		bpPODCLAGRepository.saveAll(podClags);
	}

	@Override
	public void setPodListClagList(List<BpPodProductOfDeal> podList, List<String> clagList, String ust
			, Map<String, String> mapClagUlc) {
		//bpBpeEventService.createBpeEvent(BpeEventTypeEnum.ASSIGN_EM_ASSIGNPODCLAG_CLAG_POD);
		if (Objects.nonNull(podList) && !podList.isEmpty()) {
			int podSize = podList.size();
			Map<String, String> mapUsiUlc = new HashMap<>();
			Queue<String> clagQueue = new LinkedList<>(clagList);
			int i = 0;
			while (true) {
				if (i == podSize) i = 0;
				if (clagQueue.isEmpty()) break;
				String clag = clagQueue.poll();
				if (Objects.isNull(clag)) {
					break;
				}
				BpUniqueLearningComponent ulc = bpULCService.findByCode(mapClagUlc.get(clag));
				setPODClag(podList.get(i).getCode(), clag, ulc.getMyCap().getCode(), ust);
				if (!mapUsiUlc.containsKey(podList.get(i).getMyst()))
					mapUsiUlc.put(podList.get(i).getMyst(), mapClagUlc.get(clag));
				i++;
			}
			mapUsiUlc.keySet().forEach(usiCode -> {
				BpUniqueLearningComponent ulc = bpULCService.findByCode(mapUsiUlc.get(usiCode));
				setBpBpeEvent();
				cuiEventService.createCUIJoinEvent(mapUsiUlc.get(usiCode), usiCode, ulc.getMyLcp().getCode(), null, ulc.getMyCap().getCode());
				cuiEventService.setPublishCUIE(mapUsiUlc.get(usiCode), usiCode, true);
			});
		}
	}

	private void setBpBpeEvent() {
		if (StringUtils.isEmpty(valueSaving.getPodCase())) return;
		switch (valueSaving.getPodCase()) {
			// TODO: 14/05/2023 move to const
			case "UGE":
				bpBpeEventService.createBpeEvent(BpeEventTypeEnum.ASSIGN_EM_CREATE_CUIE_UGE_POD);
				break;
			case "UCO":
				bpBpeEventService.createBpeEvent(BpeEventTypeEnum.ASSIGN_EM_CREATE_CUIE_UCO_POD);
				break;
			case "UDL":
				bpBpeEventService.createBpeEvent(BpeEventTypeEnum.ASSIGN_EM_CREATE_CUIE_UDL_POD);
				break;
			case "ULI":
				bpBpeEventService.createBpeEvent(BpeEventTypeEnum.ASSIGN_EM_CREATE_CUIE_ULI_POD);
				break;
		}

	}

	@Override
	public List<BpPODCLAG> findByPodAndCap(String pod, Timestamp from, Timestamp to, Boolean published) {
		return bpPODCLAGRepository.findByPodAndCap(pod, from, to, published);
	}

	@Override
	public List<BpPODCLAG> findByCadyAndUstAndClag(String cady, String ust, String clag) {
		return bpPODCLAGRepository.findByCapAndUstAnndClag(cady, ust, clag);
	}

	@Override
	public List<BpPODCLAG> findByUstAndClag(String ust, String clag) {
		return bpPODCLAGRepository.findByUstAnndClag(ust, clag);
	}

	@Override
	public BpPODCLAG findByCode(String code) {
		return bpPODCLAGRepository.findFirstByCode(code)
				.orElseThrow(() -> new NotFoundException("Coun't find podClag by code: " + code));
	}

	@Override
	public List<BpPODCLAG> findByPodAndClagAndPublishedAndCady(String pod, String clag, Boolean published, CalendarPeriod cady) {
		return bpPODCLAGRepository.findAllByPodAndClagAndCap(pod, clag, cady.getStartTime(), cady.getEndTime(), published);
	}

	@Override
	public void save(BpPODCLAG podclag) {
		bpPODCLAGRepository.createOrUpdate(podclag);
	}

	@Override
	public String getPodClagCode(String pod, String clag, Timestamp start, Timestamp end) {
		String startString = String.valueOf(start.getTime());
		String endString = String.valueOf(end.getTime());
		return String.join("-", pod, clag, startString, endString);
	}
}
