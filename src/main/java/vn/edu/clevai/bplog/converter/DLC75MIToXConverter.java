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
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.lms.payload.request.CreateDscRequest;

import java.util.List;

@Component
@Slf4j
public class DLC75MIToXConverter extends BaseXConverter {

	public DLC75MIToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {

		if (CollectionUtils.isEmpty(ulcs) || ulcs.size() > 1) {
			throw new BadRequestException("ulcs size must be 1 for DLC75MIToXConverter!!!");
		}

		BpUniqueLearningComponent ulc = ulcs.get(0);
		log.info("Started converting a {} ulc (code = {}) to XDailyScheduledClass (code = {})", lct, ulc.getCode(), xdsc);

		getLmsService().createOrUpdateXDailyScheduledClass(
				CreateDscRequest.builder()
						.classCode(ulc.getCode())
						.className(getClassName(ulc.getCode()))
						.classType(2)
						.liveAt(DateUtils.addSecondToTimestamp(ulc.getMyCap().getStartTime(), -5 * 60))
						.active(ulc.getPublished())
						.classLevelId(Cep200ToC100Utils.toC100DfdlId(ulc.getMyDfdl()))
						.coachDuration(2700)
						.countdownBefore(120)
						.gradeId(Cep200ToC100Utils.toC100GradeId(ulc.getMyGg()))
						.subjectId(1L)
						.liveDuration(7200)
						.streamingTypeId(4L)
						.trainingTypeId(1L)
						.maxActiveStudents(30)
						.hasTestBattle(true)
						.lateComeAllowed(300)
						.build()
		);

		log.info("Finished Converting a {} ulc (code = {}) to XDailyScheduledClass (code = {}) successfully", lct, ulc.getCode(), xdsc);
	}
}