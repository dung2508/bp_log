package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpPTProductType;

import java.util.List;

public interface PTService {

	List<BpPTProductType> findAll();

	List<BpPTProductType> findProductTeacherScheduleAssign();

	BpPTProductType findByCode(String code);

	BpPTProductType findById(Integer id);

}
