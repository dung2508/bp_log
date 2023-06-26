package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.sheet.CoqSheetDTO;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("CoqSheetImportingServiceImpl")
public class CoqSheetImportingServiceImpl extends AbstractGheetExtractorService<CoqSheetDTO> {
	@Value("")
	private String gsheetId;

	@Value("3. Bảng mã Dilive.G")
	private String sheetName;

	@Value("!A2:N")
	private String range;

	@Autowired
	CurriculumPeriodService curriculumPeriodService;

	public CoqSheetImportingServiceImpl() {
	}

	@Override
	protected String getSpreadsheetSheetID() {
		log.info("Import from google sheet id: {}", gsheetId);
		return gsheetId;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<CoqSheetDTO> listData) throws Exception {
		List<Boolean> results = new ArrayList<>();
		curriculumPeriodService.saveCurriculumCoq(spreadsheetCode, name, listData);
		return results;
	}

	@Override
	public String getServiceName() {
		return "CoqSheetImportingServiceImpl";
	}


	@Override
	protected String combineSheetNameAndRange(String sheet) {
		return sheetName.concat(range);
	}


	@Override
	protected CoqSheetDTO extractFromRowData(List<Object> row) throws Exception {
		int OFFSET = 1;
		String dfdl = row.size() >= 1 && row.get(1 - OFFSET) != null ? (String) row.get(1 - OFFSET) : null;
		String bl4Code = row.size() >= 4 && row.get(4 - OFFSET) != null ? (String) row.get(4 - OFFSET) : "";
		String bl5Code = row.size() >= 6 && row.get(6 - OFFSET) != null ? (String) row.get(6 - OFFSET) : "";
		String bl4Difficulty = row.size() >= 7 && row.get(7 - OFFSET) != null ? (String) row.get(7 - OFFSET) : null;

		return CoqSheetDTO.builder()
				.dfdl(dfdl)
				.bl4Code(bl4Code)
				.bl5Code(bl5Code)
				.bl4Difficulty(bl4Difficulty)
				.build();
	}
}
