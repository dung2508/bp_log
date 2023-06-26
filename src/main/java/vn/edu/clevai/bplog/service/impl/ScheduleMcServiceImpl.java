package vn.edu.clevai.bplog.service.impl;

import jodd.net.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.common.enumtype.BpTaskStatusEnum;
import vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.CurriculumPeriodEnum;
import vn.edu.clevai.bplog.common.enumtype.ProductTypeEnum;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.enums.Lcp;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.repository.BpDfdlDifficultygradeRepository;
import vn.edu.clevai.bplog.repository.BpGGGradeGroupRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.model.MessageResponseDTO;
import vn.edu.clevai.common.api.slack.SlackService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ScheduleMcServiceImpl extends BaseScheduleServiceImpl implements ScheduleService, ScheduleMcService {
	private final CalendarPeriodService capService;

	private final BpLCPService lcpService;

	private final CurriculumProgramPackageService crppService;

	private final BpTaskInfoService bpTaskInfoService;

	private final BpGGGradeGroupRepository ggRepository;

	private final BpDfdlDifficultygradeRepository dfdlRepository;

	private final CurriculumPeriodService cupService;

	private final CurriculumProgramSheetService crpsService;

	private final SlackService slackService;

	private final BpClagClassgroupService clagClassgroupService;

	private final BpPodProductOfDealService productOfDealService;

	private final static Map<ProductTypeEnum, Lcp> myMapMN = new ConcurrentHashMap<>();

	static {
		myMapMN.put(ProductTypeEnum.BC, Lcp.PKBC_1PK_FDX_MC_1MN);
		myMapMN.put(ProductTypeEnum.PM, Lcp.PKPM_1PK_FDX_MC_1MN);
		myMapMN.put(ProductTypeEnum.PO, Lcp.PKPO_1PK_FDX_MC_1MN);
		myMapMN.put(ProductTypeEnum.TH, Lcp.PKTH_1PK_FDX_MC_1MN);
		myMapMN.put(ProductTypeEnum.OM, Lcp.PKOM_1PK_FDX_MC_1MN);
		myMapMN.put(ProductTypeEnum.OE, Lcp.PKOE_1PK_FDX_MC_1MN);
	}

	public ScheduleMcServiceImpl(
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
			BpTaskInfoService bpTaskInfoService,
			BpGGGradeGroupRepository ggService,
			BpDfdlDifficultygradeRepository dfdlRepository,
			BpClagClassgroupService clagClassgroupService, BpPodProductOfDealService productOfDealService) {
		super(capService, lcpService, lctService, crppService, usidService, crpsService, cupService, clagService, podService, slackService, ulcService, cuiService, cuieService);

		this.capService = capService;
		this.lcpService = lcpService;
		this.crppService = crppService;
		this.bpTaskInfoService = bpTaskInfoService;
		this.ggRepository = ggService;
		this.dfdlRepository = dfdlRepository;
		this.cupService = cupService;
		this.crpsService = crpsService;
		this.slackService = slackService;
		this.clagClassgroupService = clagClassgroupService;
		this.productOfDealService = productOfDealService;
	}

	@Override
	public void schedule(String camn) {
		schedule(capService.findByCodeAndCapType(camn, CalendarPeriodTypeEnum.MONTH.getCode()));
	}

	@Transactional
	@Override
	public MessageResponseDTO scheduleMcAll(ScheduleRequest scheduleRequest, CalendarPeriod camn) {


		List<String> ggCodes = scheduleRequest.getGgs();
		List<String> dfdlCodes = scheduleRequest.getDfdls();
		if (scheduleRequest.getGgs() == null || scheduleRequest.getGgs().isEmpty()) {
			ggCodes = ggRepository.findAllByPublishedTrue().stream().map(BpGGGradeGroup::getCode).collect(Collectors.toList());
		}
		if (scheduleRequest.getDfdls() == null || scheduleRequest.getDfdls().isEmpty()) {
			dfdlCodes = dfdlRepository.findAllByPublishedTrue().stream().map(BpDfdlDifficultygrade::getCode).collect(Collectors.toList());
		}
		boolean isDone = false; // check schedule done
		boolean isFirstTime = true; // kiểm tra là lần đầu
		boolean isError = false;
		for (String pt : scheduleRequest.getPt()) {
			String lcp = null;
			Set<ProductTypeEnum> keySet = myMapMN.keySet();
			for (ProductTypeEnum key : keySet) {
				if (key.getName().equals(pt)) {
					lcp = myMapMN.get(key).getCode();
				}
			}
			for (String gg : ggCodes) {
				for (String dfdl : dfdlCodes) {
					String taskName = String.format(
							"scheduleCamn-%s-%s-%s-%s",
							pt, gg, dfdl, camn.getCode()
					);
					isDone = false;
					if (!StringUtils.isEmpty(bpTaskInfoService.getTaskInfo(taskName))) {
						isFirstTime = false;
					}
					if (BpTaskStatusEnum.PROCESSING.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
						throw new BadRequestException("A task for " + taskName + " is processing!!!");
					}
					if (BpTaskStatusEnum.DONE.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
						isDone = true;
					}
					if (BpTaskStatusEnum.ERROR.name().equals(bpTaskInfoService.getTaskInfo(taskName))) {
						isError = true;
						slackService.notifySlack(
								":red_circle: Error scheduleMc with parameters: cap = " + scheduleRequest.getCapCode()
										+ ", pt = " + pt
										+ ", lcp = " + lcp
										+ ", gg = " + gg
										+ ", dfdl = " + dfdl
						);
					}
				}
			}
		}

		if (isDone || isFirstTime || isError) {
			for (String pt : scheduleRequest.getPt()) {
				String lcp = null;
				Set<ProductTypeEnum> keySet = myMapMN.keySet();
				for (ProductTypeEnum key : keySet) {
					if (key.getName().equals(pt)) {
						lcp = myMapMN.get(key).getCode();
					}
				}
				for (String gg : ggCodes) {
					for (String dfdl : dfdlCodes) {
						String finalLcp = lcp;
						CompletableFuture.runAsync(
								() -> {
									String taskName = String.format(
											"scheduleCamn-%s-%s-%s-%s",
											pt, gg, dfdl, camn.getCode()
									);
									try {
										bpTaskInfoService.create(taskName, BpTaskStatusEnum.PROCESSING, null);
										CurriculumProgramPackage crpp = crppService.findByPtAndTime(pt, camn.getStartTime());

										CurriculumProgramSheet crps = crpsService.getCrps(crpp.getCode(), gg);

										CurriculumPeriod cup = cupService.getCUPByCAP(
												crps.getCode(),
												camn.getCode(),
												CurriculumPeriodEnum.CURR_MONTH.getCode()
										);

										schedule(
												camn,
												lcpService.findByCode(finalLcp),
												cup,
												pt,
												gg,
												dfdl
										);

										bpTaskInfoService.create(taskName, BpTaskStatusEnum.DONE, null);
									} catch (Exception e) {
										log.error("Got error: {} when {}", e.getLocalizedMessage(), taskName, e);

										bpTaskInfoService.create(taskName, BpTaskStatusEnum.ERROR, e.getLocalizedMessage());
									}
								}
						);
					}
				}
			}
			return MessageResponseDTO.builder()
					.code(HttpStatus.ok().status())
					.message("OK").build();
		}
		return null;
	}

	@Override
	public void scheduleMC(CalendarPeriod capmn, String pt, String gg, String dfdl, BpPODCLAG podclag) {
		log.info("Start rescheduleMC for podClag : {} ", podclag.getCode());
		CurriculumProgramPackage crpp = crppService.findByPtAndTime(pt, capmn.getStartTime());
		String lcp = null;
		Set<ProductTypeEnum> keySet = myMapMN.keySet();
		for (ProductTypeEnum key : keySet) {
			if (key.getName().equals(pt)) {
				lcp = myMapMN.get(key).getCode();
			}
		}

		CurriculumProgramSheet crps = crpsService.getCrps(crpp.getCode(), gg);

		CurriculumPeriod cup = cupService.getCUPByCAP(
				crps.getCode(),
				capmn.getCode(),
				CurriculumPeriodEnum.CURR_MONTH.getCode()
		);

		BpClagClassgroup clag = clagClassgroupService.findByCode(podclag.getMyclag());
		BpPodProductOfDeal pod = productOfDealService.findByCode(podclag.getMypod());
		if (Objects.nonNull(clag) && Objects.nonNull(pod)) {
			Map<String, List<BpPodProductOfDeal>> map = new HashMap<>();
			map.put(clag.getCode(), Collections.singletonList(pod));

			try {
				schedule(
						capmn,
						lcpService.findByCode(lcp),
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
