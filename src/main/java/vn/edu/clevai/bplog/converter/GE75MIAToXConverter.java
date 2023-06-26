package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;

import java.util.List;

@Slf4j
@Component
public class GE75MIAToXConverter extends BaseXConverter {

	public GE75MIAToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
	}

	@Override
	protected void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		createOrUpdateXDscSlide(xdsc, lct, dfge, ulcs.get(0));
	}
}