package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.common.enumtype.BpTaskStatusEnum;

public interface BpTaskInfoService {

	String getTaskInfo(String name);

	void create(String taskName, BpTaskStatusEnum statusEnum, String errorMsg);

	void delete(String taskName);
}
