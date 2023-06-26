package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.projection.DQSProjection;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.authoring.payload.response.QuizResponse;
import vn.edu.clevai.common.proxy.lms.payload.request.DailyScheduledClassLiveQuizCreationRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DQSAAToXConverter extends BaseXConverter {

	public DQSAAToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		List<DQSProjection> projections = getContentItemService().getDqsInfo(
				ulcs.stream().map(BpUniqueLearningComponent::getCode).collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(projections)) {
			log.warn("DQSAAToXConverter convert 0 quizzes for xdsc: " + xdsc + " lct: " + lct + " dfge: " + dfge);

			return;
		}

		List<String> bl5s = projections.stream().map(DQSProjection::getMybl5qp)
				.filter(StringUtils::isNotBlank).collect(Collectors.toList());

		if (bl5s.isEmpty()) {
			throw new NotFoundException("Valid bl5s not found");
		}

		/* <QuizCode, QuizId> */
		Map<String, Long> quizIds = getAuthoringService().get(bl5s)
				.stream().collect(Collectors.toMap(QuizResponse::getQuizCode, QuizResponse::getId));

		if (quizIds.isEmpty()) {
			throw new NotFoundException("No valid bl5s found in AUTHORING");
		}

		/* Create a request to create or update dsc_live_quizzes. */
		List<DailyScheduledClassLiveQuizCreationRequest> request = projections.stream().map(
						p -> DailyScheduledClassLiveQuizCreationRequest
								.builder()
								.ordering(p.getOrdering())
								.quizId(quizIds.get(p.getMybl5qp()))
								.build()
				)
				.collect(Collectors.toList());

		getLmsService().createOrUpdateXDailyScheduledClassLiveQuizzes(xdsc, request);
	}
}