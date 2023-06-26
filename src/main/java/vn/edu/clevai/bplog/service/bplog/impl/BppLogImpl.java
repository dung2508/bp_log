package vn.edu.clevai.bplog.service.bplog.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.redis.BpeEventDTO;
import vn.edu.clevai.bplog.dto.redis.BppProcessDTO;
import vn.edu.clevai.bplog.dto.redis.BpsStepDTO;
import vn.edu.clevai.bplog.entity.BpBpeEvent;
import vn.edu.clevai.bplog.entity.BpBppProcess;
import vn.edu.clevai.bplog.entity.BpBpsStep;
import vn.edu.clevai.bplog.repository.bplog.BpBpeBpEventRepository;
import vn.edu.clevai.bplog.repository.bplog.BpBppProcessRepository;
import vn.edu.clevai.bplog.repository.bplog.BpBpsStepRepository;
import vn.edu.clevai.bplog.service.bplog.BppLog;

@Service
@RequiredArgsConstructor
public class BppLogImpl implements BppLog {

	private final BpBppProcessRepository bpBppProcessRepository;
	private final BpBpsStepRepository bpBpsStepRepository;
	private final BpBpeBpEventRepository bpBpeBpEventRepository;

	private final ModelMapper modelMapper;

	@Override
	public void saveBpeEvent(BpeEventDTO dto) {
		bpBpeBpEventRepository.save(modelMapper.map(dto, BpBpeEvent.class));
	}

	@Override
	public void saveBppProcess(BppProcessDTO dto) {
		bpBppProcessRepository.save(modelMapper.map(dto, BpBppProcess.class));
	}

	@Override
	public void saveBpsStep(BpsStepDTO dto) {
		bpBpsStepRepository.save(modelMapper.map(dto, BpBpsStep.class));
	}

}
