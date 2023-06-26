package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.dto.sheet.CurriculumProgramDTO;
import vn.edu.clevai.bplog.dto.sheet.TeachingProgramDTO;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;
import vn.edu.clevai.bplog.service.data.importation.ImportService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("CurriculumProgramImportServiceImpl")
public class CurriculumProgramImportServiceImpl extends AbstractGheetExtractorService<TeachingProgramDTO> {

	@Value("${gsheet.importing.M03-CRPP.sheet.name:M03-CRPS_040123}")
	private String sheetName;

	private String range = "!A6:H";

	private String spreadsheetSheetID = "";


	@Override
	protected String combineSheetNameAndRange(String sheetName) {
		return sheetName.concat(range);
	}

	@Override
	protected TeachingProgramDTO extractFromRowData(List<Object> rowData) throws Exception {
		TeachingProgramDTO dto = new TeachingProgramDTO();
		for (int i = 0; i < rowData.size(); i++) {
			switch (i) {
				case 0:
					dto.setTimeWeek(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				case 1:
					dto.setCurrWeek(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				case 2:
					dto.setCurrShift(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				case 3:
					dto.setBl3QGroup(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				case 4:
					dto.setBL4QType(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				case 5:
					dto.setMsnTE(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				case 6:
					dto.setS21Skill(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				case 7:
					dto.setS21Content(Optional.ofNullable(rowData.get(i)).orElse("").toString());
					break;

				default:
					break;

			}
		}

		return dto;
	}

	@Override
	protected String getSpreadsheetSheetID() {
		return spreadsheetSheetID;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<TeachingProgramDTO> listData) throws Exception {
		log.info("[CurriculumProgramImportServiceImpl] Size of list data + {}", CollectionUtils.isEmpty(listData) ? 0 : listData.size());
		// TODO: 22/05/2023 do something
		return Arrays.asList(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
	}

	@Override
	public String getServiceName() {
		return "CurriculumProgramImportServiceImpl";
	}

}
