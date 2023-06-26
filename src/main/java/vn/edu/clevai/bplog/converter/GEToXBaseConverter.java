package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.entity.BpPODCLAG;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.entity.zoom.ZoomMeeting;
import vn.edu.clevai.bplog.repository.BpClagClassgroupRepository;
import vn.edu.clevai.bplog.repository.zoom.ZoomMeetingRepository;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.BpClagPODService;
import vn.edu.clevai.bplog.service.BpPodProductOfDealService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.lms.payload.request.DscSessionGroupCreationRequest;
import vn.edu.clevai.common.proxy.lms.payload.request.ModifyGTETeacherRequest;
import vn.edu.clevai.common.proxy.lms.payload.request.ZoomMeetingCreationRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GEToXBaseConverter extends BaseXConverter {


	private final BpClagClassgroupRepository clagRepository;
	private final ZoomMeetingRepository zoomMeetingRepository;

	private final BpClagPODService bpClagPODService;
	private final BpPodProductOfDealService podService;

	private final ModelMapper modelMapper;

	private final Long trainingTypeId;

	public GEToXBaseConverter(
			BpClagClassgroupService bpClagClassgroupService,
			AuthoringService authoringService,
			LmsService lmsService,
			UserService userService,
			ContentItemService contentItemService,
			CuiService cuiService,
			BpClagClassgroupRepository clagRepository,
			ZoomMeetingRepository zoomMeetingRepository,
			BpClagPODService bpClagPODService,
			BpPodProductOfDealService podService,
			ModelMapper modelMapper,
			Long trainingTypeId
	) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
		this.clagRepository = clagRepository;
		this.zoomMeetingRepository = zoomMeetingRepository;
		this.bpClagPODService = bpClagPODService;
		this.podService = podService;
		this.modelMapper = modelMapper;
		this.trainingTypeId = trainingTypeId;
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {

		String converterName = getClass().getSimpleName();

		log.info("Start convert {} {} ulcs", converterName, CollectionUtils.size(ulcs));

		AtomicInteger successfulCount = new AtomicInteger();
		AtomicInteger failedCount = new AtomicInteger();
		AtomicInteger ignoredCount = new AtomicInteger();

		CollectionUtils.emptyIfNull(ulcs)
				.forEach(ulc -> {

					try {
						List<BpClagClassgroup> clags = clagRepository.findByUlc(ulc.getCode());
						if (CollectionUtils.isEmpty(clags)) {
							log.error("Not found CLAG mapping with ULC: {}", ulc.getCode());
							failedCount.incrementAndGet();
							return;
						}
						BpClagClassgroup clag = clags.get(0);
						if (clags.size() > 1) {
							log.warn("Found {} CLAG mapping with ULC: {}, using first CLAG: {}",
									clags.size(), ulc.getCode(), clag.getCode());
						}

						List<BpPODCLAG> gtePodClags = bpClagPODService.findByUstAndClag(USTEnum.GTE.getName(), clag.getCode());
						if (CollectionUtils.isEmpty(clags)) {
							log.error("Not found any POD_CLAG with CLAG: {} and UST: {}", clag.getCode(), USTEnum.GTE);
							failedCount.incrementAndGet();
							return;
						}

						BpPodProductOfDeal gtePod = podService.findByCode(gtePodClags.get(0).getMypod());
						String gteCode = gtePod.getMyst();

						if (clag.getXsessiongroup() != null) {
							try {
								getLmsService().modifyGETTeacher(ModifyGTETeacherRequest.builder()
										.dscCode(xdsc)
										.sessionGroupCode(clag.getXsessiongroup())
										.userName(gteCode)
										.build());
							} catch (Exception e) {
								log.error("Error when modify teacher : {}", DebuggingDTO.build(e));
							}
							ignoredCount.incrementAndGet();
							return;
						}

						ZoomMeeting zoomMeeting = zoomMeetingRepository.findFirstByUlcAndUsi(ulc.getCode(), gteCode)
								.orElseThrow(() -> new NotFoundException("Not found ZoomMeeting with ULC " +
										ulc.getCode() + " , USI {} " + gteCode));

						String xSessionGroup = getLmsService()
								.createXDscSessionGroup(DscSessionGroupCreationRequest.builder()
										.dailyScheduledClassCode(xdsc)
										.productId(Cep200ToC100Utils.toC100PtId(ulc.getMyPt()))
										.trainingTypeId(trainingTypeId)
										.teacherUsername(gteCode)
										.maxActiveStudents(clag.getMaxtotalstudents())
										.category(ulc.getMyDfge())
										.zoomMeeting(modelMapper.map(zoomMeeting, ZoomMeetingCreationRequest.class))
										.build());

						clag.setXsessiongroup(xSessionGroup);
						clagRepository.save(clag);

						successfulCount.incrementAndGet();
					} catch (Exception e) {
						log.error("Error when convert {} ULC: {} cause {}", converterName, ulc.getCode(), DebuggingDTO.build(e));
						failedCount.incrementAndGet();
					}

				});

		log.info("Finished convert {} {} ulcs: {} successful, {} failed, {} ignored",
				converterName, CollectionUtils.size(ulcs), successfulCount.get(), failedCount.get(), ignoredCount.get());

	}

}