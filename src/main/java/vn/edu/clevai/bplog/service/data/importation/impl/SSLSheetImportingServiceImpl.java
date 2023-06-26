package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.sheet.SSLSheetDTO;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("SSLSheetImportingServiceImpl")
public class SSLSheetImportingServiceImpl extends AbstractGheetExtractorService<SSLSheetDTO> {
	@Value("")
	private String gsheetId;

	@Value("")
	private String sheetName;

	@Value("!A6:AK")
	private String range;

	@Autowired
	CurriculumPeriodService curriculumPeriodService;

	public SSLSheetImportingServiceImpl() {
	}

	@Override
	protected String getSpreadsheetSheetID() {
		log.info("Import from google sheet id: {}", gsheetId);
		return gsheetId;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<SSLSheetDTO> listData) throws Exception {
		List<Boolean> results = new ArrayList<>();
		curriculumPeriodService.saveCurriculumSsl(listData);
		return results;
	}


	@Override
	public String getServiceName() {
		return "SSLSheetImportingServiceImpl";
	}


	@Override
	protected String combineSheetNameAndRange(String sheet) {
		return Objects.isNull(sheet) ? sheetName.concat(range) : sheet.concat(range);
	}


	@Override
	protected SSLSheetDTO extractFromRowData(List<Object> rowData) throws Exception {
		SSLSheetDTO dto = new SSLSheetDTO();
		for (int i = 0; i < rowData.size(); i++) {
			Object obj = rowData.get(i);
			switch (i) {
				case 3:
					dto.setSslName(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 8:
					dto.setSslCode(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 9:
					dto.setDlC1Te(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 10:
					dto.setDlC1St(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 11:
					dto.setDlC2Te(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 12:
					dto.setDlC2St(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 13:
					dto.setDlgC1GEATe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 14:
					dto.setDlgC1GEBTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 15:
					dto.setDlgC1GECTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 16:
					dto.setDlgC1GEDTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 17:
					dto.setDlgC2GEATe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 18:
					dto.setDlgC2GEBTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 19:
					dto.setDlgC2GECTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 20:
					dto.setDlgC2GEDTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 21:
					dto.setGesC1GEATe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 22:
					dto.setGesC1GEASt(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 23:
					dto.setGesC1GEBTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 24:
					dto.setGesC1GEBSt(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 25:
					dto.setGesC1GECTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 26:
					dto.setGesC1GECSt(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 27:
					dto.setGesC1GEDTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 28:
					dto.setGesC1GEDSt(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 29:
					dto.setGesC2GEATe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 30:
					dto.setGesC2GEASt(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 31:
					dto.setGesC2GEBTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 32:
					dto.setGesC2GEBSt(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 33:
					dto.setGesC2GECTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 34:
					dto.setGesC2GECSt(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 35:
					dto.setGesC2GEDTe(Objects.isNull(obj) ? null : (String) obj);
					break;
				case 36:
					dto.setGesC2GEDSt(Objects.isNull(obj) ? null : (String) obj);
					break;
				default:
					break;
			}
		}
		return dto;
	}
}
