package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.dto.sheet.CtiPCSheetDTO;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service("CtiPcImportService")
public class CtiPcImportServiceImpl extends AbstractGheetExtractorService<CtiPCSheetDTO> {

	@Value("")
	private String gsheetId;

	@Value("26/06/23 - 02/07/23")
	private String sheetName;

	@Value("!A4:H")
	private String range;


	@Autowired
	CurriculumPeriodService curriculumPeriodService;

	@Override
	protected String combineSheetNameAndRange(String sheetName) {
		return Objects.isNull(sheetName) ? this.sheetName.concat(range) : sheetName.concat(range);
	}

	@Override
	protected CtiPCSheetDTO extractFromRowData(List<Object> rowData) throws Exception {
		CtiPCSheetDTO dto = new CtiPCSheetDTO();
		for (int i = 0; i < rowData.size(); i++) {
			Object obj = rowData.get(i);
			switch (i) {
				case 0:
					dto.setTimeWeek(Optional.ofNullable(obj).orElse("").toString());
					break;

				case 1:
					dto.setCurrWeek(Optional.ofNullable(obj).orElse("").toString());
					break;

				case 2:
					dto.setContentCode(Optional.ofNullable(obj).orElse("").toString());
					break;

				case 3:
					dto.setContentTitle(Optional.ofNullable(obj).orElse("").toString());
					break;

				case 4:
					dto.setGiveOn(Optional.ofNullable(obj).orElse("").toString());
					break;

				case 5:
					dto.setDuration(Optional.ofNullable(obj).orElse("").toString());
					break;

				case 6:
					dto.setCtiBL4QT(Optional.ofNullable(obj).orElse("").toString());
					break;

				case 7:
					dto.setQPieceNum(Optional.ofNullable(obj).orElse("").toString());
					break;

				default:
					break;

			}
		}
		return dto;
	}

	@Override
	protected String getSpreadsheetSheetID() {
		return gsheetId;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<CtiPCSheetDTO> listData) throws Exception {
		List<Boolean> results = new ArrayList<>();
		log.info("Size of data {}", CollectionUtils.isEmpty(listData) ? 0 : listData.size());
		//23_24-MT-OM-G3-WK5-DY1
		curriculumPeriodService.saveCurriculumPC(spreadsheetCode, name, listData);
		return results;
	}

	@Override
	public String getServiceName() {
		return "CtiPcImportServiceImpl";
	}
}
