package vn.edu.clevai.bplog.service;

import org.springframework.http.ResponseEntity;

import vn.edu.clevai.common.proxy.bplog.payload.response.AcademicYearResponse;

import java.sql.Timestamp;
import java.util.List;

public interface AcademicYearService {
	ResponseEntity<List<AcademicYearResponse>> getAllAcademicYear();

	String getByTime(Timestamp time);
}
