package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.entity.BpDfdlDifficultygrade;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.repository.BpDfdlDifficultygradeRepository;
import vn.edu.clevai.bplog.service.BpDfdlDifficultygradeService;
import vn.edu.clevai.bplog.service.BpPodProductOfDealService;
import vn.edu.clevai.bplog.service.proxy.lmsproxy.LmsService;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfdlDifficultgradeResponse;

import java.util.List;

@Service
public class BpDfdlDifficultygradeServiceImpl extends BaseProxyService implements BpDfdlDifficultygradeService {

	@Autowired
	private BpPodProductOfDealService bpPodProductOfDealService;

	@Autowired
	private BpDfdlDifficultygradeRepository bpDfdlDifficultygradeRepository;

	@Autowired
	private LmsService lmsService;

	@Override
	public BpDfdlDifficultygrade findByCode(String code) {
		return bpDfdlDifficultygradeRepository
				.findByCode(code)
				.orElseThrow(() -> new NotFoundException("Could not find BpDfdlDifficultygrade using code = " + code));
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_DFDL_FROM_X
	)
	public BpDfdlDifficultgradeResponse getDFDLFromX(Integer xdfdl) {
		String code = Cep100TransformUtils.toDfdlCode(xdfdl);
		BpDfdlDifficultygrade bpDfdlDifficultygrade = findByCode(code);

		return BpDfdlDifficultgradeResponse
				.builder()
				.code(bpDfdlDifficultygrade.getCode())
				.name(bpDfdlDifficultygrade.getName())
				.description(bpDfdlDifficultygrade.getDescription())
				.createdAt(bpDfdlDifficultygrade.getCreatedAt())
				.updatedAt(bpDfdlDifficultygrade.getUpdatedAt())
				.build();
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.FIND_XDFDL
	)
	public Integer findXDFDL(Long xXdealid) {
		return lmsService.getClassLevelByXDEAL(xXdealid);
	}

	@Override
	@WriteUnitTestLog
	public BpDfdlDifficultgradeResponse getPOD_DFDL(String podCode) {
		BpPodProductOfDeal bd = bpPodProductOfDealService.findByCode(podCode);
		return getDFDLFromX(findXDFDL(bd.getXdeal()));
	}

	@Override
	public List<BpDfdlDifficultygrade> findAll() {
		return bpDfdlDifficultygradeRepository.findAll();
	}

	@Override
	public BpDfdlDifficultygrade findById(Integer id) {
		return bpDfdlDifficultygradeRepository
				.findById(id)
				.orElseThrow(() -> new NotFoundException("Could not find BpDfdlDifficultygrade using id = " + id));
	}
}
