package vn.edu.clevai.bplog.service.clag;

import vn.edu.clevai.bplog.payload.response.cep100.*;
import vn.edu.clevai.common.proxy.lms.payload.response.SessionGroupStudentResponse;

import java.util.List;

public interface CEP100Service {
	/**
	 * @param username
	 * @return
	 */
	CEP100StudentResponse getCEPStudent(String username);

	/**
	 * @param cep100StudentId
	 * @return
	 */
	CEP100StudentGradeResponse getCEP100StudentGrade(Long cep100StudentId);

	/**
	 * @param cep100StudentId
	 * @param clagType
	 * @return
	 */
	CEP100StudentClassResponse getCEPStudentClass(Long cep100StudentId, String clagType);

	/**
	 * @param classId
	 * @return
	 */
	List<CEP100LearningScheduleClassResponse> getCEP100LearningSchedule(Long classId);

	/**
	 * @param classId
	 * @return
	 */
	CEP100ClassLevelResponse getCEP100ClassLevel(Long classId);

	/**
	 * @param classId
	 * @return
	 */
	Integer getCEP100ClassCodeIndex(Long classId);

	/**
	 * @param classId
	 * @return
	 */
	Integer getCEP100TrainingTypeId(Long classId);


	/**
	 * @param studentId
	 * @return
	 */
	SessionGroupStudentResponse getSessionGroupStudent(Long studentId);
}
