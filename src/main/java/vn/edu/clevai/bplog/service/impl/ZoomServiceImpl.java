package vn.edu.clevai.bplog.service.impl;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.ZoomService;
import vn.edu.clevai.common.api.constant.ClevaiConstant;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.zoom.payload.request.ZoomMeetingCreationRequest;
import vn.edu.clevai.common.proxy.zoom.payload.response.ZoomMeetingCreationResponse;
import vn.edu.clevai.common.proxy.zoom.payload.response.ZoomMeetingDetailsResponse;
import vn.edu.clevai.common.proxy.zoom.proxy.ZoomProxy;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Objects;

@Service
@Slf4j
public class ZoomServiceImpl implements ZoomService {
	@Autowired
	private ZoomProxy zoomProxy;

	@Override
	public String generateAuthorizationHeader(String apiKey, String apiSecret) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		SecretKey key = Keys.hmacShaKeyFor(apiSecret.getBytes(StandardCharsets.UTF_8));

		Timestamp now = DateUtils.now();
		Timestamp expiry = DateUtils.addSecondToTimestamp(now, ClevaiConstant.Livestream.JWT_EXPIRATION_TIME);

		String jwt = Jwts
				.builder()
				.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
				.setIssuer(apiKey)
				.setExpiration(expiry)
				.signWith(key, signatureAlgorithm)
				.compact();

		return String.format("Bearer %s", jwt);
	}

	@Override
	@SneakyThrows
	public ZoomMeetingCreationResponse createMeeting(
			String usi,
			String authorizationHeader,
			ZoomMeetingCreationRequest request
	) {
		try {
			return zoomProxy
					.createMeeting(
							usi,
							authorizationHeader,
							request
					)
					.getBody();
		} catch (NotFoundException e) {
			String message = e.getMessage();

			if (Objects.isNull(message)) {
				message = String.format("User '%s' not found", usi);
			}

			throw new NotFoundException(
					String.format("Error in communicating with Zoom: %s", message)
			);
		}
	}

	@Override
	public ZoomMeetingDetailsResponse getMeetingDetails(
			Long meetingId,
			String authorizationHeader
	) {
		return zoomProxy.getMeetingDetails(meetingId, authorizationHeader).getBody();
	}
}
