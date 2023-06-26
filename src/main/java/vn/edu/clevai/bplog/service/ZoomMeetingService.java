package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.zoom.ZoomMeeting;

public interface ZoomMeetingService {
	ZoomMeeting findById(Long id);

	ZoomMeeting renewStartUrl(Long meetingId);

	ZoomMeeting create(String usi, String ulc, String clag);

	ZoomMeeting createAndAssign(String usi, String ulc, String clag) throws Exception;
}
