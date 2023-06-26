package vn.edu.clevai.bplog.service.data.importation;

public interface CurriculumProgramImportService {
	void doImport(String spreadsheetCode, String name, String spreadsheetUrl) throws Exception;
}
