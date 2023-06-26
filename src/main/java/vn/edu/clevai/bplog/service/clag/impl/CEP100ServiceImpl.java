package vn.edu.clevai.bplog.service.clag.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.payload.response.cep100.*;
import vn.edu.clevai.bplog.service.clag.CEP100Service;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.lms.payload.response.*;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CEP100ServiceImpl extends BaseProxyService implements CEP100Service {

	private final LmsService lmsService;

	private final UserService userService;

	public CEP100ServiceImpl(LmsService lmsService, UserService userService) {
		this.lmsService = lmsService;
		this.userService = userService;
	}

	@Override
	public CEP100StudentResponse getCEPStudent(String username) {
		try {
			ResponseEntity<UserAccountResponse> responseEntity = userService.getStudentProfile(username);
			if (Objects.nonNull(responseEntity) && responseEntity.hasBody()) {
				UserAccountResponse response = responseEntity.getBody();
				return CEP100StudentResponse.builder().studentId(response.getId()).build();
			}
		} catch (Exception e) {
			log.error("Error to get student by username " + username, e);
		}
		return null;
	}

	@Override
	public CEP100StudentGradeResponse getCEP100StudentGrade(Long cep100StudentId) {
		try {
			ResponseEntity<StudentGradeResponse> response = lmsService.getStudentGrade(cep100StudentId);
			if (Objects.nonNull(response) && response.hasBody()) {
				StudentGradeResponse sgs = response.getBody();
				return new CEP100StudentGradeResponse(sgs.getGradeId());
			}
		} catch (Exception e) {
			log.error("Error to get student grade with student id " + cep100StudentId, e);
		}
		return null;
	}

	@Override
	public CEP100StudentClassResponse getCEPStudentClass(Long cep100StudentId, String clagType) {
		try {
			ResponseEntity<StudentClassResponse> responseEntity = lmsService.getStudentClass(cep100StudentId, ClagType.PERMANANT.getCode());
			if (Objects.nonNull(responseEntity) && responseEntity.hasBody()) {
				StudentClassResponse clsResponse = responseEntity.getBody();
				return new CEP100StudentClassResponse(clsResponse.getClassId(), clsResponse.getClassCode(),
						clsResponse.getClassName());
			}
		} catch (Exception e) {
			log.error("Error to get student class", e);
		}
		return null;
	}

	@Override
	public List<CEP100LearningScheduleClassResponse> getCEP100LearningSchedule(Long studentId) {
		try {
			ResponseEntity<List<StudentWeeklyScheduleResponse>> entity = lmsService.getStudentWeeklyScheduled(studentId);
			if (Objects.nonNull(entity) && entity.hasBody()) {
				List<StudentWeeklyScheduleResponse> listRes = entity.getBody();
				if (!CollectionUtils.isEmpty(listRes)) {
					return listRes.stream()
							.map(k -> new CEP100LearningScheduleClassResponse(k.getCode(), k.getDayOfWeek()))
							.collect(Collectors.toList());
				}
			}
		} catch (Exception e) {
			log.error("Error to learning schedule class", e);
		}
		return null;
	}

	@Override
	public CEP100ClassLevelResponse getCEP100ClassLevel(Long classId) {
		try {
			ResponseEntity<ClassLevelResponse> entity = lmsService.getClassLevelByClass(classId);
			if (Objects.nonNull(entity) && entity.hasBody()) {
				ClassLevelResponse response = entity.getBody();
				return new CEP100ClassLevelResponse(response.getId().intValue(), response.getName(),
						response.getCode());
			}
		} catch (Exception e) {
			log.error("Error to get class level of class " + classId, e);
		}
		return null;
	}

	@Override
	public Integer getCEP100ClassCodeIndex(Long classId) {
		try {
			ResponseEntity<Integer> entity = lmsService.getClassCodeIndex(classId);
			if (Objects.nonNull(entity) && entity.hasBody()) {
				return entity.getBody();
			}
		} catch (Exception e) {
			log.error("Error to get class code index of class " + classId, e);
		}
		return null;
	}

	@Override
	public Integer getCEP100TrainingTypeId(Long classId) {
		try {
			ResponseEntity<Integer> entity = lmsService.getClassTrainingTypeId(classId);
			if (Objects.nonNull(entity) && entity.hasBody()) {
				return entity.getBody();
			}
		} catch (Exception e) {
			log.error("Error to get class training type of class " + classId, e);
		}
		return null;
	}

	@Override
	public SessionGroupStudentResponse getSessionGroupStudent(Long studentId) {
		return lmsService.getLastSessionGroupStudent(studentId);
	}

}
