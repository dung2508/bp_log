package vn.edu.clevai.bplog.repository;

import java.util.List;

import vn.edu.clevai.bplog.repository.projection.DteAssignedProjection;
import vn.edu.clevai.bplog.repository.projection.RegistedProjection;
import vn.edu.clevai.bplog.repository.projection.GetAssignedProjection;

public interface CustomBpUSIDutyRepository {
	List<RegistedProjection> dteRegistedQuantity(Integer productId, Integer gradeId,
			Integer classLevelId, String startDate, String endDate, Integer subjectId);

	List<RegistedProjection> lteRegistedQuantity(Integer productId, Integer gradeId,
			Integer classLevelId, String startDate, String endDate, Integer subjectId);

	List<RegistedProjection> gteRegistedQuantity(Integer productId, Integer gradeId,
			Integer classLevelId, String startDate, String endDate, Integer subjectId);

	List<RegistedProjection> qoRegistedQuantity(Integer productId, Integer gradeId,
			Integer classLevelId, String startDate, String endDate, Integer subjectId);

	List<DteAssignedProjection> dteAssignedQuantity(Integer productId, Integer gradeId,
			Integer classLevelId, String startDate, String endDate, Integer subjectId);

	List<DteAssignedProjection> qoAssignedQuantity(Integer productId, Integer gradeId,
			Integer classLevelId, String startDate, String endDate, Integer subjectId);

	List<DteAssignedProjection> lteAssignedQuantity(Integer productId, Integer gradeId,
			Integer classLevelId, String startDate, String endDate, Integer subjectId);

	List<GetAssignedProjection> gteAssignedQuatity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId);

}
