package vn.edu.clevai.bplog.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.entity.Term;
import vn.edu.clevai.bplog.repository.TermRepository;
import vn.edu.clevai.bplog.service.TermService;
import vn.edu.clevai.common.proxy.bplog.payload.response.TrimesterResponse;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TermServiceImpl implements TermService {

	@Autowired
	private TermRepository termRepository;

	@Autowired
	private ModelMapper modelMapper;


	@Override
	public ResponseEntity<List<TrimesterResponse>> getAllTrimesters() {
		List<Term> trimesters = termRepository.findAll();

		return ResponseEntity.ok(trimesters.stream()
				.map(
						item -> modelMapper.map(item, TrimesterResponse.class)
				).collect(Collectors.toList()));
	}

	@Override
	public String getByTime(Timestamp time) {
		return null;
	}

	@Override
	public List<Term> getAll() {
		return termRepository.findAll();
	}
}
