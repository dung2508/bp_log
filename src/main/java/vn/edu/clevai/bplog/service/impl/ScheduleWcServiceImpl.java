package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.CurriculumPeriodEnum;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.enums.Lcp;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.slack.SlackService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ScheduleWcServiceImpl extends BaseScheduleServiceImpl implements ScheduleService, ScheduleWcService {
	private final CalendarPeriodService capService;

	/* Replace by the Redis service later. */
	private final ConcurrentHashMap<String, List<String>> cachedCtis;

	private final CuiService cuiService;

	private final BpLCPService lcpService;

	private final CurriculumProgramPackageService crppService;

	private final CurriculumProgramSheetService crpsService;

	private final CurriculumPeriodService cupService;

	private final BpClagClassgroupService clagClassgroupService;

	private final BpPodProductOfDealService productOfDealService;

	public ScheduleWcServiceImpl(
			CalendarPeriodService capService,
			BpLCPService lcpService,
			BpLctService lctService,
			CurriculumProgramPackageService crppService,
			BpUSIDutyService usidService,
			CurriculumProgramSheetService crpsService,
			CurriculumPeriodService cupService,
			BpClagClassgroupService clagService,
			BpPodProductOfDealService podService,
			SlackService slackService,
			BpULCService ulcService,
			CuiService cuiService,
			CuiEventService cuieService,
			BpClagClassgroupService clagClassgroupService, BpPodProductOfDealService productOfDealService) {
		super(
				capService,
				lcpService,
				lctService,
				crppService,
				usidService,
				crpsService,
				cupService,
				clagService,
				podService,
				slackService,
				ulcService,
				cuiService,
				cuieService
		);
		this.clagClassgroupService = clagClassgroupService;
		this.productOfDealService = productOfDealService;

		this.cachedCtis = new ConcurrentHashMap<>();
		this.capService = capService;
		this.crppService = crppService;
		this.crpsService = crpsService;
		this.cuiService = cuiService;
		this.cupService = cupService;
		this.lcpService = lcpService;
	}

	@Override
	public void schedule(String cawk) {
		schedule(capService.findByCodeAndCapType(cawk, CalendarPeriodTypeEnum.WEEK.getCode()));
	}

	@Override
	protected Boolean shouldBeScheduled(BpLCP lcp, CalendarPeriod parentCap) {
		if (!lcp.getCode().equals(Lcp.WC_1WK_ED1_PC_40MI.getCode())) {
			return true;
		}

		if (!parentCap.getCapType().equals(CalendarPeriodTypeEnum.WEEK.getCode())) {
			return true;
		}

		CalendarPeriod grandparentCap = capService.findByCode(parentCap.getMyParent());

		List<CalendarPeriod> caps = capService.findByParentAndCapType(grandparentCap.getCode(), CalendarPeriodTypeEnum.WEEK.getCode());

		/* Do not create the weekly PC-40MI for the last week of the month. */
		return !parentCap.getNumberAsChild().equals(String.valueOf(caps.size()));
	}

	@Override
	protected String getCti(
			String pt,
			String gg,
			String dfdl,
			CalendarPeriod cap, CurriculumPeriod cup,
			BpLCP lcp,
			Map<String, List<BpPodProductOfDeal>> pods,
			Integer index
	) {
		if (!lcp.getCode().equals(Lcp.HRV_EA_AAX_AQR0_AA.getCode())) {
			return super.getCti(pt, gg, dfdl, cap, cup, lcp, pods, index);
		}

		BpPodProductOfDeal pod = pods.values().stream().flatMap(Collection::stream).findFirst().orElse(null);

		if (pod == null) {
			return null;
		}

		if (!cachedCtis.containsKey(pod.getMyst())) {
			/* Find 4 ctis and place into the cache. */
			List<String> ctis = cuiService.findHrvCtis(pt, gg, dfdl, cap.getCode(), pod.getMyst());

			cachedCtis.put(pod.getMyst(), ctis);
		}

		List<String> ctis = cachedCtis.get(pod.getMyst());
		String cti = ctis.remove(ctis.size() - 1); /* pop the last element. */

		if (ctis.isEmpty()) {
			cachedCtis.remove(pod.getMyst());
		}

		/* Get cti from cache. */
		return cti;
	}

	@Override
	public void scheduleWCAll(String pt, String gg, String dfdl, String cawk) {
		CalendarPeriod cap = capService.findByCodeAndCapType(cawk, CalendarPeriodTypeEnum.WEEK.getCode());

		BpLCP lcp = lcpService.findWcByPt(pt);

		CurriculumProgramPackage crpp = crppService.findByPtAndTime(pt, cap.getStartTime());

		CurriculumProgramSheet crps = crpsService.getCrps(crpp.getCode(), gg);

		CurriculumPeriod cup = cupService.getCUPByCAP(
				crps.getCode(),
				cawk,
				CurriculumPeriodEnum.CURR_WEEK.getCode()
		);

		schedule(
				cap,
				lcp,
				cup,
				pt,
				gg,
				dfdl
		);
	}

	@Override
	public void scheduleWC(CalendarPeriod cawk, String pt, String gg, String dfdl, BpPODCLAG podclag) {
		log.info("Start scheduleWc for podClag : {}", podclag.getCode());
		BpLCP lcp = lcpService.findWcByPt(pt);
		CurriculumProgramPackage crpp = crppService.findByPtAndTime(pt, cawk.getStartTime());

		CurriculumProgramSheet crps = crpsService.getCrps(crpp.getCode(), gg);

		CurriculumPeriod cup = cupService.getCUPByCAP(
				crps.getCode(),
				cawk.getCode(),
				CurriculumPeriodEnum.CURR_WEEK.getCode()
		);

		BpClagClassgroup clag = clagClassgroupService.findByCode(podclag.getMyclag());
		BpPodProductOfDeal pod = productOfDealService.findByCode(podclag.getMypod());
		if (Objects.nonNull(clag) && Objects.nonNull(pod)) {
			Map<String, List<BpPodProductOfDeal>> map = new HashMap<>();
			map.put(clag.getCode(), Collections.singletonList(pod));

			try {
				schedule(
						cawk,
						lcp,
						cup,
						pt,
						gg,
						dfdl,
						Collections.singletonList(clag),
						map
				);
			} catch (Exception e) {
				log.error("Error when schedule for podclag : {} with ERROR : {} ", podclag.getCode(), DebuggingDTO.build(e));
			}
			log.info("Schedule for podClag : {} success", podclag.getCode());
		}
	}
}
