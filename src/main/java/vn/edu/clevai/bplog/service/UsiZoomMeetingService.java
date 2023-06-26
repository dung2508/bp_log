package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.zoom.UsiZoomMeeting;

import java.util.List;

public interface UsiZoomMeetingService {
	UsiZoomMeeting createOrUpdate(UsiZoomMeeting usiZoomMeeting);

	void unpublished(String usiZoomMeetingCode);

	List<UsiZoomMeeting> findByUlcAndPublished(String ulc, Boolean published);
}
