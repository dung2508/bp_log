package vn.edu.clevai.bplog.service.cuievent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.clevai.bplog.entity.logDb.BpCuiContentUserUlc;
import vn.edu.clevai.bplog.entity.projection.AnswerAndQuestionPJ;
import vn.edu.clevai.bplog.entity.projection.ScheduleMonthCalendarPJ;
import vn.edu.clevai.bplog.payload.request.bp.BPScheduleUssRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPUlcRequest;
import vn.edu.clevai.bplog.payload.request.bp.GetQuestionAnswerRequest;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.payload.response.cti.LeaningObjectResponse;
import vn.edu.clevai.bplog.payload.response.student.StudentLearningPackageResponse;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.authoring.payload.response.LearningObjectWithRewardResponse;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface CuiService {
	void scheduleMyCUI(BPUlcRequest cuiReq, BPScheduleUssRequest sussReq);

	void setPublishedCUIs(List<String> ushChildCodes, boolean published);

	BpCuiContentUserUlc getOrCreateCUI(String ulc, String usi, String cti, Boolean published);

	void unpublishCUI(String ulcCode, String myst);

	void unpublishCUICUIE(String ulcCode, String myst);

	void unpublishCUITE(String ulcCode, String myst);

	BpCuiContentUserUlc findByCode(String cuieCuicode);

	BpCuiContentUserUlc getOrCreateCUIWithBps(String ulc, String usi, String cti, String bpsCode, Boolean published);


	BpCuiContentUserUlc createCui(String usi, String ctiCode, String ulcCode);

	BpCuiContentUserUlc findCuiMainFromUlc(String ulc);

	Page<LeaningObjectResponse> getHomeworkCtiFromSt(String usi, String pt, Long LOId, Integer page, Integer size);

	String findFirstCuiUsi(String ulcCode, String ust);

	List<StudentLearningPackageResponse> getAllLearningPackage(String usi, String pt);

	Page<ScheduleMonthCalendarPJ> findAllByCondition(ScheduleRequest request);

	List<BpCuiContentUserUlc> findCuiByUlcInAndUst(Collection<String> ulcs, String ust);

	GeneralPageResponse<LearningObjectWithRewardResponse> search(
			String keyword,
			String pt,
			String usi,
			Integer page,
			Integer size
	);

	List<String> findHrvCtis(String pt, String gg, String dfdl, String cap, String usi);

	Page<AnswerAndQuestionPJ> findCuiQuestion(GetQuestionAnswerRequest request, Pageable pageable);

	AnswerAndQuestionPJ findDetailsQuestion(String cuiCode);

	BpCuiContentUserUlc getCuiByUlcAndCti(String ulcCode, String code, boolean b);

	void unpublishedCuiUnnecessaryOfModifyStudent(String usi, Timestamp from, Timestamp to, Boolean published);

	void unpublishedCuiUnnecessaryForScheduleMPForOM(String usi, Boolean published);

	List<BpCuiContentUserUlc> findCuiByCapAndClagAndUsi(String usi, String clag, Timestamp from, Timestamp to);

	void saveAll(List<BpCuiContentUserUlc> cuiList);

	Page<ScheduleMonthCalendarPJ> findAllScheduleWeek(ScheduleRequest request);

}
