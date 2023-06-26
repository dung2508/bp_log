package vn.edu.clevai.bplog.service.unittest.impl;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.entity.BPUnitTestLog;
import vn.edu.clevai.bplog.payload.request.bp.BPCuiEventRequest;
import vn.edu.clevai.bplog.repository.BPUnitTestLogRepository;
import vn.edu.clevai.bplog.service.unittest.BPUnitTestService;

@Service
@RequiredArgsConstructor
public class BPUnitTestServiceImpl implements BPUnitTestService {

	@Autowired
	private BPUnitTestLogRepository bpUnitTestLogRepository;

	@Autowired
	private ModelMapper mapper;

	@Override
	public void saveUnitTestLog(BPCuiEventRequest request) {
		bpUnitTestLogRepository.save(mapper.map(request, BPUnitTestLog.class));
	}
}
