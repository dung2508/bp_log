package vn.edu.clevai.bplog.service.data.importation.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.sheet.CrppDTO;
import vn.edu.clevai.bplog.dto.sheet.CurriculumProgramDTO;
import vn.edu.clevai.bplog.dto.sheet.ProgramPackageInfoDTO;
import vn.edu.clevai.bplog.service.data.importation.ImportService;
import vn.edu.clevai.bplog.utils.GoogleAuthorizationUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CuriculumImportServiceImpl implements ImportService {

	@Value("1QMUxq8Z7bj30Qn0fLCfsnb7j9Xp-vMoVMWSO2p6-v9U")
	private String gsheetId;

	private String sheetName;

	@Value("!A:O")
	private String range;

	private Sheets sheetService;

	@Override
	public String getServiceName() {
		return "Curriculum-Import";
	}

	@Override
	public void doImport(String spreadsheetCode, String name, String spreadsheetUrl) throws Exception {
		beforeRun(getServiceName());
		String spreadsheetId = getSpreadsheetSheetID();
		if (StringUtils.isNoneBlank(spreadsheetId)) {
			String sheetId = getSheetId(spreadsheetUrl);
			Sheet sheet = sheetService.spreadsheets().get(spreadsheetId).execute().getSheets().stream()
					.filter(s -> s.getProperties().getSheetId().toString().equals(sheetId)).findAny().orElse(null);

			if (Objects.nonNull(sheet)) {
				sheetName = sheet.getProperties().getTitle();
			}
		}

		log.info("Start get from sheet id {} {}", spreadsheetId, sheetName);
		String rangeCombineSheetName = combineSheetNameAndRange(sheetName);
		ValueRange response = sheetService.spreadsheets().values().get(spreadsheetId, rangeCombineSheetName).execute();
		log.info("Sheet name combine: {}", rangeCombineSheetName);
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			return;
		}

		CurriculumProgramDTO curriculum = parserCurriculum(values);
		importCurriculumProgram(curriculum);
	}

	private String getSheetId(String spreadsheetUrl) {
		String[] parts = spreadsheetUrl.split("#gid=");
		return parts[1];
	}

	private void beforeRun(String serviceName) throws Exception {
		log.info("Before run import for service {}", getServiceName());
		NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
		sheetService = new Sheets.Builder(netHttpTransport, GsonFactory.getDefaultInstance(),
				GoogleAuthorizationUtilities.getCredentials(netHttpTransport)).setApplicationName(serviceName).build();
	}

	protected String combineSheetNameAndRange(String sheet) {
		return StringUtils.isBlank(sheet) ? sheetName.concat(range) : sheet.concat(range);
	}

	private String getSpreadsheetSheetID() {
		return gsheetId;
	}

	private CurriculumProgramDTO parserCurriculum(List<List<Object>> values) {
		CurriculumProgramDTO result = CurriculumProgramDTO.builder().build();
		int rIndex = 1;
		ProgramPackageInfoDTO ppDTO = ProgramPackageInfoDTO.builder().build();
		List<CrppDTO> listCrpp = new ArrayList<>();
		CrppDTO previous = null;
		for (List<Object> row : values) {
			if (rIndex <= 2) {
				parserProgramPackage(ppDTO, row, rIndex);
			}
			if (rIndex >= 6) {
				previous = parserCRPP(previous, row, rIndex);
				listCrpp.add(previous);
			}
			rIndex++;

		}
		result.setDetails(listCrpp);
		return result;
	}

	private ProgramPackageInfoDTO parserProgramPackage(ProgramPackageInfoDTO dto, List<Object> row, Integer index) {
		ProgramPackageInfoDTO nDto = ProgramPackageInfoDTO.builder().academicYear(dto.getAcademicYear())
				.grade(dto.getGrade()).subject(dto.getSubject()).trimeter(dto.getTrimeter()).build();
		if (index == 1) {
			String academicYear = (String) row.get(4);
			nDto.setAcademicYear(extractAcademicYear(academicYear));
			String trimeter = (String) row.get(6);
			nDto.setTrimeter(extractTrimeter(trimeter));
		} else {
			String subject = (String) row.get(1);
			nDto.setSubject(extractSubject(subject));
			nDto.setGrade(extractGrade((String) row.get(3)));
		}
		return nDto;
	}

	private CrppDTO parserCRPP(CrppDTO previous, List<Object> row, Integer rowIndex) {
		String timeWeek = row.size() >= 1 && Objects.nonNull(row.get(0)) ? (String) row.get(0) : null;
		if (StringUtils.isEmpty(timeWeek) && Objects.nonNull(previous)) {
			timeWeek = previous.getTimeWeek();
		}
		Integer currWeek = row.size() >= 2 && Objects.nonNull(row.get(1))
				&& StringUtils.isNoneBlank((String) row.get(1)) ? new Integer((String) row.get(1)) : null;
		if (Objects.nonNull(currWeek) && Objects.nonNull(previous)) {
			currWeek = previous.getCurrWeek();
		}
		String currShift = row.size() >= 3 && Objects.nonNull(row.get(2)) ? (String) row.get(2) : null;
		String bl3 = row.size() >= 4 && Objects.nonNull(row.get(3)) ? (String) row.get(3) : null;
		String bl4 = row.size() >= 5 && Objects.nonNull(row.get(4)) ? (String) row.get(4) : null;
		if (StringUtils.isEmpty(bl4) && Objects.nonNull(previous)) {
			bl4 = previous.getBl4();
		}
		String missionTest = row.size() >= 6 && Objects.nonNull(row.get(5)) ? (String) row.get(5) : null;
		if (StringUtils.isEmpty(missionTest) && Objects.nonNull(previous)) {
			missionTest = previous.getMissionTest();
		}
		String s21 = row.size() >= 7 && Objects.nonNull(row.get(6)) ? (String) row.get(6) : null;
		if (StringUtils.isEmpty(s21) && Objects.nonNull(previous)) {
			s21 = previous.getS21();
		}

		String s21Content = row.size() >= 8 && Objects.nonNull(row.get(7)) ? (String) row.get(7) : null;
		if (StringUtils.isEmpty(s21Content) && Objects.nonNull(previous)) {
			s21Content = previous.getS21Content();
		}
		String ctiPcLink = row.size() >= 15 && Objects.nonNull(row.get(14)) ? (String) row.get(14) : null;
		if (StringUtils.isEmpty(ctiPcLink) && Objects.nonNull(previous) && rowIndex % 2 == 1) {
			ctiPcLink = previous.getCtiPcLink();
		}
		return CrppDTO.builder().timeWeek(timeWeek).currWeek(currWeek).currShift(currShift).bl3QGroup(bl3).bl4(bl4)
				.missionTest(missionTest).s21(s21).s21Content(s21Content).ctiPcLink(ctiPcLink).build();
	}

	private void importCurriculumProgram(CurriculumProgramDTO dto) {
		List<CrppDTO> details = dto.getDetails();
		for (CrppDTO crpp : details) {
			if (StringUtils.isNoneBlank(crpp.getCtiPcLink())) {
				ImportService imService = new CTIImportServiceImpl(null);
				try {
					imService.doImport(getSheetId(crpp.getCtiPcLink()), null, crpp.getCtiPcLink());
				} catch (Exception e) {
					log.error("Exception when import CTI", e);
				}
			}
		}
	}

	private String extractAcademicYear(String input) {
		// Sample Academic Year: "Năm học (Academic Year): 2022-2023"
		return input;
	}

	private String extractTrimeter(String input) {
		// Sample trimeter: Học kì (Trimester): Chính khóa (Main)
		return input;
	}

	private String extractSubject(String input) {
		// Sample Subject: Môn (Subject): Toán (Math)
		return input;
	}

	private Integer extractGrade(String grade) {
		return Integer.parseInt("7");
	}
}
