package vn.edu.clevai.bplog.service.data.importation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.dto.sheet.OnRampProductCRPPDTO;
import vn.edu.clevai.bplog.service.CurriculumPeriodService;
import vn.edu.clevai.bplog.service.data.importation.AbstractGheetExtractorService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("CrppMpImportingServiceImpl")
public class CrppMpImportingServiceImpl extends AbstractGheetExtractorService<OnRampProductCRPPDTO> {

	@Value("")
	private String gsheetId;

	@Value("")
	private List<String> sheetNames;

	private String range = "!A5:G";

	@Autowired
	private CurriculumPeriodService curriculumPeriodService;

	private static final List<String> SHOULD_EMPTY = Arrays.asList("N/A", "NA");

	@Override
	protected String combineSheetNameAndRange(String sheetName) {
		return sheetName.concat(range);
	}

	@Override
	protected OnRampProductCRPPDTO extractFromRowData(List<Object> rowData) throws Exception {
		OnRampProductCRPPDTO dto = new OnRampProductCRPPDTO();
		for (int i = 0; i < rowData.size(); i++) {
			switch (i) {
				case 0:
					dto.setLessonName(parseString(rowData.get(i)));
					break;

				case 1:
					dto.setShiftName(parseString(rowData.get(i)));
					break;

				case 2:
					dto.setVideoLink(parseString(rowData.get(i)));
					break;

				case 3:
					dto.setSessionsC1(parseString(rowData.get(i)));
					break;

				case 4:
					dto.setSessionsC2(parseString(rowData.get(i)));
					break;

				case 5:
					String data5 = parseString(rowData.get(i));
					dto.setBl4c1(Arrays.stream(data5.trim().split(";")).collect(Collectors.toSet()));
					break;

				case 6:
					String data6 = parseString(rowData.get(i));
					dto.setBl4c2(Arrays.stream(data6.trim().split(";")).collect(Collectors.toSet()));
					break;

				default:
					break;

			}
		}
		return dto;
	}

	@Override
	protected String getSpreadsheetSheetID() {
		log.info("Import from google sheet id: {}", gsheetId);
		return gsheetId;
	}

	@Override
	protected List<Boolean> doImport(String spreadsheetCode, String name, List<OnRampProductCRPPDTO> listData) throws Exception {
		log.info("On {} Receive objs listData size = {} ", name, CollectionUtils.isEmpty(listData) ? 0 : listData.size());
		curriculumPeriodService.saveCurriculumHWOm(spreadsheetCode, name, listData);
		return Arrays.asList(Boolean.TRUE);
	}

	@Override
	public String getServiceName() {
		return "CrppMpImportingServiceImpl";
	}

	public <T> T parseObject(T clazz, Object o) {
		if (o == null) return null;
		if (clazz == String.class && SHOULD_EMPTY.contains(o.toString())) {
			o = "";
		}
		return (T) o;
	}

	public String parseString(Object o) {
		String toString = Optional.ofNullable(o).orElse("").toString();
		return SHOULD_EMPTY.contains(toString) ? "" : toString;
	}

}
