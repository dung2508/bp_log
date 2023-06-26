package vn.edu.clevai.bplog.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.service.data.importation.MultipleImportService;

import java.util.List;

@EnableAsync
@Component
@Slf4j
public class ImportCTIScheduler {
	@Autowired
	@Qualifier("MultipleImportService")
	private MultipleImportService multipleImportService;

	@Value("${gsheet.importing.om.crpp.sheet.url}")
	private String omCrppUrl;

	@Value("${gsheet.importing.om.crpp.sheet.names}")
	private List<String> omCrppSheetNames;

	@Value("${gsheet.importing.om.ssl.sheet.url}")
	private String omSslUrl;

	@Value("${gsheet.importing.om.ssl.sheet.names}")
	private List<String> omSslSheetNames;

	@Value("${gsheet.importing.bc_pm_po.crpp.sheet.url}")
	private String bcPMPOCrppUrl;

	@Value("${gsheet.importing.bc_pm_po.crpp.sheet.names}")
	private List<String> bcPMPOCrppSheetNames;

	@Value("${gsheet.importing.bc_pm_po.ssl.sheet.url}")
	private String bcPMPOSslUrl;

	@Value("${gsheet.importing.bc_pm_po.ssl.sheet.names}")
	private List<String> bcPMPOSslSheetNames;

	@Scheduled(cron = "0 0 17 * * *")
	@Async

	public void migrateSSLOM() throws Exception {
		log.info("migrateSSLOM");
		multipleImportService.importList(omSslUrl, omSslSheetNames, "SSL");

		log.info("migrateSSLBCPMPO");
		multipleImportService.importList(bcPMPOSslUrl, bcPMPOSslSheetNames, "SSL");
	}

	@Scheduled(cron = "0 0 18 * * *")
	@Async

	public void migrateCRPPOM() throws Exception {
		log.info("migrateCRPPOM");
		multipleImportService.importList(omCrppUrl, omCrppSheetNames, "CRPP");

		log.info("migrateCRPPBCPMPO");
		multipleImportService.importList(bcPMPOCrppUrl, bcPMPOCrppSheetNames, "CRPP");
	}
}
