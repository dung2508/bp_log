package vn.edu.clevai.bplog.repository;

import java.util.List;

import vn.edu.clevai.bplog.repository.projection.DteRequiredProjection;
import vn.edu.clevai.bplog.repository.projection.GteRequiredProjection;

public interface CustomClagRepository {
	List<GteRequiredProjection> gteRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId);

	List<DteRequiredProjection> dteRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId);

	List<DteRequiredProjection> qoRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId);

	List<DteRequiredProjection> lteRequiredQuatity(Integer productId, Integer gradeId, Integer classLevelId,
			String startDate, String endDate, Integer subjectId);
}
