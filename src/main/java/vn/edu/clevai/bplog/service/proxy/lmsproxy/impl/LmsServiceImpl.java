package vn.edu.clevai.bplog.service.proxy.lmsproxy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.utils.Cep200ToC100Utils;
import vn.edu.clevai.common.api.annotation.RunAsAdmin;
import vn.edu.clevai.common.api.eureka.EurekaDiscoveryClientService;
import vn.edu.clevai.common.api.eureka.LookupStrategyEnum;
import vn.edu.clevai.common.api.exception.ConflictException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectWithRewardResponse;
import vn.edu.clevai.common.proxy.lms.payload.request.*;
import vn.edu.clevai.common.proxy.lms.payload.response.*;
import vn.edu.clevai.common.proxy.lms.proxy.LmsServiceProxy;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
public class LmsServiceImpl implements LmsService {
	@Value("${internal.apigate.services.lms.name}")
	private String lmsServiceName;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private LmsServiceProxy lmsServiceProxy;

	public URI buildLmsServiceUri() {
		return EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient,
				lmsServiceName, LookupStrategyEnum.RANDOM);
	}

	public DscSessionGroupResponse getDscSessionGroupDetails(String xSessionGroupId, Timestamp liveAt) {
		return lmsServiceProxy.getDscSessionGroupDetails(
				buildLmsServiceUri(),
				xSessionGroupId, liveAt).getBody().data();
	}

	@Override
	public SessionGroupStudentResponse getLastSessionGroupStudent(Long studentId) {
		return lmsServiceProxy.getLastSessionGroupStudent(buildLmsServiceUri(), studentId).getBody().data();
	}

	@Override
	public Long getGradeIdByStudentId(Long studentId) {
		return lmsServiceProxy.getGradeIdByStudentId(buildLmsServiceUri(), studentId).getBody();
	}

	@Override
	public GeneralPageResponse<ClassInfoResponse> filterClasses(Integer page, Integer size) {
		return lmsServiceProxy.filterClasses(
				buildLmsServiceUri(),
				page,
				size,
				"id",
				"DESC",
				Collections.singletonList(2L) /* GES */
		).getBody();
	}

	@Override
	public ResponseEntity<StudentGradeResponse> getStudentGrade(Long cep100StudentId) {
		return lmsServiceProxy.getStudentGrade(buildLmsServiceUri(), cep100StudentId);
	}

	@Override
	public ResponseEntity<StudentClassResponse> getStudentClass(Long studentId, String type) {
		return lmsServiceProxy.getStudentClass(buildLmsServiceUri(), studentId, type);
	}

	@Override
	public ResponseEntity<List<StudentWeeklyScheduleResponse>> getStudentWeeklyScheduled(Long studentId) {
		return lmsServiceProxy.getStudentWeeklyScheduled(buildLmsServiceUri(), studentId);
	}

	@Override
	public ResponseEntity<ClassLevelResponse> getClassLevelByClass(Long classId) {
		return lmsServiceProxy.getClassLevelByClass(buildLmsServiceUri(), classId);
	}

	@Override
	public ResponseEntity<Integer> getClassCodeIndex(Long classId) {
		return lmsServiceProxy.getClassCodeIndex(buildLmsServiceUri(), classId);
	}

	@Override
	public ResponseEntity<Integer> getClassTrainingTypeId(Long classId) {
		return lmsServiceProxy.getClassTrainingTypeId(buildLmsServiceUri(), classId);
	}

	@Override
	public String getClassDayByXDEAL(Long xDealId) {
		return lmsServiceProxy.getClassDayByXDEAL(buildLmsServiceUri(), xDealId).getBody();
	}

	@Override
	public BPDscInfoResponse getDscInfoByXdsc(String xdsc) {
		return lmsServiceProxy.getDscInfoByXdsc(buildLmsServiceUri(), xdsc).getBody();
	}

	@Override
	public List<BPDscInfoResponse> getByXdsc(String xdsc) {
		return lmsServiceProxy.getByXdsc(buildLmsServiceUri(), xdsc).getBody();
	}

	@Override
	public String findXDSC(String xclass) {
		return lmsServiceProxy.findXDSC(buildLmsServiceUri(), xclass).getBody();
	}

	@Override
	public Integer getClassLevelByXDEAL(Long xDealId) {
		return lmsServiceProxy.getClassLevelByXDEAL(buildLmsServiceUri(), xDealId).getBody();
	}

	@Override
	public XSessionGroupInfoResponse findXSESSIONGROUP(Long xDealId, String xCash) {
		return lmsServiceProxy.findXSESSIONGROUP(buildLmsServiceUri(), xDealId, xCash).getBody();
	}

	@Override
	public BPClassInfoResponse getClassByCode(String xClassId) {
		return lmsServiceProxy.getClassByCode(buildLmsServiceUri(), xClassId).getBody();
	}

	@Override
	public String getClassCodeByXDeal(Long xDealId) {
		return lmsServiceProxy.getClassCodeByXDeal(buildLmsServiceUri(), xDealId).getBody();
	}

	@Override
	public XClassInfoResponse getXClassInfoByXDeal(Long xdeal) {
		return lmsServiceProxy.getXClassInfoByXDeal(buildLmsServiceUri(), xdeal).getBody();
	}

	@Override
	public SagpRateFilterResponse getSAGPRate(Long xpt, Long xgg, Long xdfdl, String partOfXWSO, Timestamp from, Long subjectId) {
		List<SagpRateFilterResponse> responses = lmsServiceProxy.getSAGPRate(buildLmsServiceUri(), xpt, xgg, xdfdl, partOfXWSO, from, subjectId).getBody();

		if (CollectionUtils.isEmpty(responses)) {
			throw new NotFoundException("Couldn't find sagpRate with xpt: " + xpt + " xgg: " + xgg + " xdfdl: "
					+ xdfdl + " partOfXWSO: " + partOfXWSO + " from: " + from + " subjectId: " + subjectId);
		} else if (responses.size() > 1) {
			throw new ConflictException("Expected 1 sagpRate but got: " + responses.size() + " with xpt: " + xpt + " xgg: " + xgg + " xdfdl: "
					+ xdfdl + " partOfXWSO: " + partOfXWSO + " from: " + from + " subjectId: " + subjectId);
		}
		return responses.get(0);
	}

	@Override
	public List<String> getGESStudentUsername(String gg, String dfdl, String learningDate) {
		return lmsServiceProxy.getGESStudentUsername(buildLmsServiceUri(),
				Cep200ToC100Utils.toC100GradeId(gg), Cep200ToC100Utils.toC100DfdlId(dfdl), learningDate).getBody();
	}

	@Override
	public List<TeachingScheduleAssigningQuantityResponse> getGteQuantity(
			@RequestParam Integer userAccountTypeId,
			@RequestParam Integer productId,
			@RequestParam Integer gradeId,
			@RequestParam Integer subjectId,
			@RequestParam Integer classLevelId,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
	) {
		return lmsServiceProxy.getGteQuantity(
						buildLmsServiceUri(),
						userAccountTypeId,
						productId,
						gradeId,
						subjectId,
						classLevelId,
						startDate,
						endDate)
				.getBody()
				.data();
	}

	@Override
	public String createOrUpdateXDailyScheduledClass(CreateDscRequest request) {
		return lmsServiceProxy.createOrUpdateXDailyScheduledClass(buildLmsServiceUri(), request).getBody();
	}

	@Override
	public String createXDscSessionGroup(DscSessionGroupCreationRequest request) {
		return lmsServiceProxy.createXDscSessionGroup(buildLmsServiceUri(), request).getBody();
	}

	@Override
	public Map.Entry<String, String> createXDscSessionGroupAndClassMappingGes(DscSessionGroupAndClassMappingGesCreationRequest request) {
		return lmsServiceProxy.createXDscSessionGroupAndClassMappingGes(buildLmsServiceUri(), request).getBody();
	}

	@Override
	public String assignStudentsToXSessionGroupAndClassGes(SessionGroupAndClassStudentAssigningRequest request) {
		return lmsServiceProxy.assignStudentsToXSessionGroupAndClassGes(buildLmsServiceUri(), request).getBody();
	}

	@Override
	public void createOrUpdateXDscClassesMapping(String xdsc, Collection<String> xclasses) {
		lmsServiceProxy.createOrUpdateXDscClassesMapping(buildLmsServiceUri(), xdsc, xclasses);
	}

	@Override
	public void createOrUpdateXDscBattleQuiz(String xdsc, List<CreateDscBattleQuizRequest> requests) {
		lmsServiceProxy.createOrUpdateXDscBattleQuiz(buildLmsServiceUri(), xdsc, requests);
	}

	@Override
	public void createOrUpdateXDscClassLearningObjects(
			String xdsc,
			List<CreateOrUpdateDscClassLearningObjectRequest> requests
	) {
		lmsServiceProxy.createOrUpdateXDscClassLearningObjects(
				buildLmsServiceUri(), xdsc, requests
		);
	}

	@Override
	public void createOrUpdateXDailyScheduledClassLiveQuizzes(String xdsc, List<DailyScheduledClassLiveQuizCreationRequest> requests) {
		lmsServiceProxy.createOrUpdateXDailyScheduledClassLiveQuizzes(buildLmsServiceUri(), xdsc, requests);
	}

	@Override
	public void createOrUpdateXDscForward(String xdsc, List<CreateDscForwardRequest> requests) {
		lmsServiceProxy.createOrUpdateXDscForward(buildLmsServiceUri(), xdsc, requests);
	}

	@Override
	public void createOrUpdateXDscSlide(String xdsc, String dfge, List<Long> slideIds) {
		lmsServiceProxy.createOrUpdateXDscSlide(buildLmsServiceUri(), xdsc, dfge, slideIds);
	}

	@Override
	public void updateDscName(String xdsc, String name) {
		lmsServiceProxy.updateDscName(buildLmsServiceUri(), xdsc, name);
	}

	@Override
	public void createOrUpdateXDscLecturer(String xdsc, Long lecturerId) {
		lmsServiceProxy.createOrUpdateXDscLecturer(buildLmsServiceUri(), xdsc, lecturerId);
	}

	@Override
	public void createOrUpdateXDscAssistant(String xdsc, Long assistantId) {
		lmsServiceProxy.createOrUpdateXDscAssistant(buildLmsServiceUri(), xdsc, assistantId);
	}

	@Override
	public void createOrUpdateXDscStreamingConfigId(String xdsc, String url) {
		lmsServiceProxy.createOrUpdateXDscStreamingConfigId(buildLmsServiceUri(), xdsc, url);
	}

	@Override
	public void createOrUpdateXStudentTest(String xdsc, String username, List<CreateStudentTestQuizRequest> requestList) {
		lmsServiceProxy.createOrUpdateXStudentTest(buildLmsServiceUri(), xdsc, username, requestList);
	}

	@Override
	public Map<Long, Double> getProgress(String usi, List<Long> LoIds) {
		return lmsServiceProxy.getProgress(buildLmsServiceUri(), LoIds.toArray(new Long[0]), usi).getBody();
	}

	@Override
	public List<LearningObjectWithRewardResponse> getLOAndReward(Long xGrade, Long xSubject, String[] codes) {
		return lmsServiceProxy.getExerciseSummary(buildLmsServiceUri(), xGrade, xSubject, codes).getBody();
	}

	@Override
	public void createOrUpdateXAssignBackwardPackage(
			Long xgrade,
			Long xsubject,
			Long studentId,
			Integer ordering,
			List<Long> loIds) {
		lmsServiceProxy.createOrUpdateAssignBackwardPackage(buildLmsServiceUri(), xgrade, xsubject, studentId, 1, loIds.toArray(new Long[0]));
	}

	@Override
	@RunAsAdmin
	public List<GesStudentInfoResponse> getGesStudentInfos(String dscCode, String[] usernames) {
		if (usernames.length == 0) {
			return new ArrayList<>();
		}

		return lmsServiceProxy.getGesStudentInfos(buildLmsServiceUri(), dscCode, usernames).getBody().data();
	}

	@Override
	public void modifyGETTeacher(ModifyGTETeacherRequest request) {
		lmsServiceProxy.modifyGETTeacher(buildLmsServiceUri(), request);
	}

}
