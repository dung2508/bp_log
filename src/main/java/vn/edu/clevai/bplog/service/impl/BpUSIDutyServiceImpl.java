package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.CodeGenerator;
import vn.edu.clevai.bplog.common.enumtype.CLCTypeEnum;
import vn.edu.clevai.bplog.dto.sheet.UsidRegisterSO5DTO;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.payload.request.RegisterEMRequest;
import vn.edu.clevai.bplog.repository.BpUSIDutyRepository;
import vn.edu.clevai.bplog.repository.BpUsiDutyClassCategoryRepository;
import vn.edu.clevai.bplog.repository.projection.UsidDistinctInfoProjection;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.utils.Utils;
import vn.edu.clevai.common.api.exception.FileSavedException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.payload.request.SetUsiDutyFromXRequest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BpUSIDutyServiceImpl implements BpUSIDutyService {

	private final BpUSIDutyRepository usiDutyRepository;
	private final BpUsiUserItemService usiService;

	private final ClassCategoryService classCategoryService;

	private final BpUsiDutyClassCategoryRepository usiDutyCashStaRepository;

	private final BpLCPService lcpService;

	private final AccYearService accYearService;

	private final BpWsoWeeklyscheduleoptionService wsoService;
	private final CalendarPeriodService calendarPeriodService;

	private final BpPodProductOfDealService bpPodProductOfDealService;
	private final ModelMapper modelMapper;

	private final int SEVEN_HOURS_IN_SECONDS = 60 * 60 * 7;

	public BpUSIDutyServiceImpl(BpUSIDutyRepository usiDutyRepository, @Lazy BpUsiUserItemService usiService, ClassCategoryService classCategoryService, BpUsiDutyClassCategoryRepository usiDutyCashStaRepository, BpLCPService lcpService, AccYearService accYearService, BpWsoWeeklyscheduleoptionService wsoService, @Lazy CalendarPeriodService calendarPeriodService, BpPodProductOfDealService bpPodProductOfDealService, ModelMapper modelMapper) {
		this.usiDutyRepository = usiDutyRepository;
		this.usiService = usiService;
		this.classCategoryService = classCategoryService;
		this.usiDutyCashStaRepository = usiDutyCashStaRepository;
		this.lcpService = lcpService;
		this.accYearService = accYearService;
		this.wsoService = wsoService;
		this.calendarPeriodService = calendarPeriodService;
		this.bpPodProductOfDealService = bpPodProductOfDealService;
		this.modelMapper = modelMapper;
	}


	@Override
	public BpUsiDuty setUsiDFromX(
			String lcet, String lcp, String ust, String cap, String chrt, String usi, String gg, String dfdl, String dfge) {
		try {
			usiDutyRepository.save(BpUsiDuty.builder()
					.myLcet(lcet)
					.myLcp(lcp)
					.myUst(ust)
					.myUsi(usi)
					.myCap(cap)
					.myChrt(chrt)
					.code(getUsiDutyCode(cap, lcp, lcet, ust, usi, chrt))
					.build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new FileSavedException("Coun't save to db because : " + e.getMessage());
		}
		return null;
	}

	public String getUsiDutyCode(
			String capCode, String lcpCode, String lcetCode,
			String ustCode, String usiCode, String chrtCode) {
		return String.join("",
				Objects.isNull(capCode) ? "" : capCode + "-",
				Objects.isNull(lcpCode) ? "" : lcpCode + "-",
				Objects.isNull(lcetCode) ? "" : lcetCode + "-",
				Objects.isNull(ustCode) ? "" : ustCode + "-",
				Objects.isNull(usiCode) ? "" : usiCode + "-",
				Objects.isNull(chrtCode) ? "" : chrtCode);
	}

	@Override
	public void registerTeToCap(SetUsiDutyFromXRequest request) {
		//  approve
	}

	@Override
	public List<String> findUsi(String lcet, String ust, String cap, String chrt, String lcp, String excludeUsi, String gg, String dfdl, String dfge) {
		return usiDutyRepository.findByUlc(lcet, ust, cap, chrt, lcp, excludeUsi, gg, dfdl, dfge);
	}

	@Override
	public BpUsiDuty setUsiDutyFromX(String lcp, String cap, String ust, String usi, String lcet, String chrt) {
		String usiDCode = getUsiDutyCode(cap, lcp, lcet, ust, usi, chrt);
		BpUsiDuty bpUsiDuty = usiDutyRepository.findFirstByCode(usiDCode)
				.orElseGet(() -> usiDutyRepository.save(BpUsiDuty.builder()
						.myLcet(lcet)
						.myLcp(lcp)
						.myCap(cap)
						.myUst(ust)
						.myUsi(usi)
						.myChrt(chrt)
						.code(usiDCode)
						.isApproved(true)
						.build()));
		return bpUsiDuty;
	}

	@Override
	public UsiDutyClassCategory setUsiDutyClc(String clcCode, String usiDutyCode, Boolean isEnable) {
		String code = usiDutyCode + "-" + clcCode;
		UsiDutyClassCategory usiDutyClassCategory = usiDutyCashStaRepository.findFirstByCode(code)
				.orElseGet(() -> usiDutyCashStaRepository.save(UsiDutyClassCategory.builder()
						.myClassCategory(clcCode)
						.myUsiD(usiDutyCode)
						.isEnable(isEnable)
						.code(code)
						.build()));
		usiDutyClassCategory.setIsEnable(isEnable);
		return usiDutyClassCategory;
	}

	@Override
	public String getWSOFromListDay(List<String> listDay) {
		return listDay.stream().map(Integer::parseInt).sorted().map(Object::toString).collect(Collectors.joining());
	}

	public Boolean getIsEnableFromSUI(Long requestType, Long approvedStatus) {
		if (approvedStatus == 2 || approvedStatus == 1) {
			if (requestType == 1) return true;
			if (requestType == 2) return false;
		}
		return null;
	}

	@Override
	public List<String> findUsiFromBp(String pt, String cap, String ust, String gg, String dfdl,
									  String dfge, String lcp, String position) {
		log.info("findUsiFromBp pt-{} cap-{} gg-{} dfdl-{} ust-{}, dfge-{} lcp-{} position-{}",
				pt, cap, gg, dfdl, ust, dfge, lcp, position);
		return usiDutyRepository.findUsiFromBp(cap, ust, pt, gg, dfdl, dfge, lcp, position);
	}

	@Override
	public List<String> findRegister5Users(String pt, List<String> cap, String ust, String gg, String dfdl,
										   String dfge, String lcp, String position) {
		log.info("findRegister5Users pt-{} cap-{} gg-{} dfdl-{} ust-{}, dfge-{} lcp-{} position-{}",
				pt, cap, gg, dfdl, ust, dfge, lcp, position);
		return usiDutyRepository.findRegister5Users(cap, ust, pt, gg, dfdl, dfge, lcp, position);
	}

	@Override
	public List<String> findUsiBackupByCady(String cady) {
		return usiDutyRepository.findUsiBackupByCady(cady);
	}

	@Override
	public List<String> findEmFromCady(String cady, String lcet, String ust, String lcp) {
		return usiDutyRepository.findEmFromCady(cady, lcet, ust, lcp);
	}

	@Override
	public void registerEMDiLive(RegisterEMRequest registerEMRequest) {
		CalendarPeriod calendarPeriod = calendarPeriodService.findByCode(registerEMRequest.getCady());
		String accYearCode = accYearService.findByTime(calendarPeriod.getEndTime()).getCode();
		String term = Utils.getMyTermFromTime(calendarPeriod.getStartTime());
		ClassCategory clcCsh = classCategoryService.findClcCsh(accYearCode, registerEMRequest.getGg(), term, true);
		BpWsoWeeklyscheduleoption wso = wsoService.getWSOFromX(String.valueOf(DateUtils.getDayOfWeek(new Date(calendarPeriod.getStartTime().getTime())).getValue() + 1));
		BpUsiUserItem usiUserItem = usiService.findByCode(registerEMRequest.getUsi());

		Timestamp timeCheck = DateUtils.addSecondToTimestamp(calendarPeriod.getStartTime(), SEVEN_HOURS_IN_SECONDS);

		List<BpPodProductOfDeal> productOfDealList = bpPodProductOfDealService.findByUsi(registerEMRequest.getUsi(), timeCheck);
		productOfDealList.forEach(pod -> {
			log.info("---------productOfDeal pod {}", pod);
			String mypt = pod.getMypt();
			ClassCategory classCategory = ClassCategory.builder()
					.myAccYear(accYearCode)
					.myTerm(term)
					.myPt(mypt)
					.myGg(registerEMRequest.getGg())
					.myWso(wso.getCode())
					.myDfdl(registerEMRequest.getDfdl())
					.myCashSta(clcCsh.getMyCashSta())
					.clcType(CLCTypeEnum.CLC_TED.getCode())
					.build();

			ClassCategory clc = classCategoryService.createOrUpdate(classCategory);
			log.info("---------ClassCategory clc {}", clc);
			try {
				BpLCP lcp = lcpService.findFirstByMyptAndMylct(mypt, registerEMRequest.getLct());
				log.info("---------BpLCP lcp {}", lcp);

				//create or update usiD
				String usiDutyCode = getUsiDutyCode(calendarPeriod.getCode(), lcp.getCode(), registerEMRequest.getLcet(), usiUserItem.getMyust(), registerEMRequest.getUsi(), registerEMRequest.getChrt());
				BpUsiDuty bpUsiDuty = usiDutyRepository.findFirstByCode(usiDutyCode).orElseGet(() -> BpUsiDuty.builder().code(usiDutyCode).build());
				bpUsiDuty.setMyLcet(registerEMRequest.getLcet());
				bpUsiDuty.setMyUst(usiUserItem.getMyust());
				bpUsiDuty.setMyCap(calendarPeriod.getCode());
				bpUsiDuty.setMyChrt(registerEMRequest.getChrt());
				bpUsiDuty.setMyUsi(registerEMRequest.getUsi());
				bpUsiDuty.setMyLcp(lcp.getCode());
				BpUsiDuty usiDuty = usiDutyRepository.save(bpUsiDuty);
				log.info("---------usiDuty {}", usiDuty);

				//create or update dtc
				String dtcCode = CodeGenerator.buildNormalCode(usiDutyCode + clc.getCode());
				UsiDutyClassCategory usiDutyClassCategory = usiDutyCashStaRepository.findFirstByCode(dtcCode).orElseGet(() -> UsiDutyClassCategory.builder().code(dtcCode).build());
				usiDutyClassCategory.setMyClassCategory(clc.getCode());
				usiDutyClassCategory.setMyUsiD(usiDutyCode);
				usiDutyClassCategory.setIsEnable(true);
				UsiDutyClassCategory dtc = usiDutyCashStaRepository.save(usiDutyClassCategory);
				log.info("---------dtc {}", dtc);
			} catch (NotFoundException exception) {
				log.error("Not found BpLCP with PT: {} , LCT: {} ", mypt, registerEMRequest.getLct());
			}
		});
	}

	@Override
	public void registerEMGET(RegisterEMRequest registerRequest) {
		BpUsiUserItem userItem = usiService.findByCode(registerRequest.getUsi());
		CalendarPeriod calendarPeriod = calendarPeriodService.findByCode(registerRequest.getCady());

		//usi -> ust done
		//usi -> pod(s) done
		Timestamp timeCheck = DateUtils.addSecondToTimestamp(calendarPeriod.getStartTime(), SEVEN_HOURS_IN_SECONDS);

		List<BpPodProductOfDeal> productOfDeals = bpPodProductOfDealService.findByUsi(registerRequest.getUsi(), timeCheck);
		productOfDeals.forEach(pod -> {
					String mypt = pod.getMypt();
					try {
						BpLCP lcp = lcpService.findFirstByMyptAndMylct(mypt, registerRequest.getLct());
						String usiDutyCode = getUsiDutyCode(calendarPeriod.getCode(),
								lcp.getCode(),
								registerRequest.getLcet(),
								userItem.getMyust(),
								registerRequest.getUsi(),
								registerRequest.getChrt());
						log.info("In pod [{}] will create or update BpUsiDuty with code {}", pod.getCode(), usiDutyCode);
						BpUsiDuty bpUsiDuty = usiDutyRepository.findFirstByCode(usiDutyCode).orElseGet(() -> BpUsiDuty.builder().code(usiDutyCode).build());
						bpUsiDuty.setMyLcet(registerRequest.getLcet());
						bpUsiDuty.setMyUst(userItem.getMyust());
						bpUsiDuty.setMyCap(calendarPeriod.getCode());
						bpUsiDuty.setMyChrt(registerRequest.getChrt());
						bpUsiDuty.setMyUsi(registerRequest.getUsi());
						bpUsiDuty.setMyLcp(lcp.getCode());
						usiDutyRepository.save(bpUsiDuty);
					} catch (NotFoundException ex) {
						log.error("Not found BpLCP with PT: {} , LCT: {} ", mypt, registerRequest.getLct());
					}
				}
		);
	}

	@Override
	public List<UsidDistinctInfoProjection> findDistinctInfo(String ay) {
		return usiDutyRepository.findDistinctInfo(ay);
	}

	@Override
	public List<BpUsiDuty> findBy(String pt, String gg, String dfdl, String cap, String lcp, String ust) {
		return usiDutyRepository.findAllByMyptAndMyggAndMydfdlAndMyCapAndMyLcpAndMyUstAndPublishedTrue(pt, gg, dfdl, cap, lcp, ust);
	}

	@Override
	public BpUsiDuty findCashStart(String ay, String term, String pt, String gg, String dfdl) {
		return usiDutyRepository.findFirstByMyaccyearAndMytermAndMyptAndMyggAndMydfdlAndPublishedTrueOrderByCreatedAtDesc
				(ay, term, pt, gg, dfdl)
				.orElseThrow(() -> new NotFoundException("Coun't find crpp by BpUsiDuty :" + ay + term + pt + gg));
	}

	@Override
	public void doImport(List<UsidRegisterSO5DTO> listData) {
		listData.stream().forEach(usidRegisterSO5DTO -> {
			try {
				BpUsiDuty usiDuty = modelMapper.map(usidRegisterSO5DTO, BpUsiDuty.class);
				usiDutyRepository.save(usiDuty);
			} catch (Exception ex) {
				log.error("~~~ Cannot import data usiDuty {} ", usidRegisterSO5DTO);
				log.error("Exception {} ", ex);
			}
		});
	}

	@Override
	public List<BpUsiDuty> findAllConfigurationsByPtAndAccYearAndTerm(
			String pt,
			String ay,
			String term
	) {
		return usiDutyRepository.findByMyptAndMyaccyearAndMytermAndMybppLikeAndPublishedTrue(
				pt,
				ay,
				term,
				"%BPPRegister1%"
		);
	}
}
