package vn.edu.clevai.bplog.service.cuievent.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.*;
import vn.edu.clevai.bplog.dto.bp.ValueDto;
import vn.edu.clevai.bplog.dto.redis.BpeEventDTO;
import vn.edu.clevai.bplog.dto.redis.BpsStepDTO;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpCuiContentUserUlc;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.entity.projection.AnswerAndQuestionPJ;
import vn.edu.clevai.bplog.entity.projection.LcpCodePJ;
import vn.edu.clevai.bplog.entity.projection.ScheduleMonthCalendarPJ;
import vn.edu.clevai.bplog.payload.request.bp.*;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.payload.response.cti.LeaningObjectResponse;
import vn.edu.clevai.bplog.payload.response.student.StudentLearningPackageResponse;
import vn.edu.clevai.bplog.queue.RedisMessagePublisher;
import vn.edu.clevai.bplog.repository.BpLCPRepository;
import vn.edu.clevai.bplog.repository.bplog.BpCtiContentItemRepository;
import vn.edu.clevai.bplog.repository.bplog.BpCuiContentUserUlcRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.api.util.ListUtils;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectDetailResponse;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectWithRewardResponse;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningProgramLearningObjectResponse;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static vn.edu.clevai.bplog.common.enumtype.BpsStepTypeEnum.SET_PUBLISH_CUIS;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuiServiceImpl implements CuiService {

	private final BpCtiContentItemRepository bpCtiContentItemRepository;
	private final BpCuiContentUserUlcRepository cuiRepo;

	private final BpPodProductOfDealService podService;
	private final BpULCService bpULCService;
	private final BpUsiUserItemService userItemService;
	private final CuiEventService cuiEventService;
	private final BpBpsStepService bpBpsStepService;
	private final BpBpeEventService bpBpeEventService;
	private final CalendarPeriodService capService;

	private final RedisMessagePublisher publisher;

	private final LocalValueSaving valueSaving;

	private final ContentItemService contentItemService;

	private final ModelMapper mapper;
	private final ListUtils listUtils;

	private final BpLCPService lcpService;

	private final BpLctService lctService;

	private final AuthoringService authoringService;
	private final LmsService lmsService;
	private final BpLCPRepository bpLCPRepository;
	private final int ONE_DAY_IN_SECONDS = 60 * 60 * 24;


//	private final List<String> LCP_GET_CONTENT_SCHEDULE_MONTH = Arrays.asList(
//			Lcp.PKOM_1PK_FDX_MC_1MN.getCode(),
//			Lcp.MC_1MN_ED1_PC_40MI.getCode(),
//			Lcp.PC_40MI_AAX_CQR_AA.getCode(),
//			Lcp.CQR_AA_AAX_CQS_AA.getCode()
//	);

	@Override
	public void scheduleMyCUI(BPUlcRequest ulcReq, BPScheduleUssRequest sussReq) {
		BPCuiRequest cuiReq = BPCuiRequest.builder().build();
		BPCuiEventRequest cuieReq = BPCuiEventRequest.builder().build();

		try {
			cuiReq.setCuiPublished(ulcReq.getUlcPublished());
			createMainCUI(ulcReq, sussReq, cuiReq);
			cuieReq.setCuieCuicode(cuiReq.getCuiCuicode());
			cuieReq.setCuieMyusi(cuiReq.getCuiUsicode());
			cuieReq.setCuieMylcet("DR-JN-JRQ"); // Hard code
			cuieReq.setCuieMylcp(cuiReq.getCuiMylcp());
			cuieReq.setCuiePlantime(sussReq.getSussCassStartPeriod());
			cuieReq.setCuiePublished(cuiReq.getCuiPublished());
		} catch (Exception e) {
			log.error("CREATE MAIN CUI ERROR {}", DebuggingDTO.build(e));
		}

		try {
			cuiEventService.planCuiEvents(cuieReq, sussReq);
		} catch (Exception e) {
			log.error("planCuiEvents ERROR {}", DebuggingDTO.build(e));
		}


		try {
			String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_USS_OF_TO);
			valueSaving.setBpsCode(bpsCode, false);
			String bpeCode = bpBpeEventService.generateBpeCode(BpeEventTypeEnum.CREATE_CUIE_JOIN_REQUEST_PLAINTIME_OF_TO);
			valueSaving.setBpeCode(bpeCode, false);
			createUserCUI(ulcReq, sussReq, cuiReq, cuieReq, "TO", sussReq.getSussTo());
			if (bpsCode.equals(valueSaving.getBpsCode())) {
				publisher.publish(BpsStepDTO.builder()
						.name(bpsCode)
						.code(bpsCode)
						.bpstype(BpsStepTypeEnum.CREATE_CUI_USS_OF_TO.getCode())
						.myprocess(valueSaving.getBppCode())
						.build());
			}
			if (bpeCode.equals(valueSaving.getBpeCode())) {
				publisher.publish(BpeEventDTO.builder()
						.name(bpeCode)
						.code(bpeCode)
						.bpetype(BpeEventTypeEnum.CREATE_CUIE_JOIN_REQUEST_PLAINTIME_OF_TO.getCode())
						.mybps(valueSaving.getBpsCode())
						.build());
			}
			valueSaving.setBpsCode(null, false);
			valueSaving.setBpeCode(null, false);
		} catch (Exception e) {
			log.error("CREATE TO CUI ERROR {}", DebuggingDTO.build(e));
		}

		try {
			String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_USS_OF_CO);
			valueSaving.setBpsCode(bpsCode, false);
			String bpeCode = bpBpeEventService.generateBpeCode(BpeEventTypeEnum.CREATE_CUIE_JOIN_REQUEST_PLAINTIME_OF_CO);
			valueSaving.setBpeCode(bpeCode, false);
			createUserCUI(ulcReq, sussReq, cuiReq, cuieReq, "CO", sussReq.getSussCo());
			if (bpsCode.equals(valueSaving.getBpsCode())) {
				publisher.publish(BpsStepDTO.builder()
						.name(bpsCode)
						.code(bpsCode)
						.bpstype(BpsStepTypeEnum.CREATE_CUI_USS_OF_CO.getCode())
						.myprocess(valueSaving.getBppCode())
						.build());
			}
			if (bpeCode.equals(valueSaving.getBpeCode())) {
				publisher.publish(BpeEventDTO.builder()
						.name(bpeCode)
						.code(bpeCode)
						.bpetype(BpeEventTypeEnum.CREATE_CUIE_JOIN_REQUEST_PLAINTIME_OF_CO.getCode())
						.mybps(valueSaving.getBpsCode())
						.build());
			}
			valueSaving.setBpsCode(null, false);
			valueSaving.setBpeCode(null, false);
		} catch (Exception e) {
			log.error("CREATE CO CUI ERROR {}", DebuggingDTO.build(e));
		}

		Set<String> teList = new HashSet<>();
		List<String> clagList = new ArrayList<>();
		sussReq.getSussClagList().forEach(c -> {
			teList.add(c.getClagTe());
			clagList.add(c.getClagCode());
		});

		try {
			String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_USS_OF_TE);
			valueSaving.setBpsCode(bpsCode, false);
			String bpeCode = bpBpeEventService.generateBpeCode(BpeEventTypeEnum.CREATE_CUIE_JOIN_REQUEST_PLAINTIME_OF_TE);
			valueSaving.setBpeCode(bpeCode, false);
			createUserCUI(ulcReq, sussReq, cuiReq, cuieReq, "TE", teList);
			if (bpsCode.equals(valueSaving.getBpsCode())) {
				publisher.publish(BpsStepDTO.builder()
						.name(bpsCode)
						.code(bpsCode)
						.bpstype(BpsStepTypeEnum.CREATE_CUI_USS_OF_TE.getCode())
						.myprocess(valueSaving.getBppCode())
						.build());
				if (bpeCode.equals(valueSaving.getBpeCode())) {
					publisher.publish(BpeEventDTO.builder()
							.name(bpeCode)
							.code(bpeCode)
							.bpetype(BpeEventTypeEnum.CREATE_CUIE_JOIN_REQUEST_PLAINTIME_OF_TE.getCode())
							.mybps(valueSaving.getBpsCode())
							.build());
				}
			}
			valueSaving.setBpsCode(null, false);
			valueSaving.setBpeCode(null, false);
		} catch (Exception e) {
			log.error("CREATE TO CUI ERROR {}", DebuggingDTO.build(e));
		}

		try {
			// Create cui and cuievent for student
			Set<String> students = podService.findByClagsAndDate(clagList, new Date())
					.stream().map(BpPodProductOfDeal::getMyst).collect(Collectors.toSet());
			String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.CREATE_CUI_USS_OF_ST);
			valueSaving.setBpsCode(bpsCode, false);
			String bpeCode = bpBpeEventService.generateBpeCode(BpeEventTypeEnum.CREATE_CUIE_PLAINTIME_OF_ST);
			valueSaving.setBpeCode(bpeCode, false);
			createUserCUI(ulcReq, sussReq, cuiReq, cuieReq, "ST", students);
			if (bpsCode.equals(valueSaving.getBpsCode())) {
				publisher.publish(BpsStepDTO.builder()
						.name(bpsCode)
						.code(bpsCode)
						.bpstype(BpsStepTypeEnum.CREATE_CUI_USS_OF_ST.getCode())
						.myprocess(valueSaving.getBppCode())
						.build());
			}
			if (bpeCode.equals(valueSaving.getBpeCode())) {
				publisher.publish(BpeEventDTO.builder()
						.name(bpeCode)
						.code(bpeCode)
						.bpetype(BpeEventTypeEnum.CREATE_CUIE_PLAINTIME_OF_ST.getCode())
						.mybps(valueSaving.getBpsCode())
						.build());
			}
			valueSaving.setBpsCode(null, false);
			valueSaving.setBpeCode(null, false);
		} catch (Exception e) {
			log.error("CREATE ST CUI ERROR {}", DebuggingDTO.build(e));
		}

	}

	@Override
	@Transactional
	public void setPublishedCUIs(List<String> ushChildCodes, boolean published) {
		List<BpCuiContentUserUlc> userUlcs = cuiRepo.findByMyulcIn(ushChildCodes);
		Map<String, String> mapUshChildBpp = valueSaving.getLocal().get().getMapUshBpp();
		Map<String, String> mapCuiBPS = new HashMap<>();

		List<String> ulcCodes = new ArrayList<>();
		userUlcs.forEach(u -> {
			String bpsCode = bpBpsStepService.generateBpsCode(SET_PUBLISH_CUIS);
			mapCuiBPS.put(u.getCode(), bpsCode);

			publisher.publish(BpsStepDTO.builder()
					.bpstype(SET_PUBLISH_CUIS.getCode())
					.myprocess(mapUshChildBpp.get(u.getCode()))
					.code(bpsCode)
					.build());
			u.setPublished(published);
			if (published) {
				u.setPublishbps(bpsCode);
			} else {
				u.setUnpublishbps(bpsCode);
			}
			ulcCodes.add(u.getCode());

		});

		valueSaving.doClean();
		valueSaving.getLocal().set(ValueDto.builder().mapCuiBps(mapCuiBPS).build());
		cuiEventService.setPublishedCUIEvents(ulcCodes, published);
	}

	private void createUserCUI(BPUlcRequest ulcReq, BPScheduleUssRequest sussReq,
							   BPCuiRequest cuiReq, BPCuiEventRequest cuieReq,
							   String ust, Collection<String> listUsi) {
		listUsi.forEach(us -> {
			cuiReq.setCuiPublished(ulcReq.getUlcPublished());
			BpCuiContentUserUlc cui = getOrCreateCUI(ulcReq.getUlcUlccode(), us, "", cuiReq.getCuiPublished());
			cuiReq.setCuiCuicode(cui.getCode());
			cuiReq.setCuiMylcp(sussReq.getSussLcp());
			cuiReq.setCuiMycti("");
			cuiReq.setCuiUsicode(us);
			cuiReq.setCuiUst(ust);

			cuieReq.setCuieCuicode(cuiReq.getCuiCuicode());
			cuieReq.setCuieMyusi(cuiReq.getCuiUsicode());
			cuieReq.setCuieMylcet("DR-JN-JRQ"); // Hard code
			cuieReq.setCuieMylcp(cuiReq.getCuiMylcp());
			cuieReq.setCuiePlantime(sussReq.getSussCassStartPeriod());
			cuieReq.setCuiePublished(cuiReq.getCuiPublished());
			cuiEventService.createCuiEvent(cuieReq);
		});

	}

	private void createMainCUI(BPUlcRequest ulcReq, BPScheduleUssRequest sussReq, BPCuiRequest cuiReq) {
		BpCuiContentUserUlc mainCUi = getOrCreateCUI(ulcReq.getUlcUlccode(), "AU", "", ulcReq.getUlcPublished());
		cuiReq.setCuiCuicode(mainCUi.getCode());
		cuiReq.setCuiMylcp(sussReq.getSussLcp());
		cuiReq.setCuiMycti("");
		cuiReq.setCuiUsicode("AU"); // Hard code
		cuiReq.setCuiUst("MN"); // Hard code
	}

	@Override
	public BpCuiContentUserUlc getOrCreateCUI(String ulc, String usi, String cti, Boolean published) {
		BpCuiContentUserUlc cui = cuiRepo.findByCode(buildCuiCode(ulc, usi)).orElse(null);
		BpContentItem contentItem = StringUtils.isEmpty(cti) ? null : bpCtiContentItemRepository.findFirstByCode(cti).orElse(null);
		if (Objects.isNull(cui)) {
			cui = cuiRepo.createOrUpdate(BpCuiContentUserUlc.builder()
					.code(buildCuiCode(ulc, usi))
					.myUsi(Objects.isNull(usi) ? null : userItemService.findByCode(usi))
					.myUlc(bpULCService.findByCode(ulc))
					.published(published)
					.myCti(contentItem)
					.mybps(valueSaving.getBpsCode())
					.build());
		} else {
			cui.setPublished(published);
			if (Objects.nonNull(contentItem)) {
				cui.setMyCti(contentItem);
			}
		}

		valueSaving.setBpsCode(cui.getMybps(), false);
		valueSaving.setCuiCode(cui.getCode(), false);
		return cui;
	}

	@Override
	@Transactional
	public void unpublishCUI(String ulcCode, String myst) {
		cuiRepo.findByMyulcAndMyusi(ulcCode, myst)
				.ifPresent(c -> c.setPublished(false));
	}

	@Override
	public void unpublishCUICUIE(String ulcCode, String myst) {
		BpCuiContentUserUlc cui = cuiRepo.findByMyulcAndMyusi(ulcCode, myst).orElse(null);
		if (Objects.nonNull(cui)) {
			cui.setPublished(false);
			cuiEventService.unpublishedCUIE(cui.getCode(), false);
		}

	}

	@Override
	@Transactional
	public void unpublishCUITE(String ulcCode, String myst) {
		// create bps
		String bpsCode = bpBpsStepService.generateBpsCode(BpsStepTypeEnum.UNPUBLISH_CUIEVENT_TE);
		publisher.publish(BpsStepDTO.builder()
				.name(bpsCode)
				.code(bpsCode)
				.bpstype(BpsStepTypeEnum.UNPUBLISH_CUIEVENT_TE.getCode())
				.myprocess(valueSaving.getBppCode())
				.build());

		// create Bpe
		String bpeCode = bpBpeEventService.generateBpeCode(BpeEventTypeEnum.UNPUBLISH_CUIEVENT_TE);
		valueSaving.setUnPublishBpe(bpeCode, true);
		publisher.publish(BpeEventDTO.builder()
				.name(bpeCode)
				.code(bpeCode)
				.bpetype(BpeEventTypeEnum.UNPUBLISH_CUIEVENT_TE.getCode())
				.mybps(bpsCode)
		);

		cuiRepo.findByMyulcAndMyusi(ulcCode, myst)
				.ifPresent(c ->
						c.setPublished(false));
	}

	@Override
	public BpCuiContentUserUlc findByCode(String code) {
		return cuiRepo.findByCode(code).orElseThrow(
				() -> new NotFoundException("Couldn't find cui by code: " + code));
	}

	@Override
	@Transactional
	public BpCuiContentUserUlc createCui(String usi, String ctiCode, String ulcCode) {
		return getOrCreateCUI(ulcCode, usi, ctiCode, true);
	}

	@Override
	public BpCuiContentUserUlc findCuiMainFromUlc(String ulc) {
		return cuiRepo.findCuiMainFromUlc(ulc).orElse(null);
	}


	@Override
	public Page<LeaningObjectResponse> getHomeworkCtiFromSt(String usi, String pt, Long LOId, Integer page, Integer size) {
		if (!pt.equals(ProductTypeEnum.OM.getName())) {

			throw new NotFoundException("We do not support this product type");
		}
		BpUsiUserItem userItem = userItemService.findByCode(usi);
		if (Objects.isNull(userItem)) {
			throw new NotFoundException("Not found user item with usiCode : " + usi);
		}
		if (page == 0) page = 1;
		page = page - 1;
		BpLCP lcpHw = lcpService.findLcpSsForOMByLCK(LCKEnum.LCK_OM_SS_HW.getCode());
		BpLCP lcpRL = lcpService.findLcpSsForOMByLCK(LCKEnum.LCK_OM_SS_RL.getCode());
		Long totalElements = 0L;
		List<LeaningObjectResponse> result = new ArrayList<>();
		log.info("Start get learning Object for OM");
		// SS
		List<BpCuiContentUserUlc> cuiList = new ArrayList<>();
		if (Objects.isNull(LOId)) {
			cuiList = cuiRepo.findCuiFromUsiLcp(usi, lcpHw.getCode(),
					size, size * page);
			totalElements = cuiRepo.getCountCuiFromUsiLcp(usi, lcpRL.getCode()).longValue();
		} else {
			BpCuiContentUserUlc cui = cuiRepo.findCuiByUsiLcpUlcNo(usi, lcpHw.getCode(), LOId.intValue()).orElse(null);
			if (Objects.isNull(cui)) throw new NotFoundException("Not found Ulc by ulc no : " + LOId);
			cuiList.add(cui);
			totalElements = 1L;
		}

		for (BpCuiContentUserUlc cuiHw : cuiList) {
			if (Objects.isNull(cuiHw)) {
				log.error("Not found cuiHw by usi : {} and lcp : {} ", usi, lcpHw.getCode());
				continue;
			}
			BpCuiContentUserUlc cuiRL = cuiRepo.findCuiByUsiLcpUlcNo(usi, lcpRL.getCode(), cuiHw.getMyUlc().getUlcNo()).orElse(null);
			if (Objects.isNull(cuiRL)) {
				log.error("Not found cuiRL by usi : {} and lcp : {} ", usi, lcpRL.getCode());
				continue;
			}
			BpCuiContentUserUlc cuiMainRl = findCuiMainFromUlc(cuiRL.getMyUlc().getCode());
			if (Objects.isNull(cuiMainRl)) {
				log.error("Not found cuiMain of Ulc : {}", cuiRL.getMyUlc().getCode());
				continue;
			}
			BpContentItem ctiRl = cuiMainRl.getMyCti();
			if (Objects.isNull(ctiRl)) {
				log.error("Not found ctiRl of cuiMainRL : {}", cuiMainRl.getCode());
				continue;
			}

			List<BpContentItem> ctiRlUrl = contentItemService.findByCtiParentAndCtt(
					ctiRl.getCode(), CTTEnum.RECORDED_LINK.getCode());
			// SC
			List<BpUniqueLearningComponent> aqrUlcList = bpULCService.findAllUlcFromParentUlc(cuiHw.getMyUlc().getCode());
			List<BpContentItem> ctiAQR = aqrUlcList.stream().map(e -> findCuiMainFromUlc(e.getCode()))
					.map(BpCuiContentUserUlc::getMyCti)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			Long xgrade = Cep200ToC100Utils.toC100GradeId(cuiHw.getMyUlc().getMyGg());
			Long xsubject = 1L;

			List<LearningObjectWithRewardResponse> learningObject = lmsService.getLOAndReward(
					xgrade, xsubject,
					ctiAQR.stream()
							.filter(Objects::nonNull)
							.map(BpContentItem::getMyLo).toArray(String[]::new));
			/*
			Map<Long, Double> progress =
					lmsService.getProgress(usi, learningObject.stream().map(LearningObjectWithRewardResponse::getId).collect(Collectors.toList()));
			learningObject.forEach(e -> {
				if (progress.containsKey(e.getId())) e.setProgress(progress.get(e.getId()));
			});
			 */

			int totalCompleteLearningObject = learningObject.isEmpty() ? 0 :
					(int) learningObject.stream().filter(e -> e.getProgress().compareTo(new BigDecimal(100)) == 0).count();

			List<LearningObjectDetailResponse> LOMain = authoringService.getLoByCodes(Collections.singletonList(
					ctiRl.getMyLo()
			));

			result.add(LeaningObjectResponse.builder()
					.index(Long.valueOf(cuiHw.getMyUlc().getUlcNo()))
					.trainingTypeId(6L)
					.className(Objects.isNull(ctiRl.getMyLo()) ? ctiRl.getName() : LOMain.isEmpty() ? null : LOMain.get(0).getLearningObjectName())
					.classCode(Objects.isNull(ctiRl.getMyLo()) ? ctiRl.getCode() : LOMain.isEmpty() ? null : LOMain.get(0).getLearningObjectCode())
					.videoBackupUrl(ctiRlUrl.isEmpty() ? null : ctiRlUrl.get(0).getMyValueSet())
					.totalCompleteLearningObjects((long) totalCompleteLearningObject)
					.totalLearningObjects((long) learningObject.size())
					.learningObjectWithRewardResponseList(learningObject)
					.build());
		}
		return new PageImpl<>(new ArrayList<>(result),
				PageRequest.of(page, size),
				totalElements);
	}

	@Override
	public String findFirstCuiUsi(String ulcCode, String ust) {
		return cuiRepo.findFirstCuiUsi(ulcCode, ust);
	}

	@Override
	public List<StudentLearningPackageResponse> getAllLearningPackage(String usi, String pt) {
		if (!pt.equals(ProductTypeEnum.OM.getName())) {
			throw new NotFoundException("We do not support this product : " + pt);
		}
		BpUsiUserItem userItem = userItemService.findByCode(usi);
		if (Objects.isNull(userItem)) {
			throw new NotFoundException("Not found user item with usiCode : " + usi);
		}
		BpLCP lcpRL = lcpService.findLcpSsForOMByLCK(LCKEnum.LCK_OM_SS_RL.getCode());
		List<BpCuiContentUserUlc> cuiRlList = cuiRepo.findAllByUsiLcp(usi, lcpRL.getCode());
		List<StudentLearningPackageResponse> listLearningPackage = new ArrayList<>();
		for (BpCuiContentUserUlc cui : cuiRlList) {
			BpCuiContentUserUlc cuiMain = findCuiMainFromUlc(cui.getMyUlc().getCode());
			if (Objects.isNull(cuiMain)) continue;
			BpContentItem ctiRl = cuiMain.getMyCti();
			if (Objects.isNull(ctiRl)) continue;
			StudentLearningPackageResponse response = mapper.map(ctiRl, StudentLearningPackageResponse.class);
			response.setUlcNo(cuiMain.getMyUlc().getUlcNo());
			response.setTrainingTypeid(6L);
			listLearningPackage.add(response);
		}
		return listLearningPackage;
	}

	@Override
	public Page<ScheduleMonthCalendarPJ> findAllByCondition(ScheduleRequest request) {
		PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize());
		request.setDfdlsIsNull(CollectionUtils.isEmpty(request.getDfdls()));
		request.setGgsIsNull(CollectionUtils.isEmpty(request.getGgs()));
		request.setGgsIsNull(CollectionUtils.isEmpty(request.getGgs()));

		List<BpLCP> lcpList = bpLCPRepository.findMCByPT(
				request.getLcl(),
				request.getLck(),
				request.getPt()
		);
		if (CollectionUtils.isEmpty(lcpList)) {
			String content = String.format("Could not find any lcp with lcl %s, lck %s, pt %s ",
					request.getLcl(),
					request.getLck(),
					request.getPt()
			);
			log.error(content);
			throw new BadRequestException(content);
		}
		List<String> lcpToCode = lcpList.stream().map(BpLCP::getCode).collect(Collectors.toList());
		request.setLcps(lcpToCode);
		return cuiRepo.findALlByCondition(request, pageRequest);
	}

	@Override
	public List<BpCuiContentUserUlc> findCuiByUlcInAndUst(Collection<String> ulcs, String ust) {
		return cuiRepo.findCuiByUlcInAndUst(ulcs, ust);
	}

	@Override
	public GeneralPageResponse<LearningObjectWithRewardResponse> search(String keyword, String pt, String usi, Integer page, Integer size) {
		if (!pt.equals(ProductTypeEnum.OM.getName())) {
			throw new NotFoundException("We do not support this product : " + pt);
		}
		BpUsiUserItem userItem = userItemService.findByCode(usi);
		if (Objects.isNull(userItem)) {
			throw new NotFoundException("Not found user item with usiCode : " + usi);
		}
		/*
		BpPodProductOfDeal pod = podService.findByUsi(usi, pt);
		if (Objects.isNull(pod)) {
			throw new NotFoundException("Not found pod with usi : " + usi + " adn pt : " + pt);
		}
		*/

		BpLCP lcpHw = lcpService.findLcpSsForOMByLCK(LCKEnum.LCK_OM_SS_HW.getCode());

		BpCuiContentUserUlc cuiHw = cuiRepo.findAllByUsiLcp(usi, lcpHw.getCode()).stream().findFirst()
				.orElseThrow(() -> new NotFoundException("Coun't find any ULC for this usi: " + usi));
		Long xgrade = Cep200ToC100Utils.toC100GradeId(cuiHw.getMyUlc().getMyGg());
		Long xsubject = 1L;

		List<String> AssignedLO = cuiRepo.findAssignLo(usi, lcpHw.getCode());


		List<LearningObjectWithRewardResponse> learningObject = new ArrayList<>();
		int step = 0;
		int subSize = 10;
		while (true) {
			List<String> subList;
			if (step + subSize >= AssignedLO.size()) subList = AssignedLO.subList(step, AssignedLO.size() - 1);
			else subList = AssignedLO.subList(step, step + subSize - 1);
			List<LearningProgramLearningObjectResponse> los = authoringService.searchByCodes(
					keyword, subList.toArray(new String[0]), 1, 10).getContent();
			List<LearningObjectWithRewardResponse> loReward = lmsService.getLOAndReward(xgrade, xsubject,
					los.stream().filter(Objects::nonNull).map(LearningObjectWithRewardResponse::getCode).toArray(String[]::new));
			if (!loReward.isEmpty()) learningObject.addAll(loReward);
			step = step + subSize;
			if (step >= AssignedLO.size() - 1) break;
		}

         /*
		if (!learningObject.isEmpty()) {
			Map<Long, Double> progress =
					lmsService.getProgress(usi, learningObject.stream().map(LearningObjectWithRewardResponse::getId).collect(Collectors.toList()));
			learningObject.forEach(e -> {
				if (progress.containsKey(e.getId())) e.setProgress(progress.get(e.getId()));
			});
		}
        */

		if (page == 0) page = 1;
		page = page - 1;

		final int start = (int) PageRequest.of(page, size).getOffset();
		final int end = Math.min((start + PageRequest.of(page, size).getPageSize()), learningObject.size());

		return GeneralPageResponse.toResponse(
				new PageImpl<>(learningObject.subList(start, end), PageRequest.of(page, size), learningObject.size()));
	}

	@Override
	public List<String> findHrvCtis(String pt, String gg, String dfdl, String cap, String usi) {
		List<String> ctis = cuiRepo.findHrvCtis(pt, gg, dfdl, cap, usi);

		while (ctis.size() < 4) {
			ctis.add(null);
		}

		return ctis;
	}

	@Override
	public Page<AnswerAndQuestionPJ> findCuiQuestion(GetQuestionAnswerRequest request, Pageable pageable) {
		return cuiRepo.findCuiQuestion(request, request.getSla(), ONE_DAY_IN_SECONDS, pageable);
	}

	@Override
	public AnswerAndQuestionPJ findDetailsQuestion(String cuiCode) {
		return cuiRepo.findDetailsQuestion(cuiCode);
	}

	@Override
	public BpCuiContentUserUlc getCuiByUlcAndCti(String ulcCode, String ctiCode, boolean isActive) {
		return cuiRepo.getCuiByUlcAndCti(ulcCode, ctiCode, isActive);
	}

	@Override
	public void unpublishedCuiUnnecessaryOfModifyStudent(String usi, Timestamp from, Timestamp to, Boolean published) {
		cuiRepo.unpublishedCuiUnnecessaryOfModifyStudent(usi, from, to, published);
	}

	@Override
	public void unpublishedCuiUnnecessaryForScheduleMPForOM(String usi, Boolean published) {
		cuiRepo.unpublishedCuiUnnecessaryForScheduleMPForOM(usi, false);
	}

	@Override
	public List<BpCuiContentUserUlc> findCuiByCapAndClagAndUsi(String usi, String clag, Timestamp from, Timestamp to) {
		return cuiRepo.findCuiByCapAndClagAndUsi(usi, clag, from, to);
	}

	@Override
	public void saveAll(List<BpCuiContentUserUlc> cuiList) {
		cuiList.forEach(cuiRepo::createOrUpdate);
	}

	public Page<ScheduleMonthCalendarPJ> findAllScheduleWeek(ScheduleRequest request) {
		PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize());
		request.setDfdlsIsNull(CollectionUtils.isEmpty(request.getDfdls()));
		request.setGgsIsNull(CollectionUtils.isEmpty(request.getGgs()));
		request.setGgsIsNull(CollectionUtils.isEmpty(request.getGgs()));
		List<LcpCodePJ> comboLcp = bpLCPRepository.findMCByPT2(
				request.getLck(),
				request.getPt()
		);

		if (CollectionUtils.isEmpty(comboLcp)) {
			String content = String.format("Could not find any lcp with lcl %s, lck %s, pt %s ",
					request.getLcl(),
					request.getLck(),
					request.getPt()
			);
			log.error(content);
			throw new BadRequestException(content);
		}
		List<String> lcpToCode = new ArrayList<>();
		comboLcp.forEach(fo -> {
			lcpToCode.add(fo.getLcpsh());
			lcpToCode.add(fo.getLcpsl());
			lcpToCode.add(fo.getLcpss());
			lcpToCode.add(fo.getLcpsc());
		});

		request.setLcps(lcpToCode);
		return cuiRepo.findALlByCondition(request, pageRequest);

	}

	private String buildCuiCode(String ulc, String usi) {
		return ulc + "-" + usi;
	}

	@Override
	public BpCuiContentUserUlc getOrCreateCUIWithBps(String ulc, String usi, String cti, String bpsCode, Boolean published) {
		BpCuiContentUserUlc cui = cuiRepo.findByCode(buildCuiCode(ulc, usi)).orElse(null);
		if (cui == null) {
			bpBpeEventService.createBpeEvent(BpeEventTypeEnum.SUBMIT_REPORT2);
			cui = cuiRepo.createOrUpdate(BpCuiContentUserUlc.builder()
					.code(buildCuiCode(ulc, usi))
					.myUsi(userItemService.findByCode(usi))
					.myUlc(bpULCService.findByCode(ulc))
					.published(published)
					.myCti(null)
					.mybps(bpsCode)
					.build());
		}
		return cui;
	}
}
