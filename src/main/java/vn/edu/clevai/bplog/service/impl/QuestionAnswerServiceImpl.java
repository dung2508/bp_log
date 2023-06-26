package vn.edu.clevai.bplog.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.clevai.bplog.common.LocalValueSaving;
import vn.edu.clevai.bplog.common.enumtype.CTTEnum;
import vn.edu.clevai.bplog.common.enumtype.CtiQuestionEnum;
import vn.edu.clevai.bplog.common.enumtype.LCETCodeEnum;
import vn.edu.clevai.bplog.common.enumtype.ProductTypeEnum;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.entity.ContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpContentItem;
import vn.edu.clevai.bplog.entity.logDb.BpCuiContentUserUlc;
import vn.edu.clevai.bplog.entity.logDb.BpCuiEvent;
import vn.edu.clevai.bplog.entity.logDb.BpUniqueLearningComponent;
import vn.edu.clevai.bplog.entity.projection.AnswerAndQuestionPJ;
import vn.edu.clevai.bplog.payload.request.bp.BPCuiEventRequest;
import vn.edu.clevai.bplog.payload.request.bp.GetQuestionAnswerRequest;
import vn.edu.clevai.bplog.payload.request.bp.SubmitQARequest;
import vn.edu.clevai.bplog.payload.response.orp.AnswerAndQuestionResponse;
import vn.edu.clevai.bplog.payload.response.orp.AnswerQuestionCTIDetail;
import vn.edu.clevai.bplog.repository.bplog.BpCuiEventRepository;
import vn.edu.clevai.bplog.repository.bplog.BpUniqueLearningComponentRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.service.cuievent.CuiEventService;
import vn.edu.clevai.bplog.service.cuievent.CuiService;
import vn.edu.clevai.common.api.aws.AWSS3Service;
import vn.edu.clevai.common.api.exception.BadRequestException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang.StringUtils.isBlank;
import static vn.edu.clevai.bplog.common.enumtype.CTTEnum.CTI_AWS;
import static vn.edu.clevai.bplog.common.enumtype.CTTEnum.CTI_QTS;
import static vn.edu.clevai.bplog.common.enumtype.LCKEnum.LCK_OM_SS_QA;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

	private final AWSS3Service s3Service;
	private final BpULCService ulcService;
	private final CuiService cuiService;
	private final CuiEventService cuiEventService;
	private final ContentItemService ctiService;
	private final ObjectMapper objectMapper;
	private final CalendarPeriodService capService;
	private final BpLCPService bpLCPService;
	private final BpUsiUserItemService usiUserItemService;
	private final LocalValueSaving valueSaving;
	private static final int SEVEN_HOURS_IN_MILLI_SECONDS = 7 * 60 * 60 * 1000; //  25 200

	@Value("${clevai.cdn.host.name}")
	private String cdnHostName;

	@Value("${aws.s3.bucket.name}")
	private String bucketName;

	@Override
	@SneakyThrows
	@Transactional
	public Object createQuestion(SubmitQARequest request) {
		String cdnLink = pushFileToS3(request.getFile());
		BpUniqueLearningComponent ulc = ulcService.createUlc(ProductTypeEnum.OM.getName(),
				LCK_OM_SS_QA.getCode(),
				request.getXclass(),
				request.getCurrentUsi());
		String ctiUlcCode = String.join("-", CTTEnum.ULC.getCode(), request.getCurrentUsi(), String.valueOf(System.currentTimeMillis()));
		log.info("Create cti with code {}", ctiUlcCode);
		ctiService.createOrUpdate(
				BpContentItem.builder()
						.code(ctiUlcCode)
						.name(ctiUlcCode)
						.myCtt(CTTEnum.ULC.getCode())
						.published(true)
						.build()
		);
		valueSaving.setCuiCode("", true);
		cuiEventService.createCUIAndCuiEvent(
				ulc.getCode(), request.getCurrentUsi(), ctiUlcCode, ulc.getMyLcp().getCode(), request.getActualTimeFet(), ulc.getMyCap().getCode()
		);
		Map<String, String> mapValueImage = new HashMap<>();
		mapValueImage.put("text", request.getQuestionText());
		mapValueImage.put("file_url", cdnLink);
		String toJson = objectMapper.writeValueAsString(mapValueImage);

		String ctiChildCode = String.join("-", ctiUlcCode, CTI_QTS.getCode());
		log.info("Will create Cti for question with code {} and parent cti-code is {} ", ctiChildCode, ctiUlcCode);
		ctiService.createOrUpdate(
				BpContentItem.builder()
						.code(ctiChildCode)
						.name(ctiChildCode)
						.myParent(ctiUlcCode)
						.myValueSet(toJson)
						.myCtt(CTTEnum.CTI_QTS.getCode())
						.published(true)
						.build()
		);
		return getQuestionDetails(valueSaving.getCuiCode());
	}

	@Override
	@SneakyThrows
	@Transactional
	public Object createAnswer(SubmitQARequest request) {
		String cdnLink = pushFileToS3(request.getFile());
		BpCuiContentUserUlc cui = Optional.ofNullable(cuiService.findByCode(request.getCuiCode())).orElseThrow(
				() -> new NotFoundException(String.format("Not found cui with code %s", request.getCuiCode()))
		);
		final BpUniqueLearningComponent ulc = Optional.ofNullable(ulcService.findByCode(cui.getMyUlc().getCode())).orElseThrow(
				() -> new NotFoundException(String.format("Not found ulc with code %s ", cui.getMyUlc().getCode()))
		);

		if (cui.getMyUsiCode().equals(request.getCurrentUsi())) {
			throw new BadRequestException("Question creator and question answerer are the same account");
		}
		Timestamp cuiEPlanTime = StringUtils.isBlank(ulc.getMyCap().getCode()) ? null : capService.findByCode(ulc.getMyCap().getCode()).getStartTime();
		BPCuiEventRequest cuiEAnswer = BPCuiEventRequest.builder()
				.cuieCuicode(cui.getCode())
				.cuieMyusi(request.getCurrentUsi())
				.cuieMylcet(LCETCodeEnum.PL_SC.getCode())
				.cuieMylcp(ulc.getMyLcp().getCode())
				.cuiePlantime(cuiEPlanTime)
				.cuiePublished(true)
				.cuieActualtimeFet(request.getActualTimeFet())
				.cuieActualtimeBet(new Timestamp(System.currentTimeMillis()))
				.build();
		cuiEventService.createCuiEvent(cuiEAnswer);

		Map<String, String> mapValueImage = new HashMap<>();
		mapValueImage.put("text", Optional.ofNullable(request.getAnswerText()).orElse(""));
		mapValueImage.put("file_url", cdnLink);
		String toJson = objectMapper.writeValueAsString(mapValueImage);

		String ctiChildCode = String.join("-", cui.getMyCtiCode(), CTI_AWS.getCode());
		ctiService.createOrUpdate(
				BpContentItem.builder()
						.code(ctiChildCode)
						.name(ctiChildCode)
						.myParent(cui.getMyCtiCode())
						.myValueSet(toJson)
						.myCtt(CTI_AWS.getCode())
						.published(true)
						.build()
		);
		return getQuestionDetails(cui.getCode());
	}

	@Override
	public Page<AnswerAndQuestionResponse> getMyQuestionAnswer(GetQuestionAnswerRequest request) {
		return null;
	}

	@Override
	public Page<AnswerAndQuestionResponse> getQuestionAnswer(GetQuestionAnswerRequest request) {
		if (!isBlank(request.getStatus())) {
			CtiQuestionEnum.StatusEnum.validate(request.getStatus());
		}
		Sort sort = Sort.by(Sort.Order.desc("cti.created_at"));
		Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
		BpLCP lcp = Optional.ofNullable(bpLCPService.findUlcSL(ProductTypeEnum.OM.getName(), LCK_OM_SS_QA.getCode()))
				.orElseThrow(() -> new NotFoundException(String.format("Not found lcp with pt %s, lck %s ",
						ProductTypeEnum.OM.getName(), LCK_OM_SS_QA.getCode()))
				);
		request.setLcpLike(lcp.getCode());
		request.setFilterByTime(Objects.nonNull(request.getFromTime()) && Objects.nonNull(request.getToTime()));
		Page<AnswerAndQuestionPJ> fromDB = cuiService.findCuiQuestion(request, pageable);

		List<AnswerAndQuestionResponse> content = fromDB.getContent().parallelStream()
				.map(this::convertToQADetails).collect(Collectors.toList());

		return new PageImpl<>(content, pageable, fromDB.getTotalElements());
	}

	@Override
	public AnswerAndQuestionResponse getQuestionDetails(String cuiCode) {
		AnswerAndQuestionPJ PJ = Optional.ofNullable(cuiService.findDetailsQuestion(cuiCode)).orElseThrow(
				() -> new NotFoundException(String.format("Not found question with cui %s", cuiCode))
		);
		return convertToQADetails(PJ);
	}

	private AnswerAndQuestionResponse convertToQADetails(AnswerAndQuestionPJ PJ) {
		log.info("Will parse projection to details ");
		AnswerAndQuestionResponse converted = objectMapper.convertValue(PJ, AnswerAndQuestionResponse.class);
		List<BpCuiEvent> cuiEvents = cuiEventService.findCuiEByCui(converted.getCuiCode());

		BpCuiEvent eventByAnswerer = cuiEvents.stream().filter(fi -> !fi.getMyUsi().equals(PJ.getMyUsiCui()))
				.findFirst().orElse(null);

		BpCuiEvent eventByStudent = cuiEvents.stream().filter(fi -> fi.getMyUsi().equals(PJ.getMyUsiCui()))
				.findFirst().orElse(null);

		ctiService.findByParent(converted.getRootCtiCode())
				.forEach(cti -> {
					if (CTI_QTS.getCode().equals(cti.getContentItemType())) {
						//student submit question -> cui created by myusi = student
						AnswerQuestionCTIDetail question = convertCtiToQA(cti);
						question.setMyusi(PJ.getMyUsiCui());
						question.setUsiFullName(PJ.getMyUsiFullName());
						question.setMyust(PJ.getMyUst());
						if (eventByStudent != null) question.setCreatedAt(eventByStudent.getCreatedAt());
						converted.setQuestion(question);

					} else {
						AnswerQuestionCTIDetail answer = convertCtiToQA(cti);
						try {
							BpUsiUserItem userItem = usiUserItemService.findByUsername(eventByAnswerer.getMyUsi());
							answer.setMyusi(userItem.getUsername());
							answer.setUsiFullName(userItem.getFullname());
							answer.setMyust(userItem.getMyust());
							answer.setCreatedAt(eventByAnswerer.getEventActualTimeFet());
							answer.setCreatedAt(eventByAnswerer.getCreatedAt());
						} catch (Exception e) {
							log.error("Data loss event created by teacher is null {}", e.getMessage());
						}
						converted.setAnswer(answer);
					}
				});
		return converted;
	}

	private AnswerQuestionCTIDetail convertCtiToQA(ContentItem cti) {
		AnswerQuestionCTIDetail result = null;
		try {
			Map<String, String> toMap = objectMapper.readValue(cti.getValueSet(), Map.class);
			result = objectMapper.convertValue(cti, AnswerQuestionCTIDetail.class);
			result.setText(toMap.get("text"));
			result.setFileUrl(toMap.get("file_url"));
		} catch (Exception e) {
			log.error("Failed to parse json q&a cti, error: {}", e.getMessage());
		}
		return result;
	}

	private String pushFileToS3(MultipartFile file) {
		if (file == null) return "";
		String cdnLink = "";
		try {
			String key = "picture/".concat(file.getName())
					.concat("/").concat(file.getOriginalFilename());
			log.info("Key is:" + key);
			s3Service.putInputStreamWithContentType(bucketName, key, file.getInputStream(), file.getContentType());
			cdnLink = buildCdnURL().concat("/").concat(key.replace(" ", "%20"));
		} catch (Exception e) {
			log.error("Cant upload image file to s3", e);
			throw new RuntimeException("Cant upload image file to s3");
		}

		return cdnLink;
	}


	private String buildCdnURL() {
		return "https://".concat(cdnHostName);
	}
}
