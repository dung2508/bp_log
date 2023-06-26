package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.model.GeneralPageResponse;
import vn.edu.clevai.common.proxy.lms.payload.response.ClassInfoResponse;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.Cep100LmsService;
import vn.edu.clevai.bplog.service.PermanentClagMigrationService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PermanentClagMigrationServiceImpl implements PermanentClagMigrationService {
	@Autowired
	private Cep100LmsService cep100LmsService;

	@Autowired
	private BpClagClassgroupService bpClagClassgroupService;

	@Autowired
	@Lazy
	private PermanentClagMigrationService permanentClagMigrationService;

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("Migrate-CLAG-PERM")
	public BpClagClassgroupResponse migrateClag(String xclass) {
		BpClagClassgroupResponse bpClagClassgroupResponse = bpClagClassgroupService.getCLAGPERMFromX(xclass);

		return bpClagClassgroupService.createOrUpdatePermanentClag(
				bpClagClassgroupResponse.getCode(),
				bpClagClassgroupResponse.getMypt(),
				bpClagClassgroupResponse.getMygg(),
				bpClagClassgroupResponse.getMydfdl(),
				bpClagClassgroupResponse.getMywso(),
				ClagType.PERMANANT.getCode(),
				bpClagClassgroupResponse.getXclass(),
				bpClagClassgroupResponse.getMaxtotalstudents()
		);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("Migrate-CLAG-PERM")
	public void migrateClagPage(Integer page, Integer size) {
		GeneralPageResponse<ClassInfoResponse> p = cep100LmsService.getXPermanentClasses(page, size);

		permanentClagMigrationService.migrateClags(p.getContent());
	}

	@Override
	public List<BpClagClassgroupResponse> migrateClags(List<ClassInfoResponse> xclasses) {
		List<BpClagClassgroupResponse> output = new ArrayList<>();

		for (ClassInfoResponse xclass : xclasses) {
			try {
				output.add(permanentClagMigrationService.migrateClag(xclass.getClassCode()));
			} catch (Exception e) {
				log.error("Could not migrate data for xclass = " + xclass.getClassCode(), e);
			}
		}

		return output;
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("Migrate-CLAG-PERM")
	public void migrateAllClags() {
		int page = 1;
		int size = 100;

		while (true) {
			GeneralPageResponse<ClassInfoResponse> xclasses = cep100LmsService.getXPermanentClasses(page, size);

			log.info("Migrate-CLAG-PERM page {} / {}", page, xclasses.getTotalPages());

			permanentClagMigrationService.migrateClags(xclasses.getContent());

			page++;
			if (xclasses.isLast()) {
				break;
			}
		}
	}
}
