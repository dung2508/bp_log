package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.common.enumtype.DfgeEnum;
import vn.edu.clevai.bplog.entity.BpCLAGULC;
import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.entity.BpPODCLAG;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.entity.zoom.ZoomMeeting;
import vn.edu.clevai.bplog.repository.BpCLAGULCRepository;
import vn.edu.clevai.bplog.repository.BpClagClassgroupRepository;
import vn.edu.clevai.bplog.repository.BpPODCLAGRepository;
import vn.edu.clevai.bplog.repository.BpPodProductOfDealRepository;
import vn.edu.clevai.bplog.repository.bplog.BpCuiContentUserUlcRepository;
import vn.edu.clevai.bplog.repository.bplog.BpCuiEventRepository;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.repository.zoom.ZoomMeetingRepository;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.BpClagPODService;
import vn.edu.clevai.bplog.service.BpPodProductOfDealService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.lms.payload.request.DscSessionGroupAndClassMappingGesCreationRequest;
import vn.edu.clevai.common.proxy.lms.payload.request.ModifyGTETeacherRequest;
import vn.edu.clevai.common.proxy.lms.payload.request.SessionGroupAndClassStudentAssigningRequest;
import vn.edu.clevai.common.proxy.lms.payload.request.ZoomMeetingCreationRequest;
import vn.edu.clevai.common.proxy.lms.payload.response.GesStudentInfoResponse;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GE75MIXToXConverter extends BaseXConverter {

	private final BpCuiContentUserUlcRepository cuiRepository;
	private final BpCuiEventRepository cuieRepository;
	private final BpUniqueLearningComponentRepository ulcRepository;
	private final BpCLAGULCRepository clagUlcRepository;
	private final BpPODCLAGRepository podClagRepository;
	private final BpClagClassgroupRepository clagRepository;
	private final BpPodProductOfDealRepository podRepository;
	private final ZoomMeetingRepository zoomMeetingRepository;

	private final CuiEventService cuiEventService;
	private final BpClagPODService clagPodService;
	private final BpPodProductOfDealService podService;

	private final ModelMapper modelMapper;

	private final GE75MIXToXConverter thisConverter;

	public GE75MIXToXConverter(
			BpClagClassgroupService bpClagClassgroupService,
			AuthoringService authoringService,
			LmsService lmsService,
			UserService userService,
			ContentItemService contentItemService,
			CuiService cuiService,
			BpCuiContentUserUlcRepository cuiRepository,
			BpUniqueLearningComponentRepository ulcRepository,
			BpCLAGULCRepository clagUlcRepository,
			BpPODCLAGRepository podClagRepository,
			BpClagClassgroupRepository clagRepository,
			BpPodProductOfDealRepository podRepository,
			CuiEventService cuiEventService,
			BpCuiEventRepository cuieRepository,
			ZoomMeetingRepository zoomMeetingRepository,
			BpClagPODService clagPodService,
			BpPodProductOfDealService podService,
			ModelMapper modelMapper,
			@Lazy
			GE75MIXToXConverter thisConverter
	) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
		this.cuiRepository = cuiRepository;
		this.ulcRepository = ulcRepository;
		this.clagUlcRepository = clagUlcRepository;
		this.podClagRepository = podClagRepository;
		this.clagRepository = clagRepository;
		this.podRepository = podRepository;
		this.cuiEventService = cuiEventService;
		this.cuieRepository = cuieRepository;
		this.zoomMeetingRepository = zoomMeetingRepository;
		this.clagPodService = clagPodService;
		this.podService = podService;
		this.modelMapper = modelMapper;
		this.thisConverter = thisConverter;
	}

	@Override
	@Transactional
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {

		// xEntity means entity with dfge = X
		// categoryEntity means entity with dfge != X

		ulcs.forEach(ulc -> {

			assert ulc.getMyDfge().equals(DfgeEnum.X.getCode());

			// group ULC by category
			Map<String, List<BpUniqueLearningComponent>> ulcMap = new HashMap<>();

			// group CLAG_ULC by category
			Map<String, List<BpCLAGULC>> clagUlcMap = new LinkedHashMap<>();

			// map CLAG.code - CLAG
			Map<String, BpClagClassgroup> clagMap;

			// group POD_CLAG by CLAG.code
			Map<String, List<BpPODCLAG>> podClagMap;

			// map POD.code - POD
			Map<String, BpPodProductOfDeal> podMap;

			// map username - category
			Map<String, String> studentCategoryMap;

			ulcMap.put(DfgeEnum.X.getCode(), Collections.singletonList(ulc));

			List<BpUniqueLearningComponent> siblingUlcs = ulcRepository
					.findByMyParentInAndMyLctCodeAndPublishedTrue(Collections.singleton(ulc.getMyParent()), ulc.getMyLctCode())
					.stream()
					.filter(sibling -> !Objects.equals(sibling.getMyDfge(), DfgeEnum.X.getCode()))
					.collect(Collectors.toList());

			ulcMap.putAll((Map<? extends String, ? extends List<BpUniqueLearningComponent>>) siblingUlcs.stream()
					.collect(Collectors.groupingBy(BpUniqueLearningComponent::getMyDfge, LinkedHashMap::new, Collectors.toCollection(ArrayList::new))));

			ulcMap.forEach((category, categoryUlcs) -> {

				List<String> siblingCodes = categoryUlcs.stream()
						.map(BpUniqueLearningComponent::getCode)
						.collect(Collectors.toList());

				List<BpCLAGULC> clagUlcs = siblingCodes.isEmpty() ? Collections.emptyList() :
						clagUlcRepository.findByMyulcIn(siblingCodes);
				clagUlcMap.put(category, clagUlcs);

			});

			List<String> clagCodes = clagUlcMap.values()
					.stream()
					.flatMap(Collection::stream)
					.map(BpCLAGULC::getMyclag)
					.collect(Collectors.toList());

			List<BpClagClassgroup> clags = clagRepository.findByCodeInAndActiveTrue(clagCodes);
			clagMap = clags.stream()
					.collect(Collectors.toMap(BpClagClassgroup::getCode, x -> x, (x1, x2) -> x2));

			List<BpPODCLAG> podClags = clagCodes.isEmpty() ? Collections.emptyList() :
					podClagRepository.findByUstAndClagIn(USTEnum.ST.getName(), clagCodes);
			podClagMap = podClags.stream()
					.collect(Collectors.groupingBy(BpPODCLAG::getMyclag, LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

			List<String> podCodes = podClags.stream()
					.map(BpPODCLAG::getMypod)
					.collect(Collectors.toList());

			List<BpPodProductOfDeal> pods = podCodes.isEmpty() ? Collections.emptyList() :
					podRepository.findALlByListCode(podCodes);
			podMap = pods.stream()
					.collect(Collectors.toMap(BpPodProductOfDeal::getCode, x -> x, (x1, x2) -> x2));

			String[] students = podMap.values()
					.stream()
					.map(BpPodProductOfDeal::getMyst)
					.distinct()
					.toArray(String[]::new);

			List<GesStudentInfoResponse> categoryStudents = getLmsService()
					.getGesStudentInfos(xdsc, students);
			studentCategoryMap = categoryStudents.stream()
					.collect(Collectors.toMap(GesStudentInfoResponse::getUsername, GesStudentInfoResponse::getCategory, (x1, x2) -> x2));

			ListUtils.emptyIfNull(clagUlcMap.get(DfgeEnum.X.getCode()))
					.forEach(xClagUlc -> {
						BpClagClassgroup xClag = clagMap.get(xClagUlc.getMyclag());
						if (xClag == null) {
							throw new NotFoundException("Not found CLAG with code: " + xClagUlc.getMyclag());
						}
						podClagMap.getOrDefault(xClag.getCode(), Collections.emptyList())
								.forEach(xPodClag -> {

									try {
										// Calculate category
										String podCode = xPodClag.getMypod();
										BpPodProductOfDeal pod = podMap.get(podCode);
										String category = studentCategoryMap.get(pod.getMyst());
										if (category == null) {
											throw new NotFoundException("Not found category of student: " + pod.getMyst());
										}

										// Find available Clag
										BpCLAGULC categoryClagUlc = clagUlcMap.getOrDefault(category, Collections.emptyList())
												.stream()
												.filter(clagUlc -> CollectionUtils.size(podClagMap.get(clagUlc.getMyclag())) < 12)
												.findFirst()
												.orElseThrow(() -> new BadRequestException("Not enough available clag for category: " + category));
										BpClagClassgroup categoryClag = clagMap.get(categoryClagUlc.getMyclag());
										BpUniqueLearningComponent categoryUlc = ulcMap.getOrDefault(category, Collections.emptyList())
												.stream()
												.filter(item -> Objects.equals(item.getCode(), categoryClagUlc.getMyulc()))
												.findFirst()
												.orElseThrow(() -> new BadRequestException("Not found ulc with code: " + categoryClagUlc.getMyulc()));

										// create DscSessionGroup and DscClassMapping if needed
										if (categoryClag.getXsessiongroup() == null || categoryClag.getXclass() == null) {

											// Find GTE, ZoomMeeting
											List<BpPODCLAG> gtePodClags = clagPodService.findByUstAndClag(USTEnum.GTE.getName(), categoryClag.getCode());
											if (CollectionUtils.isEmpty(gtePodClags)) {
												throw new NotFoundException("Not found any POD_CLAG with CLAG: " + categoryClag.getCode() +
														" and UST: " + USTEnum.GTE);
											}
											BpPodProductOfDeal gtePod = podService.findByCode(gtePodClags.get(0).getMypod());
											String gteCode = gtePod.getMyst();
											ZoomMeeting zoomMeeting = zoomMeetingRepository.findFirstByUlcAndUsi(categoryUlc.getCode(), gteCode)
													.orElseThrow(() -> new NotFoundException("Not found ZoomMeeting with ULC " +
															categoryUlc.getCode() + " , USI " + gteCode));

											// create DscSessionGroup DscClassMapping
											thisConverter.createXDscSessionGroupAndClassMappingGes(categoryClag,
													DscSessionGroupAndClassMappingGesCreationRequest.builder()
															.dailyScheduledClassCode(xdsc)
															.productId(Cep200ToC100Utils.toC100PtId(ulc.getMyPt()))
															.trainingTypeId(2L)
															.teacherUsername(gteCode)
															.maxActiveStudents(categoryClag.getMaxtotalstudents())
															.category(categoryUlc.getMyDfge())
															.zoomMeeting(modelMapper.map(zoomMeeting, ZoomMeetingCreationRequest.class))
															.build());
										}

										if (Objects.nonNull(categoryClag.getXsessiongroup()) && Objects.nonNull(categoryClag.getXclass())) {
											List<BpPODCLAG> gtePodClags = clagPodService.findByUstAndClag(USTEnum.GTE.getName(), categoryClag.getCode());
											if (CollectionUtils.isEmpty(gtePodClags)) {
												throw new NotFoundException("Not found any POD_CLAG with CLAG: " + categoryClag.getCode() +
														" and UST: " + USTEnum.GTE);
											}
											BpPodProductOfDeal gtePod = podService.findByCode(gtePodClags.get(0).getMypod());
											String gteCode = gtePod.getMyst();
											try {
												getLmsService().modifyGETTeacher(ModifyGTETeacherRequest.builder()
														.sessionGroupCode(categoryClag.getXsessiongroup())
														.clag(categoryClag.getXclass())
														.userName(gteCode)
														.dscCode(xdsc).build());
											} catch (Exception e) {
												log.error("Error when modify get teacher : {} ", DebuggingDTO.build(e));
											}
										}

										thisConverter.createPodClagAndConvertToX(
												pod,
												xPodClag,
												categoryClag,
												ulc,
												categoryUlc,
												SessionGroupAndClassStudentAssigningRequest.builder()
														.dailyScheduledClassCode(xdsc)
														.sessionGroupCode(categoryClag.getXsessiongroup())
														.classCode(categoryClag.getXclass())
														.studentUsername(pod.getMyst())
														.category(category)
														.build()
										);

									} catch (Exception e) {
										log.error("Error when convert xPodClag {} cause {}", xPodClag.getCode(), DebuggingDTO.build(e));
									}

								});
					});

		});

	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void createXDscSessionGroupAndClassMappingGes(
			BpClagClassgroup clag,
			DscSessionGroupAndClassMappingGesCreationRequest request
	) {
		Map.Entry<String, String> entry = getLmsService().createXDscSessionGroupAndClassMappingGes(request);
		clag.setXsessiongroup(entry.getKey());
		clag.setXclass(entry.getValue());
		clagRepository.save(clag);
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public void createPodClagAndConvertToX(
			BpPodProductOfDeal pod,
			BpPODCLAG xPodClag,
			BpClagClassgroup categoryClag,
			BpUniqueLearningComponent xUlc,
			BpUniqueLearningComponent categoryUlc,
			SessionGroupAndClassStudentAssigningRequest request
	) {

		// Create or update PodClag
		podClagRepository.createOrUpdate(BpPODCLAG.builder()
				.code(String.join("-", pod.getCode(), categoryClag.getCode()))
				.mypod(pod.getCode())
				.myclag(categoryClag.getCode())
				.memberType(ClagType.DYNAMIC.getCode())
				.assignedAt(DateUtils.now())
				.active(true)
				.myUst(USTEnum.ST.getName())
				.build());

		// Create CUI, CUIE
		cuiEventService.createCUIJoinEvent(
				categoryUlc.getCode(),
				pod.getMyst(),
				categoryUlc.getMyLcp().getCode(),
				null,
				categoryUlc.getMyCap().getCode()
		);

		// Disable xPodClag
		xPodClag.setActive(false);
		podClagRepository.save(xPodClag);

		// Unpublished CUI, CUIE
		cuiRepository.findByMyulcAndMyusi(xUlc.getCode(), pod.getMyst())
				.ifPresent(cui -> {
					cui.setPublished(false);
					cuieRepository.findByMyCuiCode(cui.getCode())
							.forEach(cuie -> cuie.setPublished(false));
				});

		// assign students to SessionGroup and Class
		getLmsService().assignStudentsToXSessionGroupAndClassGes(request);
	}

}