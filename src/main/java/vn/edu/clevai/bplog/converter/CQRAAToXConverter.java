package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.BpLCPService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CQRAAToXConverter extends BaseXConverter {

	private final BpLCPService bpLCPService;

	public CQRAAToXConverter(BpClagClassgroupService bpClagClassgroupService, AuthoringService authoringService, LmsService lmsService, UserService userService, ContentItemService contentItemService, CuiService cuiService, BpLCPService bpLCPService) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService);
		this.bpLCPService = bpLCPService;
	}

	@Override
	public void execute(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		List<String> loCodes = getContentItemService().getCQR(ulcs.stream().map(BpUniqueLearningComponent::getCode).collect(Collectors.toList()));

		if (!CollectionUtils.isEmpty(loCodes)) {
			Integer countNumberOfQuiz = bpLCPService.findLCPKids(ulcs.get(0).getMyLct().getCode())
					.stream().map(BpLCP::getNolcp)
					.reduce(Integer::sum)
					.orElse(0);
			getAuthoringService().createOrUpdateXTestLO(xdsc, countNumberOfQuiz, loCodes);
		} else {
			log.warn("createOrUpdateXTestLO got 0 LO to create!!!");
		}
	}
}