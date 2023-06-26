package vn.edu.clevai.bplog.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.entity.BpPODDFDL;
import vn.edu.clevai.bplog.repository.BpPODDFDLRepository;
import vn.edu.clevai.bplog.service.BpPODDFDLService;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODDFDLResponse;

@Service
public class BpPODDFDLServiceImpl implements BpPODDFDLService {

	private final BpPODDFDLRepository bpPODDFDLRepository;

	private final ModelMapper modelMapper;

	public BpPODDFDLServiceImpl(BpPODDFDLRepository bpPODDFDLRepository, ModelMapper modelMapper) {
		this.bpPODDFDLRepository = bpPODDFDLRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	@Transactional
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_POD_DFDL
	)
	public BpPODDFDLResponse setPOD_DFDL(String podPod_code, String podMydfdl) {
		String code = podPod_code + "-" + podMydfdl;
		return modelMapper.map(bpPODDFDLRepository.findByCode(code).orElseGet(() -> bpPODDFDLRepository.save(BpPODDFDL.builder()
				.code(code)
				.dfdl(podMydfdl)
				.mypod(podPod_code)
				.build())), BpPODDFDLResponse.class);
	}
}
