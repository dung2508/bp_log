package vn.edu.clevai.bplog.service.proxy.authoringproxy;

import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.authoring.payload.request.GetOrCreateSlideRequest;
import vn.edu.clevai.common.proxy.authoring.payload.request.GetOrCreateTestRequest;
import vn.edu.clevai.common.proxy.authoring.payload.request.UpdateXTestRequest;
import vn.edu.clevai.common.proxy.authoring.payload.response.*;

import java.util.Collection;
import java.util.List;

public interface AuthoringService {
	List<QuizResponse> get(Collection<String> codes);

	List<LearningObjectDetailResponse> getLoByCodes(Collection<String> codes);

	List<LearningObjectResponse> getLoReqByCodes(Collection<String> codes);

	List<Long> getOrCreateSlideByUrls(List<GetOrCreateSlideRequest> requests);

	List<PCResponse> getPCBl4Qts(List<String> bl4Qts);

	void createOrUpdateXTest(GetOrCreateTestRequest request);

	void createOrUpdateXTestLO(String xdsc, Integer countTotalLCPChild, List<String> loCodes);

	void updateXTest(String xdsc, UpdateXTestRequest request);

	GeneralPageResponse<LearningProgramLearningObjectResponse> searchByCodes(
			String keyword,
			String[] inclusions,
			Integer page,
			Integer size);

}
