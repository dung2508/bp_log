package vn.edu.clevai.bplog.service.impl;

import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.CodeGenerator;
import vn.edu.clevai.bplog.common.EmailContentDecorate;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.common.enumtype.ChrtCodeEnum;
import vn.edu.clevai.bplog.dto.bp.EmailTextTemplate;
import vn.edu.clevai.bplog.dto.email.ChsiCheckStepEmailDTO;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.entity.logDb.*;
import vn.edu.clevai.bplog.payload.response.logdb.BpChptCheckProcessTempResponse;
import vn.edu.clevai.bplog.payload.response.logdb.CHLIResponse;
import vn.edu.clevai.bplog.payload.response.logdb.CHSIResponse;
import vn.edu.clevai.bplog.repository.bplog.*;
import vn.edu.clevai.bplog.repository.projection.BpCuiContentULCProjection;
import vn.edu.clevai.bplog.repository.projection.CheckProcessTempProjection;
import vn.edu.clevai.bplog.repository.projection.ChriInfoProjection;
import vn.edu.clevai.bplog.service.BpService;
import vn.edu.clevai.bplog.service.BpUsiUserItemService;
import vn.edu.clevai.bplog.service.EmailService;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.model.ApiResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BpServiceImpl implements BpService {

	@Autowired
	private BpChptCheckProcessTempRepository bpChptCheckProcessTempRepository;

	@Autowired
	private BpChpiCheckProcessItemRepository bpChpiCheckProcessItemRepository;

	@Autowired
	private BpChstCheckStepTempRepository bpChstCheckStepTempRepository;

	@Autowired
	private BpChsiCheckStepItemRepository bpChsiCheckStepItemRepository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private BpCuiEventRepository bpCuiEventRepository;

	@Autowired
	private BpCuiContentUserUlcRepository bpCuiContentUserUlcRepository;

	@Autowired
	private BpUniqueLearningComponentRepository bpUniqueLearningComponentRepository;

	@Autowired
	private BpCtiContentItemRepository bpCtiContentItemRepository;

	@Autowired
	private BpCheckListTempRepository bpChltRepository;

	@Autowired
	private BpCheckListItemRepository bpChliRepository;

	@Autowired
	private BpCheckerTypeRepository bpChrtRepository;

	@Autowired
	private BpCheckerItemRepository bpChriRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private BpUsiUserItemService bpUsiService;

	@Value("${chli.checking.url}")
	private String chltLinkChecking;

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_CHPT4)
	public BpChptCheckProcessTempResponse findCHPT5(String lctCode, String lcetCode, String triggerUserType,
													String checkerUserType, String chptType) {
		CheckProcessTempProjection checkProcessTempProjection = bpChptCheckProcessTempRepository
				.findCHPT(lctCode, lcetCode, triggerUserType, checkerUserType, chptType).orElse(null);
		if (Objects.isNull(checkProcessTempProjection)) {
			throw new NotFoundException("Couldn't find chpt-checksteptemp by LCT:" + lctCode + " and  LCET : "
					+ lcetCode + " and USTTrigger " + triggerUserType + " and USTChecker " + checkerUserType
					+ " and CHPTType " + chptType);
		}

		return BpChptCheckProcessTempResponse.builder().id(checkProcessTempProjection.getId())
				.myChptType(checkProcessTempProjection.getMychpttype()).myLct(checkProcessTempProjection.getMyLct())
				.chptCode(checkProcessTempProjection.getCode())
				.checkerUserType(checkProcessTempProjection.getCheckerusertype())
				.createdAt(checkProcessTempProjection.getCreateAt()).updatedAt(checkProcessTempProjection.getUpdateAt())
				.triggerUserType(checkProcessTempProjection.getTriggerusertype())
				.myLcEg(checkProcessTempProjection.getMylceg()).myLcEt(checkProcessTempProjection.getMylcet())
				.myLctFilter(checkProcessTempProjection.getMylctfilter()).name(checkProcessTempProjection.getName())
				.build();
	}

	@Override
	@Transactional
	@WriteUnitTestLog
//	@WriteBPUnitTestLog(BPLogProcessEnum.CREATE_CHPI)
	public BpChpiCheckProcessItem createBpCHPI(String chptCode, String cti1Code, String cti2Code, String cti3Code,
											   String cuieCuiecode, String toSendEmail) {
		BpChpiCheckProcessItem chpi = createCHPI(chptCode, cti1Code, cti2Code, cti3Code, cuieCuiecode);
		List<BpChstCheckStepTemp> chstCheckStepTemps = findCHST(chptCode);
		List<BpChsiCheckStepItem> listChsi = new ArrayList<>();
		for (BpChstCheckStepTemp chst : chstCheckStepTemps) {
			BpChsiCheckStepItem chsi = createCHSI(chst, chpi);
			listChsi.add(chsi);
			BpCheckListTemp chlt = chst.getMyChlt();
			if (Objects.nonNull(chlt)) {
				createChliRecursive(chlt, null, chpi, chsi);
			}
		}
		chpi.setListChsi(new HashSet<>(listChsi));
		bpChpiCheckProcessItemRepository.save(chpi);
		if (Objects.equals(toSendEmail, "T")) {
			CompletableFuture.runAsync(() -> processSendEmail(listChsi));
		}
		return chpi;
	}

	private void processSendEmail(List<BpChsiCheckStepItem> listChsi) {
		for (BpChsiCheckStepItem chsi : listChsi) {
			try {
				log.info("Start send email");
				sendEmail(chsi);
			} catch (Exception e) {
				log.info("Error when send email", e);
			}
		}
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_CHPT4)
	public BpChptCheckProcessTempResponse findCHPT4(String cuieCuiecode) throws Exception {
		BpCuiEvent cuiEvent = bpCuiEventRepository.findFirstByCode(cuieCuiecode).orElse(null);
		if (Objects.nonNull(cuiEvent)) {
			String lcetCode = Objects.nonNull(cuiEvent.getMyLcet()) ? cuiEvent.getMyLcet().getCode() : null;
			BpCuiContentUserUlc myCui = cuiEvent.getMyCui();
			BpUniqueLearningComponent myUlc = myCui.getMyUlc();
			BpLCP myLcp = myUlc.getMyLcp();
			log.info("Start find CHPT4 with cuieventcode: {}, myLcp: {}, lcet code: {}", cuiEvent.getCode(), myLcp,
					lcetCode);
			BpChptCheckProcessTemp item = bpChptCheckProcessTempRepository
					.findFirstByMyLcpCodeAndMyLcetCode(myLcp.getCode(), lcetCode)
					.orElseThrow(() -> new NotFoundException("Couldn't find CHPT4 - myLcp: " + myLcp + lcetCode));
			return BpChptCheckProcessTempResponse.builder().id(item.getId()).chptCode(item.getCode())
//					.myChptType(item.getMyChptType())
//					.myLcEt(lcetCode)..checkerUserType(item.getMyChptType())
//					.createdAt(item.getCreatedAt()).updatedAt(item.getUpdatedAt())
//					.myLcEg(Objects.nonNull(item.getMyLceg()) ? item.getMyLceg().getCode() : null)
//					.myLctFilter(item.getMyLctFilter())
					.name(item.getName()).build();
		}
		throw new NotFoundException("Couldn't find CHPT4 - cuiEventCode: " + cuieCuiecode);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("BPPCreateCHSI")
	public CHSIResponse bpCreateCHSI(String chstCode, String chpiCode) {
		BpChstCheckStepTemp chst = bpChstCheckStepTempRepository.findByCode(chstCode).stream().findFirst().orElse(null);
		BpChpiCheckProcessItem chpi = bpChpiCheckProcessItemRepository.findByCode(chpiCode).stream().findFirst()
				.orElse(null);
		BpChsiCheckStepItem chsi = this.createCHSI(chst, chpi);
		if (!Objects.isNull(chsi)) {
			bpChsiCheckStepItemRepository.save(chsi);
			return mapper.map(chsi, CHSIResponse.class);
		}
		return null;
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("BPPCreateCHLI")
	public CHLIResponse bpCreateCHLI(String chltCode, String chliCode, String chsiCode) {
		if (!bpChsiCheckStepItemRepository.existsByCode(chsiCode)) {
			throw new NotFoundException("Couldn't find CHSI by CHSI_code : " + chsiCode);
		}

		BpCheckListTemp chlt = bpChltRepository.findByCode(chltCode)
				.orElseThrow(() -> new NotFoundException("Couldn't find CHLT by chltCode :" + chltCode));
		BpCheckListItem chli = this.createCHLI(chlt, chliCode,
				bpChsiCheckStepItemRepository.findFirstByCode(chsiCode).orElse(null));
		if (!Objects.isNull(chli)) {
			bpChliRepository.save(chli);
			return mapper.map(chli, CHLIResponse.class);
		}
		return null;
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("BPFindUsiCode")
	public String bpFindUsiCode(String chrtCode, String cuiEventCode) {
		return this.getUSICode(cuiEventCode);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("BPPAssign")
	@Transactional
	public String bpAssignData(String chsiCode, String chriCode) {
		BpChsiCheckStepItem chsi = bpChsiCheckStepItemRepository.findFirstByCode(chsiCode)
				.orElseThrow(() -> new NotFoundException("Couldn't find CHSI by CHSI_code : " + chsiCode));
		BpCheckerItem chri = bpChriRepository.findFirstByCode(chriCode)
				.orElseThrow(() -> new NotFoundException("Couldn't find CHRI by CHRI_code : " + chriCode));
		this.assign(chsi, chri);
		return ApiResponse.SUCCESS;
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.SEND_EMAIL_CHSI)
	public void bpSendEmail(String chsiCode, String chriCode) throws MessagingException {
		BpChsiCheckStepItem chsi = bpChsiCheckStepItemRepository.findFirstByCode(chsiCode)
				.orElseThrow(() -> new NotFoundException("Couldn't find CHSI by CHSI_code : " + chsiCode));
		this.sendEmail(chsi);
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.SEND_EMAIL_CHSI)
	public void sendEmailCHSI(String chsiCode) throws Exception {
		BpChsiCheckStepItem chsi = bpChsiCheckStepItemRepository.findFirstByCode(chsiCode)
				.orElseThrow(() -> new NotFoundException("Couldn't find CHSI by CHSI_code : " + chsiCode));
		this.sendEmail(chsi);
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.SEND_EMAIL_CHSI)
	public void sendEmailCHPI(String chpiCode) throws MessagingException {
		for (BpChsiCheckStepItem chsi : bpChsiCheckStepItemRepository.findAllByChpiCode(chpiCode)) {
			try {
				sendEmail(chsi);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("sendEmail ERROR {}", e.getMessage());
			}
		}
	}

	@WriteUnitTestLog
	protected EmailTextTemplate GetEmailTextTemplate(String chsiCode) {
		BpChsiCheckStepItem chsi = bpChsiCheckStepItemRepository.findFirstByCode(chsiCode)
				.orElseThrow(() -> new NotFoundException("Couldn't find check step item by chsi_code : " + chsiCode));
		String chptCode = Objects.isNull(chsi.getMyChst()) ? null : chsi.getMyChst().getMyChpt();
		BpChptCheckProcessTemp chpt = bpChptCheckProcessTempRepository.findFirstByCode(chptCode).orElse(null);
		return EmailTextTemplate.builder().chsiCode(chsiCode).chstCode(chsi.getMyChst().getCode()).chpt(chpt)
				.chptCode(chpt.getCode()).build();
	}

	@WriteUnitTestLog
	@UnitFunctionName("CheckProcess")
	protected void checkProcess() {
	}

	@WriteUnitTestLog
	@UnitFunctionName("WriteULC")
	protected void writeULC(String chptCode) {
	}

	@WriteUnitTestLog
	protected void WriteCheckStep() {
	}

	@WriteUnitTestLog
	protected void WriteLinkCHSI() {
	}

	;

	protected BpLearningComponentEventType getLCETByCUIEventCode(String cuiEventCode) {
		if (cuiEventCode == null) {
			return null;
		}
		BpCuiEvent cuiEvent = bpCuiEventRepository.findBpCuiEventsByCode(cuiEventCode).stream().findFirst()
				.orElse(null);
		if (Objects.isNull(cuiEvent)) {
			throw new NotFoundException("Couldn't find CUI Event by CUIEventCode :" + cuiEventCode);
		}
		return cuiEvent.getMyLcet();
	}

	protected BpCuiContentUserUlc getCUIContent(String cuiEventCode) {
		if (cuiEventCode == null) {
			return null;
		}
		BpCuiEvent cuiEvent = bpCuiEventRepository.findBpCuiEventsByCode(cuiEventCode).stream().findFirst()
				.orElse(null);
		if (Objects.isNull(cuiEvent)) {
			throw new NotFoundException("Couldn't find CUI Event by CUIEventCode :" + cuiEventCode);
		}
		return cuiEvent.getMyCui();
	}

	protected BpUniqueLearningComponent getULC(String cuiCode) {
		if (cuiCode == null) {
			return null;
		}
		BpCuiContentUserUlc cuiContentUserUlc = bpCuiContentUserUlcRepository.findBpCuiContentUserUlcsByCode(cuiCode)
				.stream().findFirst().orElse(null);
		if (Objects.isNull(cuiContentUserUlc)) {
			throw new NotFoundException("Couldn't find CUI Content by CUICode :" + cuiCode);
		}
		return cuiContentUserUlc.getMyUlc();
	}

	protected BpLearningComponentType getLCT(Integer ULCId) {
		if (ULCId == null) {
			return null;
		}
		BpUniqueLearningComponent uniqueLearningComponent = bpUniqueLearningComponentRepository.findById(ULCId)
				.orElse(null);
		if (Objects.isNull(uniqueLearningComponent)) {
			throw new NotFoundException("Couldn't find learning component type by  ULCId :" + ULCId);
		}
		return uniqueLearningComponent.getMyLct();
	}

	protected BpChpiCheckProcessItem createCHPI(String chptCode, String cti1Code, String CTI2Code, String CTI3Code,
												String cuiEventCode) {
		BpChptCheckProcessTemp myChpt = bpChptCheckProcessTempRepository.findFirstByCode(chptCode).orElseThrow(
				() -> new NotFoundException("Couldn't find chpt_checkprocesstemp with chptCode :" + chptCode));
		if (!bpCtiContentItemRepository.existsByCode(cti1Code) && !bpCtiContentItemRepository.existsByCode(CTI2Code)
				&& !bpCtiContentItemRepository.existsByCode(CTI3Code)) {
			throw new NotFoundException(
					"Couldn't find content item with CTICode :" + cti1Code + " and " + CTI2Code + " and " + CTI3Code);
		}
		String lastChpiCode = bpChpiCheckProcessItemRepository.findLastByCHPT(chptCode)
				.map(BpChpiCheckProcessItem::getCode).orElse(null);
		String newCHPICode = CodeGenerator.toCode(chptCode, lastChpiCode, 0);
		return BpChpiCheckProcessItem.builder().code(newCHPICode).name(newCHPICode).myChpt(myChpt)
//				.myLct(Objects.nonNull(myChpt.getMyLct()) ? myChpt.getMyLct().getCode() : null)
//				.myChptType(myChpt.getMyChptType())
				.myLcet(Objects.nonNull(myChpt.getMyLcet()) ? myChpt.getMyLcet().getCode() : null)
				.myCuiEvent(bpCuiEventRepository.findFirstByCode(cuiEventCode).orElse(null))
				.myTrigger(myChpt.getTriggerUserType()).myChecker(myChpt.getCheckerUserType()).myCti1(cti1Code)
				.myCti2(CTI2Code).myCti3(CTI3Code).createdAt(new Timestamp(System.currentTimeMillis()))
				.updatedAt(new Timestamp(System.currentTimeMillis())).build();
	}

	protected List<BpChstCheckStepTemp> findCHST(String chptCode) {
		return bpChstCheckStepTempRepository.findByMyChpt(chptCode);
	}

	private BpChsiCheckStepItem createCHSI(BpChstCheckStepTemp chstCheckStepTemp,
										   BpChpiCheckProcessItem chpiCheckProcessItem) {
		if (Objects.isNull(chstCheckStepTemp) || Objects.isNull(chpiCheckProcessItem)) {
			return null;
		}

		String lastChsiCode = bpChsiCheckStepItemRepository.findLastByCHST(chstCheckStepTemp.getCode())
				.map(BpChsiCheckStepItem::getCode).orElse(null);
		String code = CodeGenerator.toCode(
				chpiCheckProcessItem.getCode().concat("-").concat(chstCheckStepTemp.getCode()),
				StringUtils.isBlank(lastChsiCode) ? chstCheckStepTemp.getMyChrt().getCode() : lastChsiCode, 0);
		return BpChsiCheckStepItem.builder().code(code).checkSample(chstCheckStepTemp.getCheckSample())
				.name(code)
				.myChpi(chpiCheckProcessItem).myChst(chstCheckStepTemp).build();
	}

	protected BpCheckListTemp findCHLT(String chltCode) {
		return bpChltRepository.findByCode(chltCode)
				.orElseThrow(() -> new NotFoundException("Could not find any CHLT with code = " + chltCode));
	}

	protected BpCheckListItem createCHLI(BpCheckListTemp chlt, String chliCode, BpChsiCheckStepItem chsi) {
		return createCHLI(chlt, chliCode, chsi, 0);
	}

	protected List<BpCheckListItem> createChliRecursive(
			BpCheckListTemp chlt,
			BpCheckListItem parentChli,
			BpChpiCheckProcessItem chpi,
			BpChsiCheckStepItem chsi
	) {
		List<BpCheckListItem> bpCheckListItems = new ArrayList<>();
		BpCheckListItem chli = BpCheckListItem.builder()
				.code(String.join("-", chpi.getCode(), chsi.getCode(),
						chlt.getCode(), String.valueOf(System.currentTimeMillis())))
				.myChsi(chsi)
				.myParentChlt(chlt)
				.myParentChli(parentChli)
				.score1Type(chlt.getScore1Type())
				.score2Type(chlt.getScore2Type())
				.doNot(chlt.getDoNot())
				.correctExample(chlt.getCorrectExample())
				.incorrectExample(chlt.getIncorrectExample())
				.build();
		bpChliRepository.save(chli);
		bpCheckListItems.add(chli);
		List<BpCheckListItem> childrenChliList = bpChltRepository.findByMyParentChltCode(chlt.getCode())
				.stream()
				.map(childChlt -> createChliRecursive(childChlt, chli, chpi, chsi))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		bpCheckListItems.addAll(childrenChliList);
		return bpCheckListItems;
	}

	private BpCheckListItem createCHLI(BpCheckListTemp chlt, String chliCode, BpChsiCheckStepItem chsi, Integer index) {
		if (Objects.isNull(chlt) || StringUtils.isBlank(chliCode) || Objects.isNull(chsi)) {
			return null;
		}
		String checkListItem1Code = bpChliRepository.findLastByParentCHLT(chlt.getCode()).map(BpCheckListItem::getCode)
				.orElse(null);
		String checkListItem2Code = bpChliRepository.findLastByParentCHLI(chliCode).map(BpCheckListItem::getCode)
				.orElse(null);
		String code = CodeGenerator.toCode(chlt.getCode(), checkListItem1Code, index);
		String subCode = CodeGenerator.toCode(chliCode, checkListItem2Code, index);
		return BpCheckListItem.builder().code(code).subCode(subCode).name(code).myChsi(chsi).myParentChlt(chlt)
				.myParentChli(bpChliRepository.findFirstByCode(chliCode).orElse(null)).doNot(chlt.getDoNot())
				.correctExample(chlt.getCorrectExample()).incorrectExample(chlt.getIncorrectExample()).build();
	}

	protected BpCheckerType findCHRT(String chrtCode) {
		return bpChrtRepository.findByCode(chrtCode).stream().findFirst().orElse(null);
	}

	public String getUSICode(String CUIEventCode) {
		return bpCuiEventRepository.findUSICodeByCUIEvent(CUIEventCode);

	}

	protected BpCheckerItem getBpCheckerUSI(BpCheckerType checkerType, BpCuiEvent cuiEvent) {
		if (Objects.isNull(checkerType) || Objects.isNull(cuiEvent)) {
			return null;
		}
		String USICode = this.getUSICode(cuiEvent.getCode());
		return bpChriRepository.findBpCheckerItemsByMyUsiAndMyChrt(USICode, checkerType.getCode()).stream().findFirst()
				.orElse(null);
	}

	protected void assign(BpChsiCheckStepItem chsi, BpCheckerItem chri) {
		chsi.setMyChri(chri);
	}

	public void sendEmail(BpChsiCheckStepItem chsiCheckStepItem) throws MessagingException {
		Asserts.notNull(chsiCheckStepItem.getMyChri(), "Chri must be not blank");
		ChriInfoProjection chriInfo = bpChriRepository.findEmailByChriCode(chsiCheckStepItem.getMyChri().getCode())
				.orElseThrow(() -> new MessagingException(
						"Cant send email because cant found Chri " + chsiCheckStepItem.getMyChri()));
		BpChstCheckStepTemp chst = chsiCheckStepItem.getMyChst();
		BpChpiCheckProcessItem chpi = chsiCheckStepItem.getMyChpi();
		if (Objects.nonNull(chst) && Objects.nonNull(chpi)) {
			BpCuiEvent cuiEvent = chpi.getMyCuiEvent();
			Asserts.notNull(cuiEvent, "CUI event code must be not null");
			BpChptCheckProcessTemp chpt = chpi.getMyChpt();

			BpCuiContentULCProjection receiverContent = bpCuiContentUserUlcRepository
					.findByCUIEventCode(cuiEvent.getCode()).orElse(null);
			BpCheckListTemp chlt = chst.getMyChlt();
			Asserts.notNull(chlt, "CHLT of CHST: " + chst.getCode() + " -> must be not null");
			Page<BpCheckListItem> pageChli = bpChliRepository.findFirstByChsiCode(chsiCheckStepItem.getCode(),
					PageRequest.of(0, 1));
			if (!CollectionUtils.isEmpty(pageChli.getContent())) {
				BpCheckListItem chli = pageChli.getContent().iterator().next();
				ChsiCheckStepEmailDTO emailDTO = ChsiCheckStepEmailDTO.builder().chriUsi(chriInfo.getName())
						.chliCode(chli.getCode()).link(chltLinkChecking).chrtCode(chriInfo.getMychrt())
						.ulcCode(receiverContent.getMyulc()).chstName(chst.getName()).chptName(chpt.getName())
						.triggerUst(chpt.getTriggerUserType()).build();
				emailService.send(Objects.isNull(chriInfo) ? "clevai@gmail.com" : chriInfo.getEmail(),
						EmailContentDecorate.buildEmailTitle(emailDTO),
						EmailContentDecorate.buildEmailContent(emailDTO), null);
			} else {
				ChsiCheckStepEmailDTO emailDTO = ChsiCheckStepEmailDTO.builder().chriUsi(chriInfo.getName())
						.chliCode("").link(chltLinkChecking).chrtCode(chriInfo.getMychrt())
						.ulcCode(receiverContent.getMyulc()).chstName(chst.getName()).chptName(chpt.getName())
						.triggerUst(chpt.getTriggerUserType()).build();
				emailService.send(Objects.isNull(chriInfo) ? "clevai@gmail.com" : chriInfo.getEmail(),
						EmailContentDecorate.buildEmailTitle(emailDTO),
						EmailContentDecorate.buildEmailContent(emailDTO), null);
			}
		}
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("assignCHRI")
	public List<String> bpAssignChri(String chpiCode) throws Exception {
		BpChpiCheckProcessItem chpiItem = bpChpiCheckProcessItemRepository.findFirstByCode(chpiCode)
				.orElseThrow(() -> new NotFoundException("Cant found chpi code " + chpiCode));
		BpChptCheckProcessTemp chpt = chpiItem.getMyChpt();
		Assert.notNull(chpt, "CHPT must be not null");
		BpCuiEvent cuiEvent = chpiItem.getMyCuiEvent();
		Assert.notNull(cuiEvent, "CUI event must be not null");
		List<BpChsiCheckStepItem> listChsiItems = bpChsiCheckStepItemRepository.findAllByChpiCode(chpiItem.getCode());
		if (CollectionUtils.isEmpty(listChsiItems)) {
			throw new NotFoundException("Cant found chsi from chpi code " + chpiCode);
		}
		BpLearningComponentEventType mylcet = chpt.getMyLcet();
		BpCuiContentUserUlc cuiUlc = cuiEvent.getMyCui();
		BpUniqueLearningComponent ulc = cuiUlc.getMyUlc();
		CalendarPeriod cap = ulc.getMyCap();
		BpLCP lcp = ulc.getMyLcp();
		String checkerUserType = chpt.getCheckerUserType();
		List<BpCheckerItem> listChri = new ArrayList<>();
		for (BpChsiCheckStepItem chsi : listChsiItems) {
			try {
				BpChstCheckStepTemp chst = chsi.getMyChst();
				BpCheckerType chrt = chst.getMyChrt();
				String chrtCode = chrt.getCode();
				ChrtCodeEnum chrtEnum = ChrtCodeEnum.findByCode(chrtCode);

				String combineCode = chsi.getCode() + "-" + chrt.getCode();
				String lastChriCode = bpChriRepository.findLastByCode(combineCode).map(BpCheckerItem::getCode)
						.orElse(null);
				String newCode = CodeGenerator.toCode(combineCode, lastChriCode, 0);
				String name = CodeGenerator.toCode("CHRI-" + combineCode, lastChriCode, 0);
				BpCheckerItem checkItem = BpCheckerItem.builder().code(newCode).name(name).myChrt(chrt)
						.myCap(cap.getCode()).build();
				String chriMyUsi = null;
				if (Objects.nonNull(chrtEnum)) {
					log.info("bpAssignChri {} {} {} {} {}", mylcet.getCode(), checkerUserType,
							cap.getCode(), chrt.getCode(), lcp.getCode());

					BpUsiUserItemResponse response = bpUsiService.findUSI(mylcet.getCode(), checkerUserType,
							cap.getCode(), chrt.getCode(), lcp.getCode(), null);
					if (Objects.nonNull(response)) {
						chriMyUsi = response.getCode();
					}
				}
				if (!StringUtils.isBlank(chriMyUsi)) {
					checkItem.setMyUsi(chriMyUsi);
					listChri.add(checkItem);
					assign(chsi, checkItem);
				}
			} catch (Exception e) {
				log.error("Error when assign chsi {}", chsi);
			}
		}
		if (!CollectionUtils.isEmpty(listChri)) {
			bpChriRepository.saveAll(listChri);
			bpChsiCheckStepItemRepository.saveAll(listChsiItems);
		}
		return listChri.stream().map(BpCheckerItem::getCode).collect(Collectors.toList());
	}

}
