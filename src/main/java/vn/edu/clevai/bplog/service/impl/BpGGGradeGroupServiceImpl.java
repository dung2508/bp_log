package vn.edu.clevai.bplog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.entity.BpGGGradeGroup;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.repository.BpGGGradeGroupRepository;
import vn.edu.clevai.bplog.service.BpGGGradeGroupService;
import vn.edu.clevai.bplog.service.BpUsiUserItemService;
import vn.edu.clevai.bplog.service.Cep100LmsService;
import vn.edu.clevai.bplog.service.Cep100UserService;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpGgGradegroupResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.util.List;

@Service
public class BpGGGradeGroupServiceImpl implements BpGGGradeGroupService {
	@Autowired
	private BpGGGradeGroupRepository bpGGGradeGroupRepository;

	@Autowired
	private Cep100UserService cep100UserService;

	@Autowired
	private Cep100LmsService cep100LmsService;

	@Autowired
	private BpUsiUserItemService bpUsiUserItemService;

	@Override
	public BpGGGradeGroup findByCode(String code) {
		return bpGGGradeGroupRepository.findByCode(code)
				.orElseThrow(
						() -> new NotFoundException("Could not find any BpGGGradeGroup using code = " + code)
				);
	}

	@Override
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_GG_FROM_X
	)
	public BpGgGradegroupResponse getGGFromX(Long xGrade_id) {
		String code = Cep100TransformUtils.toGGCode(xGrade_id);

		BpGGGradeGroup bpGGGradeGroup = findByCode(code);

		return BpGgGradegroupResponse
				.builder()
				.id(bpGGGradeGroup.getId())
				.name(bpGGGradeGroup.getName())
				.mycashsta(bpGGGradeGroup.getCashStart())
				.code(bpGGGradeGroup.getCode())
				.build();
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.FIND_XGG
	)
	public Long findXGG(String xst) {
		UserAccountResponse user = cep100UserService.getByUsername(xst);

		return cep100LmsService.findXGG(user.getId());
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(
			BPLogProcessEnum.GET_ST_GG
	)
	public BpGgGradegroupResponse getST_GG(String ST) {
		BpUsiUserItem bpUsiUserItem = bpUsiUserItemService.findByCode(ST);
		return getGGFromX(findXGG(bpUsiUserItem.getUsername()));
	}

	@Override
	public List<BpGGGradeGroup> findAll() {
		return bpGGGradeGroupRepository.findAll();
	}

	@Override
	public BpGGGradeGroup findById(Integer id) {
		return bpGGGradeGroupRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Could not find any BpGGGradeGroup using id = " + id));
	}
}
