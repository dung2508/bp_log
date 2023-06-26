package vn.edu.clevai.bplog.service.data.importation.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vn.edu.clevai.bplog.common.CodeGenerator;
import vn.edu.clevai.bplog.common.enumtype.TermEnum;
import vn.edu.clevai.bplog.dto.sheet.UsidRegisterSO5DTO;
import vn.edu.clevai.bplog.service.AccYearService;
import vn.edu.clevai.bplog.service.BpUSIDutyService;
import vn.edu.clevai.bplog.service.data.importation.AbstractImportService;
import vn.edu.clevai.bplog.utils.GoogleAuthorizationUtilities;
import vn.edu.clevai.bplog.utils.Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service("UsidRegisterSO5Impl")
public class UsidRegisterSO5Impl extends AbstractImportService<UsidRegisterSO5DTO> {

	@Value("")
	private String gsheetId;

	@Value("")
	private String sheetName;

	private String range = "!A1:AW36";

	private Sheets sheetService;

	@Autowired
	private AccYearService accYearService;

	@Autowired
	private BpUSIDutyService bpUSIDutyService;

	private Map<String, String> configData = new HashMap<>();
	private Map<Integer, String> ptMap = new HashMap<>();
	private Map<Integer, String> cashstaMap = new HashMap<>();
	private Map<Integer, String> wsoMap = new HashMap<>();
	private Map<Integer, String> capMap = new HashMap<>();
	private Map<Integer, String> lcpMap = new HashMap<>();

	private static final String TICK = "x";
	private static final String BPE = "BPE";
	private static final String LCET = "LCET";
	private static final String UST = "UST";
	private static final String ACC_YEAR = "accYear";
	private static final String TERM = "term";

	private static final Integer PT_ROW = 9;
	private static final Integer CASHSTA_ROW = 11;
	private static final Integer WSO_ROW = 12;
	private static final Integer CAP_ROW = 15;
	private static final Integer LCP_ROW = 16;


	@Override
	public String getServiceName() {
		return "Usid Register SO5 Import Service";
	}

	protected String combineSheetNameAndRange(String sheet) {
		return Objects.isNull(sheet) ? sheetName.concat(range) : sheet.concat(range);
	}

	@Override
	protected void beforeRun(String serviceName) throws Exception {
		log.info("Before run import for service {}", getServiceName());
		NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
		sheetService = new Sheets.Builder(netHttpTransport, GsonFactory.getDefaultInstance(),
				GoogleAuthorizationUtilities.getCredentials(netHttpTransport))
				.setApplicationName(serviceName).build();
	}

	@Override
	protected String getSpreadsheetSheetID() {
		log.info("Import from google sheet id: {}", gsheetId);
		return gsheetId;
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
	protected List<UsidRegisterSO5DTO> extractFromGSheet(String spreadsheetId, String spreadsheetUrl) throws Exception {
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
		List<UsidRegisterSO5DTO> listData = new ArrayList<>();

		int recordCount = 1;
		for (List<Object> row : values) {
			if (recordCount <= 5) {
				String name = row.size() >= 1 && Objects.nonNull(row.get(0)) ? (String) row.get(0) : null;
				String value = row.size() >= 1 && Objects.nonNull(row.get(1)) ? (String) row.get(1) : null;
				configData.put(name, value);
			}

			if (recordCount == PT_ROW) {
				//update config Data
				if (Objects.nonNull(configData.get("Date"))) {
					String dateString = configData.get("Date");
					Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
					String accYearCode = accYearService.findByTime(date).getCode();
					configData.put(ACC_YEAR, accYearCode);
					String myTerm = Utils.getMyTermFromTime(date);
					configData.put(TERM, myTerm);
				}

				for (int column = 0; column < row.size(); column++) {
					try {
						if (!StringUtils.isEmpty(row.get(column))) {
							String value = (String) row.get(column);
							ptMap.put(column, value);
						}
					} catch (IndexOutOfBoundsException ex) {
						System.out.println(ex);
					}
				}
			}

			if (recordCount == CASHSTA_ROW) {
				for (int column = 0; column < row.size(); column++) {
					if (Objects.nonNull(row.get(column))) {
						String value = (String) row.get(column);
						cashstaMap.put(column, value);
					}
				}
			}

			if (recordCount == WSO_ROW) {
				for (int column = 0; column < row.size(); column++) {
					if (Objects.nonNull(row.get(column))) {
						String value = (String) row.get(column);
						wsoMap.put(column, value);
					}
				}
			}

			if (recordCount == CAP_ROW) {
				for (int column = 0; column < row.size(); column++) {
					if (Objects.nonNull(row.get(column))) {
						String value = (String) row.get(column);
						capMap.put(column, value);
					}
				}
			}

			if (recordCount == LCP_ROW) {
				for (int column = 0; column < row.size(); column++) {
					if (Objects.nonNull(row.get(column))) {
						String value = (String) row.get(column);
						lcpMap.put(column, value);
					}
				}
			}

			if (recordCount > 16) {
				try {
					List<UsidRegisterSO5DTO> t = extractFromRowData(row);
					if (Objects.nonNull(t)) {
						listData.addAll(t);
					}
				} catch (Exception e) {
					log.error("Error when extract data in record " + recordCount, e);
				}
			}
			recordCount++;
		}
		log.info("List data import {}", listData.size());
		return listData;
	}

	@Override
	protected void afterRun(String serviceName) {
		log.info("After run import for service {}", serviceName);
	}


	private List<UsidRegisterSO5DTO> extractFromRowData(List<Object> rowData) throws Exception {
		if (CollectionUtils.isEmpty(rowData)) {
			return null;
		}

		List<UsidRegisterSO5DTO> usidRegisterSO5DTOList = new ArrayList<>();
		try {
			String mychrt = rowData.get(0).toString();
			String myUsi = rowData.get(2).toString();
			for (int column = 3; column < rowData.size(); column++) {
				if (TICK.equals(rowData.get(column))) {
					String myBpe = configData.get(BPE);
					String myLcet = configData.get(LCET);
					String myUst = configData.get(UST);
					String myPt = getMyPT(column);
					String accYear = configData.get(ACC_YEAR);
					String term = configData.get(TERM);
					String myWso = wsoMap.get(column);
					String cash = cashstaMap.get(column);
					String myLcp = lcpMap.get(column);
					String myCap = capMap.get(column);

					UsidRegisterSO5DTO registerSO5DTO = UsidRegisterSO5DTO.builder()
							.mybpe(myBpe)
							.myLcet(myLcet)
							.myUst(myUst)
							.mypt(myPt)
							.mycassstr(cash)
							.mywso(myWso)
							.myCap(myCap)
							.myLcp(myLcp)
							.myChrt(mychrt)
							.myUsi(myUsi)
							.myterm(term)
							.myaccyear(accYear)
							.published(Boolean.TRUE)
							.code(CodeGenerator.buildNormalCode(myBpe, myLcet, myUsi, myLcp, myPt, myCap))
							.build();
					usidRegisterSO5DTOList.add(registerSO5DTO);
				}
			}

		} catch (Exception e) {
			log.error("Exception when extract data", e);
		}
		return usidRegisterSO5DTOList;
	}


	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<UsidRegisterSO5DTO> listData) throws Exception {
		bpUSIDutyService.doImport(listData);
		return new ArrayList<>();
	}

	private String getMyPT(int column) {
		//because group column in google sheet
		if (column == 0 && StringUtils.isEmpty(ptMap.get(column)))
			return null;
		if (StringUtils.isEmpty(ptMap.get(column))) {
			column--;
			return getMyPT(column);
		}
		return ptMap.get(column);
	}

}
