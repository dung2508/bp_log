package vn.edu.clevai.bplog.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.common.SystemUser;
import vn.edu.clevai.bplog.common.enumtype.CurriculumPeriodEnum;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentType;
import vn.edu.clevai.bplog.enums.Cgbr;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.slack.SlackService;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@AllArgsConstructor
public abstract class BaseScheduleServiceImpl implements ScheduleService {
	private final CalendarPeriodService capService;

	private final BpLCPService lcpService;

	private final BpLctService lctService;


	private final CurriculumProgramPackageService crppService;

	private final BpUSIDutyService usidService;


	private final CurriculumProgramSheetService crpsService;

	private final CurriculumPeriodService cupService;

	private final BpClagClassgroupService clagService;


	private final BpPodProductOfDealService podService;

	private final SlackService slackService;

	private final BpULCService ulcService;

	private final CuiService cuiService;

	private final CuiEventService cuieService;

	@Override
	public void schedule(CalendarPeriod cap) {
		lcpService
				.findAllForSchedulingWc()
				.forEach(
						lcp -> {
							try {
								schedule(cap, lcp);
							} catch (Exception e) {
								log.error("Error in scheduling for cap = {}, lcp = {}", cap.getCode(), lcp.getCode(), e);
							}
						}
				);
	}

	@Override
	public void schedule(
			CalendarPeriod cap,
			BpLCP lcp
	) {
		BpLearningComponentType pkg = lctService.findByCode(lcp.getMylctparent());
		String pt = pkg.getMyPt();

		CurriculumProgramPackage crpp = crppService.findByPtAndTime(pt, cap.getStartTime());

		List<BpUsiDuty> configurations = new ArrayList<>(usidService.findAllConfigurationsByPtAndAccYearAndTerm(
				pkg.getMyPt(),
				crpp.getMyAccYear(),
				crpp.getMyTerm()
		)
				.stream()
				.collect(
						Collectors.toMap(
								p -> String.format(
										"%s-%s-%s-%s-%s",
										p.getMypt(),
										p.getMyaccyear(),
										p.getMyterm(),
										p.getMygg(),
										p.getMydfdl()
								),
								p -> p,
								(p, q) -> p
						)
				)
				.values()
		);

		if (configurations.isEmpty()) {
			throw new NotFoundException(
					"No configuration (usid with mybpp like %BPPRegister1% for"
							+ " pt = " + pkg.getMyPt()
							+ ", ay = " + crpp.getMyAccYear()
							+ ", term = " + crpp.getMyTerm()
							+ ", cap = " + cap.getCode()
			);
		}

		configurations.forEach(
				c -> {
					CurriculumProgramSheet crps = crpsService.getCrps(
							crpp.getCode(),
							c.getMygg()
					);

					CurriculumPeriod cup = cupService.getCUPByCAP(
							crps.getCode(),
							cap.getCode(),
							CurriculumPeriodEnum.CURR_WEEK.getCode()
					);

					schedule(cap, lcp, cup, pt, c.getMygg(), c.getMydfdl());
				}
		);

	}

	@Override
	public void schedule(
			CalendarPeriod cap,
			BpLCP lcp,
			CurriculumPeriod cup,
			String pt,
			String gg,
			String dfdl
	) {
		slackService.notifySlack(
				":red_circle: Started processing a schedule request with parameters: cap = " + cap.getCode()
						+ ", pt = " + pt
						+ ", lcp = " + lcp.getCode()
						+ ", gg = " + gg
						+ ", dfdl = " + dfdl
		);

		List<BpClagClassgroup> clags = clagService.findBy(
				pt,
				gg,
				dfdl,
				ClagType.PERMANANT.getCode()
		);

		Map<String, List<BpPodProductOfDeal>> pods = clags.stream().collect(
				Collectors.toMap(
						BpClagClassgroup::getCode,
						x -> podService.findActivePodsByClagAndCap(
								x.getCode(),
								cap.getCode(),
								Collections.singletonList(USTEnum.ST.getName())
						)
				)
		);

		clags.removeIf(c -> pods.get(c.getCode()).isEmpty());

		if (pods.isEmpty() || clags.isEmpty()) {
			/* No pod - no schedule. */
			String msg = String.format(
					"Ignored scheduling for cap = %s, lcp = %s, pt = %s, gg = %s, dfdl = %s because of no active pods at %s",
					cap.getCode(),
					lcp.getCode(),
					pt,
					gg,
					dfdl,
					cap.getStartTime()
			);

			log.warn(msg);

			slackService.notifySlack(":red_circle: " + msg);
		}

		schedule(cap, lcp, cup, pt, gg, dfdl, clags, pods);

		slackService.notifySlack(
				":red_circle: Finished processing scheduleMc with parameters: cap = " + cap.getCode()
						+ ", pt = " + pt
						+ ", lcp = " + lcp.getCode()
						+ ", gg = " + gg
						+ ", dfdl = " + dfdl
		);
	}

	@Override
	public void schedule(
			CalendarPeriod cap,
			BpLCP lcp,
			CurriculumPeriod cup,
			String pt,
			String gg,
			String dfdl,
			List<BpClagClassgroup> clags,
			Map<String, List<BpPodProductOfDeal>> pods
	) {
		String code = ulcService.generateUlcCode(
				lcp.getCode(),
				cap.getCode(),
				gg,
				dfdl,
				null,
				pt,
				null,
				null
		);

		schedule(cap, lcp, cup, pt, gg, dfdl, clags, pods, null, code, null, null);
	}

	@Override
	public void schedule(
			CalendarPeriod cap,
			BpLCP lcp,
			CurriculumPeriod cup,
			String pt,
			String gg,
			String dfdl,
			List<BpClagClassgroup> clags,
			Map<String, List<BpPodProductOfDeal>> pods,
			String parentCode,
			String code,
			Integer parentIndex,
			Integer index
	) {
		ulcService.setULC(
				parentCode,
				code,
				null,
				null,
				lcp.getMylct(),
				gg,
				pt,
				cap.getCode(),
				dfdl,
				null,
				lcp.getCode(),
				null,
				index,
				true
		);

		clags.forEach(clag -> ulcService.createOrUpdateClagUlc(clag.getCode(), code));

		String cti = getCti(pt, gg, dfdl, cap, cup, lcp, pods, index);

		cuieService.createCUIAndCuiEvent(
				code,
				SystemUser.AU.getCode(),
				cti, lcp.getCode(),
				null,
				cap.getCode()
		);

		pods.values().stream().flatMap(Collection::stream)
				.forEach(
						pod -> {
							cuiService.createCui(pod.getUsi().getCode(), null, code);

							cuieService.createCUIJoinEvent(code, pod.getUsi().getCode(),
									lcp.getCode(), null, cap.getCode());
						}
				);

		/* Find children and process. */
		List<BpLCP> children = lcpService.findByMylctparentToSchedule(lcp.getMylct());

		children.forEach(
				childLcp -> {
					if (shouldBeScheduled(childLcp, cap)) {
						CalendarPeriod childCap = getCap(cap, childLcp);

						schedule(cup, childCap, childLcp, pt, gg, dfdl, clags, pods, code, index);
					}
				}
		);
	}

	protected String getCti(
			String pt,
			String gg,
			String dfdl,
			CalendarPeriod cap,
			CurriculumPeriod cup,
			BpLCP lcp,
			Map<String, List<BpPodProductOfDeal>> pods,
			Integer index
	) {
		return cup == null ? null : cup.getMyCti();
	}

	private void schedule(
			CurriculumPeriod parentCup,
			CalendarPeriod childCap,
			BpLCP childLcp,
			String pt,
			String gg,
			String dfdl,
			List<BpClagClassgroup> clags,
			Map<String, List<BpPodProductOfDeal>> pods,
			String parentCode,
			Integer parentIndex
	) {
		if (childLcp.getNolcp() == null) {
			CurriculumPeriod cup = getCup(parentCup, dfdl, childLcp.getLcperiodno(), null);

			schedule(cup, childCap, childLcp, pt, gg, dfdl, clags, pods, parentCode, parentIndex, null);
		} else {
			IntStream
					.rangeClosed(1, childLcp.getNolcp())
					.forEach(
							index -> {
								CurriculumPeriod cup = getCup(parentCup, dfdl, childLcp.getLcperiodno(), index);

								schedule(cup, childCap, childLcp, pt, gg, dfdl, clags, pods, parentCode, parentIndex, index);
							}
					);
		}
	}

	private void schedule(
			CurriculumPeriod cup,
			CalendarPeriod cap,
			BpLCP lcp,
			String pt,
			String gg,
			String dfdl,
			List<BpClagClassgroup> clags,
			Map<String, List<BpPodProductOfDeal>> pods,
			String parentCode,
			Integer parentIndex,
			Integer index
	) {
		String baseCode = ulcService.generateUlcCode(
				lcp.getCode(),
				cap.getCode(),
				gg,
				dfdl,
				null,
				pt,
				parentIndex,
				index
		);

		Cgbr cgbr = Cgbr.valueOf(lcp.getCgbr());

		switch (cgbr) {
			case ACG:
				schedule(
						cap,
						lcp,
						cup,
						pt,
						gg,
						dfdl,
						clags,
						pods,
						parentCode,
						baseCode,
						parentIndex,
						index
				);
				break;
			case ECG:
				clags.forEach(
						clag -> {
							String code = baseCode + "-" + clag.getCode();

							Map<String, List<BpPodProductOfDeal>> clagPods = new HashMap<>();
							clagPods.put(clag.getCode(), pods.get(clag.getCode()));

							schedule(
									cap,
									lcp,
									cup,
									pt,
									gg,
									dfdl,
									Collections.singletonList(clag),
									clagPods,
									parentCode,
									code,
									parentIndex,
									index
							);
						}
				);
				break;
			case EPOD:
				pods.forEach(
						(k, v) -> {
							v.forEach(
									pod -> {
										String code = baseCode + "-" + pod.getMyst();

										Map<String, List<BpPodProductOfDeal>> clagPods = new HashMap<>();
										clagPods.put(k, Collections.singletonList(pod));

										schedule(
												cap,
												lcp,
												cup,
												pt,
												gg,
												dfdl,
												Collections.emptyList(),
												clagPods,
												parentCode,
												code,
												parentIndex,
												index
										);
									}
							);
						}
				);
				break;
		}
	}

	protected Boolean shouldBeScheduled(
			BpLCP childLcp,
			CalendarPeriod parentCap
	) {
		return true;
	}

	private CalendarPeriod getCap(CalendarPeriod parent, BpLCP lcp) {
		return Optional.ofNullable(
				capService.findByMyParentAndNumberAsChild(
						parent.getCode(),
						lcp.getLcperiodno()
				)
		).orElse(parent);
	}

	private CurriculumPeriod getCup(CurriculumPeriod parent, String dfdl, String cupNo, Integer mynoaschild) {
		if (parent == null) {
			return null;
		}

		return cupService.getCup(
				parent.getCode(),
				dfdl,
				cupNo,
				mynoaschild
		);
	}
}
