package vn.edu.clevai.bplog.repository.zoom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import vn.edu.clevai.bplog.entity.zoom.UsiZoomMeeting;
import vn.edu.clevai.bplog.entity.zoom.ZoomMeeting;

public interface ZoomMeetingRepository extends JpaRepository<ZoomMeeting, Long> {
	Optional<ZoomMeeting> findFirstByCode(String code);

	@Query(
			value = "SELECT zm.* " +
					"FROM zoom_meeting zm " +
					"         JOIN usi_zoom_meeting usiz ON usiz.zoom_meeting_code = zm.code " +
					"WHERE usiz.ulc_code = :ulcCode " +
					"  AND usiz.usi = :usi",
			nativeQuery = true
	)
	Optional<ZoomMeeting> findFirstByUlcAndUsi(String ulcCode, String usi);

}
