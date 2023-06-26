package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpCibPod;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpCipPodResponse;

public interface BpCibPodService {
	BpCibPod findLastByMypod(String mypod);

	BpCibPod createWithMydfdl(String mypod, String mydfdl);

	BpCibPod createWithMywso(String mypod, String mywso);

	BpCipPodResponse BPSetWSO(String podCode, String wso);

	BpCipPodResponse BPSetDFDL(String podCode, String dfdl);
}
