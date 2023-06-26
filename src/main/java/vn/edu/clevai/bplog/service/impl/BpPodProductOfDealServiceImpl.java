package vn.edu.clevai.bplog.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.repository.BpPodProductOfDealRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.internal.Cats5Service;
import vn.edu.clevai.bplog.service.proxy.salesproxy.SaleService;
import vn.edu.clevai.bplog.service.proxy.userproxy.UserService;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BpPodProductOfDealServiceImpl extends BaseProxyService implements BpPodProductOfDealService {
	private final BpPodProductOfDealRepository bpPodProductOfDealRepository;

	private final BpPeriodService bpPeriodService;

	private final Cats5Service cats5Service;

	private final BpUsiUserItemService bpUsiUserItemService;
	private final SaleService saleService;

	private final UserService userService;

	private final CalendarPeriodService capService;

	public BpPodProductOfDealServiceImpl(
			BpPodProductOfDealRepository bpPodProductOfDealRepository,
			BpPeriodService bpPeriodService,
			Cats5Service cats5Service,
			Cep100UserService cep100UserService,
			@Lazy BpUsiUserItemService bpUsiUserItemService,
			SaleService saleService,
			UserService userService,
			@Lazy CalendarPeriodService capService) {
		this.bpPodProductOfDealRepository = bpPodProductOfDealRepository;
		this.bpPeriodService = bpPeriodService;
		this.cats5Service = cats5Service;
		this.bpUsiUserItemService = bpUsiUserItemService;
		this.saleService = saleService;
		this.userService = userService;
		this.capService = capService;
	}

	@Override
	public BpPodProductOfDeal findByCode(String code) {
		return bpPodProductOfDealRepository.findByCode(code)
				.orElseThrow(
						() -> new NotFoundException("Could not find any BpPodProductOfDeal using code = " + code)
				);
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_POD_FROM_X
	)
	public PODResponse getPODFromX(Long xXdealid) {
		PODResponse response = saleService.getPODFromX(xXdealid);
		response.setMyprd(bpPeriodService.findByLengthAndUnit(response.getXprdLength(), response.getXprdUnit()).getCode());
		response.setMypt(Cep100TransformUtils.toPtCode(response.getXpt()));

		String date = DateUtils.format(response.getFromDate(), DateUtils.UTC_TIME_ZONE, DateUtils.yyyyMMdd);

		response.setCode(String.join("-", response.getMypt(), response.getMyprd(),
				response.getMyst(), date));
		return response;
	}

	@Override
	@WriteUnitTestLog
	public Date getExpireDate(String podCode) {
		BpPodProductOfDeal bp = findByCode(podCode);
		return cats5Service.getExpireDate(bp.getXdeal());
	}

	@Override
	public List<BpPodProductOfDeal> findByClagsAndDate(List<String> sussClagList, Date date) {
		return bpPodProductOfDealRepository.findByClagInAndDate(sussClagList, date);
	}

	@Override
	public PODResponse getPOD(String xst, Long xpt) {
		BpUsiUserItemResponse response = bpUsiUserItemService.getSTFromX(xst);
		UserAccountResponse userAccountResponse = userService.getStudentProfile(xst).getBody();
		String pt = Cep100TransformUtils.toPtCode(xpt);

		String pattern = "yyyyMMdd";
		DateFormat df = new SimpleDateFormat(pattern);
		String date = df.format(userAccountResponse.getCreatedAt());
		Date toDate = new Date(200, 11, 31); // 2100-12-31
		String prd = "AT";
		return PODResponse.builder()
				.myst(response.getCode())
				.myprd(prd)
				.mypt(pt)
				.code(String.join("-", pt, prd,
						response.getCode(), date))
				.fromDate(new java.sql.Date(DateUtils.endOfDay(userAccountResponse.getCreatedAt()).getTime()))
				.toDate(new java.sql.Date(DateUtils.endOfDay(toDate.getTime()).getTime()))
				.build();
	}

	@Override
	public List<BpPodProductOfDeal> findPodCodeByClag(List<String> clagCode, List<String> ust) {
		return bpPodProductOfDealRepository.findByClagCode(clagCode, ust);
	}

	@Override
	public List<BpPodProductOfDeal> findPodCodeByClag(List<String> clagCode, List<String> ust, Timestamp start) {

		if (CollectionUtils.isEmpty(clagCode) || CollectionUtils.isEmpty(ust)) {
			return Collections.emptyList();
		}

		return bpPodProductOfDealRepository.findPodCodeByClag(clagCode, ust, start);
	}

	@Override
	public List<BpPodProductOfDeal> findPodByClagCadyAndUst(List<String> clagCodes, List<String> ust, CalendarPeriod cady) {
		return bpPodProductOfDealRepository.findByClagAndCadyAndUst(clagCodes, ust, cady.getStartTime(), cady.getEndTime());
	}

	@Override
	public BpPodProductOfDeal findByUsi(String usi, String pt) {
		return bpPodProductOfDealRepository.findByUsiAndPt(usi, pt)
				.orElseThrow(
						() -> new NotFoundException("Coun't find pod by usi:" + usi + " and pt :" + pt)
				);
	}

	@Override
	public BpPodProductOfDeal findFirstBy(String lcetCode, String myust, String capCode, String chrtCode, String lcpCode, Collection<String> excludeUsi, String gg, String dfdl, String dfge) {
		return bpPodProductOfDealRepository.findFirstByUSIDuty(
						lcetCode,
						myust,
						capCode,
						chrtCode,
						lcpCode,
						CollectionUtils.isNotEmpty(excludeUsi) ? excludeUsi : Collections.singletonList(""),
						gg,
						dfdl,
						dfge)
				.orElseThrow(
						() -> new NotFoundException("Couldn't find pod with lcet: " + lcetCode + " myust: " + myust
								+ " cap: " + capCode + " chrt: " + chrtCode + " lcp: " + lcpCode + " excludeUsi: " + excludeUsi
								+ " gg: " + gg + " dfdl: " + dfdl + " dfge: " + dfge)
				);
	}

	@Override
	public List<BpPodProductOfDeal> findBy(String lcetCode, String myust, String capCode, String chrtCode, String lcpCode, Collection<String> excludeUsi, String gg, String dfdl, String dfge) {
		return bpPodProductOfDealRepository.findBy(
				lcetCode,
				myust,
				capCode,
				chrtCode,
				lcpCode,
				CollectionUtils.isNotEmpty(excludeUsi) ? excludeUsi : Collections.singletonList(""),
				gg,
				dfdl,
				dfge);
	}

	@Override
	public BpPodProductOfDeal findByUsiAndDate(String usi, String pt, long milliSeconds) {
		return bpPodProductOfDealRepository.findByUsiAndPtAndDate(usi, pt, new java.sql.Date(milliSeconds))
				.orElseThrow(
						() -> new NotFoundException("Coun't find pod by usi:" + usi + " and pt :" + pt)
				);
	}


	@Override
	public List<String> getPtFromCadyAndUst(String cady, String ust) {
		Timestamp capEnd = capService.findByCode(cady).getEndTime();
		return bpPodProductOfDealRepository.getPtFromCadyAndUst(capEnd, ust);
	}

	@Override
	public List<BpPodProductOfDeal> findByUsi(String usi, Timestamp time) {
		return bpPodProductOfDealRepository.findByUsi(usi, time);
	}

	@Override
	public List<BpPodProductOfDeal> findByListUsi(List<String> usis) {
		return bpPodProductOfDealRepository.findAllByMystIn(usis);
	}

	@Override
	public List<BpPodProductOfDeal> findActivePodsByClagAndCap(String clag, String cap, List<String> usts) {
		return bpPodProductOfDealRepository.findActivePodsClagAndCap(clag, cap, usts);
	}
}