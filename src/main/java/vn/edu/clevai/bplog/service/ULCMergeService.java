package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.ULCMerge;

public interface ULCMergeService {

	ULCMerge createOrUpdate(ULCMerge ulcMerge);

	ULCMerge findByCode(String code);
}
