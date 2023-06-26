package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.proxy.authoring.payload.request.GetOrCreateTestRequest;

import java.util.List;

@Component
@Slf4j
public class MC1MNToXConverter extends BaseXConverter {

	public MC1MNToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {

		if (CollectionUtils.isEmpty(ulcs) || ulcs.size() > 1) {
			throw new BadRequestException("ulcs size must be 1 for MC1MNToXConverter!!!");
		}

		BpUniqueLearningComponent ulc = ulcs.get(0);
		log.info("Started converting a {} ulc (code = {}) to createOrUpdateXTest (code = {})", lct, ulc.getCode(), xdsc);

		getAuthoringService().createOrUpdateXTest(
				GetOrCreateTestRequest.builder()
						.code(ulc.getCode())
						.start(ulc.getMyCap().getStartTime())
						.end(ulc.getMyCap().getEndTime())
						.gradeId(Cep200ToC100Utils.toC100GradeId(ulc.getMyGg()))
						.subjectId(Cep200ToC100Utils.toC100SubjectId(ulc.getMyPt()))
						.testType(1)
						.expectCompleteInSeconds(0)
						.name("")
						.build()
		);

		log.info("Finished Converting a {} ulc (code = {}) to createOrUpdateXTest (code = {}) successfully", lct, ulc.getCode(), xdsc);
	}
}