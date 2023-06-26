package vn.edu.clevai.bplog.repository.zoom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.clevai.bplog.entity.zoom.UsiZoomMeeting;

public interface UsiZoomMeetingRepository extends JpaRepository<UsiZoomMeeting, Long> {
	Optional<UsiZoomMeeting> findFirstByCode(String code);

	List<UsiZoomMeeting> findAllByUlcCodeAndPublished(String ulcCode, Boolean published);
}
