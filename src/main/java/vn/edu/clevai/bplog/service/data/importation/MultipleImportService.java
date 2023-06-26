package vn.edu.clevai.bplog.service.data.importation;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import vn.edu.clevai.bplog.utils.GoogleAuthorizationUtilities;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public interface MultipleImportService {

	default ImportService getInstance(String sheet) {
		return ImportServiceFactory.getInstanceOf(sheet);
	}

	default Sheets createSheetsService() throws IOException, GeneralSecurityException {
		NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
		return new Sheets.Builder(netHttpTransport, GsonFactory.getDefaultInstance(),
				GoogleAuthorizationUtilities.getCredentials(netHttpTransport))
				.setApplicationName("Demo sperasheet").build();
	}

	default String getSpreadsheetsId(String url) {
		String[] parts = url.split("spreadsheets/d/");
		String spreadsheetId;
		if (parts[1].contains("/edit#gid")) {
			spreadsheetId = parts[1].substring(0, parts[1].indexOf("/edit#gid"));
		} else {
			spreadsheetId = parts[1].replace("/", "");
		}

		return spreadsheetId;
	}

	default List<Sheet> getSheets(String spreadsheetId) throws GeneralSecurityException, IOException {
		Sheets sheetsService = createSheetsService();
		Sheets.Spreadsheets.Get request = sheetsService.spreadsheets().get(spreadsheetId);
		request.setRanges(new ArrayList<>());
		Spreadsheet response = request.execute();
		return response.getSheets();
	}

	default String rebuildUrl(String url, String sheetId) {
		String outPut = "";
		if (url.contains("/edit#gid")) {
			outPut = url.substring(0, url.indexOf("/edit#gid"));
		} else {
			if (url.lastIndexOf("/") == url.length() - 1) {
				outPut = url.substring(0, url.length() - 1);
			} else {
				outPut = url;
			}
		}
		outPut = outPut.concat("/edit#gid=").concat(sheetId);
		return outPut;
	}

	void importList(String toReplace, List<String> sheetName, String importType);
}
