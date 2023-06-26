package vn.edu.clevai.bplog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.CapTypeRelationEnum;
import vn.edu.clevai.bplog.common.enumtype.CurriculumPeriodEnum;
import vn.edu.clevai.bplog.dto.CalendarPeriodDTO;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.entity.CurriculumPeriod;
import vn.edu.clevai.bplog.repository.CalendarPeriodRepository;
import vn.edu.clevai.bplog.service.BpWsoWeeklyscheduleoptionService;
import vn.edu.clevai.bplog.service.CalendarPeriodService;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum.validate;


@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarPeriodServiceImpl implements CalendarPeriodService {

	@Autowired
	private CalendarPeriodRepository calendarPeriodRepository;

	@Autowired
	private CurriculumPeriodService curriculumPeriodService;

	@Autowired
	private BpWsoWeeklyscheduleoptionService wsoService;

	@Autowired
	private ObjectMapper objectMapper;

	private final List<String> CAP_TYPE_CAWK_WEEK = Arrays.asList(
			CalendarPeriodTypeEnum.WEEK.getCode(),
			CalendarPeriodTypeEnum.DAY.getCode()
	);

	@Override
	public CalendarPeriod getCAPByTime(Timestamp time, String capType) {
		return calendarPeriodRepository.findByTimeAndCapType(time, capType)
				.orElseThrow(
						() -> new NotFoundException("Could not find any CAP using code and type = " + time + capType)
				);
	}

	@Override
	@WriteUnitTestLog
	public CalendarPeriod getCAP(String inputCapCode, String capType, String capNo) {
		CalendarPeriod inputCap = calendarPeriodRepository.findByCode(inputCapCode).orElse(null);
		if (Objects.isNull(inputCap)) {
			throw new NotFoundException("Couldn't find cap by inputCapCode: " + inputCapCode
					+ " capType: " + capType + " capNo: " + capNo);
		}
		CalendarPeriodTypeEnum inputCapType =
				CalendarPeriodTypeEnum.findByCode(inputCap.getCapType());
		CalendarPeriodTypeEnum outputCapType =
				CalendarPeriodTypeEnum.findByCode(capType);

		if (Objects.isNull(inputCapType) || Objects.isNull(outputCapType)) {
			throw new NotFoundException("Couldn't find cap by inputCapCode: " + inputCapCode
					+ " capType: " + capType + " capNo: " + capNo);
		}

		CapTypeRelationEnum delta = CapTypeRelationEnum
				.findByDeltaLevel(outputCapType.getLevel() - inputCapType.getLevel());

		switch (delta) {
			case CHILD:
				return findByMyParentAndNumberAsChild(inputCapCode, capNo);
			case GRAND_CHILD:
				return findByMyGrandParentAndCapTypeAndNumberAsGrandChild(inputCapCode, capType, capNo);
			default:
				throw new NotFoundException("Couldn't find cap by inputCapCode: " + inputCapCode
						+ " capType: " + capType + " capNo: " + capNo);
		}
	}

	@Override
	@WriteUnitTestLog
	public CalendarPeriod getCAWK(String cupCode) {
		CurriculumPeriod curriculumPeriod = curriculumPeriodService.findByCodeAndCurrPeriodType
				(cupCode, CurriculumPeriodEnum.CURR_WEEK.getCode());

		if (Objects.nonNull(curriculumPeriod.getMyCap())) {
			return findByCodeAndCapType(curriculumPeriod.getMyCap(),
					CalendarPeriodTypeEnum.WEEK.getCode());
		}

		return null;
	}

	@Override
	public CalendarPeriod getCASS(String inputCapCode, String lcpPeriodNo, String capStructure) {
		return calendarPeriodRepository.findByMyParentAndNumberAsChildAndAndMystructureAndCapTypeAndPublishedTrue
				(inputCapCode, lcpPeriodNo, capStructure, CalendarPeriodTypeEnum.SESSION.getCode())
				.orElse(null);
	}

	@Override
	public CalendarPeriod getCASH(String cady, String pt, String gg, String dfdl, String prd, String cashStart) {
		return calendarPeriodRepository.findByMyParentAndMyPrdAndCashStartAndCapTypeAndPublishedTrue
				(cady, prd, cashStart, CalendarPeriodTypeEnum.SHIFT.getCode()).orElse(null);
	}

	@Override
	@WriteUnitTestLog
	public CalendarPeriod getCADY(String cupCode, String wsoCode) {
		CurriculumPeriod cudy = curriculumPeriodService.findByCodeAndCurrPeriodType
				(cupCode, CurriculumPeriodEnum.CURR_DAY.getCode());
		CurriculumPeriod cuwk = curriculumPeriodService.findByCodeAndCurrPeriodType
				(cudy.getMyParentCup(), CurriculumPeriodEnum.CURR_WEEK.getCode());
		String dayOfWeek = wsoService.findDayOfWeek(wsoCode, cudy.getMycupno());

		return findByMyParentAndNumberAsChild
				(cuwk.getMyCap(), dayOfWeek);
	}

	@Override
	public CalendarPeriod findByCode(String code) {
		return calendarPeriodRepository.findByCode(code)
				.orElseThrow(() -> new NotFoundException("Could not find any CAP using code = " + code));
	}

	@Override
	public CalendarPeriod findByCodeAndCapType(String capCode, String capType) {
		return calendarPeriodRepository.findByCodeAndCapType(capCode, capType)
				.orElseThrow(
						() -> new NotFoundException("Could not find any CAP using code and type = " + capCode + capType)
				);
	}

	@Override
	public CalendarPeriod findByMyParentAndNumberAsChild
			(String inputCapCode, String capNo) {
		return calendarPeriodRepository.findByMyParentAndNumberAsChildAndPublishedTrue
				(inputCapCode, capNo).orElse(null);
	}

	@Override
	public List<CalendarPeriodDTO> findAllCalendarPeriod(List<String> types) {
		validate(types);
		List<CalendarPeriod> allInDB = calendarPeriodRepository.findAllByType(types);
		List<CalendarPeriodDTO> result =
				allInDB.stream().map(item -> {
							CalendarPeriodDTO dto = objectMapper.convertValue(item, CalendarPeriodDTO.class);
							dto.setSubCap(getChild(allInDB, item));
							return dto;
						}
				).collect(Collectors.toList());

		//reduce
		try {
			CalendarPeriodDTO dto = result.stream().filter(f -> !CollectionUtils.isEmpty(f.getSubCap())).findFirst().orElse(null);
			String parentType = dto.getCapType();
			CalendarPeriodDTO subCap = dto.getSubCap().stream().findFirst().orElse(null);
			String subType = subCap.getCapType();
			List<CalendarPeriodDTO> reduce = result.stream().filter(f -> !subType.equals(f.getCapType()))
					.collect(Collectors.toList());

			result = reduce;
		} catch (Exception e) {
			log.error("Reduce list error {} ", e.getMessage());
		}

		return result;
	}

	@Override
	public List<CalendarPeriodDTO> findAllCalendarPeriodMonth(String type) {
		validate(Collections.singletonList(type));
		return calendarPeriodRepository.findAllByTypeMonth(type).stream().map(item -> {
			return objectMapper.convertValue(item, CalendarPeriodDTO.class);
		}).collect(Collectors.toList());
	}

	@Override
	public List<CalendarPeriod> getCadyFromWsoAndCawk(Timestamp from, Timestamp to, List<Integer> wso) {
		if (from.after(to)) {
			log.error("fromDay can't after toDay");
			return new ArrayList<>();
		}
		return calendarPeriodRepository.getCadyFromWsoAndCawk(from, to, wso);
	}

	@Override
	public List<CalendarPeriod> getCadyListScheduledOfClag(String clag) {
		return calendarPeriodRepository.getCadyListScheduledOfClag(clag);
	}

	@Override
	public List<CalendarPeriod> findCapListScheduledForEPOD(
			String usi, Timestamp from, Timestamp to, String pt, String gg, String dfdl, String parentCapType) {
		return calendarPeriodRepository.findCapListScheduledForEPOD(usi, from, to, pt, gg, dfdl, parentCapType);
	}

	public Set<CalendarPeriodDTO> getChild(List<CalendarPeriod> caps, CalendarPeriod currentCap) {
		Set<CalendarPeriodDTO> result = new TreeSet<>(Comparator.comparing(CalendarPeriodDTO::getCode));
		for (CalendarPeriod f : caps) {
			if (currentCap.getCode().equals(f.getMyParent())) {
				CalendarPeriodDTO calendarPeriodDTO = objectMapper.convertValue(f, CalendarPeriodDTO.class);
				result.add(calendarPeriodDTO);
			}
		}
		return result;
	}


	@Override
	public List<CalendarPeriod> findByMyGrandParentAndCapType
			(String inputCapCode, String capType) {
		return calendarPeriodRepository.findByMyGrandParentAndCapTypeAndPublishedTrue
				(inputCapCode, capType);
	}

	@Override
	public List<CalendarPeriod> findCapKid(String capCode, String capStructure) {
		return calendarPeriodRepository.findByMyParentAndMystructureAndMyLctIsNull(capCode, capStructure);
	}

	@Override
	public List<CalendarPeriod> findByParentAndCapType(String parent, String capType) {
		return calendarPeriodRepository.findAllByMyParentAndCapTypeAndPublishedTrueOrderByNumberAsChildDesc(parent, capType);
	}

	private CalendarPeriod findByMyGrandParentAndCapTypeAndNumberAsGrandChild
			(String inputCapCode, String capType, String capNo) {
		return calendarPeriodRepository.findByMyGrandParentAndCapTypeAndNumberAsGrandChildAndPublishedTrue
				(inputCapCode, capType, capNo).orElse(null);
	}
}
