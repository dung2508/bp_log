package vn.edu.clevai.bplog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.clevai.bplog.payload.response.shifttox.BpClagClassgroupResponse;
import vn.edu.clevai.bplog.payload.response.shifttox.BpUniqueLearningResponse;
import vn.edu.clevai.bplog.service.BpShiftToXService;
import vn.edu.clevai.common.api.model.ApiResponse;

@RestController
@RequestMapping("/bpp-shift-tox")
public class BpShiftToXController {

	@SuppressWarnings("unused")
	@Autowired
	private BpShiftToXService bpShiftService;

	@GetMapping("/get-all-ush-from-cady/{cady-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllUshFromCady(
			@PathVariable("cady-code") String cadyCode) {
		return null;
	}

	@GetMapping("/get-all-udl-from-ush/{cady-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllUdlFromUSH(
			@PathVariable("ush-code") String ushCode) {
		return null;
	}

	@GetMapping("/get-all-urc-from-ush/{cady-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllUrcFromUSH(
			@PathVariable("urc-code") String urcCode) {
		return null;
	}

	@GetMapping("/get-all-uge-from-ush/{ush-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllUgeFromUSH(
			@PathVariable("urc-code") String ushCode) {
		return null;
	}

	@GetMapping("/get-all-uli-from-ush/{ush-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllUliFromUSH(
			@PathVariable("ush-code") String ushCode) {
		return null;
	}

	@GetMapping("/get-all-clag-from-ulc/{ulc-code}")
	public ResponseEntity<ApiResponse<List<BpClagClassgroupResponse>>> getAllClagFromULC(
			@PathVariable("ulc-code") String ulcCode) {
		return null;
	}

	@GetMapping("/get-all-cti-from-ush/{ush-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllCtiFromUSH(String ushCode) {
		return null;
	}

	@GetMapping("/get-all-cti-from-urc/{urc-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllCtiFromURC(String urcCode) {
		return null;
	}

	@GetMapping("/get-all-cti-from-udl/{udl-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllCtiFromUDL(String udlCode) {
		return null;
	}

	@GetMapping("/get-all-cti-from-uge/{uge-code}")
	public ResponseEntity<ApiResponse<List<BpUniqueLearningResponse>>> getAllCtiFromUGE(String ugeCode) {
		return null;
	}

	@GetMapping("/converti-ush-xdailyscheduleclass/{ush-code}")
	public ResponseEntity<ApiResponse<String>> convertIUshXDailyScheduleClass(String ushCode) {
		return null;
	}

	@GetMapping("/converti-udl-xdailyscheduleclass/{udl-code}")
	public ResponseEntity<ApiResponse<String>> convertIUdlXDailyScheduleClass(String udlCode) {
		return null;
	}

	@GetMapping("/converti-urc-xdailyscheduleclass/{urc-code}")
	public ResponseEntity<ApiResponse<String>> convertIUrcXDailyScheduleClass(String urcCode) {
		return null;
	}

	@GetMapping("/converti-uco-xdailyscheduleclass/{uco-code}")
	public ResponseEntity<ApiResponse<String>> convertIUcoXDailyScheduleClass(String ucoCode) {
		return null;
	}

	@GetMapping("/converti-uge-xdailyscheduleclass/{uge-code}")
	public ResponseEntity<ApiResponse<String>> convertIUgeXDailyScheduleClass(String ugeCode) {
		return null;
	}

	@GetMapping("/converti-uli-xdailyscheduleclass/{uli-code}")
	public ResponseEntity<ApiResponse<String>> convertIUliXDailyScheduleClass(String uliCode) {
		return null;
	}

	@GetMapping("/converti-clag-xdscsessiongroup/{clag-code}")
	public ResponseEntity<ApiResponse<String>> convertIClagXDSCSessionGroup(String clagCode) {
		return null;
	}

	@GetMapping("/converti-clag-xsessiongroup/{clag-code}")
	public ResponseEntity<ApiResponse<String>> convertIClagXSessionGroup(String clagCode) {
		return null;
	}

	@GetMapping("/converti-cti-xdailyscheduleclass/{cti-code}")
	public ResponseEntity<ApiResponse<String>> convertICtiXDailyScheduleClass(String ctiCode) {
		return null;
	}

	@GetMapping("/converti-cti-xdscbattlequizs/{cti-code}")
	public ResponseEntity<ApiResponse<String>> convertICtiXDSCBattleQuizs(String ctiCode) {
		return null;
	}

	@GetMapping("/converti-cti-xdsclivequizs/{cti-code}")
	public ResponseEntity<ApiResponse<String>> convertICtiXDSCLiveQuizs(String ctiCode) {
		return null;
	}

	@GetMapping("/converti-cti-xdscliveanswers/{cti-code}")
	public ResponseEntity<ApiResponse<String>> convertICtiXDSCLiveQuizAnswers(String ctiCode) {
		return null;
	}

	@GetMapping("/converti-cti-xdscslides/{cti-code}")
	public ResponseEntity<ApiResponse<String>> convertCtiXDSCSlides(String ctiCode) {
		return null;
	}

	@GetMapping("/converti-cti-xtreamconfig/{cti-code}")
	public ResponseEntity<ApiResponse<String>> convertICtiXStreamingConfig(String ctiCode) {
		return null;
	}

	@GetMapping("/converti-cti-xzoommeetings/{cti-code}")
	public ResponseEntity<ApiResponse<String>> convertICtiXZoomMeetings(String ctiCode) {
		return null;
	}
}
