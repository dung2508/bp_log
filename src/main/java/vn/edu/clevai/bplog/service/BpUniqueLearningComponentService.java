package vn.edu.clevai.bplog.service;

import org.springframework.data.domain.Page;
import vn.edu.clevai.bplog.payload.request.filter.ScheduleRequest;
import vn.edu.clevai.bplog.entity.projection.UlcScheduleShiftPJ;

public interface BpUniqueLearningComponentService {

	Page<UlcScheduleShiftPJ> findAllByCondition(ScheduleRequest request);
}
