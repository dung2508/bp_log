package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.projection.RQSProjection;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.proxy.authoring.payload.response.QuizResponse;
import vn.edu.clevai.common.proxy.lms.payload.request.CreateDscBattleQuizRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RQS1MIToXConverter extends BaseXConverter {

	public RQS1MIToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		List<RQSProjection> projections = getContentItemService().getRQSOrCQSInfo(ulcs.stream()
				.map(BpUniqueLearningComponent::getCode).collect(Collectors.toSet()));

		if (!CollectionUtils.isEmpty(projections)) {
			Map<String, QuizResponse> quizIdMap = getAuthoringService().get(projections.stream().map(RQSProjection::getMybl5qp)
							.filter(StringUtils::isNotBlank).collect(Collectors.toSet())).stream()
					.collect(Collectors.toMap(QuizResponse::getQuizCode, v -> v));
			getLmsService().createOrUpdateXDscBattleQuiz(xdsc, projections.stream().map(p -> {
						if (quizIdMap.containsKey(p.getMybl5qp())) {
							QuizResponse response = quizIdMap.get(p.getMybl5qp());
							return CreateDscBattleQuizRequest.builder()
									.quizId(response.getId())
									.ordering(p.getOrdering())
									.duration(response.getExpectedCompleteTime())
									.build();
						} else {
							log.error("createOrUpdateXDscBattleQuiz Couldn't find quiz for getMybl5qp: {} ordering: {}", p.getMybl5qp(), p.getOrdering());
							return null;
						}
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList()));
		} else {
			log.warn("createOrUpdateXDscBattleQuiz 0 quiz lct: {} dfge: {}", lct, dfge);
		}
	}
}