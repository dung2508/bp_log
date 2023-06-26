package vn.edu.clevai.bplog.service.proxy.authoringproxy.impl;

import feign.template.UriUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.authoring.payload.request.GetOrCreateSlideRequest;
import vn.edu.clevai.common.proxy.authoring.payload.request.GetOrCreateTestRequest;
import vn.edu.clevai.common.proxy.authoring.payload.request.UpdateXTestRequest;
import vn.edu.clevai.common.proxy.authoring.payload.response.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class AuthoringServiceImpl extends BaseProxyService implements AuthoringService {
	@Override
	public List<QuizResponse> get(Collection<String> codes) {
		return getAuthoringServiceProxy().get(buildAuthoringServiceUri(), codes.toArray(new String[0])).getBody();
	}

	@Override
	public List<LearningObjectDetailResponse> getLoByCodes(Collection<String> codes) {
		return getAuthoringServiceProxy().getLoByCodes(buildAuthoringServiceUri(), codes.toArray(new String[0])).getBody();
	}

	@Override
	public List<LearningObjectResponse> getLoReqByCodes(Collection<String> codes) {
		return getAuthoringServiceProxy().getLOByCodeList(buildAuthoringServiceUri(), codes.toArray(new String[0])).getBody();
	}

	@Override
	public List<Long> getOrCreateSlideByUrls(List<GetOrCreateSlideRequest> requests) {
		return getAuthoringServiceProxy().getOrCreateSlideByUrls(buildAuthoringServiceUri(), requests).getBody();
	}

	@Override
	public List<PCResponse> getPCBl4Qts(List<String> bl4Qts) {
		return getAuthoringServiceProxy().getPCBl4Qts(buildAuthoringServiceUri(), bl4Qts).getBody();
	}

	@Override
	public void createOrUpdateXTest(GetOrCreateTestRequest request) {
		getAuthoringServiceProxy().createOrUpdateXTest(buildAuthoringServiceUri(), request);
	}

	@Override
	public void createOrUpdateXTestLO(String xdsc, Integer countTotalLCPChild, List<String> loCodes) {
		getAuthoringServiceProxy().createOrUpdateXTestLO(buildAuthoringServiceUri(), xdsc, countTotalLCPChild, loCodes);
	}

	@Override
	public void updateXTest(String xdsc, UpdateXTestRequest request) {
		getAuthoringServiceProxy().updateXTest(buildAuthoringServiceUri(), xdsc, request);
	}

	@Override
	public GeneralPageResponse<LearningProgramLearningObjectResponse> searchByCodes(String keyword, String[] inclusions, Integer page, Integer size) {
		if (inclusions.length == 0) {
			return GeneralPageResponse.toResponse(new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0));
		}
		return getAuthoringServiceProxy().searchByCode(buildAuthoringServiceUri(), UriUtils.encode(keyword), inclusions, page, size).getBody();
	}
}
