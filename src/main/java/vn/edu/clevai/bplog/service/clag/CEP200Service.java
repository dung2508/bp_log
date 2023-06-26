package vn.edu.clevai.bplog.service.clag;

import vn.edu.clevai.bplog.dto.cep200.*;
import vn.edu.clevai.bplog.payload.response.cep100.CEP100LearningScheduleClassResponse;

import java.util.List;

public interface CEP200Service {
	/**
	 * @param podId
	 * @return
	 */
	CEP200StudentDTO getStudent(String podCode);

	/**
	 * @param cep100GradeId
	 * @return
	 */
	CEP200GradeGroupDTO getCEP200GradeGroup(Integer cep100GradeId);

	/**
	 * @param listLearningSchedules
	 * @return
	 */
	CEP200WsoDTO getCEP200Wso(List<CEP100LearningScheduleClassResponse> listLearningSchedules);

	/**
	 * @param cep100ClassLevelId
	 * @return
	 */
	CEP200DfdlDTO getCEP200DFDL(Integer cep100ClassLevelId);

	/**
	 * @param podId
	 * @return
	 */
	CEP200PTDTO getProductType(Integer trainingTypeId);

}
