package vn.edu.clevai.bplog.service.data.importation;

/**
 * @author trungnt9
 */
public interface ImportService {

	String getServiceName();

	void doImport(String spreadsheetCode, String name, String spreadsheetUrl) throws Exception;
}
