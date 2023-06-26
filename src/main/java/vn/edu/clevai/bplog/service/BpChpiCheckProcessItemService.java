package vn.edu.clevai.bplog.service;

import java.util.List;

public interface BpChpiCheckProcessItemService {

	List<String> prepareChecking(String cuiEventCode, String cti1Code, String cti2Code, String cti3Code, String toSendEmail) throws Exception;

}
