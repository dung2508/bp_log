package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpDfdlDifficultygrade;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfdlDifficultgradeResponse;

import java.util.List;

public interface BpDfdlDifficultygradeService {
	BpDfdlDifficultygrade findByCode(String code);

	BpDfdlDifficultgradeResponse getDFDLFromX(Integer xDfdl);

	Integer findXDFDL(Long xdeal);

	BpDfdlDifficultgradeResponse getPOD_DFDL(String podCode);

	List<BpDfdlDifficultygrade> findAll();

	BpDfdlDifficultygrade findById(Integer code);

}
