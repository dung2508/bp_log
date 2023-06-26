package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.common.enumtype.ModifyTypeEnum;
import vn.edu.clevai.bplog.entity.BpWsoWeeklyscheduleoption;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.bplog.payload.response.*;
import vn.edu.clevai.common.proxy.lms.payload.response.XSessionGroupInfoResponse;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class BPPServiceImpl extends BaseProxyService implements BPPService {

	private final PodService podService;
	private final BpUsiUserItemService bpUsiUserItemService;
	private final BpGGGradeGroupService bpGGGradeGroupService;
	private final BpGgStService bpGgStService;
	private final BpClagClassgroupService bpClagClassgroupService;
	private final BpPodProductOfDealService bpPodProductOfDealService;
	private final BpDfdlDifficultygradeService bpDfdlDifficultygradeService;
	private final BpPODDFDLService bpPODDFDLService;
	private final BpWsoWeeklyscheduleoptionService bpWsoWeeklyscheduleoptionService;
	private final BPPService bppService;

	private final BpULCService bpULCService;

	public BPPServiceImpl(PodService podService,
						  BpUsiUserItemService bpUsiUserItemService,
						  BpGGGradeGroupService bpGGGradeGroupService,
						  BpGgStService bpGgStService,
						  @Lazy BpClagClassgroupService bpClagClassgroupService,
						  BpPodProductOfDealService bpPodProductOfDealService,
						  @Lazy BpDfdlDifficultygradeService bpDfdlDifficultygradeService,
						  @Lazy BpPODDFDLService bpPODDFDLService,
						  @Lazy BpWsoWeeklyscheduleoptionService bpWsoWeeklyscheduleoptionService,
						  @Lazy BPPService bppService,
						  @Lazy BpULCService bpULCService) {
		this.podService = podService;
		this.bpUsiUserItemService = bpUsiUserItemService;
		this.bpGGGradeGroupService = bpGGGradeGroupService;
		this.bpGgStService = bpGgStService;
		this.bpClagClassgroupService = bpClagClassgroupService;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.bpDfdlDifficultygradeService = bpDfdlDifficultygradeService;
		this.bpPODDFDLService = bpPODDFDLService;
		this.bpWsoWeeklyscheduleoptionService = bpWsoWeeklyscheduleoptionService;
		this.bppService = bppService;
		this.bpULCService = bpULCService;
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.BPP_ASSIGN_GG
	)
	public void bppAssignGG(Long xdeal) {
		String xst = podService.findXST(xdeal);
		BpUsiUserItemResponse response = podService.getSTFromX(xst);
		Long xgg = bpGGGradeGroupService.findXGG(xst);
		BpGgGradegroupResponse response1 = bpGGGradeGroupService.getGGFromX(xgg);
		log.info("setST_GG stCode: {} ggCode: {}", response.getCode(), response1.getCode());
		BpGgStResponse response2 = bpGgStService.setST_GG(response.getCode(), response1.getCode());
		response2.setStCode(response2.getCode());
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.BPP_ASSIGN_CLAGPERM
	)
	public void bppAssignCLAGPERM(Long xdeal, String modifyType) {
		String xclass = bpClagClassgroupService.findXCLASS(xdeal);
		BpClagClassgroupResponse response = bpClagClassgroupService.getCLAGPERMFromX(xclass);

		log.info("setCLAGPERM code: {} mypt: {} mygg: {} mydfdl: {} mywso: {} clagType: {} xclass: {} maxTotalStudent: {}", response.getCode(), response.getMypt(), response.getMygg(), response.getMydfdl(), response.getMywso(), response.getClagtype(), xclass, response.getMaxtotalstudents());
		bpClagClassgroupService.createOrUpdatePermanentClag(response.getCode(), response.getMypt(), response.getMygg(), response.getMydfdl(), response.getMywso(), response.getClagtype(), xclass, response.getMaxtotalstudents());

		PODResponse response2 = bpPodProductOfDealService.getPODFromX(xdeal);
		BpClagClassgroupResponse response1 = bpClagClassgroupService.getPOD_CLAGPERM(response2.getCode());
		log.info("setPOD_CLAGPERM podCode: {} clagCode: {} assignedAt: {} unassignedAt: {} clagType: {}", response2.getCode(), response1.getCode(), response1.getAssignedAt(), response1.getUnassignedAt(), response.getClagtype());
		bpClagClassgroupService.setPOD_CLAGPERM(response2.getCode(), response1.getCode(), response1.getAssignedAt(), response1.getUnassignedAt(), response.getClagtype(), modifyType);

	}

	public void bppUpdatePODCLAG(Long xdeal, Timestamp newStartDate, Timestamp newEndDate, String modifyType) {
		PODResponse response2 = bpPodProductOfDealService.getPODFromX(xdeal);
		BpClagClassgroupResponse response1 = bpClagClassgroupService.getPOD_CLAGPERM(response2.getCode());
		log.info("bppUpdatePODCLAG podCode: {} clagCode: {} assignedAt: {} unassignedAt: {}", response2.getCode(), response1.getCode(), response1.getAssignedAt(),
				response1.getUnassignedAt());
		bpClagClassgroupService.setPOD_CLAGPERM(response2.getCode(), response1.getCode(),
				response1.getAssignedAt(), response1.getUnassignedAt(), ClagType.PERMANANT.getCode(), modifyType);

		java.sql.Date fromDate = Objects.nonNull(newStartDate) ? new java.sql.Date(newStartDate.getTime()) : null;
		java.sql.Date endDate = Objects.nonNull(newEndDate) ? new java.sql.Date(newEndDate.getTime()) : null;
		log.info("setPOD code: {} mypt: {} myst: {} myprd: {} from: {} to: {} xdeal: {}",
				response2.getCode(), response2.getMypt(), response2.getMyst(),
				response2.getMyprd(), fromDate, endDate, xdeal);
		podService.setPOD(response2.getCode(), response2.getMypt(), response2.getMyst(),
				response2.getMyprd(), fromDate, endDate, xdeal);
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.BPP_ASSIGN_WSO
	)
	public void bppAssignWSO(Long xdeal) {
		PODResponse response = bpPodProductOfDealService.getPODFromX(xdeal);
		String xwso = bpWsoWeeklyscheduleoptionService.findXWSO(xdeal);
		BpWsoWeeklyscheduleoption bpWsoWeeklyscheduleoption = bpWsoWeeklyscheduleoptionService.getWSOFromX(xwso);

		log.info("setPOD_WSO podCode: {} wso: {}", response.getCode(), bpWsoWeeklyscheduleoption.getCode());
		BpPODWSOResponse response1 = bpWsoWeeklyscheduleoptionService.setPOD_WSO(response.getCode(), bpWsoWeeklyscheduleoption.getCode());
		response1.setPodMywso(response1.getCode());
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.BPP_ASSIGN_DFDL
	)
	public void bppAssignDFDL(Long xdeal) {
		PODResponse response = bpPodProductOfDealService.getPODFromX(xdeal);
		Integer xdfdl = bpDfdlDifficultygradeService.findXDFDL(xdeal);
		BpDfdlDifficultgradeResponse response1 = bpDfdlDifficultygradeService.getDFDLFromX(xdfdl);

		log.info("setPOD_DFDL podCode: {} dfdlCode: {}", response.getCode(), response1.getCode());
		BpPODDFDLResponse response2 = bpPODDFDLService.setPOD_DFDL(response.getCode(), response1.getCode());
		response2.setPodMydfdl(response2.getCode());
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.BPP_ASSIGN_STUDENT
	)
	public void bppAssignStudent(Long xdeal) {
		String xst = podService.findXST(xdeal);
		BpUsiUserItemResponse response = podService.getSTFromX(xst);

		log.info("setST: code: {} lastname: {} firstname: {} st: {} username: {}", response.getCode(), response.getLastname(), response.getFirstname(), USTEnum.ST.getName(), response.getUsername());
		bpUsiUserItemService.createOrUpdateUsi
				(response.getCode(), response.getLastname(), response.getFirstname(),
						USTEnum.ST.getName(), response.getUsername(), response.getFullName(), response.getPhone(), null);
	}

	@Override
	public void bppChangeGG(Long xdeal) {
		bppService.bppAssignWSO(xdeal);
		bppService.bppAssignDFDL(xdeal);
		bppService.bppAssignCLAGPERM(xdeal, null);
		bppService.bppAssignGG(xdeal);
	}

	@Override
	public void bppChangeDFDL(Long xdeal) {
		bppService.bppAssignWSO(xdeal);
		bppService.bppAssignDFDL(xdeal);
		bppService.bppAssignCLAGPERM(xdeal, null);
	}

	@Override
	public void bppChangeWSO(Long xdeal) {
		bppService.bppAssignWSO(xdeal);
		bppService.bppAssignDFDL(xdeal);
		bppService.bppAssignCLAGPERM(xdeal, null);
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.BPP_PURCHASE
	)
	public void bppPurchase(Long xdeal) {
		bppService.bppAssignStudent(xdeal);
		PODResponse response = bpPodProductOfDealService.getPODFromX(xdeal);

		log.info("setPOD code: {} mypt: {} myst: {} myprd: {} from: {} to: {} xdeal: {}", response.getCode(), response.getMypt(), response.getMyst(), response.getMyprd(), response.getFromDate(), response.getToDate(), xdeal);
		podService.setPOD(response.getCode(), response.getMypt(), response.getMyst(), response.getMyprd(), response.getFromDate(), response.getToDate(), xdeal);

		bppService.bppAssignWSO(xdeal);
		bppService.bppAssignDFDL(xdeal);
		bppService.bppAssignCLAGPERM(xdeal, ModifyTypeEnum.PURCHASE.getName());
		bppService.bppAssignGG(xdeal);
	}

	@Override
	public void bppRenewRepeat(Long xdeal) {
		bppService.bppPurchase(xdeal);
	}

	@Override
	public void bppRenewCrossSell(Long xdeal) {
		bppService.bppPurchase(xdeal);
	}

	@Override
	public void bppRenewTransfer(Long xdeal1, Timestamp startDateDeal1, Timestamp endDateDeal1,
								 Long xdeal2) {
		bppService.bppTransfer(xdeal1, startDateDeal1, endDateDeal1);
		bppService.bppPurchase(xdeal2);
	}

	@Override
	public void bppRenewTopUp(Long xdeal1, Timestamp startDateDeal1, Timestamp endDateDeal1,
							  Long xdeal2) {
		bppService.bppTopup(xdeal1, startDateDeal1, endDateDeal1);
		bppService.bppPurchase(xdeal2);
	}

	@Override
	public BpPODCLAGResponse bppAssignCLAGDYN(Long xdeal, String xcady) throws ParseException {
		String xst = podService.findXST(xdeal);
		Long xgg = bpGGGradeGroupService.findXGG(xst);

		String xcash = bpClagClassgroupService.getXCASH(xcady, xgg);
		Date unassignedAt = DateUtils.endOfDay(DateUtils.parse(xcash, DateUtils.MEDIUM_PATTERN, DateUtils.UTC_TIME_ZONE).getTime());
		XSessionGroupInfoResponse response = bpClagClassgroupService.findXSESSIONGROUP(xdeal, xcash);
		BpClagClassgroupResponse response1 = bpClagClassgroupService.getCLAGDYNFromX(response.getCode(), xcash);

		log.info("setCLAGDYN code: {} mypt: {} mygg: {} mydfdl: {} mydfge: {} mywso: {} maxtotalstudent: {} clagType: {} xsessiongroup: {} xcash: {}", response1.getCode(), response1.getMypt(), response1.getMygg(), response1.getMydfdl(), response1.getMydfge(), response1.getMywso(), response1.getMaxtotalstudents(), ClagType.DYNAMIC.getCode(), response.getCode(), xcash);
		bpClagClassgroupService.createOrUpdateDynamicClag(response1.getCode(), response1.getMypt(), response1.getMygg(), response1.getMydfdl(), response1.getMydfge(), response1.getMywso(), response1.getMaxtotalstudents(), ClagType.DYNAMIC.getCode(), response.getCode(), xcash);

		PODResponse response2 = bpPodProductOfDealService.getPODFromX(xdeal);

		log.info("setPOD_CLAGDYN podCode: {} clagCode: {} assignedAt: {} unassignedAt: {} clagType: {}", response2.getCode(), response1.getCode(), response.getAssignedAt(), unassignedAt, ClagType.DYNAMIC.getCode());

		return bpClagClassgroupService.setPOD_CLAGDYN(response2.getCode(), response1.getCode(), response.getAssignedAt(), unassignedAt, ClagType.DYNAMIC.getCode());
	}

	@Override
	public void bppDeferAfterSignup(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, ModifyTypeEnum.DEFER_AFTER_SIGNUP.getName());
	}

	@Override
	public void bppDeferBeforeSignup(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, ModifyTypeEnum.DEFER_BEFORE_SIGNUP.getName());
	}

	@Override
	public void bppSuspend(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, ModifyTypeEnum.SUSPEND.getName());
	}

	@Override
	public void bppUnSuspend(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, ModifyTypeEnum.UNSUSPEND.getName());
	}

	@Override
	public void bppRefund(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, ModifyTypeEnum.REFUND.getName());
	}

	@Override
	public void bppExtend(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, null);
	}

	@Override
	public void bppTransfer(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, ModifyTypeEnum.RENEW_TRANFER.getName());
	}

	@Override
	public void bppTopup(Long xdeal, Timestamp newStartDate, Timestamp newEndDate) {
		bppService.bppUpdatePODCLAG(xdeal, newStartDate, newEndDate, ModifyTypeEnum.RENEW_TOPUP.getName());
	}
}
