package vn.edu.clevai.bplog.service.proxy.lmsproxy;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectWithRewardResponse;
import vn.edu.clevai.common.proxy.lms.payload.request.*;
import vn.edu.clevai.common.proxy.lms.payload.response.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author trungnt9
 */
public interface LmsService {

	DscSessionGroupResponse getDscSessionGroupDetails(String xSessionGroupId, Timestamp liveAt);

	SessionGroupStudentResponse getLastSessionGroupStudent(Long studentId);

	Long getGradeIdByStudentId(Long studentId);

	GeneralPageResponse<ClassInfoResponse> filterClasses(Integer page, Integer size);

	ResponseEntity<StudentGradeResponse> getStudentGrade(Long cep100StudentId);

	ResponseEntity<StudentClassResponse> getStudentClass(Long studentId, String type);

	ResponseEntity<List<StudentWeeklyScheduleResponse>> getStudentWeeklyScheduled(Long studentId);

	ResponseEntity<ClassLevelResponse> getClassLevelByClass(Long classId);

	ResponseEntity<Integer> getClassCodeIndex(Long classId);

	ResponseEntity<Integer> getClassTrainingTypeId(Long classId);

	String getClassDayByXDEAL(Long xDealId);

	BPDscInfoResponse getDscInfoByXdsc(String xdsc);

	List<BPDscInfoResponse> getByXdsc(String xdsc);

	String findXDSC(String xclass);

	Integer getClassLevelByXDEAL(Long xDealId);

	XSessionGroupInfoResponse findXSESSIONGROUP(Long xDealId, String xCash);

	BPClassInfoResponse getClassByCode(String xClassId);

	String getClassCodeByXDeal(Long xDealId);

	XClassInfoResponse getXClassInfoByXDeal(Long xdeal);

	SagpRateFilterResponse getSAGPRate(Long xpt,
									   Long xgg,
									   Long xdfdl,
									   String partOfXWSO,
									   Timestamp from,
									   Long subjectId);

	List<String> getGESStudentUsername(String gg, String dfdl, String learningDate);

	List<TeachingScheduleAssigningQuantityResponse> getGteQuantity(
			@RequestParam Integer userAccountTypeId,
			@RequestParam Integer productId,
			@RequestParam Integer gradeId,
			@RequestParam Integer subjectId,
			@RequestParam Integer classLevelId,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	);

	String createOrUpdateXDailyScheduledClass(CreateDscRequest request);

	String createXDscSessionGroup(DscSessionGroupCreationRequest request);

	Map.Entry<String, String> createXDscSessionGroupAndClassMappingGes(DscSessionGroupAndClassMappingGesCreationRequest request);

	String assignStudentsToXSessionGroupAndClassGes(SessionGroupAndClassStudentAssigningRequest request);

	void createOrUpdateXDscClassesMapping(String xdsc, Collection<String> xclasses);

	void createOrUpdateXDscBattleQuiz(String xdsc, List<CreateDscBattleQuizRequest> requests);

	void createOrUpdateXDscClassLearningObjects(
			String xdsc,
			List<CreateOrUpdateDscClassLearningObjectRequest> requests
	);

	void createOrUpdateXDailyScheduledClassLiveQuizzes(String xdsc, List<DailyScheduledClassLiveQuizCreationRequest> requests);

	void createOrUpdateXDscForward(String xdsc, List<CreateDscForwardRequest> requests);

	void createOrUpdateXDscSlide(String xdsc, String dfge, List<Long> slideIds);

	void updateDscName(String xdsc, String name);

	void createOrUpdateXDscLecturer(String xdsc, Long lecturerId);

	void createOrUpdateXDscAssistant(String xdsc, Long assistantId);

	void createOrUpdateXDscStreamingConfigId(String xdsc, String url);

	void createOrUpdateXStudentTest(String xdsc, String username, List<CreateStudentTestQuizRequest> requestList);

	Map<Long, Double> getProgress(String usi, List<Long> LoIds);

	List<LearningObjectWithRewardResponse> getLOAndReward(Long xgrade, Long xsubject, String[] codes);

	void createOrUpdateXAssignBackwardPackage(
			Long gradeId,
			Long subjectId,
			Long studentId,
			Integer ordering,
			List<Long> loIds);

	List<GesStudentInfoResponse> getGesStudentInfos(String dscCode, String[] usernames);

	void modifyGETTeacher(ModifyGTETeacherRequest request);

}
