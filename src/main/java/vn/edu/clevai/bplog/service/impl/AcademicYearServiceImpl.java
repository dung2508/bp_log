package vn.edu.clevai.bplog.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.edu.clevai.bplog.entity.AcademicYear;
import vn.edu.clevai.common.proxy.bplog.payload.response.AcademicYearResponse;
import vn.edu.clevai.bplog.repository.AcademicYearRepository;
import vn.edu.clevai.bplog.service.AcademicYearService;

@Service
public class AcademicYearServiceImpl implements AcademicYearService {
	@Autowired
	private AcademicYearRepository academicYearRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public ResponseEntity<List<AcademicYearResponse>> getAllAcademicYear() {
		List<AcademicYear> listAcademicYear = academicYearRepository.findAll();
		return new ResponseEntity<>(listAcademicYear.stream()
				.map(item -> modelMapper.map(item, AcademicYearResponse.class))
				.collect(Collectors.toList()), HttpStatus.OK);
	}

	@Override
	public String getByTime(Timestamp time) {
		return null;
	}
}
