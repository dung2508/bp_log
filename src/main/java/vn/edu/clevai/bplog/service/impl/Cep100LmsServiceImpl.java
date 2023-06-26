package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.lms.payload.response.ClassInfoResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.DscSessionGroupResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.SessionGroupStudentResponse;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.service.Cep100LmsService;

import java.sql.Timestamp;

@Service
public class Cep100LmsServiceImpl extends BaseProxyService implements Cep100LmsService {

	@Autowired
	private LmsService lmsService;

	@Override
	public SessionGroupStudentResponse getSessionGroupStudent(Long studentId) {
		return lmsService.getLastSessionGroupStudent(studentId);
	}

	@Override
	public Long findXGG(Long studentId) {
		return lmsService.getGradeIdByStudentId(studentId);
	}

	@Override
	public DscSessionGroupResponse getDscSessionGroupDetails(String sessionGroupCode, Timestamp liveAt) {
		return lmsService.getDscSessionGroupDetails(sessionGroupCode, liveAt);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("getXCLASS")
	public GeneralPageResponse<ClassInfoResponse> getXPermanentClasses(Integer page, Integer size) {
		return lmsService.filterClasses(page, size);
	}
}
