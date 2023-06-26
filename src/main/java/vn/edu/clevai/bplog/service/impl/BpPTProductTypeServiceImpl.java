package vn.edu.clevai.bplog.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.dto.bp.PtGgDfdlDTO;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.entity.projection.UsiDutyPJ;
import vn.edu.clevai.bplog.repository.BpPTProductTypeRepository;
import vn.edu.clevai.bplog.repository.BpUSIDutyRepository;
import vn.edu.clevai.bplog.service.BpPTProductTypeService;
import vn.edu.clevai.bplog.service.CalendarPeriodService;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BpPTProductTypeServiceImpl implements BpPTProductTypeService {

	private final BpPTProductTypeRepository productTypeRepository;
	private final BpUSIDutyRepository bpUSIDutyRepository;

	private final CalendarPeriodService calendarPeriodService;

	@Override
	public List<BpPTProductType> findAllByCode(List<String> codes) {
		return productTypeRepository.findAllByCodeIn(codes);
	}

	@Override
	public List<BpPTProductType> findAll() {
		return productTypeRepository.findAllByPublishedTrueOrderByCode();
	}

	@Override
	public PtGgDfdlDTO findGGDFDLByProductCode(String capCode, String pt, String register) {
		StringBuilder registerLike = new StringBuilder("%").append(register).append("%");
		CalendarPeriod cap = calendarPeriodService.findByCode(capCode);
		List<UsiDutyPJ> fromDB = bpUSIDutyRepository.findAllByPT(cap.getStartTime(), pt, registerLike.toString());
		Set<PtGgDfdlDTO.GG> ggList = new TreeSet<>(Comparator.comparing(PtGgDfdlDTO.GG::getCode));
		fromDB.forEach(m -> {
			Set<PtGgDfdlDTO.DFDL> dfdlSet = new HashSet<>();
			try {
				Set<String> split = Arrays.stream(m.getMydfdl().split(",")).collect(Collectors.toSet());
				split.forEach(dfdlItem -> {
					dfdlSet.add(
							PtGgDfdlDTO.DFDL.builder().code(dfdlItem).ggCode(m.getMygg()).build()
					);
				});
			} catch (Exception e) {
				log.error("Split error with message {}", e.getMessage());
			}
			ggList.add(PtGgDfdlDTO.GG.builder()
					.ptCode(pt)
					.code(m.getMygg())
					.dfdls(dfdlSet).build()
			);
		});

		return PtGgDfdlDTO.builder()
				.pt(pt)
				.ggs(ggList)
				.build();
	}

	@Override
	public PtGgDfdlDTO findGGByPT(String pt, String register) {
		StringBuilder registerLike = new StringBuilder("%").append(register).append("%");
		List<UsiDutyPJ> fromDB = bpUSIDutyRepository.findAllByPT(new Timestamp(System.currentTimeMillis()), pt, registerLike.toString());
		Set<PtGgDfdlDTO.GG> ggList = new TreeSet<>(Comparator.comparing(PtGgDfdlDTO.GG::getCode));
		fromDB.forEach(m -> {
			Set<PtGgDfdlDTO.DFDL> dfdlSet = new HashSet<>();
			try {
				Set<String> split = Arrays.stream(m.getMydfdl().split(",")).collect(Collectors.toSet());
				split.forEach(dfdlItem -> {
					dfdlSet.add(
							PtGgDfdlDTO.DFDL.builder().code(dfdlItem).ggCode(m.getMygg()).build()
					);
				});
			} catch (Exception e) {
				log.error("Split error with message {}", e.getMessage());
			}
			ggList.add(PtGgDfdlDTO.GG.builder()
					.ptCode(pt)
					.code(m.getMygg())
					.dfdls(dfdlSet).build()
			);
		});

		return PtGgDfdlDTO.builder()
				.pt(pt)
				.ggs(ggList)
				.build();
	}

}
