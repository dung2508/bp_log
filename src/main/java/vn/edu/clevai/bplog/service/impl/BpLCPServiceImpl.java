package vn.edu.clevai.bplog.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.common.enumtype.LCLEnum;
import vn.edu.clevai.bplog.common.enumtype.TrainingTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.UsiTypeEnum;
import vn.edu.clevai.bplog.entity.BpLCP;
import vn.edu.clevai.bplog.enums.Lck;
import vn.edu.clevai.bplog.repository.BpLCPRepository;
import vn.edu.clevai.bplog.service.BpLCPService;
import vn.edu.clevai.common.api.exception.NotFoundException;

import java.util.List;
import java.util.Objects;

@Service
public class BpLCPServiceImpl implements BpLCPService {

	private final BpLCPRepository bpLCPRepository;

	public BpLCPServiceImpl(BpLCPRepository bpLCPRepository) {
		this.bpLCPRepository = bpLCPRepository;
	}

	@Override
	public List<BpLCP> findAllForSchedulingMc() {
		return bpLCPRepository.findAllByMyLctAndMyLckAndMyParentLct(
				LCLEnum.LCMN.getName(),
				Lck.MC.name(),
				LCLEnum.LCPK.getName()
		);
	}

	@Override
	public List<BpLCP> findAllForSchedulingWc() {
		return bpLCPRepository.findAllByMyLctAndMyLckAndMyParentLct(
				LCLEnum.LCWK.getName(),
				Lck.WC.name(),
				LCLEnum.LCPK.getName()
		);
	}

	@Override
	public BpLCP findByCode(String code) {
		return bpLCPRepository.findByCode(code)
				.orElseThrow(() -> new NotFoundException("LCP using code = " + code));
	}

	@Override
	public BpLCP findFirstByMyptAndMylct(String mypt, String mylct) {
		return bpLCPRepository.findFirstByMyptAndMylct(mypt, mylct)
				.orElseThrow(
						() -> new NotFoundException("Couldn't find LCP by mypt: " + mypt + " mylct: " + mylct)
				);
	}

	@Override
	public BpLCP findShiftLcp(String mypt, String mylct) {
		return bpLCPRepository.findByMyptAndMylctAndLcperiodnoLike(mypt, mylct, "C%")
				.orElseThrow(
						() -> new NotFoundException("Could not find shift LCP by mypt: " + mypt + " mylct: " + mylct)
				);
	}

	@Override
	public List<BpLCP> findLCPKids(String parentLct) {
		return bpLCPRepository.findLCPKids(parentLct);
	}

	@Override
	public BpLCP findLcpFromPtUst(String pt, String ust, String lcl) {
		String lck = "";
		if (Objects.equals(ust, UsiTypeEnum.DTE.getName())) lck = TrainingTypeEnum.DILIVE.getCode();
		if (Objects.equals(ust, UsiTypeEnum.GTE.getName())) lck = TrainingTypeEnum.GET.getCode();
		if (Objects.equals(lcl, LCLEnum.LCSS.getName())) {
			return bpLCPRepository.findLcpSsFromPtLck(pt, lck).orElseThrow(
					() -> new NotFoundException("Coun't find lcp by pt,ust,lcl : " + pt + "," + ust + "," + lcl)
			);
		}
		return null;
	}

	@Override
	public List<BpLCP> findLCPSHByPTFromBP(String pt) {
		return bpLCPRepository.findLCPSHByPTFromBP(pt);
	}

	@Override
	public BpLCP findLcpFromPtLck(String pt, String lck, String lcl) {
		if (Objects.equals(lcl, LCLEnum.LCSS.getName())) {
			return bpLCPRepository.findLcpSsFromPtLck(pt, lck).orElse(null);
		}
		if (Objects.equals(lcl, LCLEnum.LCSH.getName())) {
			return bpLCPRepository.findLcpShByPt(pt, lck).orElse(null);
		}
		return null;
	}

	@Override
	public BpLCP findLcpshByPtAndLct(String pt, String lctShift) {
		return bpLCPRepository.findLcpshByPtAndLct(pt, lctShift).orElseThrow(
				() -> new NotFoundException("Could not find any LCP using pt and lct = " + pt + lctShift)
		);
	}

	@Override
	public BpLCP findLcpSSByPtAndLct(String pt, String lctSession) {
		return bpLCPRepository.findLcpSSByPtAndLct(pt, lctSession).orElseThrow(
				() -> new NotFoundException("Could not find any LCP using pt and lct = " + pt + lctSession)
		);
	}

	@Override
	public BpLCP findLcpSSByPtAndGE(String pt) {
		return bpLCPRepository.findLcpSSByPtAndGE(pt).orElseThrow(
				() -> new NotFoundException("Could not find any LCP using pt and lct = " + pt)
		);
	}

	@Override
	public BpLCP findByParentLctAndLct(String lctParent, String lct) {
		return bpLCPRepository.findByMylctAndMylctparentAndPublishedTrue(lct, lctParent).orElseThrow(
				() -> new NotFoundException("Could not find any LCP using pt and parentLct = " + lct + lctParent)
		);
	}

	@Override
	public String findPtFromLcpAndLck(String lcp, String lck) {
		return bpLCPRepository.findPtFromLcpLck(lcp, lck).orElse(null);
	}

	@Override
	public List<BpLCP> findLCWK(String pt, String lcl) {
		return bpLCPRepository.findLCWK(pt, lcl);
	}

	public List<BpLCP> findByMylctparentToSchedule(String parentLct) {
		return bpLCPRepository.findByMylctparentToSchedule(parentLct);
	}

	@Override
	public List<BpLCP> findLCPKids(String parentLct, String lcl) {
		return bpLCPRepository.findLCPKids(parentLct, lcl);
	}

	@Override
	public BpLCP findLcpSsForOMByLCK(String lck) {
		return bpLCPRepository.findLcpSsForOMByLck(lck).orElse(null);
	}

	@Override
	public BpLCP findWcByPt(String pt) {
		return bpLCPRepository.findWcByPt(pt)
				.orElseThrow(
						() -> new NotFoundException("Could not find any WC-1WK lcp for pt = " + pt)
				);
	}

	@Override
	public List<BpLCP> findWcByPts(List<String> pts) {
		return bpLCPRepository.findWcByPts(pts);
	}

	@Override
	public BpLCP findUlcSL(String pt, String lck) {
		return bpLCPRepository.findUlcSL(pt, lck);
	}

}
