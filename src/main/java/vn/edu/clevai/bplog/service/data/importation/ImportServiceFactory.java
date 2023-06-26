package vn.edu.clevai.bplog.service.data.importation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static vn.edu.clevai.bplog.utils.ReflectionUtil.getFieldVal;

@Service("ImportServiceFactory")
public class ImportServiceFactory {

	private final ImportService teachingProgramImport;
	private final ImportService curriculumProgramImportServiceImpl;
	private final ImportService crppMpImportingServiceImpl;
	private final ImportService crppImportingServiceImpl;
	private final ImportService sslImportingServiceImpl;
	private static final Map<String, ImportService> MAP_SHEET_IMPORT_INSTANCE = new HashMap<>();

	private final String CRPP_MP = "CRPP_MP";
	private final String CRPP = "CRPP";
	private final String SSL = "SSL";

	@Autowired
	public ImportServiceFactory(@Qualifier("TeachingProgramImportServiceImpl") ImportService teachingProgramImport,
								@Qualifier("CurriculumProgramImportServiceImpl") ImportService curriculumProgramImportServiceImpl,
								@Qualifier("CrppMpImportingServiceImpl") ImportService crppMpImportingServiceImpl,
								@Qualifier("CrppImportingServiceImpl") ImportService crppImportingServiceImpl,
								@Qualifier("SSLSheetImportingServiceImpl") ImportService sslImportingServiceImpl) {
		this.teachingProgramImport = teachingProgramImport;
		this.curriculumProgramImportServiceImpl = curriculumProgramImportServiceImpl;
		this.crppMpImportingServiceImpl = crppMpImportingServiceImpl;
		this.crppImportingServiceImpl = crppImportingServiceImpl;
		this.sslImportingServiceImpl = sslImportingServiceImpl;
	}


	@PostConstruct
	private void init() throws NoSuchFieldException, IllegalAccessException {
		MAP_SHEET_IMPORT_INSTANCE.put(getFieldVal(teachingProgramImport, "sheetName"), teachingProgramImport);
		MAP_SHEET_IMPORT_INSTANCE.put(getFieldVal(curriculumProgramImportServiceImpl, "sheetName"), curriculumProgramImportServiceImpl);

		MAP_SHEET_IMPORT_INSTANCE.put(CRPP_MP, crppMpImportingServiceImpl);
		MAP_SHEET_IMPORT_INSTANCE.put(CRPP, crppImportingServiceImpl);
		MAP_SHEET_IMPORT_INSTANCE.put(SSL, sslImportingServiceImpl);
	}

	public static ImportService getInstanceOf(String sheetName) {
		return Optional.ofNullable(MAP_SHEET_IMPORT_INSTANCE.get(sheetName))
				.orElseThrow(() -> new NotFoundException(String.format("Not found sheet name %s", sheetName)));
	}

}
