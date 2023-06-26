package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.common.enumtype.UsiTypeEnum;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;

import java.util.List;

@Component
@Slf4j
public class DL40MIToXConverter extends BaseXConverter {

	public DL40MIToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		BpUniqueLearningComponent ulc = ulcs.get(0);

		createOrUpdateXDscClassesMapping(xdsc, lct, dfge, ulc);

		createOrUpdateXDscSlide(xdsc, lct, dfge, ulc);

		createOrUpdateXDscStreamingConfigId(xdsc, lct, dfge, ulc);

		createOrUpdateXDscLecturer(xdsc, findFirstTeacher(ulc.getCode(), UsiTypeEnum.DTE));

		createOrUpdateXDscAssistant(xdsc, findFirstTeacher(ulc.getCode(), UsiTypeEnum.QO));
	}
}