package vn.edu.clevai.bplog.service.impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.entity.BpClagClassgroup;
import vn.edu.clevai.bplog.entity.BpDfgeDifficultget;
import vn.edu.clevai.bplog.repository.BpDfgeDifficultgetRepository;
import vn.edu.clevai.bplog.service.BpClagClassgroupService;
import vn.edu.clevai.bplog.service.BpDfgeDifficultgetService;
import vn.edu.clevai.bplog.service.Cep100LmsService;
import vn.edu.clevai.common.api.constant.enumtype.ClagType;
import vn.edu.clevai.common.api.exception.ConflictException;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.api.util.DateUtils;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfgeDifficultgetResponse;

import java.sql.Timestamp;
import java.util.List;

@Service
public class BpDfgeDifficultgetServiceImpl implements BpDfgeDifficultgetService {
	@Autowired
	@Lazy
	private BpClagClassgroupService bpClagClassgroupService;

	@Autowired
	private BpDfgeDifficultgetRepository bpDfgeDifficultgetRepository;

	@Autowired
	private Cep100LmsService cep100LmsService;

	@Override
	@WriteUnitTestLog
	public BpDfgeDifficultgetResponse getDFGEFromX(String xDfge) {
		BpDfgeDifficultget bpDfgeDifficultget = bpDfgeDifficultgetRepository.findByCode(xDfge)
				.orElseThrow(() -> new NotFoundException("Could not find any BpDfgeDifficultget using code = " + xDfge));

		return BpDfgeDifficultgetResponse
				.builder()
				.code(bpDfgeDifficultget.getCode())
				.description(bpDfgeDifficultget.getDescription())
				.name(bpDfgeDifficultget.getName())
				.build();
	}

	@Override
	public BpDfgeDifficultget findByCode(String code) {
		return bpDfgeDifficultgetRepository
				.findByCode(code)
				.orElseThrow(() -> new NotFoundException("Could not find BpDfgeDifficultget using code = " + code));
	}

	@Override
	@SneakyThrows
	@WriteUnitTestLog
	public String findXDFGE(String xsessiongroup, String xcash) {
		Timestamp liveAt = DateUtils.parse(xcash);

		return cep100LmsService.getDscSessionGroupDetails(xsessiongroup, liveAt).getCategory();
	}

	@Override
	@WriteUnitTestLog
	public BpDfgeDifficultgetResponse getCLAGDYN_DFGE(String clagdynCode) {
		BpClagClassgroup bpClagClassgroup = bpClagClassgroupService.findByCode(clagdynCode);

		if (!bpClagClassgroup.getClagtype().equalsIgnoreCase(ClagType.DYNAMIC.getCode())) {
			throw new ConflictException("clagtype of BpClagClassgroup (code = " + clagdynCode + ") is PERM, not DYN");
		}
		String xDfge = findXDFGE(bpClagClassgroup.getXsessiongroup(), bpClagClassgroup.getXcash());

		return getDFGEFromX(xDfge);
	}

	@Override
	public List<BpDfgeDifficultget> findAll() {
		return bpDfgeDifficultgetRepository.findAll();
	}

}
