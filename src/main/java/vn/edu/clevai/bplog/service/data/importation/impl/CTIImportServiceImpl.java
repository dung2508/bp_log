package vn.edu.clevai.bplog.service.data.importation.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import vn.edu.clevai.bplog.dto.sheet.CtiDTO;
import vn.edu.clevai.bplog.service.data.importation.AbstractImportService;
import vn.edu.clevai.bplog.utils.GoogleAuthorizationUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CTIImportServiceImpl extends AbstractImportService<CtiDTO> {

	private Sheets sheetService;

	private String myparentcti;

	private String sheetName;

	private String range;

	public CTIImportServiceImpl(String myparentcti) {
		this.myparentcti = myparentcti;
		this.range = "!A4:H";
	}

	@Override
	public String getServiceName() {
		return "CTI Import Service";
	}

	@Override
	protected void beforeRun(String serviceName) throws Exception {
		NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
		sheetService = new Sheets.Builder(netHttpTransport, GsonFactory.getDefaultInstance(),
				GoogleAuthorizationUtilities.getCredentials(netHttpTransport)).setApplicationName(serviceName).build();
	}

	@Override
	protected String getSpreadsheetSheetID() {
		return "CTIImportServiceImpl-ID";
	}

	@Override
	protected List<CtiDTO> extractFromGSheet(String spreadId, String spreadsheetUrl) throws Exception {
		// Sample Link:
		// https://docs.google.com/spreadsheets/d/1SVPzr3Tq98I_bWXs6jewCAQPan-x8PldfT16b391IWc/edit#gid=1214460009
		String ssheetId = getSheetId(spreadsheetUrl);
		Asserts.notEmpty(ssheetId, "Spread Sheet Id must not empty");
		List<CtiDTO> results = new ArrayList<>();
		beforeRun(getServiceName());
		if (StringUtils.isNoneBlank(ssheetId)) {
			String spreadsheetId = getGSheetCode(spreadsheetUrl);
			if (!StringUtils.isBlank(spreadsheetId)) {
				Sheet sheet = sheetService.spreadsheets().get(spreadsheetId).execute().getSheets().stream()
						.filter(s -> s.getProperties().getSheetId().toString().equals(ssheetId)).findAny().orElse(null);

				if (Objects.nonNull(sheet)) {
					sheetName = sheet.getProperties().getTitle();
				}

				log.info("Start get from sheet id 2 {} {}", spreadsheetId, sheetName);
				ValueRange response = sheetService.spreadsheets().values()
						.get(spreadsheetId, combineSheetNameAndRange(sheetName)).execute();
				List<List<Object>> values = response.getValues();
				if (values == null || values.isEmpty()) {
					return results;
				}
				for (List<Object> row : values) {
					results.add(parserCTI(row));
				}
			} else {
				log.error("Cant extract spreadsheetId of url {}", spreadsheetUrl);
			}
		}
		return results;
	}

	private CtiDTO parserCTI(List<Object> row) {
		String timeWeek = row.size() >= 1 && Objects.nonNull(row.get(0)) ? (String) row.get(0) : null;
		Integer currWeek = row.size() >= 2 && Objects.nonNull(row.get(1)) ? new Integer((String) row.get(1)) : null;
		String code = row.size() >= 3 && Objects.nonNull(row.get(2)) ? (String) row.get(2) : null;
		String title = row.size() >= 4 && Objects.nonNull(row.get(3)) ? (String) row.get(3) : null;
		Integer giveOn = row.size() >= 5 && Objects.nonNull(row.get(4)) ? new Integer((String) row.get(4)) : null;
		Integer duration = row.size() >= 6 && Objects.nonNull(row.get(5)) ? new Integer((String) row.get(5)) : null;
		String bl4qt = row.size() >= 7 && Objects.nonNull(row.get(6)) ? (String) row.get(6) : null;
		Integer qpeice = row.size() >= 8 && Objects.nonNull(row.get(7)) ? new Integer((String) row.get(7)) : null;
		return CtiDTO.builder().timeWeek(timeWeek).currWeek(currWeek).code(code).title(title).giveOn(giveOn)
				.duration(duration).bl4QT(bl4qt).qPieceNum(qpeice).build();
	}

	private String getSheetId(String spreadsheetUrl) {
		String[] parts = spreadsheetUrl.split("#gid=");
		return parts[1];
	}

	private String getGSheetCode(String spreadsheetUrl) {
		// https://docs.google.com/spreadsheets/d/1SVPzr3Tq98I_bWXs6jewCAQPan-x8PldfT16b391IWc/edit#gid=1214460009
		String regex = "(.*)(\\/d\\/)(.*)(\\/.*)";
		Pattern p = Pattern.compile(regex);
		Matcher match = p.matcher(spreadsheetUrl);
		if (match.matches()) {
			return match.group(3);
		}
		return null;
	}

	protected String combineSheetNameAndRange(String sheet) {
		return Objects.isNull(sheet) ? sheetName.concat(range) : sheet.concat(range);
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<CtiDTO> listData) throws Exception {
		return null;
	}

	@Override
	protected void afterRun(String serviceName) {

	}

	public static void main(String args[]) throws Exception {
		String url = "https://docs.google.com/spreadsheets/d/1SVPzr3Tq98I_bWXs6jewCAQPan-x8PldfT16b391IWc/edit#gid=1214460009";
		String regex = "(.*)(\\/d\\/)(.*)(\\/.*)";
		Pattern p = Pattern.compile(regex);
		Matcher match = p.matcher(url);
		if (match.matches()) {
			System.out.println(match.group(3));
		}

	}
}
