package vn.edu.clevai.bplog.service.data.importation.migrate;

import java.util.List;

import vn.edu.clevai.bplog.dto.cat5.StudentValidDTO;

public interface Cat5MigrateService {

	void doMigrate(String csvFilePath);

	void doImport(List<StudentValidDTO> listData) throws Exception;

}
