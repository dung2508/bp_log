package vn.edu.clevai.bplog.service.data.importation.impl;

import com.google.api.services.sheets.v4.model.Sheet;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.data.importation.ImportService;
import vn.edu.clevai.bplog.service.data.importation.MultipleImportService;

import java.util.List;

@Service("MultipleImportService")
public class MultipleImportServiceImpl implements MultipleImportService {

	@Override
	@SneakyThrows
	public void importList(String url, List<String> sheetName, String importType) {
		String spreadsheetId = getSpreadsheetsId(url);
		List<Sheet> sheets = getSheets(spreadsheetId);
		ImportService importService = getInstance(importType);
		
		sheets.stream()
				.filter(item -> sheetName.contains(item.getProperties().getTitle()))
				.forEach(sheet -> {
					try {
						String title = sheet.getProperties().getTitle();
						String rebuildUrl = rebuildUrl(url, sheet.getProperties().getSheetId().toString());
						importService.doImport("", title, rebuildUrl);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

}
