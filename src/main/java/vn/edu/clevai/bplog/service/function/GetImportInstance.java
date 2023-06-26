package vn.edu.clevai.bplog.service.function;

import vn.edu.clevai.bplog.service.data.importation.ImportService;

@FunctionalInterface
public interface GetImportInstance {
	ImportService getInstance(String sheetName);
}
