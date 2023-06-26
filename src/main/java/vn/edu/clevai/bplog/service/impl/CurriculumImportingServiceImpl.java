package vn.edu.clevai.bplog.service.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.clevai.bplog.dto.sheet.CurriculumSheetDTO;
import vn.edu.clevai.bplog.entity.Curriculum;
import vn.edu.clevai.bplog.repository.CurriculumRepository;
import vn.edu.clevai.bplog.service.CurriculumImportingService;
import vn.edu.clevai.bplog.utils.GoogleAuthorizationUtilities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
@Slf4j
@Service
@RequiredArgsConstructor
public class CurriculumImportingServiceImpl implements CurriculumImportingService {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("1FbEngWmB03CfV8VY7_RLLdvaD_6pzGIrsYqlMkVyziY")
	private String spreadsheetId;

	@Value("G4_141022")
	private String range;

	private final CurriculumRepository curriculumRepo;

	@Override
	@SneakyThrows
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void importFromSheet() {
		try {
			NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
			Sheets sheets = new Sheets.Builder(netHttpTransport, JacksonFactory.getDefaultInstance(),
					GoogleAuthorizationUtilities.getCredentials(netHttpTransport))
					.setApplicationName(applicationName)
					.build();
			Sheets.Spreadsheets spreadsheets = sheets.spreadsheets();
			Sheets.Spreadsheets.Values values = spreadsheets.values();
			ValueRange valueRange = values.get(spreadsheetId, range).execute();
			List<List<Object>> sheet = valueRange.getValues();
			List<CurriculumSheetDTO> curriculumSheetDTOs = new ArrayList<>();
			CurriculumSheetDTO curriculumSheetDTO = CurriculumSheetDTO.builder().build();
			for (int rowIndex = 39; rowIndex < sheet.size(); rowIndex++) {
				List<Object> row = sheet.get(rowIndex);
				curriculumSheetDTO = CurriculumSheetDTO
						.builder()
						.timeWeek(StringUtils.defaultIfEmpty(String.valueOf(row.get(0)), curriculumSheetDTO.getTimeWeek()))
						.currWeek(StringUtils.defaultIfEmpty(String.valueOf(row.get(1)), curriculumSheetDTO.getCurrWeek()))
						.currShift(String.valueOf(row.get(2)))
						.qGroup(String.valueOf(row.get(3)))
						.build();
				curriculumSheetDTOs.add(curriculumSheetDTO);
			}
			for (CurriculumSheetDTO dto : curriculumSheetDTOs) {
				for (long classLevelId = 1; classLevelId <= 2; classLevelId++) {
					Optional<Curriculum> optionalCurriculum = curriculumRepo.findCurriculum(
							1L,
							8L,
							classLevelId,
							1L,
							Date.valueOf(dto.getStartDate()),
							Date.valueOf(dto.getEndDate()),
							dto.getOrdering());
					if (optionalCurriculum.isPresent()) {
						Curriculum curriculum = optionalCurriculum.get();
						curriculum.setLessonName(dto.getName());
					} else {
						Curriculum curriculum = Curriculum.builder()
								.subjectId(1L)
								.gradeId(8L)
								.classLevelId(classLevelId)
								.trainingTypeId(1L)
								.lessonCode("")
								.lessonName(dto.getName())
								.startDate(Date.valueOf(dto.getStartDate()))
								.endDate(Date.valueOf(dto.getEndDate()))
								.ordering(dto.getOrdering())
								.build();
						curriculumRepo.save(curriculum);
					}
				}
			}
			curriculumRepo.flush();
			log.info("Import successful {} curriculum", curriculumSheetDTOs.size());
		} catch (Exception e) {
			log.error("Error when importing curriculum from sheet " + spreadsheetId, e);
			throw e;
		}
	}

}
