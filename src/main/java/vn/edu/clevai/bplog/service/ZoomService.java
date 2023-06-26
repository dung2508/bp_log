package vn.edu.clevai.bplog.service;

import vn.edu.clevai.common.proxy.zoom.payload.request.ZoomMeetingCreationRequest;
import vn.edu.clevai.common.proxy.zoom.payload.response.ZoomMeetingCreationResponse;
import vn.edu.clevai.common.proxy.zoom.payload.response.ZoomMeetingDetailsResponse;

public interface ZoomService {
	String generateAuthorizationHeader(String apiKey, String apiSecret);

	ZoomMeetingCreationResponse createMeeting(
			String usi,
			String authorizationHeader,
			ZoomMeetingCreationRequest request
	);

	ZoomMeetingDetailsResponse getMeetingDetails(
			Long meetingId,
			String authorizationHeader
	);
}
