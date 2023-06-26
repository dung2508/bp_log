package vn.edu.clevai.bplog.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.BpsStepTypeEnum;
import vn.edu.clevai.bplog.payload.request.AssignVideosRequest;
import vn.edu.clevai.bplog.payload.request.TeacherFinishClassRequest;
import vn.edu.clevai.bplog.payload.request.TeacherSubmitReportRequest;
import vn.edu.clevai.bplog.payload.request.ToTeacherSubmitRequest;
import vn.edu.clevai.bplog.payload.response.CountReportResponse;
import vn.edu.clevai.bplog.payload.response.SessionOperatorAndCuiResponse;
import vn.edu.clevai.bplog.payload.response.TeacherAndCuiResponse;
import vn.edu.clevai.bplog.service.BpBppProcessService;
import vn.edu.clevai.bplog.service.BpBpsStepService;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.common.api.controller.BaseController;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.api.util.DateUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/bpcui")
public class BPCuiEventController extends BaseController {

	@Autowired
	private CuiEventService cuiService;

	@Autowired
	private BpBppProcessService bppProcessService;

	@Autowired
	private BpBpsStepService bpsStepService;

	@PostMapping("/teacher/submit-report/{report-type}")
	public ResponseEntity<ApiResponse<String>> teacherSubmitReport(
			@PathVariable("report-type") @NotNull String reportType,
			@RequestParam(value = "fet-time", required = false) Long fetTime,
			@RequestBody @Valid TeacherSubmitReportRequest request) throws Exception {
		String tUsername = getUserName();
		if (StringUtils.isNoneBlank(tUsername)) {
			switch (reportType) {
				case CuiEventService.TYPE_1A:
					cuiService.teacherSubmitReport1A(tUsername, request,
							Objects.nonNull(fetTime) ? new Timestamp(fetTime) : new Timestamp(System.currentTimeMillis()));
					break;
				case CuiEventService.TYPE_1B:
					cuiService.teacherSubmitReport1B(tUsername, request,
							Objects.nonNull(fetTime) ? new Timestamp(fetTime) : new Timestamp(System.currentTimeMillis()));
					break;
				case CuiEventService.TYPE_2:
					cuiService.teacherSubmitReport2(tUsername, request,
							Objects.nonNull(fetTime) ? new Timestamp(fetTime) : new Timestamp(System.currentTimeMillis()));
					break;
				case CuiEventService.TYPE_3:
					cuiService.teacherSubmitReport3(tUsername, request,
							Objects.nonNull(fetTime) ? new Timestamp(fetTime) : new Timestamp(System.currentTimeMillis()));
					break;
				default:
					break;
			}
		}
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@PostMapping("/teacher-operator/submit-report/{report-type}")
	public ResponseEntity<ApiResponse<String>> toSubmitReport(@PathVariable("report-type") @NotNull String reportType,
															  @RequestParam(value = "fet-time", required = false) Long fetTime,
															  @RequestBody @NotEmpty List<ToTeacherSubmitRequest> listTeacher) throws Exception {
		String toUsername = getUserName();
		if (StringUtils.isNoneBlank(toUsername)) {
			switch (reportType) {
				case CuiEventService.TYPE_1A:
					cuiService.toSubmitReport1A(toUsername, listTeacher,
							Objects.nonNull(fetTime) ? new Timestamp(fetTime) : new Timestamp(System.currentTimeMillis()));
					break;
				case CuiEventService.TYPE_1B:
					cuiService.toSubmitReport1B(toUsername, listTeacher,
							Objects.nonNull(fetTime) ? new Timestamp(fetTime) : new Timestamp(System.currentTimeMillis()));
					break;
				default:
					break;
			}

		}
		return ResponseEntity.ok(ApiResponse.success(ApiResponse.SUCCESS));
	}

	@PostMapping("/teacher/finish")
	public ResponseEntity<ApiResponse<String>> finishClass(@RequestBody TeacherFinishClassRequest request)
			throws Exception {
		request.setTeacherCode(getUserName());
		request.setBetTime(ObjectUtils.defaultIfNull(request.getBetTime(), DateUtils.now()));
		return ResponseEntity.ok(ApiResponse.success(cuiService.finishClass(request)));
	}

	@PostMapping("/count/report")
	public ResponseEntity<ApiResponse<List<CountReportResponse>>> countReport(Integer page, Integer pageSize) {
		// TODO Count for all teacher
		return null;
	}

	@PostMapping("/assign/video")
	public ResponseEntity<ApiResponse<String>> assignVideo(
			@RequestBody AssignVideosRequest request
	) {
		bppProcessService.createBppProcess(BppProcessTypeEnum.BPPASSIGNVIDEO_GE, null);
		request.setUsiCode(getUserName());
		bpsStepService.createBpsStep(BpsStepTypeEnum.GETUSI);
		return ResponseEntity.ok(ApiResponse.success(cuiService.assignVideo(request)));
	}

	@GetMapping("/detail/teacher/list")
	public ResponseEntity<ApiResponse<List<TeacherAndCuiResponse>>> listAllTeacherAndCui(
			@RequestParam("page") Integer page, @RequestParam("page-size") Integer pageSize,
			@RequestParam("date") String strDate) {
		// SELECT * FROM bp_cui_content_user_ulc_instance a, bp_usi_useritem b,
		// bp_ulc_uniquelearningcomponent c,
		// bp_cap_calendarperiod d
		// WHERE a.myusi = b.code AND a.myulc = c.code AND c.mycap = d.code AND b.myust
		// = 'TE';
		return null;
	}

	@GetMapping("/detail/session-operator/list")
	public ResponseEntity<GeneralPageResponse<SessionOperatorAndCuiResponse>> listAllSessionOperatorAndCui(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size) {
		return ResponseEntity.ok(GeneralPageResponse.toResponse(cuiService.listAllSessionOperatorAndCui(getUserName(),
				ObjectUtils.defaultIfNull(date, LocalDate.now()), PageRequest.of(page - 1, size))));
	}

	@GetMapping("/detail/get-one/{myusi}")
	public ResponseEntity<ApiResponse<TeacherAndCuiResponse>> getOneTeacherAndCui(@RequestParam("date") String strDate,
																				  @RequestParam("myusi") String myusi) {
		return null;
	}

}
