package vn.edu.clevai.bplog.converter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.common.enumtype.UsiTypeEnum;
import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.projection.CtiSlideInfoProjection;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.authoring.payload.request.GetOrCreateSlideRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
public abstract class BaseXConverter {
	private final BpClagClassgroupService bpClagClassgroupService;
	private final AuthoringService authoringService;
	private final LmsService lmsService;
	private final UserService userService;
	private final ContentItemService contentItemService;
	private final CuiService cuiService;

	public BaseXConverter(
			BpClagClassgroupService bpClagClassgroupService,
			AuthoringService authoringService,
			LmsService lmsService,
			UserService userService,
			ContentItemService contentItemService,
			CuiService cuiService
	) {
		this.bpClagClassgroupService = bpClagClassgroupService;
		this.authoringService = authoringService;
		this.lmsService = lmsService;
		this.userService = userService;
		this.contentItemService = contentItemService;
		this.cuiService = cuiService;
	}

	protected String getClassName(String ulcCode) {
		BpContentItem cti = contentItemService.findUlcCti(ulcCode);

		if (Objects.isNull(cti) || StringUtils.isBlank(cti.getName())) {
			log.warn("Missing getClassName for ulcCode: {}", ulcCode);
			return null;
		} else {
			return cti.getName();
		}
	}

	public void convert(String xdsc, String lct, String dfge, BpUniqueLearningComponent ulc) {
		convert(xdsc, lct, dfge, Collections.singletonList(ulc));
	}

	public void convert(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		if (CollectionUtils.isEmpty(ulcs)) {
			throw new BadRequestException("Missing ulc to convert for xdsc: " + xdsc + " lct: " + lct + " dfge: " + dfge);
		}

		execute(xdsc, lct, dfge, ulcs);
	}

	void createOrUpdateXDscClassesMapping(String xdsc, String lct, String dfge, BpUniqueLearningComponent ulc) {
		Set<String> xclasses = getBpClagClassgroupService()
				.findClagFromULC(ulc.getCode())
				.stream()
				.map(BpClagClassgroup::getXclass)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toSet());

		if (!CollectionUtils.isEmpty(xclasses)) {
			getLmsService().createOrUpdateXDscClassesMapping(xdsc, xclasses);
		} else {
			log.warn("createOrUpdateXDscClassesMapping 0 dcm with ulcRoot: {} lct: {} dfge: {}", ulc.getCode(), lct, dfge);
		}
	}

	void createOrUpdateXDscSlide(String xdsc, String lct, String dfge, BpUniqueLearningComponent ulc) {
		List<CtiSlideInfoProjection> projections = getContentItemService().getUlcSlideUrls(ulc.getCode());

		if (!CollectionUtils.isEmpty(projections)) {
			getLmsService().createOrUpdateXDscSlide(xdsc, dfge, getAuthoringService().getOrCreateSlideByUrls(projections.stream()
					.map(p -> GetOrCreateSlideRequest.builder()
							.code(p.getCtiCode())
							.url(p.getSlideUrl())
							.name(p.getName())
							.build())
					.collect(Collectors.toList())));
		} else {
			log.warn("createOrUpdateXDscSlide 0 dscSlide with ulcRoot: {} lct: {} dfge: {}", ulc.getCode(), lct, dfge);
		}
	}

	public Long findFirstTeacher(String ulcCode, UsiTypeEnum ust) {
		String usi = cuiService.findFirstCuiUsi(ulcCode, ust.getName());

		if (Objects.isNull(usi)) {
			return null;
		} else {
			return userService.getUserProfile(usi).getId();
		}
	}

	public void createOrUpdateXDscLecturer(String xdsc, Long lecturerId) {
		if (Objects.nonNull(lecturerId)) {
			lmsService.createOrUpdateXDscLecturer(xdsc, lecturerId);
		} else {
			log.warn("createOrUpdateXDscLecturer for xdsc got null lecturerId!!!");
		}
	}

	public void createOrUpdateXDscAssistant(String xdsc, Long assistantId) {
		if (Objects.nonNull(assistantId)) {
			lmsService.createOrUpdateXDscAssistant(xdsc, assistantId);
		} else {
			log.warn("createOrUpdateXDscAssistant for xdsc got null assistantId!!!");
		}
	}

	public void createOrUpdateXDscStreamingConfigId(String xdsc, String lct, String dfge, BpUniqueLearningComponent ulc) {
		String url = getContentItemService().getUlcVideoUrl(ulc.getCode());

		if (StringUtils.isNotBlank(url)) {
			lmsService.createOrUpdateXDscStreamingConfigId(xdsc, url);
		} else {
			throw new NotFoundException("Couldn't find video url for ulc: " + ulc.getCode() + " when createOrUpdateXDscStreamingConfigId for xdsc: " + xdsc + " lct: " + lct + " dfge: " + dfge);
		}
	}

	protected abstract void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs);
}
