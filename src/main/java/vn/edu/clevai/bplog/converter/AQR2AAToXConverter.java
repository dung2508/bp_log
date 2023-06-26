package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.projection.AQR2InfoProjection;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectDetailResponse;
import vn.edu.clevai.common.proxy.lms.payload.request.CreateDscForwardRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AQR2AAToXConverter extends BaseXConverter {

	public AQR2AAToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		List<AQR2InfoProjection> projections = getContentItemService().getAQR2Cti(ulcs.stream()
				.map(BpUniqueLearningComponent::getCode).collect(Collectors.toSet()));

		if (!CollectionUtils.isEmpty(projections)) {
			Map<String, Long> loIdMap = getAuthoringService().getLoByCodes(projections.stream().map(AQR2InfoProjection::getMylo)
							.filter(StringUtils::isNotBlank).collect(Collectors.toSet())).stream()
					.collect(Collectors.toMap(LearningObjectDetailResponse::getLearningObjectCode, LearningObjectDetailResponse::getId));

			List<CreateDscForwardRequest> request = new ArrayList<>();
			request.add(
					CreateDscForwardRequest
							.builder()
							.loId(null)
							.ordering(1) /* PACK 1 is always empty */
							.build()
			);

			request.addAll(
					projections.stream().map(p -> {
								if (loIdMap.containsKey(p.getMylo())) {
									return CreateDscForwardRequest
											.builder()
											.loId(loIdMap.get(p.getMylo()))
											.ordering(p.getOrdering() + 1) /* PACK 2 and more. */
											.build();
								} else {
									log.error("createOrUpdateXDscForward Couldn't find lo for code: {} ordering: {}", p.getMylo(), p.getOrdering());
									return null;
								}
							})
							.filter(Objects::nonNull)
							.collect(Collectors.toList())
			);

			getLmsService().createOrUpdateXDscForward(xdsc, request);
		} else {
			log.warn("createOrUpdateXDscForward 0 lo lct: {} dfge: {}", lct, dfge);
		}
	}
}