package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.entity.ULCMerge;
import vn.edu.clevai.bplog.repository.ULCMergeRepository;
import vn.edu.clevai.bplog.service.ULCMergeService;

import java.util.Optional;

@Service
public class ULCMergeServiceImpl implements ULCMergeService {

	private final ULCMergeRepository ulcMergeRepository;

	public ULCMergeServiceImpl(ULCMergeRepository ulcMergeRepository) {
		this.ulcMergeRepository = ulcMergeRepository;
	}

	@Override
	@Transactional
	public ULCMerge createOrUpdate(ULCMerge ulcMerge) {
		Optional<ULCMerge> opt = ulcMergeRepository.findFirstByCode(ulcMerge.getCode());

		if (opt.isPresent()) {
			return opt.map(a -> {
				a.setPublished(true);
				return a;
			}).get();
		} else {
			return ulcMergeRepository.save(ulcMerge);
		}
	}

	@Override
	public ULCMerge findByCode(String code) {
		return ulcMergeRepository.findFirstByCode(code).orElse(null);
	}
}
