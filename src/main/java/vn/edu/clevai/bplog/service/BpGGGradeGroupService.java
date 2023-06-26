package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpGGGradeGroup;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpGgGradegroupResponse;

import java.util.List;

public interface BpGGGradeGroupService {
	BpGGGradeGroup findByCode(String code);

	BpGgGradegroupResponse getGGFromX(Long xGg);

	Long findXGG(String xSt);

	BpGgGradegroupResponse getST_GG(String stCode);

	List<BpGGGradeGroup> findAll();

	BpGGGradeGroup findById(Integer id);

}
