package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.ModifyTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.ProductTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.UsiTypeEnum;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.entity.logDb.BpCuiContentUserUlc;
import vn.edu.clevai.bplog.entity.logDb.BpCuiEvent;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.util.DateUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModifyServiceImpl implements ModifyService {
	private final BpClagPODService clagPODService;

	private final BpPodProductOfDealService bpPodProductOfDealService;

	private final BpClagClassgroupService clagClassgroupService;

	private final BpUniqueLearningComponentRepository bpUniqueLearningComponentRepository;

	private final BpULCService ulcService;

	private final CalendarPeriodService calendarPeriodService;

	private final BpWsoWeeklyscheduleoptionService wsoWeeklyscheduleoptionService;

	private final ScheduleWcService scheduleWcService;

	private final ScheduleMcService scheduleMcService;

	private final CuiService cuiService;

	private final CuiEventService cuiEventService;

	private final BpUsiUserItemService userItemService;

	private final ZoomMeetingService zoomMeetingService;


	public ModifyServiceImpl(BpClagPODService clagPODService,
							 BpPodProductOfDealService bpPodProductOfDealService,
							 @Lazy BpClagClassgroupService clagClassgroupService,
							 @Lazy BpUniqueLearningComponentRepository bpUniqueLearningComponentRepository,
							 @Lazy BpULCService ulcService,
							 @Lazy CalendarPeriodService calendarPeriodService,
							 @Lazy BpWsoWeeklyscheduleoptionService wsoWeeklyscheduleoptionService,
							 @Lazy ScheduleWcService scheduleWcService,
							 @Lazy ScheduleMcService scheduleMcService,
							 @Lazy CuiService cuiService,
							 @Lazy CuiEventService cuiEventService,
							 @Lazy BpUsiUserItemService userItemService,
							 @Lazy ZoomMeetingService zoomMeetingService) {
		this.clagPODService = clagPODService;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.clagClassgroupService = clagClassgroupService;
		this.bpUniqueLearningComponentRepository = bpUniqueLearningComponentRepository;
		this.ulcService = ulcService;
		this.calendarPeriodService = calendarPeriodService;
		this.wsoWeeklyscheduleoptionService = wsoWeeklyscheduleoptionService;
		this.scheduleWcService = scheduleWcService;
		this.scheduleMcService = scheduleMcService;
		this.cuiService = cuiService;
		this.cuiEventService = cuiEventService;
		this.userItemService = userItemService;
		this.zoomMeetingService = zoomMeetingService;
	}


	@Override
	public void bppSyncST(String oldPodClagCode, String newPodClagCode, String modifyType) {

		BpPODCLAG newPodClag = clagPODService.findByCode(newPodClagCode);
		BpPodProductOfDeal newPod = bpPodProductOfDealService.findByCode(Objects.nonNull(newPodClag) ? newPodClag.getMypod() : null);
		BpClagClassgroup newClag = clagClassgroupService.findByCode(Objects.nonNull(newPodClag) ? newPodClag.getMyclag() : null);

		if (Arrays.asList(ModifyTypeEnum.REFUND.getName(),
						ModifyTypeEnum.SUSPEND.getName(),
						ModifyTypeEnum.DEFER_BEFORE_SIGNUP.getName(),
						ModifyTypeEnum.DEFER_AFTER_SIGNUP.getName(),
						ModifyTypeEnum.PASS.getName(),
						ModifyTypeEnum.RENEW_TRANFER.getName())
				.contains(modifyType)) {
			log.info("Start modify student for usi :{} ", newPod.getUsi().getCode());
			unpublishedUnnecessaryCui(newPod.getMyst(), DateUtils.now(), null, false);
			log.info("modify student success for usi :{}", newPod.getUsi().getCode());
		}

		if (Objects.equals(ModifyTypeEnum.UNSUSPEND.getName(), modifyType)) {
			log.info("Start modify student for usi :{} ", newPod.getUsi().getCode());
			reschedule(newPod, newClag, newPodClag, null,
					Collections.singletonList(calendarPeriodService.getCAPByTime(DateUtils.now(), CalendarPeriodTypeEnum.WEEK.getCode())),
					Collections.singletonList(calendarPeriodService.getCAPByTime(DateUtils.now(), CalendarPeriodTypeEnum.MONTH.getCode())));
			// rescheduleMP
			if (newPod.getMypt().equals(ProductTypeEnum.OM.getName())) {
				try {
					reScheduleMpForOM(newPod.getMyst(), newPodClag);
				} catch (Exception e) {
					log.error("ERROR When scheduleMP for OM : {}", DebuggingDTO.build(e));
				}
			}
			log.info("modify student success for usi :{}", newPod.getUsi().getCode());
		}
		if (Objects.equals(ModifyTypeEnum.PURCHASE.getName(), modifyType)) {
			log.info("Start modify student for usi :{} ", newPod.getUsi().getCode());
			reschedule(newPod, newClag, newPodClag, null,
					Collections.singletonList(calendarPeriodService.getCAPByTime(newPodClag.getAssignedAt(), CalendarPeriodTypeEnum.WEEK.getCode())),
					Collections.singletonList(calendarPeriodService.getCAPByTime(newPodClag.getAssignedAt(), CalendarPeriodTypeEnum.MONTH.getCode())));
			// rescheduleMP
			if (newPod.getMypt().equals(ProductTypeEnum.OM.getName())) {
				try {
					reScheduleMpForOM(newPod.getMyst(), newPodClag);
				} catch (Exception e) {
					log.error("ERROR When scheduleMP for OM : {}", DebuggingDTO.build(e));
				}
			}
			log.info("modify student success for usi :{}", newPod.getUsi().getCode());
		}
		if (Objects.equals(ModifyTypeEnum.CHANGE_CIB.getName(), modifyType)) {
			log.info("Start modify student for usi :{} ", newPod.getUsi().getCode());
			unpublishedUnnecessaryCui(newPod.getMyst(), DateUtils.now(), null, false);
			reschedule(newPod, newClag, newPodClag, null, null, null);

			// rescheduleMP
			if (newPod.getMypt().equals(ProductTypeEnum.OM.getName())) {
				try {
					reScheduleMpForOM(newPod.getMyst(), newPodClag);
				} catch (Exception e) {
					log.error("ERROR When scheduleMP for OM : {}", DebuggingDTO.build(e));
				}
			}
			log.info("modify student success for usi :{}", newPod.getUsi().getCode());
		}
	}

	@Override
	public void bppSyncTE(String clagCode, String oldUsi, String newUsi, String cady) {

		log.info("Start modify teacher  for clag :{} , oldUsi:{} , newUsi :{}, cady :{} ", clagCode, oldUsi, newUsi, cady);
		CalendarPeriod cap = calendarPeriodService.findByCode(cady);
		BpClagClassgroup clag = clagClassgroupService.findByCode(clagCode);
		if (Objects.isNull(clag)) {
			throw new NotFoundException("Coun't find clag  by clag_code : " + clagCode);
		}
		BpPodProductOfDeal oldPod = bpPodProductOfDealService.findByUsi(oldUsi, clag.getMypt());
		if (Objects.isNull(oldPod)) {
			throw new NotFoundException("Coun't find pod  by usi : " + oldUsi + "and pt : " + clag.getMypt());
		}
		BpPodProductOfDeal newPod = bpPodProductOfDealService.findByUsi(newUsi, clag.getMypt());
		if (Objects.isNull(newPod)) {
			throw new NotFoundException("Coun't find pod  by usi : " + newUsi + "and pt : " + clag.getMypt());
		}
		clagPODService.findByPodAndClagAndPublishedAndCady(oldPod.getCode(), clag.getCode(), true, cap)
				.forEach(old -> {
					clagPODService.unpublishedPodClag(old);
					clagPODService.save(BpPODCLAG.builder()
							.active(true)
							.assignedAt(old.getAssignedAt())
							.unAssignedAt(old.getUnAssignedAt())
							.memberType(clag.getClagtype())
							.mypod(newPod.getCode())
							.myclag(clagCode)
							.myUst(newPod.getUsi().getMyust())
							.code(clagPODService.getPodClagCode(newPod.getCode(), clagCode, old.getAssignedAt(), old.getUnAssignedAt()))
							.build());
				});
		List<BpCuiEvent> oldCuiEventList = cuiEventService
				.findCuieByCapAndClagAndUsi(oldPod.getMyst(), clag.getCode(), cap.getStartTime(), cap.getEndTime());
		List<BpCuiContentUserUlc> oldCuiList = cuiService
				.findCuiByCapAndClagAndUsi(oldPod.getMyst(), clag.getCode(), cap.getStartTime(), cap.getEndTime());
		oldCuiEventList.forEach(e -> e.setPublished(false));
		oldCuiList.forEach(e -> e.setPublished(false));
		cuiEventService.saveAll(oldCuiEventList);
		cuiService.saveAll(oldCuiList);
		List<BpCuiContentUserUlc> newCuiList = new ArrayList<>();
		List<BpCuiEvent> newCuiEventList = new ArrayList<>();
		oldCuiList.forEach(cui -> {
			BpCuiContentUserUlc cuiContentUserUlc = BpCuiContentUserUlc.builder()
					.myUlc(cui.getMyUlc())
					.myUlcCode(cui.getMyUlcCode())
					.myUsi(newPod.getUsi())
					.myCti(cui.getMyCti())
					.myCtiCode(cui.getMyCtiCode())
					.description("Giáo viên dạy thay cho " + cui.getMyUsi().getCode())
					.myPodp(cui.getMyPodp())
					.published(true)
					.mybps(cui.getMybps())
					.publishbps(cui.getPublishbps())
					.unpublishbps(cui.getUnpublishbps())
					.code(cui.getMyUlcCode().concat("-").concat(newPod.getMyst()))
					.build();
			newCuiList.add(cuiContentUserUlc);
			BpCuiEvent cuie = oldCuiEventList.stream().filter(e ->
					e.getMyCui().getCode().equals(cui.getCode())).findFirst().orElse(null);
			if (Objects.isNull(cuie)) return;
			newCuiEventList.add(BpCuiEvent.builder()
					.myCui(cuiContentUserUlc)
					.myLcet(cuie.getMyLcet())
					.eventPlanTime(cuie.getEventPlanTime())
					.eventActualTimeFet(cuie.getEventActualTimeFet())
					.eventActualTimeBet(cuie.getEventActualTimeBet())
					.triggerAt(cuie.getTriggerAt())
					.published(true)
					.mycui(cui.getMyUlcCode().concat("-").concat(newPod.getMyst()))
					.value1(cuie.getValue1())
					.planbpe(cuie.getPlanbpe())
					.publishbpe(cuie.getPublishbpe())
					.unpublishbpe(cuie.getUnpublishbpe())
					.myUsi(cuiContentUserUlc.getMyUsi().getCode())
					.code(String.join("-", cuiContentUserUlc.getCode(), cuie.getMyLcet() + "_" + System.currentTimeMillis()))
					.build());
		});
		cuiService.saveAll(newCuiList);
		cuiEventService.saveAll(newCuiEventList);
		// zoom meeting for GTE
		if (ClagType.DYNAMIC.getCode().equals(clag.getClagtype())
				&& UsiTypeEnum.GTE.getName().equals(newPod.getUsi().getMyust())
				&& UsiTypeEnum.GTE.getName().equals(oldPod.getUsi().getMyust())) {
			newCuiList.forEach(cui -> {
				try {
					zoomMeetingService.createAndAssign(newUsi, cui.getMyUlc().getCode(), clagCode);
				} catch (Exception e) {
					log.error("Error when create usi zoom for new usi:{}", DebuggingDTO.build(e));
				}
			});
		}
		// convert to X
		log.info("Start convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{}", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
		ulcService.convertBpToX(ScheduleRequest.builder()
				.cady(cap.getCode())
				.pt(new ArrayList<>(Collections.singletonList(clag.getMypt())))
				.ggs(new ArrayList<>(Collections.singletonList(clag.getMygg())))
				.dfdls(new ArrayList<>(Collections.singletonList(clag.getMydfdl())))
				.build());
		log.info("Convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{} success", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
		log.info("modify teacher with newUsi :{} and oldUsi:{} success", newUsi, oldUsi);
	}

	@Override
	public void bppSyncTE(String oldUsi, String newUsi, CalendarPeriod cady, String pt) {
		BpPodProductOfDeal oldPod = bpPodProductOfDealService.findByUsi(oldUsi, pt);
		if (Objects.isNull(oldPod)) {
			throw new NotFoundException("Not found pod by usi: " + oldUsi + "and pt: " + pt);
		}
		clagPODService.findByPodAndCap(oldPod.getCode(), cady.getStartTime(), cady.getEndTime(), true)
				.forEach(podClag -> {
					bppSyncTE(podClag.getMyclag(), oldUsi, newUsi, cady.getCode());
				});
	}

	public List<CalendarPeriod> reCalculateShChangedCIBStudent(
			String usi,
			String newClag, String newPod) {
		log.info("Start recaculate coming up learning day of student :{} ", usi);
		List<CalendarPeriod> cadyList;
		// new Clag
		cadyList = calendarPeriodService.getCadyListScheduledOfClag(newClag);
		if (!cadyList.isEmpty()) {
			return cadyList;
		}
		// old Clag
		Timestamp lastCapSchedule = bpUniqueLearningComponentRepository
				.findLastScheduleByUsi(usi).orElse(null);
		if (Objects.isNull(lastCapSchedule)) {
			return cadyList;
		}
		BpWsoWeeklyscheduleoption wso = wsoWeeklyscheduleoptionService.getPOD_WSO(newPod);
		if (Objects.isNull(wso)) {
			log.error("Coun't find wso of pod : {}", newPod);
			return cadyList;
		}
		List<CalendarPeriod> calendarPeriods = getCadyFromWsoAndCawk(DateUtils.now(), lastCapSchedule, wso);
		cadyList = calendarPeriods.stream().filter(Objects::nonNull).collect(Collectors.toList());
		return cadyList;
	}

	public List<CalendarPeriod> reCalculateWcOrMcChangedCIBStudent(
			String usi,
			Timestamp from,
			Timestamp to,
			String pt, String gg, String dfdl,
			String capType) {
		return calendarPeriodService
				.findCapListScheduledForEPOD(
						usi, from, to,
						pt, gg, dfdl,
						capType);
	}

	public List<CalendarPeriod> getCadyFromWsoAndCawk(Timestamp from, Timestamp to, BpWsoWeeklyscheduleoption wso) {
		List<Integer> wsoSplit = wso.getCode().chars().map(Character::getNumericValue).boxed().collect(Collectors.toList());
		return calendarPeriodService.getCadyFromWsoAndCawk(from, to, wsoSplit);
	}

	public void reScheduleMpForOM(String usi, BpPODCLAG podclag) throws Exception {
		BpUsiUserItem userItem = userItemService.findByCode(usi);
		log.info("Reset schedule MO Flag for user {}", userItem.getCode());
		cuiEventService.unpublishedCuieUnnecessaryForScheduleMPForOM(usi, false);
		cuiService.unpublishedCuiUnnecessaryForScheduleMPForOM(usi, false);
		userItem.setScheduleOM(false);
		ulcService.scheduleMPForOM(podclag.getMyclag(), podclag.getMypod());
		log.info("Reset schedule MO Flag for user {} success", userItem.getCode());
	}

	public void unpublishedUnnecessaryCui(String usi, Timestamp from, Timestamp to, boolean published) {
		cuiEventService.unpublishedCuieUnnecessaryOfModifyStudent(
				usi, DateUtils.now(), null, false);
		cuiService.unpublishedCuiUnnecessaryOfModifyStudent(
				usi, DateUtils.now(), null, false);
	}

	public void reschedule(BpPodProductOfDeal pod,
						   BpClagClassgroup clag,
						   BpPODCLAG newPodClag,
						   List<CalendarPeriod> cadys,
						   List<CalendarPeriod> cawks,
						   List<CalendarPeriod> camns) {
		Map<String, List<BpPodProductOfDeal>> map = new HashMap<>();
		map.put(clag.getCode(), Collections.singletonList(pod));

		List<CalendarPeriod> cadyList = Objects.isNull(cadys) ?
				reCalculateShChangedCIBStudent(pod.getMyst(), clag.getCode(), pod.getCode()) : cadys;
		List<CalendarPeriod> cawkList = Objects.nonNull(cawks) ? cawks : reCalculateWcOrMcChangedCIBStudent(
				pod.getMyst(), DateUtils.now(), null,
				null, null, null,
				CalendarPeriodTypeEnum.WEEK.getCode());
		List<CalendarPeriod> camnList = Objects.nonNull(camns) ? camns : reCalculateWcOrMcChangedCIBStudent(
				pod.getMyst(), DateUtils.now(), null,
				null, null, null,
				CalendarPeriodTypeEnum.MONTH.getCode());

		// rescheduleShift
		cadyList.forEach(cady -> {
			ulcService.scheduleShiftForPodClag(clag.getMypt(), clag.getMygg(), clag.getMydfdl(),
					cady.getStartTime(), null, Collections.singletonList(clag), map);
		});
		// rescheduleWC
		cawkList.forEach(cawk -> {
			scheduleWcService.scheduleWC(cawk, clag.getMypt(), clag.getMygg(), clag.getMydfdl(), newPodClag);
		});
		// rescheduleMC
		camnList.forEach(camn -> {
			scheduleMcService.scheduleMC(camn, clag.getMypt(), clag.getMygg(), clag.getMydfdl(), newPodClag);
		});

		// convert BP To X
		// @TODO
		try {
			cadyList.forEach(cap -> {
				log.info("Start convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{}", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
				ulcService.convertBpToX(ScheduleRequest.builder()
						.cady(cap.getCode())
						.pt(new ArrayList<>(Collections.singletonList(clag.getMypt())))
						.ggs(new ArrayList<>(Collections.singletonList(clag.getMygg())))
						.dfdls(new ArrayList<>(Collections.singletonList(clag.getMydfdl())))
						.build());
				log.info("Convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{} success", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
			});
			cawkList.forEach(cap -> {
				log.info("Start convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{}", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
				ulcService.convertBpToXWeek(ScheduleRequest.builder()
						.capCode(cap.getCode())
						.pt(new ArrayList<>(Collections.singletonList(clag.getMypt())))
						.ggs(new ArrayList<>(Collections.singletonList(clag.getMygg())))
						.dfdls(new ArrayList<>(Collections.singletonList(clag.getMydfdl())))
						.build());
				log.info("Convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{} success", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
			});
			camnList.forEach(cap -> {
				log.info("Start convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{}", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
				ulcService.convertBpToXMonth(ScheduleRequest.builder()
						.capCode(cap.getCode())
						.pt(new ArrayList<>(Collections.singletonList(clag.getMypt())))
						.ggs(new ArrayList<>(Collections.singletonList(clag.getMygg())))
						.dfdls(new ArrayList<>(Collections.singletonList(clag.getMydfdl())))
						.build());
				log.info("Convert BP to X for cap:{} , pt:{}, gg:{} , dfdl:{} success", cap.getCode(), clag.getMypt(), clag.getMygg(), clag.getMydfdl());
			});
		} catch (Exception e) {
			log.error("ERROR when convert Bp to X for pt:{}, gg:{}, dfdl:{} with error :{} ", clag.getMypt(), clag.getMygg(), clag.getMydfdl(), DebuggingDTO.build(e));
		}
	}
}
