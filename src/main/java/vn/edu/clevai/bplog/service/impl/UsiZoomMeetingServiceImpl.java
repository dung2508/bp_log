package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.entity.zoom.UsiZoomMeeting;
import vn.edu.clevai.bplog.repository.zoom.UsiZoomMeetingRepository;
import vn.edu.clevai.bplog.service.UsiZoomMeetingService;

import java.util.List;
import java.util.Objects;

@Service
public class UsiZoomMeetingServiceImpl implements UsiZoomMeetingService {
	private final UsiZoomMeetingRepository usiZoomMeetingRepository;

	public UsiZoomMeetingServiceImpl(UsiZoomMeetingRepository usiZoomMeetingRepository) {
		this.usiZoomMeetingRepository = usiZoomMeetingRepository;
	}

	@Override
	@Transactional
	public UsiZoomMeeting createOrUpdate(UsiZoomMeeting usiZoomMeeting) {
		UsiZoomMeeting result = usiZoomMeetingRepository.findFirstByCode(usiZoomMeeting.getCode())
				.orElseGet(() -> usiZoomMeetingRepository.save(usiZoomMeeting));
		result.setPublished(usiZoomMeeting.getPublished());
		return result;
	}

	@Override
	@Transactional
	public void unpublished(String usiZoomMeetingCode) {
		UsiZoomMeeting usiZoomMeeting = usiZoomMeetingRepository.findFirstByCode(
				usiZoomMeetingCode
		).orElse(null);
		if (Objects.nonNull(usiZoomMeeting)) usiZoomMeeting.setPublished(false);
	}

	@Override
	public List<UsiZoomMeeting> findByUlcAndPublished(String ulc, Boolean published) {
		return usiZoomMeetingRepository.findAllByUlcCodeAndPublished(ulc, published);
	}
}
