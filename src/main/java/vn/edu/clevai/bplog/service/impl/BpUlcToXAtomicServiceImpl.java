package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.enumtype.LCLEnum;
import vn.edu.clevai.bplog.converter.BaseXConverter;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.entity.logDb.BpLearningComponentType;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.repository.LcpMergeRepository;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.common.api.exception.ConflictException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.slack.SlackService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BpUlcToXAtomicServiceImpl implements BpUlcToXAtomicService {
	@Autowired
	private BpUniqueLearningComponentRepository ulcRepository;

	@Autowired
	private LcpMergeRepository lcpMergeRepository;

	@Lazy
	@Autowired
	private BpULCService bpULCService;

	@Autowired
	private BpLCPService lcpService;

	@Autowired
	private BpLctService lctService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SlackService slackService;

	@Autowired
	private CalendarPeriodService capService;

	private final static String HYPHEN = "-";

	@Override
	public void convert(String ulcCode) {
		BpUniqueLearningComponent ulc = ulcRepository.findFirstByCode(ulcCode)
				.orElseThrow(() -> new NotFoundException("Could not find any ulc using code = " + ulcCode));

		if (!ulc.getMyLct().getMyLcl().equalsIgnoreCase(LCLEnum.LCSH.getName())) {
			throw new ConflictException(
					"Ulc (code = " + ulcCode + ") with lct = " + ulc.getMyLct().getCode() + " is not at the shift layer"
			);
		}

		String mainUlcCode = lcpMergeRepository.findMainUdlLcpMergeByUshLct(ulc.getMyLct().getCode())
				.map(
						ulcm -> {
							BpLearningComponentType pkg = lctService.findByCode(ulcm.getMylctpk());

							BpLCP mainUdlLcp = lcpService.findByCode(ulcm.getMylcp());

							BpLCP mainShiftLcp = lcpService.findLcpshByPtAndLct(
									pkg.getMyPt(),
									mainUdlLcp.getMylctparent()
							);

							CalendarPeriod cady = capService.findByCode(ulc.getMyCap().getMyParent());

							CalendarPeriod mainCash = capService.getCASH(
									cady.getCode(),
									pkg.getMyPt(),
									ulc.getMyGg(),
									ulc.getMyDfdl(),
									mainShiftLcp.getMyprd(),
									ulc.getMyCap().getCashStart()
							);

							return bpULCService.generateUlcCode(
									mainShiftLcp.getCode(),
									mainCash.getCode(),
									ulc.getMyGg(),
									ulc.getMyDfdl(),
									null,
									pkg.getMyPt(),
									null,
									null
							);
						}
				)
				.orElse(ulcCode);

		if (!Objects.equals(ulcCode, mainUlcCode)) {
			log.info("ULC {} is merged DL to ULC {}", ulcCode, mainUlcCode);
		}

		convert(mainUlcCode, ulc.getMyLct().getCode(), ulc.getMyDfge(), ulc);
	}

	@Override
	public void convertMonth(String ulcCode) {
		BpUniqueLearningComponent ulc = ulcRepository.findFirstByCode(ulcCode)
				.orElseThrow(() -> new NotFoundException("Could not find any ulc using code = " + ulcCode));

		if (!ulc.getMyLct().getMyLcl().equalsIgnoreCase(LCLEnum.LCMN.getName())) {
			throw new ConflictException(
					"Ulc (code = " + ulcCode + ") with lct = " + ulc.getMyLct().getCode() + " is not at the shift layer"
			);
		}

		convert(ulcCode, ulc.getMyLct().getCode(), ulc.getMyDfge(), ulc);
	}

	@Override
	public void convert(String ulcCode, String xdsc) {
		BpUniqueLearningComponent ulc = ulcRepository.findFirstByCode(ulcCode)
				.orElseThrow(() -> new NotFoundException("Could not find any ulc using code = " + ulcCode));

		convert(xdsc, ulc.getMyLct().getCode(), ulc.getMyDfge(), ulc);
	}

	@Override
	public void convert(List<String> ulcCodes, String xdsc) {
		List<BpUniqueLearningComponent> ulcs = ulcRepository.findAllByCodeInAndPublishedTrue(ulcCodes);

		if (ulcs.isEmpty()) {
			return;
		}

		BpUniqueLearningComponent ulc = ulcs.get(0);

		convert(xdsc, ulc.getMyLct().getCode(), ulc.getMyDfge(), ulcs);
	}

	private void convert(String xdsc, String lct, String dfge, BpUniqueLearningComponent ulc) {
		convert(xdsc, lct, dfge, Collections.singletonList(ulc));
	}

	public void convert(String xdsc, String lct, String dfge, List<BpUniqueLearningComponent> ulcs) {
		if (ulcs.isEmpty()) {
			return;
		}

		try {
			ulcs.forEach(ulc -> ulc.setXdsc(xdsc));
			/* find converter and convert to X here. */
			getConverter(lct, dfge).convert(xdsc, lct, dfge, ulcs);
			ulcRepository.saveAll(ulcs);

			/* Find all children */
			List<BpUniqueLearningComponent> children = ulcRepository.findByMyParentInAndPublishedTrue(
					ulcs.stream().map(BpUniqueLearningComponent::getCode).collect(Collectors.toList())
			);

			Map<Pair<String, String>, List<BpUniqueLearningComponent>> filteredChild = children.stream().collect(
					Collectors.groupingBy(
							x -> Pair.with(x.getMyLct().getCode(), x.getMyDfge())
					)
			);

			/* Process children here. */
			filteredChild.forEach((key, value) -> convert(xdsc, key.getValue0(), key.getValue1(), value));
		} catch (Exception e) {
			String message = "Failed to convert multiple " + lct + " ulcs (codes = " +
					ulcs.stream().map(BpUniqueLearningComponent::getCode).collect(Collectors.toList())
					+ ") to X (xdsc = " + xdsc + ") with error: " + e.getMessage();

			log.error("{}", message, e);
			log.error("Stack trace: {}", DebuggingDTO.build(e));

			slackService.notifySlack(message);
		}
	}

	private BaseXConverter getConverter(String lct, String dfge) {
		return (BaseXConverter) applicationContext.getBean(getConverterName(lct, dfge));
	}

	private String removeHyphen(String lct) {
		return StringUtils.remove(lct, HYPHEN);
	}

	private String getConverterName(String lct, String dfge) {
		String converterName = removeHyphen(lct);

		if (dfge != null) {
			converterName += dfge;
		}

		converterName += "ToXConverter";

		return converterName;
	}
}
