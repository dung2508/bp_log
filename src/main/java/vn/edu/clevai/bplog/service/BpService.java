package vn.edu.clevai.bplog.service;

import java.util.List;

import vn.edu.clevai.bplog.entity.logDb.BpChpiCheckProcessItem;
import vn.edu.clevai.bplog.payload.response.logdb.BpChptCheckProcessTempResponse;
import vn.edu.clevai.bplog.payload.response.logdb.CHLIResponse;
import vn.edu.clevai.bplog.payload.response.logdb.CHSIResponse;

import javax.mail.MessagingException;

public interface BpService {
	BpChptCheckProcessTempResponse findCHPT5(
			String lctCode, String lcetCode, String chptType, String ustTrigger, String ustChecker);

	BpChpiCheckProcessItem createBpCHPI(String chptCode, String cti1Code, String cti2Code,
										String cti3Code, String cuiEventCode, String toSendEmail);

	BpChptCheckProcessTempResponse findCHPT4(String cuiEventCode) throws Exception;

	CHSIResponse bpCreateCHSI(String chstCode, String chpiCode);

	CHLIResponse bpCreateCHLI(String chltCode, String chliCode, String chsiCode);

	String bpFindUsiCode(String chrtCode, String cuiEventCode);

	String bpAssignData(String chsiCode, String chriCode);

	void bpSendEmail(String chsiCode, String chriCode) throws MessagingException;

	void sendEmailCHSI(String chsiCode) throws Exception;

	void sendEmailCHPI(String chpiCode) throws MessagingException;

	List<String> bpAssignChri(String chpiCode) throws Exception;

}
