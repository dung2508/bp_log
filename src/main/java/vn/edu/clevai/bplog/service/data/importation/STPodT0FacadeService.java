package vn.edu.clevai.bplog.service.data.importation;

import java.util.List;

import vn.edu.clevai.bplog.payload.request.StudentValidRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.StudentValidResponse;

public interface STPodT0FacadeService {
	/**
	 * @return
	 */
	List<Long> exportXDealValidAll();

	/**
	 * @param podIds
	 * @return
	 */
	List<StudentValidResponse> getXStudent(List<Long> podIds);

	/**
	 * @param listStudents
	 */
	void setUSIs(List<StudentValidRequest> listStudents);

	/**
	 * @param dto
	 */
	void setUSI(StudentValidRequest dto);
	
	/**
	 * @param dto
	 */
	void setPOD(StudentValidRequest dto);
}
