package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpPRD;

public interface BpPeriodService {

	BpPRD findByLengthAndUnit(Integer length, String unit);
}
