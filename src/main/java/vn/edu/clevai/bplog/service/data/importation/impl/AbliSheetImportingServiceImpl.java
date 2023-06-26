package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.sheet.AbliSheetDTO;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("AbliSheetImportingServiceImpl")
public class AbliSheetImportingServiceImpl extends AbstractGheetExtractorService<AbliSheetDTO> {
	@Value("")
	private String gsheetId;

	@Value("Tổng hợp")
	private String sheetName;

	@Value("!A6:AO")
	private String range;


	@Autowired
	CurriculumPeriodService curriculumPeriodService;

	public AbliSheetImportingServiceImpl() {
	}

	@Override
	protected String getSpreadsheetSheetID() {
		log.info("Import from google sheet id: {}", gsheetId);
		return gsheetId;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<AbliSheetDTO> listData) throws Exception {
		List<Boolean> results = new ArrayList<>();
		curriculumPeriodService.saveCurriculumAbli(spreadsheetCode, name, listData);
		return results;
	}


	@Override
	public String getServiceName() {
		return "AbliSheetImportingServiceImpl";
	}


	@Override
	protected String combineSheetNameAndRange(String sheet) {
		return Objects.isNull(sheet) ? sheetName.concat(range) : sheet.concat(range);
	}


	@Override
	protected AbliSheetDTO extractFromRowData(List<Object> row) throws Exception {
		int OFFSET = 1;
		String bl3Code = row.size() >= 1 && row.get(1 - OFFSET) != null ? (String) row.get(1 - OFFSET) : null;
		String c1QType = row.size() >= 4 && row.get(4 - OFFSET) != null ? (String) row.get(4 - OFFSET) : null;
		String c1Bl4Code = row.size() >= 9 && row.get(9 - OFFSET) != null ? (String) row.get(9 - OFFSET) : null;
		String c1Bl5Code = row.size() >= 13 && row.get(13 - OFFSET) != null ? (String) row.get(13 - OFFSET) : null;
		String c2QType = row.size() >= 23 && row.get(23 - OFFSET) != null ? (String) row.get(23 - OFFSET) : null;
		String c2Bl4Code = row.size() >= 28 && row.get(28 - OFFSET) != null ? (String) row.get(28 - OFFSET) : null;
		String c2Bl5Code = row.size() >= 32 && row.get(32 - OFFSET) != null ? (String) row.get(32 - OFFSET) : null;

		AbliSheetDTO abliSheetDTO = AbliSheetDTO.builder()
				.bl3Code(bl3Code)
				.c1QType(c1QType).c1Bl4Code(c1Bl4Code).c1Bl5Code(c1Bl5Code)
				.c2QType(c2QType).c2Bl4Code(c2Bl4Code).c2Bl5Code(c2Bl5Code)
				.build();

		return abliSheetDTO;
	}
}
