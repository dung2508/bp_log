package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.enumtype.CTTEnum;
import vn.edu.clevai.bplog.common.enumtype.LCPLCTLCKEnum;
import vn.edu.clevai.bplog.dto.curriculums.ContentItemDTO;
import vn.edu.clevai.bplog.entity.ContentItem;
import vn.edu.clevai.bplog.entity.CurriculumPeriod;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.payload.request.cti.*;
import vn.edu.clevai.bplog.repository.bplog.BpCtiContentItemRepository;
import vn.edu.clevai.bplog.repository.projection.*;
import vn.edu.clevai.bplog.service.ContentItemService;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContentItemServiceImpl implements ContentItemService {
	@Autowired
	BpCtiContentItemRepository repository;

	@Autowired
	CurriculumPeriodService curriculumPeriodService;

	@Autowired
	ModelMapper modelMapper;

	@Override
	public void createOrUpdate(List<ContentItemDTO> dtos) {
		List<BpContentItem> contentItems = new ArrayList<>();
		List<String> listCode = dtos.stream().map(ContentItemDTO::getCode).collect(Collectors.toList());

		Map<String, BpContentItem> map = new HashMap<>();
		repository.findByCodeIn(listCode).forEach(i -> {
			map.put(i.getCode(), i);
		});

		dtos.forEach(d -> {
			BpContentItem contentItem = map.get(d.getCode());
			if (Objects.isNull(contentItem)) {
				// Create new
				contentItem = BpContentItem.builder()
						.code(d.getCode()).name(d.getName()).myCtt(d.getContentItemType())
						.myValueSet(d.getValueSet()).fileBeginUrl(d.getFileBeginUrl())
						.fileLocationUrl(d.getFileLocationUrl()).gDocKey(d.getGdocKey())
						.build();
			} else {
				// Update
				contentItem.setFileBeginUrl(d.getFileBeginUrl());
				contentItem.setFileLocationUrl(d.getFileLocationUrl());
				contentItem.setGDocKey(d.getGdocKey());
				contentItem.setMyValueSet(d.getValueSet());
			}

			contentItems.add(contentItem);
		});

		repository.saveAll(contentItems);
	}

	@Override
	public BpContentItem createOrUpdate(BpContentItem contentItem) {
		return repository.createOrUpdate(contentItem);
	}

	@Override
	public BpContentItem createOrUpdateCTT_ULC(CurriculumPeriod cup, CTIULCRequest ctiRequest, String lct,
											   String originLct, Integer parentNo, Integer mynoaschild) {
		log.info("createOrUpdateCTT_ULC 1 cup-{} lct-{} origin-{}", cup.getCode(), lct, originLct);
		LCPLCTLCKEnum lctEnum = LCPLCTLCKEnum.findByCode(lct);
		LCPLCTLCKEnum originLctEnum = LCPLCTLCKEnum.findByCode(originLct);

		// CTI ULC
		BpContentItem ctiUlc = BpContentItem.builder()
				.code(cup.getCode().concat("-").concat(CTTEnum.ULC.getCode()))
				.myCtt(CTTEnum.ULC.getCode())
				.published(true)
				.build();

		log.info("createOrUpdateCTT_ULC 2 cup-{} lct-{} origin-{}", cup.getCode(), lct, originLct);

		// CTI Children
		switch (lctEnum) {
			case DLC_75MI:
			case DLG_90MI:
				ctiUlc.setName(ctiRequest.getShiftName());
				ctiUlc = createOrUpdate(ctiUlc);
				// Create 3 BL3 QG
				for (int i = 0; i < ctiRequest.getCtibl3().size(); i++) {
					createOrUpdate(BpContentItem.builder()
							.myParent(ctiUlc.getCode())
							.myValueSet(ctiRequest.getCtibl3().get(i).getBl3Code())
							.code(String.join("-", ctiUlc.getCode(), CTTEnum.BL3QG.getCode(),
									String.valueOf(i + 1)))
							.myCtt(CTTEnum.BL3QG.getCode())
							.published(true)
							.build());
				}
				break;
			case GES_75MI:
			case LI0_45MI:
				ctiUlc.setName(ctiRequest.getShiftName());
				ctiUlc = createOrUpdate(ctiUlc);
				break;
			case DL_40MI:
			case LI_45MI:
				ctiUlc = createOrUpdate(ctiUlc);
				// Create slide, video link
				CTISSLRequest ctiSsl = ctiRequest.getCtiDlSsl();
				CTIVDLRequest ctiVdl = ctiRequest.getCtiVdl();

				createOrUpdate(BpContentItem.builder()
						.myParent(ctiUlc.getCode())
						.name(ctiSsl.getSslName())
						.code(String.join("-", ctiUlc.getCode(), CTTEnum.SSTE.getCode()))
						.myCtt(CTTEnum.SSTE.getCode())
						.myValueSet(ctiSsl.getSslTeacher())
						.fileLocationUrl(ctiSsl.getSslTeacher())
						.published(true)
						.build());

				createOrUpdate(BpContentItem.builder()
						.myParent(ctiUlc.getCode())
						.name(ctiSsl.getSslName())
						.code(String.join("-", ctiUlc.getCode(), CTTEnum.SSST.getCode()))
						.myCtt(CTTEnum.SSST.getCode())
						.myValueSet(ctiSsl.getSslStudent())
						.fileLocationUrl(ctiSsl.getSslStudent())
						.published(true)
						.build());

				createOrUpdate(BpContentItem.builder()
						.myParent(ctiUlc.getCode())
						.code(String.join("-", ctiUlc.getCode(), CTTEnum.VIDEO.getCode()))
						.myCtt(CTTEnum.VIDEO.getCode())
						.startUrl(ctiVdl.getStartUrl())
						.secretKey(ctiVdl.getSecretKey())
						.joinUrl(ctiVdl.getJoinUrl())
						.published(true)
						.build());
				break;
			case GE_45MI:
			case GE_75MI:
				ctiUlc = createOrUpdate(ctiUlc);
				// Create slide, get link
				CTISSLRequest geSsl = ctiRequest.getCtiGeSsl();

				createOrUpdate(BpContentItem.builder()
						.myParent(ctiUlc.getCode())
						.name(geSsl.getSslName())
						.code(String.join("-", ctiUlc.getCode(), CTTEnum.SSTE.getCode()))
						.myCtt(CTTEnum.SSTE.getCode())
						.myValueSet(geSsl.getSslTeacher())
						.fileLocationUrl(geSsl.getSslTeacher())
						.published(true)
						.build());

				createOrUpdate(BpContentItem.builder()
						.myParent(ctiUlc.getCode())
						.name(geSsl.getSslName())
						.code(String.join("-", ctiUlc.getCode(), CTTEnum.SSST.getCode()))
						.myCtt(CTTEnum.SSST.getCode())
						.myValueSet(geSsl.getSslStudent())
						.fileLocationUrl(geSsl.getSslStudent())
						.published(true)
						.build());
				break;
			case DQS_AA:
				// Create bl5 qp
				List<CTIDSCRequest> listDsc = ctiRequest.getCtiDsc();
				if (Objects.isNull(listDsc) || listDsc.isEmpty()) {
					return null;
				}
				String bl5qp = listDsc.get(parentNo - 1).getDQuizSlot();

				return createOrUpdate(BpContentItem.builder()
						.code(String.join("-", cup.getCode(), CTTEnum.BL5QP.getCode()))
						.myCtt(CTTEnum.BL5QP.getCode())
						.myBl5Qp(bl5qp)
						.published(true)
						.build());
			case RQS_1MI:
				// Create bl5 qp
				if (Objects.isNull(ctiRequest.getCtiRc()) ||
						Objects.isNull(ctiRequest.getCtiRc().getRQuizSlots()) ||
						ctiRequest.getCtiRc().getRQuizSlots().isEmpty()) {
					return null;
				}
				String bl5qpRc = ctiRequest.getCtiRc().getRQuizSlots().get(mynoaschild - 1);

				return createOrUpdate(BpContentItem.builder()
						.code(String.join("-", cup.getCode(), CTTEnum.BL5QP.getCode()))
						.myCtt(CTTEnum.BL5QP.getCode())
						.myBl5Qp(bl5qpRc)
						.published(true)
						.build());
			case AQR1_AA:
				if (Objects.isNull(ctiRequest.getCtiHrg()) ||
						Objects.isNull(ctiRequest.getCtiHrg().getCtiAqrs()) ||
						ctiRequest.getCtiHrg().getCtiAqrs().isEmpty()) {
					return null;
				}

				List<CTIAQRRequest> ctiHrgRequestList = ctiRequest.getCtiHrg().getCtiAqrs();
				String bl4qtHrg = ctiHrgRequestList.get(mynoaschild - 1).getAiQuizRun();

				return createOrUpdate(BpContentItem.builder()
						.code(String.join("-", cup.getCode(), CTTEnum.BL4QT.getCode()))
						.myCtt(CTTEnum.BL4QT.getCode())
						.myLo(bl4qtHrg)
						.published(true)
						.build());
			case AQR2_AA:
				if (Objects.isNull(ctiRequest.getCtiHav()) ||
						Objects.isNull(ctiRequest.getCtiHav().getCtiAqrs()) ||
						ctiRequest.getCtiHav().getCtiAqrs().isEmpty()) {
					return null;
				}

				List<CTIAQRRequest> ctiHavRequestList = ctiRequest.getCtiHav().getCtiAqrs();
				String bl4qtHav = ctiHavRequestList.get(mynoaschild - 1).getAiQuizRun();

				return createOrUpdate(BpContentItem.builder()
						.code(String.join("-", cup.getCode(), CTTEnum.BL4QT.getCode()))
						.myCtt(CTTEnum.BL4QT.getCode())
						.myLo(bl4qtHav)
						.published(true)
						.build());
			case RL_45MI:
				ctiUlc.setName(ctiRequest.getCtiRLOM().getName());
				ctiUlc = createOrUpdate(ctiUlc);

				createOrUpdate(BpContentItem.builder()
						.myParent(ctiUlc.getCode())
						.code(String.join("-", ctiUlc.getCode(), CTTEnum.RECORDED_LINK.getCode()))
						.myCtt(CTTEnum.RECORDED_LINK.getCode())
						.myValueSet(ctiRequest.getCtiRLOM().getVideoLink())
						.joinUrl(ctiRequest.getCtiRLOM().getVideoLink())
						.published(true)
						.build());
				break;
			case PC_40MI:
				ctiUlc.setName(ctiRequest.getCtiPc().getName());
				ctiUlc.setDuration(ctiRequest.getCtiPc().getDuration());
				ctiUlc = createOrUpdate(ctiUlc);

				break;
			case CQR_AA:
				if (Objects.isNull(ctiRequest.getCtiPc()) ||
						Objects.isNull(ctiRequest.getCtiPc().getBl4Qts()) ||
						ctiRequest.getCtiPc().getBl4Qts().isEmpty()) {
					return null;
				}

				List<CTIBl4QtRequest> listBl4qtCqr = ctiRequest.getCtiPc().getBl4Qts();
				String bl4qtCqr = listBl4qtCqr.get(mynoaschild - 1).getBl4Qt();

				return createOrUpdate(BpContentItem.builder()
						.code(String.join("-", cup.getCode(), CTTEnum.BL4QT.getCode()))
						.myCtt(CTTEnum.BL4QT.getCode())
						.myLo(bl4qtCqr)
						.published(true)
						.build());
			case CQS_AA:
				if (Objects.isNull(ctiRequest.getCtiPc()) ||
						Objects.isNull(ctiRequest.getCtiPc().getBl4Qts()) ||
						ctiRequest.getCtiPc().getBl4Qts().isEmpty()) {
					return null;
				}

				List<String> listBl5qpCqr = ctiRequest.getCtiPc().getBl4Qts().get(parentNo - 1).getBl5Qps();
				String bl5qpCqr = listBl5qpCqr.get(mynoaschild - 1);

				return createOrUpdate(BpContentItem.builder()
						.code(String.join("-", cup.getCode(), CTTEnum.BL5QP.getCode()))
						.myCtt(CTTEnum.BL5QP.getCode())
						.myBl5Qp(bl5qpCqr)
						.published(true)
						.build());
			default:
				return null;
		}
		return ctiUlc;
	}

	@Override

	public BpContentItem findByCode(String code) {
		return repository.findFirstByCode(code).orElse(null);
	}


	private Integer convertStringToInteger(String input) {
		try {
			return Integer.valueOf(input.replaceAll("[^0-9]", ""));
		} catch (Exception e) {
			log.error("CONVERT STRING TO INTEGER ERROR {}", input);
			return 0;
		}
	}

	public List<RQSProjection> getRQSOrCQSInfo(Collection<String> ulcCodes) {
		return repository.getRQSInfo(ulcCodes);
	}

	@Override
	public BpContentItem findUlcCti(String ulc) {
		return repository.findUlcCti(ulc);
	}

	@Override
	public List<String> getAQRCti(Collection<String> ulcCodes) {
		return repository.getAQR1OrCQRCti(ulcCodes);
	}

	@Override
	public List<AQR1InfoProjection> getAQR1Cti(Collection<String> ulcCodes) {
		return repository.getAQR1Cti(ulcCodes);
	}

	@Override
	public List<AQR2InfoProjection> getAQR2Cti(Collection<String> ulcCodes) {
		return repository.getAQR2Cti(ulcCodes);
	}

	@Override
	public List<CtiSlideInfoProjection> getUlcSlideUrls(String ulcCode) {
		return repository.getUlcSlideUrls(ulcCode);
	}

	@Override
	public List<DQSProjection> getDqsInfo(List<String> ulcCodes) {
		return repository.getDqsInfo(ulcCodes);
	}

	@Override
	public String getUlcVideoUrl(String ulcCode) {
		return repository.getUlcVideoUrl(ulcCode);
	}

	public List<BpContentItem> findByCtiParentAndCtt(String cti, String ctt) {
		return repository.findAllByMyParentAndPublishedTrueAndMyCtt(cti, ctt);
	}

	@Override
	public PC40MIInfoProjection getPC40MICti(String ulc) {
		return repository.getPC40MICti(ulc);
	}

	@Override
	public List<String> getCQR(Collection<String> ulcCodes) {
		return repository.getAQR1OrCQRCti(ulcCodes);
	}

	@Override
	public List<ContentItem> findByParent(String rootCtiCode) {
		return repository.findAllByParent(rootCtiCode);
	}
}
