package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpCuiContentUserUlc;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectDetailResponse;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AQR0AAToXConverter extends BaseXConverter {

	public AQR0AAToXConverter(
			BpClagClassgroupService clagService,
			AuthoringService authoringService,
			LmsService lmsService,
			UserService userService,
			ContentItemService contentItemService,
			CuiService cuiService) {
		super(
				clagService,
				authoringService,
				lmsService,
				userService,
				contentItemService,
				cuiService
		);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		List<String> ulcCodes = ulcs.stream().map(BpUniqueLearningComponent::getCode).collect(Collectors.toList());

		Long subjectId = 1L;
		Long gradeId = ulcs.stream().findAny().map(ulc -> Cep200ToC100Utils.toC100GradeId(ulc.getMyGg())).orElse(5L);

		Map<String, List<BpCuiContentUserUlc>> stCuis = getCuiService().findCuiByUlcInAndUst(
						ulcCodes, USTEnum.ST.getName()
				)
				.stream()
				.filter(cui -> cui.getMyUsi() != null)
				.collect(
						Collectors.groupingBy(
								cui -> cui.getMyUsi().getCode()
						)
				);

		stCuis.forEach(
				(k, v) -> convert(gradeId, subjectId, k, v)
		);
	}

	private void convert(Long gradeId, Long subjectId, String usi, List<BpCuiContentUserUlc> cuis) {
		List<String> ulcCodes = cuis.stream()
				.map(BpCuiContentUserUlc::getMyUlc)
				.filter(Objects::nonNull)
				.map(BpUniqueLearningComponent::getCode).collect(Collectors.toList());

		List<String> loCodes = getCuiService().findCuiByUlcInAndUst(ulcCodes, USTEnum.MN.getName())
				.stream()
				.map(BpCuiContentUserUlc::getMyCti)
				.filter(Objects::nonNull)
				.map(BpContentItem::getMyLo)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());

		if (loCodes.isEmpty()) {
			throw new NotFoundException("No valid bl4 found");
		}

		List<Long> loIds = getAuthoringService()
				.getLoByCodes(loCodes)
				.stream().map(LearningObjectDetailResponse::getId)
				.collect(Collectors.toList());

		if (loIds.isEmpty()) {
			throw new NotFoundException("No valid bl4 found in AUTHORING");
		}

		Long studentId = getUserService().getUserProfile(usi).getId();

		getLmsService().createOrUpdateXAssignBackwardPackage(gradeId, subjectId, studentId, 1, loIds);
	}
}