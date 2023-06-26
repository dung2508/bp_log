package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.payload.request.teacher.TeacherRegisterRequest;
import vn.edu.clevai.bplog.payload.response.teacher.GetAvailableResponse;
import vn.edu.clevai.bplog.payload.response.teacher.ProductGradeShiftDetailResponse;
import vn.edu.clevai.bplog.payload.response.teacher.YourselfResponse;

public interface TeacherTentativeService {
	ProductGradeShiftDetailResponse getProductGradeShiftInfo(String code);

	GetAvailableResponse getAvailableSlot(String myusi, Long startTime, Long endTime);

	String convertCodeShift(String code);

	YourselfResponse getYourself();

	void doSave(String myusi, Long start, Long end, TeacherRegisterRequest request);
}
