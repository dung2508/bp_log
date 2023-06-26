package vn.edu.clevai.bplog.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.clevai.bplog.payload.request.bp.GetQuestionAnswerRequest;
import vn.edu.clevai.bplog.payload.request.bp.SubmitQARequest;
import vn.edu.clevai.bplog.payload.response.orp.AnswerAndQuestionResponse;
import vn.edu.clevai.bplog.service.QuestionAnswerService;
import vn.edu.clevai.common.api.controller.BaseController;
import vn.edu.clevai.common.api.model.ApiResponse;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping({"/question-answer"})
@AllArgsConstructor
public class QuestionAnswerController extends BaseController {

	private final QuestionAnswerService questionAnswerService;

	@PostMapping("/submit-question")
	public ResponseEntity<ApiResponse<?>> submitQuestion(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "question", required = false) String questionText,
			@RequestParam("xClassCode") String xclass,
			@RequestParam(value = "actualTimeFet") Timestamp actualTimeFet
	) {
		Object response = questionAnswerService.createQuestion(
				SubmitQARequest.builder()
						.file(file)
						.currentUsi(getUserName())
						.questionText(
								Optional.ofNullable(questionText).orElse("")
						)
						.actualTimeFet(actualTimeFet)
						.xclass(xclass)
						.build());

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/submit-answer")
	public ResponseEntity<ApiResponse<?>> submitAnswer(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "actualTimeFet") Timestamp actualTimeFet,
			@RequestParam(value = "cuiCode") String cuiCode,
			@RequestParam(value = "answer", required = false) String answerText

	) {
		Object response = questionAnswerService.createAnswer(SubmitQARequest.builder()
				.file(file)
				.currentUsi(getUserName())
				.answerText(
						Optional.ofNullable(answerText).orElse("")
				)
				.actualTimeFet(actualTimeFet)
				.cuiCode(cuiCode)
				.build());
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/my-questions")
	public ResponseEntity<Page<AnswerAndQuestionResponse>> getMyQuestion(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "gg", required = false) String gg,
			@RequestParam(value = "sla", required = false) Boolean sla,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "fromTime", required = false) Timestamp fromTime,
			@RequestParam(value = "toTime", required = false) Timestamp toTime
	) {
		GetQuestionAnswerRequest request = GetQuestionAnswerRequest.builder()
				.currentUsi(
						Optional.ofNullable(getUserName()).orElseThrow(() -> new RuntimeException("Get current usi error"))
				)
				.gg(gg)
				.sla(sla)
				.status(status)
				.fromTime(fromTime)
				.toTime(toTime)
				.page(page)
				.size(size)
				.build();

		Page<AnswerAndQuestionResponse> response = questionAnswerService.getQuestionAnswer(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<AnswerAndQuestionResponse>> getQuestionAnswer(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "gg", required = false) String gg,
			@RequestParam(value = "sla", required = false) Boolean sla,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "fromTime", required = false) Timestamp fromTime,
			@RequestParam(value = "toTime", required = false) Timestamp toTime
	) {
		GetQuestionAnswerRequest request = GetQuestionAnswerRequest.builder()
				.gg(gg)
				.sla(sla)
				.status(status)
				.fromTime(fromTime)
				.toTime(toTime)
				.page(page)
				.size(size)
				.build();
		Page<AnswerAndQuestionResponse> response = questionAnswerService.getQuestionAnswer(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{cui-code}")
	public ResponseEntity<ApiResponse<AnswerAndQuestionResponse>> getQuestionDetails(
			@PathVariable("cui-code") String cuiCode
	) {
		AnswerAndQuestionResponse response = questionAnswerService.getQuestionDetails(cuiCode);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
