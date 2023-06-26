package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.dto.cat5.StudentValidDTO;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;
import vn.edu.clevai.bplog.service.data.importation.migrate.Cat5MigrateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class Cats5XdealSheetImportServiceImpl extends AbstractGheetExtractorService<StudentValidDTO> {

	private final String gsheetId;
	private final String sheetName;
	@Value("!A2:I")
	private String range;
	private final Cat5MigrateService migrateService;

	public Cats5XdealSheetImportServiceImpl(Cat5MigrateService service, String sheetId, String sheetName) {
		this.migrateService = service;
		this.gsheetId = sheetId;
		this.sheetName = sheetName;
	}

	@Override
	public String getServiceName() {
		log.info("Import CATS5 from google sheet id: {}", gsheetId);
		return gsheetId;
	}

	@Override
	protected String combineSheetNameAndRange(String sheet) {
		return Objects.isNull(sheet) ? sheetName.concat(range) : sheet.concat(range);
	}

	@Override
	protected StudentValidDTO extractFromRowData(List<Object> rowData) throws Exception {
		if (CollectionUtils.isEmpty(rowData)) {
			return null;
		}
		try {
			Long podId = rowData.size() >= 1 && Objects.nonNull(rowData.get(0)) ? Long.valueOf((String) rowData.get(0))
					: null;
			Long studentId = rowData.size() >= 5 && Objects.nonNull(rowData.get(4))
					? Long.valueOf((String) rowData.get(4))
					: null;
			String username = rowData.size() >= 2 && Objects.nonNull(rowData.get(1)) ? (String) rowData.get(1) : null;
			String product = rowData.size() >= 9 && Objects.nonNull(rowData.get(8)) ? (String) rowData.get(8) : null;
			String firstname = rowData.size() >= 6 && Objects.nonNull(rowData.get(5)) ? (String) rowData.get(5) : null;
			String lastname = rowData.size() >= 7 && Objects.nonNull(rowData.get(6)) ? (String) rowData.get(6) : null;
			if (Objects.nonNull(podId) && !StringUtils.isBlank(username)) {
				StudentValidDTO student = StudentValidDTO.builder().username(username).podId(podId).studentId(studentId)
						.product(product).firstname(firstname).lastname(lastname).build();
				return student;
			}
		} catch (Exception e) {
			log.error("Exception when extract data", e);
		}
		return null;
	}

	@Override
	protected String getSpreadsheetSheetID() {
		log.info("Import from google sheet id: {}", gsheetId);
		return gsheetId;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<StudentValidDTO> listData) throws Exception {
		migrateService.doImport(listData);
		return new ArrayList<Boolean>();
	}

}
