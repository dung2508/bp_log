package vn.edu.clevai.bplog.service.cuievent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.entity.logDb.BpChpiCheckProcessItem;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpCuiEvent;
import vn.edu.clevai.bplog.payload.request.AssignVideosRequest;
import vn.edu.clevai.bplog.payload.request.TeacherFinishClassRequest;
import vn.edu.clevai.bplog.payload.request.TeacherSubmitReportRequest;
import vn.edu.clevai.bplog.payload.request.ToTeacherSubmitRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPCuiEventRequest;
import vn.edu.clevai.bplog.payload.request.bp.BPScheduleUssRequest;
import vn.edu.clevai.bplog.payload.response.SessionOperatorAndCuiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCUIEventResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCUIResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpULCResponse;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CuiEventService {

	String TYPE_1A = "1A";
	String TYPE_1B = "1B";
	String TYPE_2 = "2";
	String TYPE_3 = "3";

	BpCUIEventResponse writeCUIEvent510(BPCuiEventRequest request);

	BpCUIEventResponse findCUIEvent(BPCuiEventRequest request);

	BpCuiEvent findCUIEventJoinRequest(BPCuiEventRequest request);

	Page<SessionOperatorAndCuiResponse> listAllSessionOperatorAndCui(String username, LocalDate date,
																	 Pageable pageable);

	BpCuiEvent createCuiEvent(BPCuiEventRequest request);

	void getUsi(Timestamp fetTime);

	BpCUIEventResponse findCuiCuiEvent(
			String cuiUlcCode,
			String cuiMylcp,
			String cuiUsiCode,
			String cuiUst,
			String cuieMylcet,
			String cuieCuiCode,
			String cuiCode,
			String cuiCuieCode
	);

	BpChpiCheckProcessItem findCHPI(
			String chpiCode,
			String chpiMycuie
	);

	BpContentItem createCti(
			String myContentType,
			String fileBeginUrl,
			String ctiCode,
			String cti2Code
	);

	void writeCuiEventAssignVideoTeActualTime(
			String cuiUlcCode,
			String cuiMylcp,
			String cuiUsiCode,
			String cuiUst,
			String cuiPubliched,
			String cuieMylcet,
			Timestamp cuieActualtimefet,
			String cuieCuiCode,
			String cuieMyusi,
			String cuieMylcp,
			String cuiCode,
			Timestamp cuieActualtimebet,
			String cuiCuieCode
	);

	List<BpCUIEventResponse> planCuiEvents(BPCuiEventRequest request, BPScheduleUssRequest sussReq);

	Map<String, Integer> bpGetCuiEvents(String cuiCode);

	BpCUIEventResponse writeEventPlanTime(String cuieCode, Integer eventPlanTime, String cassCode);

	void triggerPlannedCuiEvents();

	List<BpCUIEventResponse> findCuiEventsByPlanTime();

	BpCUIEventResponse writeEventActualTime(String cuiEventCode);

	BpCUIResponse getCui(String ulcCode, String usiCode);

	void teacherSubmitReport1A(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime)
			throws Exception;

	void teacherSubmitReport1B(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime)
			throws Exception;

	void teacherSubmitReport2(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime) throws Exception;

	void teacherSubmitReport3(String tUsername, TeacherSubmitReportRequest request, Timestamp fetTime) throws Exception;

	void toSubmitReport1A(String toUsername, List<ToTeacherSubmitRequest> listTeacher, Timestamp betTime)
			throws Exception;

	void toSubmitReport1B(String toUsername, List<ToTeacherSubmitRequest> listTeacher, Timestamp betTime)
			throws Exception;

	String finishClass(TeacherFinishClassRequest request) throws Exception;

	BpUsiUserItem getUsiTe(String usiCode, Timestamp fetTime);

	BpULCResponse findUlcTe(
			String clagCode,
			String ulcCode,
			String ulcCap,
			String ulcLcp,
			String ulcLct,
			String ulcMyJoinUlc,
			String ulcPublished
	);

	void writeCuiEventTimeupTeActualTime(
			String cuiUlcCode,
			String cuiMylcp,
			String cuiUsiCode,
			String cuiUst,
			String cuiPubliched,
			String cuieMylcet,
			Timestamp cuieActualtimefet,
			String cuieCuiCode,
			String cuieMyusi,
			String cuieMylcp,
			String cuiCode,
			Timestamp cuieActualtimebet,
			String cuiCuieCode
	);

	String assignVideo(AssignVideosRequest request);

	BpCuiEvent changeStatusCuiEvent(String cuiEventCode, Boolean publishedStatus);

	void setPublishedCUIEvents(List<String> ulcCodes, boolean published);

	void unpublishedCUIE(String cuiCode, Boolean isPublished);

	void bppJoinDL(String xst, Timestamp eventactualtimeFet);

	void bppJoinGEDLG(
			Long xdeal,
			String xcady,
			Timestamp cuieActualtimeFet,
			String cuieMylcp
	);

	void bppJoinGEGES(
			Long xdeal,
			String xcady,
			Timestamp cuieActualtimeFet,
			String cuieMylcp
	);

	void createCUIAndCuiEvent(String ulcCode, String stCode, String cti,
							  String lcp, Timestamp cuieActualtimeFet, String myCap);

	void createCUIJoinEvent(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap);

	void createCUIJoinEventGEDLG(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap);

	void createCUIJoinEventGEGES(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap);

	void createCUIJoinEventGETE(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap);

	void createCUIChangeClag(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap);

	void createCUITE(String ulcCode, String stCode, String lcp, Timestamp cuieActualtimeFet, String myCap);

	List<BpCuiEvent> createAllCuiEventByCui(String cuiCode);

	void setPublishCUIE(String ulc, String usi, Boolean isPublished);

	void unpublishedCuieUnnecessaryOfModifyStudent(String usi, Timestamp from, Timestamp to, Boolean published);

	void unpublishedCuieUnnecessaryForScheduleMPForOM(String usi, Boolean published);

	List<BpCuiEvent> findCuieByCapAndClagAndUsi(String usi, String clag, Timestamp from, Timestamp to);

	void saveAll(List<BpCuiEvent> cuiEventList);

	BpCuiEvent getLastCuiEByCTI(String code);

	List<BpCuiEvent> findCuiEByCui(String cuiCode);
}
