package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.entity.BpVideoDiliveConfig;
import vn.edu.clevai.bplog.repository.BpVideoDiliveConfigRepository;
import vn.edu.clevai.bplog.service.BpVideoDiliveService;

@Service
public class BpVideoDiliveServiceImpl implements BpVideoDiliveService {

	private final BpVideoDiliveConfigRepository repository;

	public BpVideoDiliveServiceImpl(BpVideoDiliveConfigRepository repository) {
		this.repository = repository;
	}


	@Override
	public BpVideoDiliveConfig findByMyptAndMyggAndMydfdl(String pt, String gg, String dfdl) {
		return repository.findByMyptAndMyggAndMydfdl(pt, gg, dfdl).orElse(null);
	}

}
