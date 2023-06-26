package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.projection.AQR1InfoProjection;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.BpULCService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectDetailResponse;
import vn.edu.clevai.common.proxy.lms.payload.request.CreateOrUpdateDscClassLearningObjectRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AQR1AAToXConverter extends BaseXConverter {
	private final BpULCService ulcService;

	public AQR1AAToXConverter(
			BpClagClassgroupService bpClagClassgroupService,
			AuthoringService authoringService,
			LmsService lmsService, UserService userService,
			ContentItemService contentItemService,
			CuiService cuiService,
			BpULCService ulcService
	) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
		this.ulcService = ulcService;
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		BpUniqueLearningComponent aqr1Aa = ulcs.stream().findFirst().orElseThrow(
				() -> new NotFoundException("No ulc to be converted")
		);

		BpUniqueLearningComponent hrgEa = ulcService.findByCode(aqr1Aa.getMyParent());

		Set<String> xclasses = getBpClagClassgroupService()
				.findClagFromULC(hrgEa.getMyParent())
				.stream()
				.map(BpClagClassgroup::getXclass)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toSet());

		if (xclasses.isEmpty()) {
			throw new NotFoundException("No valid xclass found for the shift = " + hrgEa.getMyParent());
		}

		List<String> ulcCodes = ulcs.stream()
				.collect(Collectors.groupingBy(BpUniqueLearningComponent::getUlcNo))
				.values()
				.stream()
				.map(u -> u.get(0).getCode())
				.collect(Collectors.toList());

		List<AQR1InfoProjection> projections = getContentItemService().getAQR1Cti(ulcCodes);

		if (projections.isEmpty()) {
			throw new NotFoundException("No valid bl4 found");
		}

		Map<String, List<AQR1InfoProjection>> bl42aqr1 = projections
				.stream()
				.collect(Collectors.groupingBy(AQR1InfoProjection::getMylo));

		Map<Long, String> loId2CodeMap = getAuthoringService()
				.getLoByCodes(bl42aqr1.keySet())
				.stream()
				.collect(
						Collectors.toMap(LearningObjectDetailResponse::getId, LearningObjectDetailResponse::getLearningObjectCode)
				);

		if (loId2CodeMap.isEmpty()) {
			throw new NotFoundException("No valid bl4 found in AUTHORING");
		}

		List<CreateOrUpdateDscClassLearningObjectRequest> requests = loId2CodeMap
				.entrySet()
				.stream()
				.map(
						e -> {
							String code = e.getValue();
							Integer group = bl42aqr1.get(code).get(0).getGroup();

							return CreateOrUpdateDscClassLearningObjectRequest
									.builder()
									.group(group)
									.loId(e.getKey())
									.xclasses(xclasses)
									.build();
						}
				).collect(Collectors.toList());

		getLmsService().createOrUpdateXDscClassLearningObjects(
				xdsc, requests
		);
	}
}