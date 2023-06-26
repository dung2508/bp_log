package vn.edu.clevai.bplog.service.impl;

import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.entity.logDb.BpCheckListItem;
import vn.edu.clevai.bplog.entity.logDb.BpCheckListTemp;
import vn.edu.clevai.bplog.payload.request.BpChliUpdateRequest;
import vn.edu.clevai.bplog.repository.bplog.BpCheckListItemRepository;
import vn.edu.clevai.bplog.service.BpChService;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpChliResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BpChServiceImpl implements BpChService {

	@Autowired
	private BpCheckListItemRepository chliRepo;

	@Override
	@Transactional
	public BpChliResponse getCHLTFromCHLI(String chliCode) {
		BpCheckListItem item = chliRepo.findFirstByCode(chliCode).orElse(null);
		if (Objects.nonNull(item)) {
			return toChltResponse(item);
		}
		return null;
	}

	private BpChliResponse toChltResponse(BpCheckListItem chli) {
		BpCheckListTemp chlt = chli.getMyParentChlt();
		BpChliResponse response = BpChliResponse.builder().id(chli.getId()).name(chli.getName()).code(chli.getCode())
				.myParentChlt(Objects.nonNull(chlt) ? chlt.getCode() : null)
				.myParentChltName(Objects.nonNull(chlt) ? chlt.getName() : null)
				.myChsi(Objects.nonNull(chli.getMyChsi()) ? chli.getMyChsi().getCode() : null)
				.scoreType1(chli.getScore1Type()).scoreType2(chli.getScore2Type()).score1(chli.getScore1())
				.score2(chli.getScore2()).chltDo(chli.getChltDo()).doNot(chli.getDoNot())
				.correctExample(chli.getCorrectExample())
				.description(chli.getDescription())
				.incorrectExample(chli.getIncorrectExample())
				.childrens(new ArrayList<BpChliResponse>()).build();
		if (!CollectionUtils.isEmpty(chli.getSubChli())) {
			for (BpCheckListItem child : chli.getSubChli()) {
				response.getChildrens().add(toChltResponse(child));
			}
		}
		return response;
	}

	@Override
	@Transactional
	public void updateByListChli(List<BpChliUpdateRequest> listChli) {
		Assert.notEmpty(listChli, "List chli must be not null");
		List<BpCheckListItem> bpChliItems = chliRepo
				.findAllByListChliCode(listChli.stream().map(BpChliUpdateRequest::getCode).collect(Collectors.toList()));
		for (BpChliUpdateRequest chliReq : listChli) {
			BpCheckListItem item = bpChliItems.stream().filter(k -> k.getCode().equalsIgnoreCase(chliReq.getCode()))
					.findAny().orElse(null);
			if (Objects.nonNull(item)) {
				item.setScore1(chliReq.getScore1());
				item.setScore2(chliReq.getScore2());

				if (Objects.nonNull(chliReq.getDescription())) {
					item.setDescription(chliReq.getDescription());
				}
			} else {
				log.warn("Cant found chli code {} from database", chliReq.getCode());
			}
		}
	}

}
