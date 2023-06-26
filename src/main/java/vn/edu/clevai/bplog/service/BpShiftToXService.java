package vn.edu.clevai.bplog.service;

import java.util.List;

import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;

public interface BpShiftToXService {
	List<BpUniqueLearningComponent> getAll_USH_FromCADY(String cadyCode);

	List<BpUniqueLearningComponent> getAll_UDL_FromUSH(String ush);

	List<BpUniqueLearningComponent> getAll_URC_FromUSH(String urc);

	List<BpUniqueLearningComponent> getAll_UGE_FromUSH(String ush);

	List<BpUniqueLearningComponent> getAll_ULI_FromUSH(String ush);

	List<BpClagClassgroup> getAll_CLAG_FromULC(String ulc);

	List<BpContentItem> getAll_CTI_FromUSH(String ush);

	List<BpContentItem> getAll_CTI_FromURC(String urc);

	List<BpContentItem> getAll_CTI_FromUDL(String udl);

	List<BpContentItem> getAll_CTI_FromUGE(String uge);
	
	Object getXDEALFromBP(String pod);
	
	Object getXSESSIONGROUPFromBP(String clagDyn);
	
	Object getXSTFromBP(String st);

	String convertI_USH_XDailyScheduleClass(String ush);
	
	String convertI_UDL_XDailyScheduleClass(String udl);
	
	String convertI_URC_XDailyScheduleClass(String urc);
	
	String convertI_UCO_XDailyScheduleClass(String uco);
	
	String convertI_UGE_XDailyScheduleClass(String uge);
	
	String convertI_ULI_XDailyScheduleClass(String uli);
	
	String convertI_CLAG_XDSCSessionGroup(String clag);
	
	String convertI_CLAG_XSessionGroup(String clag);
	
	String convertI_CTI_XDailyScheduleClass(String cti);
	
	String convertI_CTI_XDSCBattleQuizs(String cti);
	
	String convertI_CTI_XDSCLiveQuizs(String cti);
	
	String convertI_CTI_XDSCLiveQuizAnswers(String cti);
	
	String convertI_CTI_XDSCSlides(String cti);
	
	String convertI_CTI_XStreamingConfig(String cti);
	
	String convertI_CTI_XZoomMeetings(String cti);
}
