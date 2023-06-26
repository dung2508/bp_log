package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpVideoDiliveConfig;

public interface BpVideoDiliveService {
	BpVideoDiliveConfig findByMyptAndMyggAndMydfdl(String pt, String gg, String dfdl);
}
