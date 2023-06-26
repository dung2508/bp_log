package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.logDb.BpCuiContentUserUlc;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.projection.RQSProjection;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.proxy.authoring.payload.response.QuizResponse;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.lms.payload.request.CreateStudentTestQuizRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CQSAAToXConverter extends BaseXConverter {

	public CQSAAToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		CompletableFuture.runAsync(
				() -> {
					List<BpCuiContentUserUlc> cuiContentUserUlcs = getCuiService().findCuiByUlcInAndUst(ulcs.stream().map(BpUniqueLearningComponent::getCode)
							.collect(Collectors.toList()), USTEnum.ST.getName());

					if (CollectionUtils.isEmpty(cuiContentUserUlcs)) {
						log.warn("Found 0 cui when convert for xdsc: {} lct: {} dfge: {}", xdsc, lct, dfge);
					} else {
						cuiContentUserUlcs.stream()
								.collect(Collectors.groupingBy(BpCuiContentUserUlc::getMyUsi))
								.forEach((k, v) -> {
									try {
										List<RQSProjection> projections = getContentItemService().getRQSOrCQSInfo(v.stream()
												.map(c -> c.getMyUlc().getCode()).collect(Collectors.toSet()));

										if (!CollectionUtils.isEmpty(projections)) {
											Map<String, QuizResponse> quizIdMap = getAuthoringService().get(projections.stream().map(RQSProjection::getMybl5qp)
															.filter(StringUtils::isNotBlank).collect(Collectors.toSet())).stream()
													.collect(Collectors.toMap(QuizResponse::getQuizCode, v2 -> v2));

											getLmsService().createOrUpdateXStudentTest(xdsc, k.getUsername(), projections.stream()
													.map(p -> {
														if (quizIdMap.containsKey(p.getMybl5qp())) {
															return CreateStudentTestQuizRequest.builder()
																	.ordering(p.getOrdering())
																	.quizId(quizIdMap.get(p.getMybl5qp()).getId())
																	.build();
														} else {
															log.error("createOrUpdateXStudentTest Couldn't find quiz for getMybl5qp: {} ordering: {}", p.getMybl5qp(), p.getOrdering());
															return null;
														}
													})
													.filter(Objects::nonNull)
													.collect(Collectors.toList()));
										} else {
											log.warn("createOrUpdateXStudentTest Missing CQR for student: {} and ulc root: {}", k.getUsername(), xdsc);
										}
									} catch (Exception e) {
										log.error("Got error: {} when createOrUpdateXStudentTest for student: {} and ulc root: {}", e.getLocalizedMessage(), k.getUsername(), xdsc);
										e.printStackTrace();
									}
								});
					}
				}, Executors.newSingleThreadExecutor()
		).exceptionally(e -> {
			log.error("createOrUpdateXStudentTest got error: {} for xdsc: {} lct: {} dfge: {}", e.getLocalizedMessage(), xdsc, lct, dfge);
			e.printStackTrace();
			return null;
		});
	}

}