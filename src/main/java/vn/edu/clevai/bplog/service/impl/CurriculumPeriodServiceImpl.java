package vn.edu.clevai.bplog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.*;
import vn.edu.clevai.bplog.dto.sheet.*;
import vn.edu.clevai.bplog.entity.*;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.enums.Lct;
import vn.edu.clevai.bplog.payload.request.CombineCodeRequest;
import vn.edu.clevai.bplog.payload.request.cti.*;
import vn.edu.clevai.bplog.repository.CurriculumPeriodRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.proxy.authoringproxy.AuthoringService;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.proxy.bplog.payload.response.CombineCodeResponse;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurriculumPeriodServiceImpl implements CurriculumPeriodService {

	@Autowired
	private CurriculumPeriodRepository repository;

	@Autowired
	ContentItemService contentItemService;

	@Autowired
	CalendarPeriodService calendarPeriodService;

	@Autowired
	BpWsoWeeklyscheduleoptionServiceImpl wsoService;

	@Autowired
	AccYearService accYearService;

	@Autowired
	CurriculumProgramPackageService crppService;

	@Autowired
	CurriculumProgramSheetService crpsService;

	@Autowired
	BpLCPService bpLCPService;

	@Autowired
	BpVideoDiliveService diliveConfigService;

	@Autowired
	AuthoringService authoringService;

	@Override
	@WriteUnitTestLog
	public CurriculumPeriod getCUPByCAP(String crpsCode, String capCode, String cupType) {
		return repository.findByMyCrpsAndMyCapAndCurrPeriodTypeAndPublishedTrue
				(crpsCode, capCode, cupType).orElse(null);
	}

	public CurriculumPeriod getCUWK(String crpsCode, String cuwkNo, String cupType) {
		CurriculumPeriod cuwk = repository.findByMyCrpsAndMycupnoAndCurrPeriodTypeAndPublishedTrue
				(crpsCode, cuwkNo, cupType).orElse(null);

		if (Objects.isNull(cuwk)) {
			cuwk = repository.saveAndFlush(CurriculumPeriod.builder()
					.code(crpsCode.concat(CombineCodeEnum.WKNO.getPrefix()).concat(cuwkNo))
					.myCrps(crpsCode)
					.mycupno(cuwkNo)
					.currPeriodType(cupType)
					.description("SYSTEM CREATE")
					.published(true)
					.build()
			);
		}

		return cuwk;
	}

	@Override
	@WriteUnitTestLog
	public CurriculumPeriod getCUDY(String inputCupCode, String cupNo, String cupType) {
		CurriculumPeriod cudy = repository.findByMyParentCupAndMycupnoAndCurrPeriodTypeAndPublishedTrue
				(inputCupCode, cupNo, cupType).stream().findFirst().orElse(null);

		if (Objects.isNull(cudy)) {
			cudy = repository.saveAndFlush(CurriculumPeriod.builder()
					.code(inputCupCode.concat(CombineCodeEnum.DYNO.getPrefix()).concat(cupNo))
					.myParentCup(inputCupCode)
					.mycupno(cupNo)
					.currPeriodType(cupType)
					.description("SYSTEM CREATE")
					.published(true)
					.build()
			);
		}

		return cudy;
	}

	@Override
	@WriteUnitTestLog
	public CurriculumPeriod getCUSH(String inputCupCode, String dfdlCode, String lctCode, String cupType) {
		CurriculumPeriod cush = repository.findByMyParentCupAndMyDfdlAndMyLcTypeAndCurrPeriodTypeAndPublishedTrue
				(inputCupCode, dfdlCode, lctCode, cupType).orElse(null);
		if (Objects.isNull(cush)) {
			cush = repository.saveAndFlush(CurriculumPeriod.builder()
					.code(String.join("-", inputCupCode, dfdlCode, lctCode))
					.myParentCup(inputCupCode)
					.myDfdl(dfdlCode)
					.myLcType(lctCode)
					.currPeriodType(cupType)
					.description("SYSTEM CREATE")
					.published(true)
					.build()
			);
		}
		return cush;
	}

	@Override
	public CurriculumPeriod getCup(String parentCup, String dfdl, String cupno, Integer mynoaschild) {
		return repository.findByMyParentCupAndMycupnoAndMynoaschildAndPublishedTrueAndMyDfdl(
				parentCup, cupno, mynoaschild, dfdl
		).orElse(null);
	}

	@Override
	public CurriculumPeriod findByCodeAndCurrPeriodType(String code, String periodType) {
		return repository.findByCodeAndCurrPeriodType(code, periodType).orElse(null);
	}

	@Override
	public CurriculumPeriod getCUDY2(Timestamp time, String crpp, String gg, String cudyNo) {
		// Find Crps, input la Crpp + gg
		CurriculumProgramSheet crps = crpsService.getCrps(crpp, gg);

		// Find CAWK, input la time
		CalendarPeriod cawk = calendarPeriodService
				.getCAPByTime(time, CalendarPeriodTypeEnum.WEEK.getCode());
		// Find CUWK, input la CAWK va CRPS
		CurriculumPeriod cuwk = getCUPByCAP(crps.getCode(), cawk.getCode(), CurriculumPeriodEnum.CURR_WEEK.getCode());
		// Find CUDY, input la CUWK va CUDYno (Suy ra tu time)
		return getCUDY(cuwk == null ? null : cuwk.getCode(), cudyNo, CurriculumPeriodEnum.CURR_DAY.getCode());
	}


	@Override
	public void saveAbliName(String abliCode, String shiftName) {
		String[] abli = abliCode.trim().split("-");
		String ayCode = abli[0];
		String termCode = abli[1];
		String pt = abli[2];
		String ggCode = abli[3];
		String cuwkNo = getNumberInString(abli[4]);
		String cudyNo = getNumberInString(abli[5]);

		log.info("saveCurriculumAbliName {} {} {} {} {} {} {}", abliCode, shiftName, ggCode, cudyNo, cuwkNo, ayCode, termCode);

		if (pt.equals(ProductTypeEnum.BC.getName())) {
			// PT - BC
			createCUSH(ayCode, termCode, ProductTypeEnum.BC.getName(), ggCode, cuwkNo, cudyNo,
					LCPLCTLCKEnum.DLC_75MI.getCode(), shiftName, null, null, null, null);
			createCUSH(ayCode, termCode, ProductTypeEnum.BC.getName(), ggCode, cuwkNo, cudyNo,
					LCPLCTLCKEnum.GES_75MI.getCode(), shiftName, null, null, null, null);
			// PT - PM
			createCUSH(ayCode, termCode, ProductTypeEnum.PM.getName(), ggCode, cuwkNo, cudyNo,
					LCPLCTLCKEnum.DLG_90MI.getCode(), shiftName, null, null, null, null);
			// PT - PO
			createCUSH(ayCode, termCode, ProductTypeEnum.PO.getName(), ggCode, cuwkNo, cudyNo,
					LCPLCTLCKEnum.DLG_90MI.getCode(), shiftName, null, null, null, null);

		} else if (pt.equals(ProductTypeEnum.OM.getName())) {
			createCUSH(ayCode, termCode, ProductTypeEnum.OM.getName(), ggCode, cuwkNo, cudyNo,
					LCPLCTLCKEnum.LI0_45MI.getCode(), shiftName, null, null, null, null);
		}

	}

	private void createCUSH(String ayCode, String termCode, String ptCode, String ggCode, String cuwkNo, String cudyNo,
							String lct, String shiftName, List<CTIBL3Request> c1Bl3, List<CTIDSCRequest> c1Dsc,
							List<CTIBL3Request> c2Bl3, List<CTIDSCRequest> c2Dsc) {
		Pair<CurriculumPeriod, Boolean> cuwkNeedImporting = cuwkNeedImporting(ayCode, termCode, ptCode, ggCode, cuwkNo);

		if (!cuwkNeedImporting.getRight()) {
			return;
		}

		CTIULCRequest c1Ulc = CTIULCRequest.builder()
				.shiftName(shiftName).ctibl3(c1Bl3).ctiDsc(c1Dsc)
				.build();
		CTIULCRequest c2Ulc = CTIULCRequest.builder()
				.shiftName(shiftName).ctibl3(c2Bl3).ctiDsc(c2Dsc)
				.build();

		CurriculumPeriod cudy = getCUDY(cuwkNeedImporting.getLeft().getCode(), cudyNo, CurriculumPeriodEnum.CURR_DAY.getCode());

		createCUP_CTI(lct, ptCode, null, cudy.getCode(), DfdlEnum.C1.getCode(), lct,
				null, null, null, null, CurriculumPeriodEnum.CURR_SHIFT, c1Ulc, true);
		createCUP_CTI(lct, ptCode, null, cudy.getCode(), DfdlEnum.C2.getCode(), lct,
				null, null, null, null, CurriculumPeriodEnum.CURR_SHIFT, c2Ulc, true);
	}

	public void saveCurriculumAbli(String abliCode, String shiftName, List<AbliSheetDTO> abliSheets) {
		String[] abli = abliCode.trim().split("-");
		String ayCode = abli[0];
		String termCode = abli[1];
		String pt = abli[2];
		String ggCode = abli[3];
		String cuwkNo = getNumberInString(abli[4]);
		String cudyNo = getNumberInString(abli[5]);

		log.info("saveCurriculumAbli {} {} {} {} {} {} {}", abliCode, shiftName, ggCode, cudyNo, cuwkNo, ayCode, termCode);

		List<CTIBL3Request> c1Bl3 = new ArrayList<>();
		List<CTIBL3Request> c2Bl3 = new ArrayList<>();
		List<CTIDSCRequest> c1Dsc = new ArrayList<>();
		List<CTIDSCRequest> c2Dsc = new ArrayList<>();

		abliSheets.forEach(a -> {
			if (StringUtils.isNotEmpty(a.getC1QType())) {
				c1Bl3.add(CTIBL3Request.builder().bl3Code(a.getBl3Code().trim()).build());
				c1Dsc.add(CTIDSCRequest.builder().dQuizSlot(a.getC1Bl5Code().trim()).build());
			}

			if (StringUtils.isNotEmpty(a.getC2QType())) {
				c2Bl3.add(CTIBL3Request.builder().bl3Code(a.getBl3Code().trim()).build());
				c2Dsc.add(CTIDSCRequest.builder().dQuizSlot(a.getC2Bl5Code().trim()).build());
			}
		});


		// PT - BC
		createCUSH(ayCode, termCode, ProductTypeEnum.BC.getName(), ggCode, cuwkNo, cudyNo,
				LCPLCTLCKEnum.DLC_75MI.getCode(), shiftName, c1Bl3, c1Dsc, c2Bl3, c2Dsc);
		// PT - PM
		createCUSH(ayCode, termCode, ProductTypeEnum.PM.getName(), ggCode, cuwkNo, cudyNo,
				LCPLCTLCKEnum.DLG_90MI.getCode(), shiftName, c1Bl3, c1Dsc, c2Bl3, c2Dsc);
		// PT - PO
		createCUSH(ayCode, termCode, ProductTypeEnum.PO.getName(), ggCode, cuwkNo, cudyNo,
				LCPLCTLCKEnum.DLG_90MI.getCode(), shiftName, c1Bl3, c1Dsc, c2Bl3, c2Dsc);
	}

	@Override
	public void saveCurriculumCoq(String coqCode, String shiftName, List<CoqSheetDTO> listData) {
		String[] coq = coqCode.trim().split("-");
		String ayCode = coq[0];
		String termCode = coq[1];
		String pt = coq[2];
		String ggCode = coq[3];
		String cuwkNo = getNumberInString(coq[4]);
		String cudyNo = getNumberInString(coq[5]);
		log.info("saveCurriculumCoq {} {} {} {} {} {} {}", coq, shiftName, ggCode, cudyNo, cuwkNo, ayCode, termCode);

		saveCurriculumCoqBC(ayCode, termCode, ProductTypeEnum.BC.getName(), ggCode, cuwkNo, cudyNo,
				listData.subList(0, 8), listData.subList(9, 17));
		saveCurriculumCoqPM_PO(ayCode, termCode, ProductTypeEnum.PM.getName(), ggCode, cuwkNo, cudyNo,
				listData.subList(0, 8), listData.subList(9, 17));
		saveCurriculumCoqPM_PO(ayCode, termCode, ProductTypeEnum.PO.getName(), ggCode, cuwkNo, cudyNo,
				listData.subList(0, 8), listData.subList(9, 17));
	}

	private void saveCurriculumCoqBC(String ayCode, String termCode, String ptCode, String ggCode, String cuwkNo,
									 String cudyNo, List<CoqSheetDTO> listDataC1, List<CoqSheetDTO> listDataC2) {
		Pair<CurriculumPeriod, Boolean> cuwkNeedImporting = cuwkNeedImporting(ayCode, termCode, ptCode, ggCode, cuwkNo);

		if (!cuwkNeedImporting.getRight()) {
			return;
		}

		CurriculumPeriod cudyBC = getCUDY(cuwkNeedImporting.getLeft().getCode(), cudyNo, CurriculumPeriodEnum.CURR_DAY.getCode());

		// PT - BC
		// DLC
		createCUP_CTIRC(cudyBC, LCPLCTLCKEnum.DLC_75MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), listDataC1.subList(0, 6));
		createCUP_CTIHW(cudyBC, LCPLCTLCKEnum.DLC_75MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), listDataC1.subList(0, 6), listDataC1.subList(6, 8));
		createCUP_CTIRC(cudyBC, LCPLCTLCKEnum.DLC_75MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), listDataC2.subList(0, 6));
		createCUP_CTIHW(cudyBC, LCPLCTLCKEnum.DLC_75MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), listDataC2.subList(0, 6), listDataC2.subList(6, 8));

		// GES
		createCUP_CTIHW(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ProductTypeEnum.BC.getName(), null,
				DfdlEnum.C1.getCode(), listDataC1.subList(0, 6), listDataC1.subList(6, 8));
		createCUP_CTIHW(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ProductTypeEnum.BC.getName(), null,
				DfdlEnum.C2.getCode(), listDataC2.subList(0, 6), listDataC2.subList(6, 8));
	}

	private void saveCurriculumCoqPM_PO(String ayCode, String termCode, String ptCode, String ggCode, String cuwkNo,
										String cudyNo, List<CoqSheetDTO> listDataC1, List<CoqSheetDTO> listDataC2) {
		Pair<CurriculumPeriod, Boolean> cuwkNeedImporting = cuwkNeedImporting(ayCode, termCode, ptCode, ggCode, cuwkNo);

		if (!cuwkNeedImporting.getRight()) {
			return;
		}

		CurriculumPeriod cudy = getCUDY(cuwkNeedImporting.getLeft().getCode(), cudyNo, CurriculumPeriodEnum.CURR_DAY.getCode());

		// PT - PM
		// DLG
		createCUP_CTIRC(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode,
				null, DfdlEnum.C1.getCode(), listDataC1.subList(0, 6));
		createCUP_CTIHW(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode,
				null, DfdlEnum.C1.getCode(), listDataC1.subList(0, 6), listDataC1.subList(6, 8));
		createCUP_CTIRC(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode,
				null, DfdlEnum.C2.getCode(), listDataC2.subList(0, 6));
		createCUP_CTIHW(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode,
				null, DfdlEnum.C2.getCode(), listDataC2.subList(0, 6), listDataC2.subList(6, 8));
	}

	@Override
	public void saveCurriculumPC(String omCode, String shiftName, List<CtiPCSheetDTO> listData) {
		listData = listData.stream().filter(c -> Objects.nonNull(c.getCtiBL4QT()) && !c.getCtiBL4QT().isEmpty())
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(listData)) return;

		log.info("saveCurriculumOM listData {}", listData.size());

		String name = listData.get(0).getContentTitle();
		int duration = Integer.parseInt(listData.get(0).getDuration());
		List<String> bl4QtsCode = listData.stream().map(CtiPCSheetDTO::getCtiBL4QT)
				.collect(Collectors.toList());
		List<CTIBl4QtRequest> bl4Qts = new ArrayList<>();

		authoringService.getPCBl4Qts(bl4QtsCode).forEach(p -> {
			if (Objects.nonNull(p.getBl5Qps()) && p.getBl5Qps().size() >= 5) {
				bl4Qts.add(CTIBl4QtRequest.builder()
						.bl4Qt(p.getBl4Qt()).bl5Qps(p.getBl5Qps())
						.build());
			}
		});

		String[] coq = omCode.trim().split("-");
		String ayCode = coq[0];
		String termCode = coq[1];
		String pt = coq[2];
		String ggCode = coq[3];
		String cuwkNo = getNumberInString(coq[4]);
//		String cudyNo = getNumberInString(coq[5]);
		saveCurriculumPCByPT(ayCode, termCode, ProductTypeEnum.BC.getName(), ggCode, cuwkNo, name, duration, bl4Qts);
		saveCurriculumPCByPT(ayCode, termCode, ProductTypeEnum.PM.getName(), ggCode, cuwkNo, name, duration, bl4Qts);
		saveCurriculumPCByPT(ayCode, termCode, ProductTypeEnum.PO.getName(), ggCode, cuwkNo, name, duration, bl4Qts);
	}

	public void saveCurriculumPCByPT(String ayCode, String termCode, String pt, String ggCode, String cuwkNo,
									 String name, Integer duration, List<CTIBl4QtRequest> bl4Qts) {
		Pair<CurriculumPeriod, Boolean> cuwkNeedImporting = cuwkNeedImporting(ayCode, termCode, pt, ggCode, cuwkNo);

		if (!cuwkNeedImporting.getRight()) {
			return;
		}
		CurriculumPeriod cuwk = cuwkNeedImporting.getLeft();

		CalendarPeriod cawk = calendarPeriodService.findByCode(cuwk.getMyCap());
		CalendarPeriod camn = calendarPeriodService.getCAPByTime(cawk.getStartTime(), CalendarPeriodTypeEnum.MONTH.getCode());
		List<CalendarPeriod> cawks = calendarPeriodService
				.findByParentAndCapType(camn.getCode(), CalendarPeriodTypeEnum.WEEK.getCode());
		CurriculumPeriod cumn = getCUPByCAP(cuwk.getMyCrps(), camn.getCode(), CurriculumPeriodEnum.CURR_MONTH.getCode());

		CTIULCRequest ulcC1 = CTIULCRequest.builder()
				.ctiPc(CTIPCRequest.builder()
						.name(name)
						.duration(duration)
						.bl4Qts(bl4Qts)
						.build())
				.build();
		CTIULCRequest ulcC2 = CTIULCRequest.builder()
				.ctiPc(CTIPCRequest.builder()
						.name(name)
						.duration(duration)
						.bl4Qts(bl4Qts)
						.build())
				.build();

		if (cawks.isEmpty() || cawks.get(0).getCode().equals(cawk.getCode())) {
			log.info("saveCurriculumPC month {}", cumn.getCode());
			createCUP_CTI(LCPLCTLCKEnum.MC_1MN.getCode(), pt, null, cumn.getCode(),
					DfdlEnum.C1.getCode(), LCPLCTLCKEnum.PC_40MI.getCode(), null, "ED1", null, null,
					CurriculumPeriodEnum.CURR_SHIFT, ulcC1, true);

			createCUP_CTI(LCPLCTLCKEnum.MC_1MN.getCode(), pt, null, cumn.getCode(),
					DfdlEnum.C2.getCode(), LCPLCTLCKEnum.PC_40MI.getCode(), null, "ED1", null, null,
					CurriculumPeriodEnum.CURR_SHIFT, ulcC2, true);
		} else {
			log.info("saveCurriculumPC week {}", cuwk.getCode());
			createCUP_CTI(LCPLCTLCKEnum.WC_1WK.getCode(), pt, null, cuwk.getCode(),
					DfdlEnum.C1.getCode(), LCPLCTLCKEnum.PC_40MI.getCode(), null, "ED1", null, null,
					CurriculumPeriodEnum.CURR_SHIFT, ulcC1, true);

			createCUP_CTI(LCPLCTLCKEnum.WC_1WK.getCode(), pt, null, cuwk.getCode(),
					DfdlEnum.C2.getCode(), LCPLCTLCKEnum.PC_40MI.getCode(), null, "ED1", null, null,
					CurriculumPeriodEnum.CURR_SHIFT, ulcC2, true);
		}
	}

	@Override
	public void saveCurriculumSsl(List<SSLSheetDTO> listSsl) {
		listSsl.stream().filter(s -> Objects.nonNull(s.getSslCode())).forEach(s -> {
			String[] ssl = s.getSslCode().trim().split("-");
			String ayCode = ssl[0];
			String termCode = ssl[1];
			String pt = ssl[2];
			String ggCode = ssl[3];
			String cuwkNo = getNumberInString(ssl[4]);
			String cudyNo = getNumberInString(ssl[5]);

			log.info("saveCurriculumSsl {} {} {} {} {} {}", s.getSslCode(), ggCode, cudyNo, cuwkNo, ayCode, termCode);

			if (pt.equals(ProductTypeEnum.BC.getName())) {
				saveCurriculumSslBC(ayCode, termCode, ProductTypeEnum.BC.getName(), ggCode, cuwkNo, cudyNo, s);
				saveCurriculumSslPM_PO(ayCode, termCode, ProductTypeEnum.PM.getName(), ggCode, cuwkNo, cudyNo, s);
				saveCurriculumSslPM_PO(ayCode, termCode, ProductTypeEnum.PO.getName(), ggCode, cuwkNo, cudyNo, s);
			} else if (pt.equals(ProductTypeEnum.OM.getName())) {
				saveCurriculumSslOM(ayCode, termCode, ProductTypeEnum.OM.getName(), ggCode, cuwkNo, cudyNo, s);
			}
		});

	}

	private void saveCurriculumSslBC(String ayCode, String termCode, String ptCode, String ggCode, String cuwkNo,
									 String cudyNo, SSLSheetDTO s) {
		Pair<CurriculumPeriod, Boolean> cuwkNeedImporting = cuwkNeedImporting(ayCode, termCode, ptCode, ggCode, cuwkNo);

		if (!cuwkNeedImporting.getRight()) {
			return;
		}

		BpVideoDiliveConfig c1Config = diliveConfigService.findByMyptAndMyggAndMydfdl(ptCode, ggCode, DfdlEnum.C1.getCode());
		BpVideoDiliveConfig c2Config = diliveConfigService.findByMyptAndMyggAndMydfdl(ptCode, ggCode, DfdlEnum.C2.getCode());

		// DLC
		// C1
		CurriculumPeriod cudyBC = getCUDY(cuwkNeedImporting.getLeft().getCode(), cudyNo, CurriculumPeriodEnum.CURR_DAY.getCode());
		createCUP_CTIDL(cudyBC, LCPLCTLCKEnum.DLC_75MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.DL_40MI.getCode(), null, "FD1", null,
				s.getDlC1Te(), s.getDlC1St(), s.getSslName(), c1Config);
		// C2
		createCUP_CTIDL(cudyBC, LCPLCTLCKEnum.DLC_75MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.DL_40MI.getCode(), null, "FD1", null,
				s.getDlC2Te(), s.getDlC2St(), s.getSslName(), c2Config);


		// GES - have 1 shift / 1 week
		if (cudyNo.equals("1")) {
			// DY1
			saveCurriculumSslBCGES(cudyBC, ptCode, s);

			// DY2
			cudyBC = getCUDY(cuwkNeedImporting.getLeft().getCode(), "2", CurriculumPeriodEnum.CURR_DAY.getCode());
			saveCurriculumSslBCGES(cudyBC, ptCode, s);
		}
	}

	private void saveCurriculumSslBCGES(CurriculumPeriod cudyBC, String ptCode, SSLSheetDTO s) {
		// C1
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.A.getCode(),
				"FD1", null, s.getGesC1GEATe(), s.getGesC1GEASt(), s.getSslName());
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.B.getCode(),
				"FD1", null, s.getGesC1GEBTe(), s.getGesC1GEBSt(), s.getSslName());
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.C.getCode(),
				"FD1", null, s.getGesC1GECTe(), s.getGesC1GECSt(), s.getSslName());
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.D.getCode(),
				"FD1", null, s.getGesC1GEDTe(), s.getGesC1GEDSt(), s.getSslName());

		// C2
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.A.getCode(), "FD1",
				null, s.getGesC2GEATe(), s.getGesC2GEASt(), s.getSslName());
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.B.getCode(), "FD1",
				null, s.getGesC2GEBTe(), s.getGesC2GEBSt(), s.getSslName());
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.C.getCode(), "FD1",
				null, s.getGesC2GECTe(), s.getGesC2GECSt(), s.getSslName());
		createCUP_CTIGE(cudyBC, LCPLCTLCKEnum.GES_75MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_75MI.getCode(), DfgeEnum.D.getCode(), "FD1",
				null, s.getGesC2GEDTe(), s.getGesC2GEDSt(), s.getSslName());
	}

	private void saveCurriculumSslPM_PO(String ayCode, String termCode, String ptCode, String ggCode, String cuwkNo,
										String cudyNo, SSLSheetDTO s) {
		Pair<CurriculumPeriod, Boolean> cuwkNeedImporting = cuwkNeedImporting(ayCode, termCode, ptCode, ggCode, cuwkNo);

		if (!cuwkNeedImporting.getRight()) {
			return;
		}

		BpVideoDiliveConfig c1Config = diliveConfigService.findByMyptAndMyggAndMydfdl(ptCode, ggCode, DfdlEnum.C1.getCode());
		BpVideoDiliveConfig c2Config = diliveConfigService.findByMyptAndMyggAndMydfdl(ptCode, ggCode, DfdlEnum.C2.getCode());

		// DLG
		CurriculumPeriod cudy = getCUDY(cuwkNeedImporting.getLeft().getCode(), cudyNo, CurriculumPeriodEnum.CURR_DAY.getCode());
		// C1
		createCUP_CTIDL(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.DL_40MI.getCode(), null, "FD1", null,
				s.getDlC1Te(), s.getDlC1St(), s.getSslName(), c1Config);

		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.A.getCode(), "FD3",
				null, s.getDlgC1GEATe(), null, s.getSslName());
		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.B.getCode(), "FD3",
				null, s.getDlgC1GEBTe(), null, s.getSslName());
		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.C.getCode(), "FD3",
				null, s.getDlgC1GECTe(), null, s.getSslName());
		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C1.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.D.getCode(), "FD3",
				null, s.getDlgC1GEDTe(), null, s.getSslName());

		// C2
		createCUP_CTIDL(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.DL_40MI.getCode(), null, "FD1", null,
				s.getDlC2Te(), s.getDlC2St(), s.getSslName(), c2Config);

		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.A.getCode(), "FD3",
				null, s.getDlgC2GEATe(), null, s.getSslName());
		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.B.getCode(), "FD3",
				null, s.getDlgC2GEBTe(), null, s.getSslName());
		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.C.getCode(), "FD3",
				null, s.getDlgC2GECTe(), null, s.getSslName());
		createCUP_CTIGE(cudy, LCPLCTLCKEnum.DLG_90MI.getCode(), ptCode, null,
				DfdlEnum.C2.getCode(), LCPLCTLCKEnum.GE_45MI.getCode(), DfgeEnum.D.getCode(), "FD3",
				null, s.getDlgC2GEDTe(), null, s.getSslName());
	}

	private void saveCurriculumSslOM(String ayCode, String termCode, String ptCode, String ggCode, String cuwkNo,
									 String cudyNo, SSLSheetDTO s) {
		Pair<CurriculumPeriod, Boolean> cuwkNeedImporting = cuwkNeedImporting(ayCode, termCode, ptCode, ggCode, cuwkNo);

		if (!cuwkNeedImporting.getRight()) {
			return;
		}

		BpVideoDiliveConfig c1Config = diliveConfigService.findByMyptAndMyggAndMydfdl(ptCode, ggCode, DfdlEnum.C1.getCode());
		BpVideoDiliveConfig c2Config = diliveConfigService.findByMyptAndMyggAndMydfdl(ptCode, ggCode, DfdlEnum.C2.getCode());

		// C1
		// LI0
		CurriculumPeriod cudy = getCUDY(cuwkNeedImporting.getLeft().getCode(), cudyNo, CurriculumPeriodEnum.CURR_DAY.getCode());
		createCUP_CTIDL(cudy, LCPLCTLCKEnum.LI0_45MI.getCode(), ptCode, null, DfdlEnum.C1.getCode(),
				LCPLCTLCKEnum.LI_45MI.getCode(), null, "FD1", null, s.getDlC1Te(),
				s.getDlC1St(), s.getSslName(), c1Config);

		// C2
		// LI0
		createCUP_CTIDL(cudy, LCPLCTLCKEnum.LI0_45MI.getCode(), ptCode, null, DfdlEnum.C2.getCode(),
				LCPLCTLCKEnum.LI_45MI.getCode(), null, "FD1", null, s.getDlC2Te(),
				s.getDlC2St(), s.getSslName(), c2Config);
	}

	@Override
	public void saveCurriculumHWOm(String spreadsheetCode, String name, List<OnRampProductCRPPDTO> listData) {
		String[] crpsCode = name.trim().trim().split("-");
		String ayCode = crpsCode[2];
		String termCode = crpsCode[3];
		String pt = crpsCode[4];
		String ggCode = crpsCode[5].split("_")[0];

		listData.forEach(s -> {
			Integer hwNo = Integer.valueOf(s.getLessonName().replaceAll("[^0-9]", ""));
			String periodNoHW = "AAX";
			String periodNoRL = "ADX";

			log.info("saveCurriculumHWOm {} {} {} {}", hwNo, ggCode, ayCode, termCode);
			CurriculumProgramPackage crpp = crppService.getCrppByAccYearAndTermAndPt(ayCode, termCode, ProductTypeEnum.OM.getName());
			CurriculumProgramSheet crps = crpsService.getCrps(crpp.getCode(), ggCode);

			List<CTIAQRRequest> c1AQR = s.getBl4c1().stream()
					.map(c -> CTIAQRRequest.builder().aiQuizRun(c).build())
					.collect(Collectors.toList());
			CTIULCRequest ulcC1 = CTIULCRequest.builder()
					.ctiHrg(CTIHRGRequest.builder().ctiAqrs(c1AQR).build())
					.ctiRLOM(CTIRLRequest.builder().name(s.getShiftName()).videoLink(s.getVideoLink()).build())
					.build();

			List<CTIAQRRequest> c2AQR = s.getBl4c2().stream()
					.map(c -> CTIAQRRequest.builder().aiQuizRun(c).build())
					.collect(Collectors.toList());
			CTIULCRequest ulcC2 = CTIULCRequest.builder()
					.ctiHrg(CTIHRGRequest.builder().ctiAqrs(c2AQR).build())
					.ctiRLOM(CTIRLRequest.builder().name(s.getShiftName()).videoLink(s.getVideoLink()).build())
					.build();

			// C1
			// HW
			createCUP_CTI(LCPLCTLCKEnum.MP40L_1MN.getCode(), ProductTypeEnum.OM.getName(), crps.getCode(),
					null, DfdlEnum.C1.getCode(), LCPLCTLCKEnum.HORG_AA.getCode(), null,
					periodNoHW, hwNo, null, CurriculumPeriodEnum.CURR_SESSION, ulcC1, true);
			// RL
			createCUP_CTI(LCPLCTLCKEnum.MP40L_1MN.getCode(), ProductTypeEnum.OM.getName(), crps.getCode(),
					null, DfdlEnum.C1.getCode(), LCPLCTLCKEnum.RL_45MI.getCode(), null,
					periodNoRL, hwNo, null, CurriculumPeriodEnum.CURR_SESSION, ulcC1, true);
			// C2
			// HW
			createCUP_CTI(LCPLCTLCKEnum.MP40L_1MN.getCode(), ProductTypeEnum.OM.getName(), crps.getCode(),
					null, DfdlEnum.C2.getCode(), LCPLCTLCKEnum.HORG_AA.getCode(), null,
					periodNoHW, hwNo, null, CurriculumPeriodEnum.CURR_SESSION, ulcC2, true);
			// RL
			createCUP_CTI(LCPLCTLCKEnum.MP40L_1MN.getCode(), ProductTypeEnum.OM.getName(), crps.getCode(),
					null, DfdlEnum.C2.getCode(), LCPLCTLCKEnum.RL_45MI.getCode(), null,
					periodNoRL, hwNo, null, CurriculumPeriodEnum.CURR_SESSION, ulcC2, true);
		});
	}


	private Pair<CurriculumPeriod, Boolean> cuwkNeedImporting(String ayCode, String termCode, String ptCode,
															  String ggCode, String cuwkNo) {
		int DELTA_WEEK_NEED_IMPORT = 3;
		CurriculumProgramPackage crpp = crppService.getCrppByAccYearAndTermAndPt(ayCode, termCode, ptCode);
		CurriculumProgramSheet crps = crpsService.getCrps(crpp.getCode(), ggCode);
		CurriculumPeriod cuwk = getCUWK(crps.getCode(), cuwkNo, CurriculumPeriodEnum.CURR_WEEK.getCode());

		CalendarPeriod cawk = calendarPeriodService.getCAPByTime(new Timestamp(System.currentTimeMillis()),
				CalendarPeriodTypeEnum.WEEK.getCode());
		CurriculumPeriod cuwkNow = getCUPByCAP(crps.getCode(), cawk.getCode(), CurriculumPeriodEnum.CURR_WEEK.getCode());

		boolean needImport = Integer.parseInt(cuwk.getMycupno()) >= Integer.parseInt(cuwkNow.getMycupno()) &&
				Integer.parseInt(cuwk.getMycupno()) <= Integer.parseInt(cuwkNow.getMycupno()) + DELTA_WEEK_NEED_IMPORT;
		log.info("cuwkNeedImporting {} {} {}", cuwk.getCode(), cuwkNow.getCode(), needImport);

		return Pair.of(cuwk, needImport);
	}

	@Override
	public CurriculumPeriod findAllByMyParentCupAndLCPPeriod(String parent, String lcperiodno, Integer no) {
		return repository.findByMyParentCupAndMycupnoAndMynoaschildAndPublishedTrue(parent, lcperiodno, no).orElse(null);
	}

	private void createCUP_CTIDL(CurriculumPeriod cudy, String originLct, String pt, String crps, String dfdl,
								 String lctCode, String dfge, String cupNo, Integer index, String sslTeacher,
								 String sslStudent, String sslName, BpVideoDiliveConfig dlVideoConfig) {
		CTIULCRequest ulc = CTIULCRequest.builder()
				.ctiDlSsl(CTISSLRequest.builder().sslTeacher(sslTeacher)
						.sslStudent(sslStudent).sslName(sslName)
						.build())
				.ctiVdl(Objects.nonNull(dlVideoConfig) ? CTIVDLRequest.builder()
						.startUrl(dlVideoConfig.getStartUrl())
						.secretKey(dlVideoConfig.getSecretKey())
						.joinUrl(dlVideoConfig.getJoinUrl()).build() : null)
				.build();
		CurriculumPeriod cush = getCUSH(cudy.getCode(), dfdl, originLct, CurriculumPeriodEnum.CURR_SHIFT.getCode());
		createCUP_CTI(originLct, pt, crps, cush.getCode(), null, lctCode,
				dfge, cupNo, index, cush.getMynoaschild(), CurriculumPeriodEnum.CURR_SESSION, ulc, false);
	}

	private void createCUP_CTIGE(CurriculumPeriod cudy, String originLct, String pt, String crps, String dfdl, String lctCode,
								 String dfge, String cupNo, Integer index, String sslLinkTe, String sslLinkSt, String sslName) {
		CTIULCRequest ulc = CTIULCRequest.builder()
				.ctiGeSsl(CTISSLRequest.builder().sslTeacher(sslLinkTe)
						.sslStudent(sslLinkSt)
						.sslName(sslName).build())
				.build();
		CurriculumPeriod cush = getCUSH(cudy.getCode(), dfdl, originLct, CurriculumPeriodEnum.CURR_SHIFT.getCode());
		createCUP_CTI(originLct, pt, crps, cush.getCode(), null, lctCode,
				dfge, cupNo, index, null, CurriculumPeriodEnum.CURR_SESSION, ulc, false);
	}

	private void createCUP_CTIRC(CurriculumPeriod cudy, String originLct, String pt, String crps,
								 String dfdl, List<CoqSheetDTO> hrgDTO) {
		List<String> rqs = hrgDTO.stream()
				.map(CoqSheetDTO::getBl5Code).collect(Collectors.toList());

		CTIULCRequest ulc = CTIULCRequest.builder()
				.ctiRc(CTIRCRequest.builder().rQuizSlots(rqs).build())
				.build();
		CurriculumPeriod cush = getCUSH(cudy.getCode(), dfdl, originLct, CurriculumPeriodEnum.CURR_SHIFT.getCode());
		createCUP_CTI(originLct, pt, crps, cush.getCode(), null, LCPLCTLCKEnum.RC_5MI.getCode(),
				null, "FD2", null, null, CurriculumPeriodEnum.CURR_SESSION, ulc, true);
	}

	private void createCUP_CTIHW(CurriculumPeriod cudy, String originLct, String pt, String crps,
								 String dfdl, List<CoqSheetDTO> hrgDTO, List<CoqSheetDTO> havDTO) {
		List<CTIAQRRequest> hrgAqr = hrgDTO.stream()
				.map(c -> CTIAQRRequest.builder()
						.aiQuizRun(c.getBl4Code().trim())
						.build()).collect(Collectors.toList());
		List<CTIAQRRequest> havAqr = havDTO.stream()
				.map(c -> CTIAQRRequest.builder()
						.aiQuizRun(c.getBl4Code().trim())
						.build()).collect(Collectors.toList());

		CTIULCRequest ulc = CTIULCRequest.builder()
				.ctiHrg(CTIHRGRequest.builder().ctiAqrs(hrgAqr).build())
				.ctiHav(CTIHAVRequest.builder().ctiAqrs(havAqr).build())
				.build();
		CurriculumPeriod cush = getCUSH(cudy.getCode(), dfdl, originLct, CurriculumPeriodEnum.CURR_SHIFT.getCode());
		createCUP_CTI(originLct, pt, crps, cush.getCode(), null, LCPLCTLCKEnum.HRG_EA.getCode(),
				null, "EA1", null, null, CurriculumPeriodEnum.CURR_SESSION, ulc, true);
		createCUP_CTI(originLct, pt, crps, cush.getCode(), null, LCPLCTLCKEnum.HAV_EA.getCode(),
				null, "EA2", null, null, CurriculumPeriodEnum.CURR_SESSION, ulc, true);
	}

	public void createCUP_CTI(String originLct, String pt, String crps, String parentCup, String dfdlCode,
							  String lct, String dfgeCode, String cupNo, Integer mynoaschild, Integer parentNo,
							  CurriculumPeriodEnum cupEnum, CTIULCRequest ctiRequest, Boolean isScheduleChild) {
		log.info("createCUP_CTI parentCup-{} dfdl-{} lct-{} dfge-{} cupNo-{} index-{} cupEnum-{} ctiRequest-{}",
				parentCup, dfdlCode, lct, dfgeCode, cupNo, mynoaschild, cupEnum.getCode(), ctiRequest);
		CurriculumPeriod cup = getOrCreateCup(pt, crps, parentCup, dfdlCode, lct, dfgeCode, cupNo,
				mynoaschild, cupEnum);
		try {
			BpContentItem cti = contentItemService.createOrUpdateCTT_ULC(cup, ctiRequest, lct, originLct, parentNo, mynoaschild);
			if (Objects.nonNull(cti)) {
				cup.setMyCti(cti.getCode());
				repository.save(cup);
			}
		} catch (Exception e) {
			log.error("Error createOrUpdateCTT_ULC {} {}", cup.getCode(), DebuggingDTO.build(e));
		}

		if (isScheduleChild) {
			List<BpLCP> lcp = bpLCPService.findByMylctparentToSchedule(lct);
			log.info("Start create child CUP_CTI cup-{} dfdl-{} lct-{} dfge-{} cupNo-{} index-{} cupEnum-{}",
					cup.getCode(), dfdlCode, lct, dfgeCode, cupNo, mynoaschild, cupEnum.getCode());

			if (!lcp.isEmpty()) {
				// Create child
				lcp.forEach(l -> {
					if (Objects.isNull(l.getNolcp())) {
						createCUP_CTI(originLct, pt, crps, cup.getCode(), dfdlCode, l.getMylct(), dfgeCode, l.getLcperiodno(),
								null, mynoaschild, CurriculumPeriodEnum.findByLCL(l.getLct().getMyLcl()), ctiRequest, true);
					} else {
						for (int i = 1; i <= l.getNolcp(); i++) {
							createCUP_CTI(originLct, pt, crps, cup.getCode(), dfdlCode, l.getMylct(), dfgeCode, l.getLcperiodno(),
									i, mynoaschild, CurriculumPeriodEnum.findByLCL(l.getLct().getMyLcl()), ctiRequest, true);
						}
					}
				});
			}
		}
	}

	@Override
	public CurriculumPeriod getOrCreateCup
			(String pt, String crps, String parentCup, String dfdl, String lct,
			 String dfge, String cupNo, Integer noAsChild, CurriculumPeriodEnum cupType) {
		String cupCode = pt.equals(ProductTypeEnum.OM.getName()) ?
				getCupCodeOM(cupType, crps, parentCup, dfdl, lct, dfge, cupNo, noAsChild) :
				getCupCode(cupType, crps, parentCup, dfdl, lct, dfge, cupNo, noAsChild);

		CurriculumPeriod cup = findByCodeAndCurrPeriodType(cupCode, cupType.getCode());

		if (Objects.isNull(cup)) {
			try {
				cup = CurriculumPeriod.builder()
						.code(cupCode)
						.myCrps(crps)
						.myParentCup(parentCup)
						.currPeriodType(cupType.getCode())
						.mycupno(cupNo)
						.myDfdl(dfdl)
						.myLcType(lct)
						.myDfge(dfge)
						.mynoaschild(noAsChild)
						.published(true)
						.description("SYSTEM CREATE")
						.build();
				repository.saveAndFlush(cup);
			} catch (Exception e) {
				log.error("SAVE CUP ERROR type {} code {}", cupType.getCode(), cupCode);
			}
		}

		return cup;
	}

	@Override
	public CurriculumPeriod getCup
			(String pt, String crps, String parentCup, String dfdl, String lct,
			 String dfge, String cupNo, Integer noAsChild, CurriculumPeriodEnum cupType) {
		String cupCode = pt.equals(ProductTypeEnum.OM.getName()) ?
				getCupCodeOM(cupType, crps, parentCup, dfdl, lct, dfge, cupNo, noAsChild) :
				getCupCode(cupType, crps, parentCup, dfdl, lct, dfge, cupNo, noAsChild);

		return findByCodeAndCurrPeriodType(cupCode, cupType.getCode());
	}

	@Override
	public CurriculumPeriod getCUP(String crps, String cupno, String cupType) {
		return repository.findFirstByMyCrpsAndMycupnoAndPublishedTrueAndCurrPeriodType(crps, cupno, cupType);
	}

	@Override
	public CurriculumPeriod getCUP(String crps, String cupno, int mynoaschild, String cupType) {
		return repository.findFirstByMyCrpsAndMycupnoAndMynoaschildAndPublishedTrueAndCurrPeriodType(crps, cupno, mynoaschild, cupType);
	}

	@Override
	public void save(CurriculumPeriod currSession) {
		repository.save(currSession);
	}

	@Override
	@WriteUnitTestLog
	public String combineCurrCode(CombineCodeRequest r) {
		final String[] result = {""};

		Arrays.stream(CombineCodeEnum.values())
				.sorted(Comparator.comparingInt(CombineCodeEnum::getOrder))
				.forEach(c -> {
					try {
						Class<?> clazz = CombineCodeRequest.class;
						Field field = clazz.getDeclaredField(c.getObjectName());
						field.setAccessible(true);
						Object fieldValue = field.get(r);

						if (Objects.nonNull(fieldValue)) {
							result[0] = result[0] + c.getPrefix() + fieldValue;
						}
					} catch (Exception e) {
						log.error("CombineCode error field-{} r-{}", c.getObjectName(), r.toString());
					}
				});

		return result[0];
	}

	@Override
	@WriteUnitTestLog
	public CombineCodeResponse breakCurrCode(String cupCode) {
		CombineCodeResponse response = CombineCodeResponse.builder().build();

		String[] values = cupCode.split("-");


		Arrays.stream(CombineCodeEnum.values())
				.forEach(c -> {
					try {
						Class<?> clazz = CombineCodeResponse.class;
						Field field = clazz.getDeclaredField(c.getObjectName());
						field.setAccessible(true);

						Integer delta = 0;
						String value = values[c.getOrder() - delta];
						field.set(response, value);

					} catch (Exception e) {
						log.error("breakCurrCode error field-{}", c.getObjectName());
					}
				});

		response.setAyCode(values[0].concat("-").concat(values[1]));
		return response;
	}

	private String getCupCode(CurriculumPeriodEnum cupType, String crps, String parentCup, String dfdl,
							  String lct, String dfge, String cupNo, Integer index) {
		String result = Objects.isNull(parentCup) ? crps : parentCup;
		// Build code
		switch (cupType) {
			case CURR_SHIFT:
				if (Objects.nonNull(dfdl)) {
					result = result.concat(CombineCodeEnum.DFDL.getPrefix()).concat(dfdl);
				}
				if (Objects.nonNull(lct)) {
					result = result.concat(CombineCodeEnum.LCT.getPrefix()).concat(lct);
				}
				break;
			case CURR_SESSION:
				if (Objects.nonNull(dfge)) {
					result = result.concat(CombineCodeEnum.DFGE.getPrefix()).concat(dfge);
				}
				if (Objects.nonNull(cupNo)) {
					result = result.concat(CombineCodeEnum.CUPNO.getPrefix()).concat(cupNo);
				}
				break;
			default:
				if (Objects.nonNull(cupNo)) {
					result = result.concat(CombineCodeEnum.CUPNO.getPrefix()).concat(cupNo);
				}
				break;
		}
		if (Objects.nonNull(index)) {
			result = result.concat(CombineCodeEnum.INDEX.getPrefix()).concat(index.toString());
		}

		return result;
	}

	private String getCupCodeOM(CurriculumPeriodEnum cupType, String crps, String parentCup, String dfdl,
								String lct, String dfge, String cupNo, Integer index) {
		String result = Objects.isNull(parentCup) ? crps : parentCup;
		// Build code
		switch (Lct.findByCode(lct)) {
			case HORG_AA:
			case RL_45MI:
			case LI0_45MI:
			case LCH_45MI:
				if (Objects.nonNull(dfdl)) {
					result = result.concat(CombineCodeEnum.DFDL.getPrefix()).concat(dfdl);
				}
				if (Objects.nonNull(lct)) {
					result = result.concat(CombineCodeEnum.LCT.getPrefix()).concat(lct);
				}
				break;
			case PC_40MI:
				if (Objects.nonNull(dfdl)) {
					result = result.concat(CombineCodeEnum.DFDL.getPrefix()).concat(dfdl);
				}
				if (Objects.nonNull(lct)) {
					result = result.concat(CombineCodeEnum.LCT.getPrefix()).concat(lct);
				}
				if (Objects.nonNull(cupNo)) {
					result = result.concat(CombineCodeEnum.CUPNO.getPrefix()).concat(cupNo);
				}
				break;
			default:
				if (Objects.nonNull(cupNo)) {
					result = result.concat(CombineCodeEnum.CUPNO.getPrefix()).concat(cupNo);
				}
				break;
		}
		if (Objects.nonNull(index)) {
			result = result.concat(CombineCodeEnum.INDEX.getPrefix()).concat(index.toString());
		}

		return result;
	}

	private String getNumberInString(String a) {
		return Integer.valueOf(a.replaceAll("[^0-9]", "")).toString();
	}
}
