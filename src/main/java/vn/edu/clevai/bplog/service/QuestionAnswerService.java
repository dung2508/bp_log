package vn.edu.clevai.bplog.service;

import org.springframework.data.domain.Page;
import vn.edu.clevai.bplog.payload.request.bp.GetQuestionAnswerRequest;
import vn.edu.clevai.bplog.payload.request.bp.SubmitQARequest;
import vn.edu.clevai.bplog.payload.response.orp.AnswerAndQuestionResponse;

public interface QuestionAnswerService {
	Object createQuestion(SubmitQARequest request);
	Object createAnswer(SubmitQARequest request);

	Page<AnswerAndQuestionResponse> getMyQuestionAnswer(GetQuestionAnswerRequest request);

	Page<AnswerAndQuestionResponse> getQuestionAnswer(GetQuestionAnswerRequest request);

	AnswerAndQuestionResponse getQuestionDetails(String cuiCode);
}
