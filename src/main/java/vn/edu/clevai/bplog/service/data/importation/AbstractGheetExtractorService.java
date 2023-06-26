package vn.edu.clevai.bplog.service.data.importation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import lombok.extern.slf4j.Slf4j;
import vn.edu.clevai.bplog.utils.GoogleAuthorizationUtilities;

@Slf4j
public abstract class AbstractGheetExtractorService<T> extends AbstractImportService<T> {

	private Sheets sheetService;

	public AbstractGheetExtractorService() {
	}

	@Override
	protected final void beforeRun(String serviceName) throws Exception {
		log.info("Before run import for service {}", getServiceName());
		NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
		sheetService = new Sheets.Builder(netHttpTransport, GsonFactory.getDefaultInstance(),
				GoogleAuthorizationUtilities.getCredentials(netHttpTransport))
				.setApplicationName(serviceName).build();
	}

	@Override
	protected final List<T> extractFromGSheet(String spreadsheetId, String spreadsheetUrl) throws Exception {
		log.info("Start get from sheet id 1 {}", spreadsheetId);
		String sheetName = null;

		if (Objects.nonNull(spreadsheetUrl)) {
			spreadsheetId = getSpreadsheetId(spreadsheetUrl);
			String sheetId = getSheetId(spreadsheetUrl);
			Sheet sheet = sheetService.spreadsheets()
					.get(spreadsheetId).execute().getSheets().stream()
					.filter(s -> s.getProperties().getSheetId().toString().equals(sheetId))
					.findAny().orElse(null);

			if (Objects.nonNull(sheet)) {
				sheetName = sheet.getProperties().getTitle();
			}
		}
		log.info("Start get from sheet id 2 {} {}", spreadsheetId, sheetName);
		ValueRange response = sheetService.spreadsheets().values()
				.get(spreadsheetId, combineSheetNameAndRange(sheetName)).execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			return null;
		}
		List<T> listData = new ArrayList<T>();

		int recordCount = 1;
		for (List<Object> row : values) {
			try {
				T t = extractFromRowData(row);
				if (Objects.nonNull(t)) {
					listData.add(t);
				}
			} catch (Exception e) {
				log.error("Error when extract data in record " + recordCount, e);
			}
			recordCount++;
		}
		return listData;
	}

	private String getSpreadsheetId(String spreadsheetUrl) {
		String[] parts = spreadsheetUrl.split("spreadsheets/d/");
		String result;
		if (parts[1].contains("/")) {
			String[] parts2 = parts[1].split("/");
			result = parts2[0];
		} else {
			result = parts[1];
		}
		return result;
	}

	private String getSheetId(String spreadsheetUrl) {
		String[] parts = spreadsheetUrl.split("#gid=");
		return parts[1];
	}

	@Override
	protected final void afterRun(String serviceName) {
		log.info("After run import for service {}", serviceName);
	}


	protected abstract String combineSheetNameAndRange(String sheetName);

	protected abstract T extractFromRowData(List<Object> rowData) throws Exception;

	protected void doUpdateGsheets(String range, String gsheetId, List<List<Object>> values,
								   Sheets service, String sheetName) throws Exception {
		ValueRange body = new ValueRange().setValues(values);
		service.spreadsheets().values().update(gsheetId, sheetName.concat(range), body).setValueInputOption("RAW").execute();
	}

}
