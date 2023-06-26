package vn.edu.clevai.bplog.service.data.importation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @param <T>
 * @author trungnt9
 */

@Slf4j
public abstract class AbstractImportService<T> implements ImportService {
	@Override
	public void doImport(String spreadsheetCode, String name, String spreadsheetUrl) throws Exception {
		beforeRun(getServiceName());

		String spreadsheetId = getSpreadsheetSheetID();

		if (!Objects.isNull(spreadsheetId)) {
			List<T> data = extractFromGSheet(spreadsheetId, spreadsheetUrl);
			if (!CollectionUtils.isEmpty(data) || data != null) {
				log.info("Service {}, spreadsheet {}, Record Size {}", getServiceName(),
						spreadsheetId, data.size());
				doImport(spreadsheetCode, name, data);
			}
		}
		afterRun(getServiceName());
	}

	/**
	 * Before run. Use to initiate
	 */
	protected abstract void beforeRun(String serviceName) throws Exception;

	/**
	 * @return
	 */
	protected abstract String getSpreadsheetSheetID();

	/**
	 * @param spreadsheetId
	 * @return
	 * @throws Exception
	 */
	protected abstract List<T> extractFromGSheet(String spreadsheetId, String spreadsheetUrl) throws Exception;

	/**
	 * @param listData
	 * @throws Exception
	 */
	protected abstract List<Boolean> doImport(String spreadsheetCode, String name, List<T> listData) throws Exception;

	/**
	 * Do finalize for import method
	 */
	protected abstract void afterRun(String serviceName);
}
