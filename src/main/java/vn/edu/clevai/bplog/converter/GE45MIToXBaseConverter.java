package vn.edu.clevai.bplog.converter;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import vn.edu.clevai.bplog.repository.BpClagClassgroupRepository;
import vn.edu.clevai.bplog.repository.zoom.ZoomMeetingRepository;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.BpClagPODService;
import vn.edu.clevai.bplog.service.BpPodProductOfDealService;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;

@Slf4j
public class GE45MIToXBaseConverter extends GEToXBaseConverter {

	private static final Long TRAINING_TYPE_ID = 5L;

	public GE45MIToXBaseConverter(
			BpClagClassgroupService bpClagClassgroupService,
			AuthoringService authoringService,
			LmsService lmsService,
			UserService userService,
			ContentItemService contentItemService,
			CuiService cuiService,
			BpClagClassgroupRepository clagRepository,
			ZoomMeetingRepository zoomMeetingRepository,
			BpClagPODService bpClagPODService,
			BpPodProductOfDealService podService,
			ModelMapper modelMapper
	) {
		super(bpClagClassgroupService, authoringService, lmsService, userService, contentItemService, cuiService, clagRepository, zoomMeetingRepository, bpClagPODService, podService, modelMapper, TRAINING_TYPE_ID);
	}

}