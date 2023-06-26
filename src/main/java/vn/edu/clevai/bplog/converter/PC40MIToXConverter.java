package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.projection.PC40MIInfoProjection;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.proxy.authoring.payload.request.GetOrCreateTestRequest;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class PC40MIToXConverter extends BaseXConverter {

	public PC40MIToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		BpUniqueLearningComponent ulc = ulcs.get(0);
		log.info("Started converting a {} ulc (code = {}) to createOrUpdateXTest (code = {})", lct, ulc.getCode(), xdsc);

		PC40MIInfoProjection projection = getContentItemService().getPC40MICti(ulc.getCode());

		if (Objects.isNull(projection)) {
			log.warn("Missing cti to convert xdsc: {} lct: {} dfge: {}", xdsc, lct, dfge);
		}

		getAuthoringService().createOrUpdateXTest(
				GetOrCreateTestRequest.builder()
						.code(ulc.getMyParent())
						.start(ulc.getMyCap().getStartTime())
						.end(ulc.getMyCap().getEndTime())
						.gradeId(Cep200ToC100Utils.toC100GradeId(ulc.getMyGg()))
						.subjectId(Cep200ToC100Utils.toC100SubjectId(ulc.getMyPt()))
						.testType(1)
						.name(Objects.nonNull(projection) ? projection.getName() : "")
						.expectCompleteInSeconds(Objects.nonNull(projection) ? projection.getDuration() : 0)
						.build()
		);

		log.info("Finished Converting a {} ulc (code = {}) to createOrUpdateXTest (code = {}) successfully", lct, ulc.getCode(), xdsc);
	}
}