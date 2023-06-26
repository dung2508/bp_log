package vn.edu.clevai.bplog.service;

import org.springframework.data.domain.Pageable;
import vn.edu.clevai.bplog.payload.request.filter.TeacherRegisterToCapAssigneeFilter;
import vn.edu.clevai.bplog.payload.request.teacher.RegisToCapPublishRequest;
import vn.edu.clevai.bplog.payload.request.teacher.TeachingScheduledQuantityRequest;
import vn.edu.clevai.bplog.payload.response.BPProductGradeClassLevelResponse;
import vn.edu.clevai.bplog.payload.response.ChildResponse;
import vn.edu.clevai.bplog.payload.response.teacher.tocap.*;
import vn.edu.clevai.common.api.model.GeneralPageResponse;

import java.util.List;

public interface TeacherRegisToCapService {
	List<QuantityResponse> gteQuantity(Integer productId, Integer gradeId, Integer classLevelId, String startDate,
			String endDate, Integer subjectId);

	List<QuantityResponse> dteQuantity(Integer productId, Integer gradeId, Integer classLevelId, String startDate,
			String endDate, Integer subjectId);

	List<QuantityResponse> qoQuantity(Integer productId, Integer gradeId, Integer classLevelId, String startDate,
			String endDate, Integer subjectId);

	List<QuantityResponse> lteQuantity(Integer productId, Integer gradeId, Integer classLevelId, String startDate,
			String endDate, Integer subjectId);

	void publish(RegisToCapPublishRequest regisToCap, String userName);

	BPProductGradeClassLevelResponse getListProductGradeClassLevel(String startDate);

	GeneralPageResponse<UserAccountResponse> findAllTeacherWithFilter(String name, Pageable pageable);

	BpTeachingScheduleAssigningSlotGroupResponse getAvailableSlots(String bpp);

	List<TeachingScheduleAssigningQuantityResponse> dteQuantity(TeachingScheduledQuantityRequest request);

	GeneralPageResponse<TeacherApprovedAndReportResponse> getAssignee(
			TeacherRegisterToCapAssigneeFilter teacherRegisterToCapAssigneeFilter, Pageable pageable);

	List<UserTypeResponse> findAll();

	List<ChildResponse> getAllMenu();
}
