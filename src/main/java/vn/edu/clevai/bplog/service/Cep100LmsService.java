package vn.edu.clevai.bplog.service;

import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.ClassInfoResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.DscSessionGroupResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.SessionGroupStudentResponse;

import java.sql.Timestamp;

public interface Cep100LmsService {
	SessionGroupStudentResponse getSessionGroupStudent(Long studentId);

	Long findXGG(Long studentId);

	DscSessionGroupResponse getDscSessionGroupDetails(String sessionGroupCode, Timestamp liveAt);

	GeneralPageResponse<ClassInfoResponse> getXPermanentClasses(Integer page, Integer size);
}
