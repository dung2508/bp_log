package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.sheet.AblsSheetDTO;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;
import vn.edu.clevai.bplog.service.data.importation.ImportService;
import vn.edu.clevai.common.api.model.DebuggingDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("CrppImportingServiceImpl")
public class CrppImportingServiceImpl extends AbstractGheetExtractorService<AblsSheetDTO> {
	@Value("")
	private String gsheetId;

	@Value("")
	private List<String> sheetNames;

	@Value("!A6:V")
	private String range;

	@Autowired
	@Qualifier("AbliSheetImportingServiceImpl")
	private ImportService abliImportService;

	@Autowired
	@Qualifier("CoqSheetImportingServiceImpl")
	private ImportService coqImportService;

	@Autowired
	@Qualifier("CtiPcImportService")
	private ImportService ctiPcImportService;

	@Autowired
	private CurriculumPeriodService curriculumPeriodService;

	public CrppImportingServiceImpl() {
	}

	@Override
	protected String getSpreadsheetSheetID() {
		log.info("Import from google sheet id: {}", gsheetId);
		return gsheetId;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<AblsSheetDTO> listData) throws Exception {
		List<Boolean> results = new ArrayList<>();

		listData.forEach(d -> {
			try {
				if (d.getAbliCode() != null && !d.getAbliCode().isEmpty()) {
					curriculumPeriodService.saveAbliName(d.getAbliCode(), d.getShiftName());
				}
			} catch (Exception e) {
				log.error("saveAbliName error {} {}", d.getAbliCode(), DebuggingDTO.build(e));
			}

			try {
				if (d.getAbliLink() != null && !d.getAbliLink().isEmpty()) {
					abliImportService.doImport(d.getAbliCode(), d.getShiftName(), d.getAbliLink());
				}
			} catch (Exception e) {
				log.error("saveAbli error {} {}", d.getAbliCode(), DebuggingDTO.build(e));
			}

			try {
				if (d.getCoqLink() != null && !d.getCoqLink().isEmpty()) {
					coqImportService.doImport(d.getCoqCode(), d.getShiftName(), d.getCoqLink());
				}
			} catch (Exception e) {
				log.error("saveCoq error {} {}", d.getAbliCode(), DebuggingDTO.build(e));
			}

			try {
				if (d.getCtiPcLink() != null && !d.getCtiPcLink().isEmpty()) {
					ctiPcImportService.doImport(d.getCtiPcCode(), d.getShiftName(), d.getCtiPcLink());
				}
			} catch (Exception e) {
				log.error("saveCTIPc error {} {}", d.getAbliCode(), DebuggingDTO.build(e));
			}
		});

		return results;
	}


	@Override
	public String getServiceName() {
		return "CrppImportingServiceImpl";
	}


	@Override
	protected String combineSheetNameAndRange(String sheet) {
		return sheet.concat(range);
	}


	@Override
	protected AblsSheetDTO extractFromRowData(List<Object> row) throws Exception {
		int OFFSET = 1;
		String shiftName = row.size() >= 4 && row.get(4 - OFFSET) != null ? (String) row.get(4 - OFFSET) : null;

		String abliCode = row.size() >= 9 && row.get(9 - OFFSET) != null ? (String) row.get(9 - OFFSET) : null;
		String abliLink = row.size() >= 10 && row.get(10 - OFFSET) != null ? (String) row.get(10 - OFFSET) : null;

		String coqCode = row.size() >= 19 && row.get(19 - OFFSET) != null ? (String) row.get(19 - OFFSET) : null;
		String coqLink = row.size() >= 20 && row.get(20 - OFFSET) != null ? (String) row.get(20 - OFFSET) : null;

		String ctiPcCode = row.size() >= 21 && row.get(21 - OFFSET) != null ? (String) row.get(21 - OFFSET) : null;
		String ctiPcLink = row.size() >= 22 && row.get(22 - OFFSET) != null ? (String) row.get(22 - OFFSET) : null;

		return AblsSheetDTO.builder()
				.shiftName(shiftName)
				.abliCode(abliCode).abliLink(abliLink)
				.coqCode(coqCode).coqLink(coqLink)
				.ctiPcCode(ctiPcCode).ctiPcLink(ctiPcLink)
				.build();
	}
}
