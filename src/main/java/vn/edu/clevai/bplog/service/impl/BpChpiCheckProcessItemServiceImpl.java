package vn.edu.clevai.bplog.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.logDb.BpChpiCheckProcessItem;
import vn.edu.clevai.bplog.payload.response.logdb.BpChptCheckProcessTempResponse;
import vn.edu.clevai.bplog.service.BpChpiCheckProcessItemService;
import vn.edu.clevai.bplog.service.BpService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BpChpiCheckProcessItemServiceImpl implements BpChpiCheckProcessItemService {

	private final BpService bpService;

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("initiateChecking")
	public List<String> prepareChecking(
			String cuiEventCode,
			String cti1Code,
			String cti2Code,
			String cti3Code,
			String toSendEmail
	) throws Exception {

		// findCHPT4
		BpChptCheckProcessTempResponse chpt = bpService.findCHPT4(cuiEventCode);

		// createCHPI
		BpChpiCheckProcessItem chpi = bpService.createBpCHPI(
				chpt.getChptCode(),
				cti1Code,
				cti2Code,
				cti3Code,
				cuiEventCode,
				toSendEmail
		);

		// assignCHRI
		return bpService.bpAssignChri(chpi.getCode());

	}
}