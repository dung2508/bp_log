package vn.edu.clevai.bplog.service;

import org.springframework.http.ResponseEntity;
import vn.edu.clevai.bplog.entity.Term;
import vn.edu.clevai.common.proxy.bplog.payload.response.TrimesterResponse;

import java.sql.Timestamp;
import java.util.List;

public interface TermService {
	ResponseEntity<List<TrimesterResponse>> getAllTrimesters();

	String getByTime(Timestamp time);

	List<Term> getAll();
}
