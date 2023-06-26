package vn.edu.clevai.bplog.service.impl;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.common.enumtype.CalendarPeriodTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.ProductTypeEnum;
import vn.edu.clevai.bplog.common.enumtype.UsiTypeEnum;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.bplog.payload.request.MigrationRequest;
import vn.edu.clevai.bplog.repository.BpUsiUserItemRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.BaseProxyService;
import vn.edu.clevai.common.proxy.bplog.constant.USTEnum;
import vn.edu.clevai.common.proxy.bplog.payload.response.AssignDoerResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpGgGradegroupResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BpUsiUserItemServiceImpl extends BaseProxyService implements BpUsiUserItemService {

	@Autowired
	private BpUsiUserItemRepository bpUsiUserItemRepository;

	@Autowired
	private Cep100UserService cep100UserService;

	@Autowired
	private BpGGGradeGroupService bpGGGradeGroupService;

	@Autowired
	private BpGgStService bpGgStService;


	@Autowired
	private CalendarPeriodService calendarPeriodService;

	@Lazy
	@Autowired
	private BpUsiUserItemService bpUsiUserItemService;

	@Autowired
	private ModelMapper modelMapper;

	private final List<String> LIST_TEACHER_UST = ImmutableList.of(
			USTEnum.DTE.getName(),
			USTEnum.GTE.getName()
	);


	@Override
	public BpUsiUserItem findByCode(String usiCode) {
		return bpUsiUserItemRepository.findByCode(usiCode)
				.orElseThrow(
						() -> new NotFoundException("Could not find any BpUsiUserItem using code = " + usiCode)
				);
	}

	@Override
	public BpUsiUserItem findByUsername(String username) {
		return bpUsiUserItemRepository
				.findByUsername(username)
				.orElseThrow(
						() -> new NotFoundException("Could not find any BpUsiUserItem using username = " + username)
				);
	}

	@Override
	@WriteUnitTestLog
	public List<BpUsiUserItemResponse> getUSI(String ust, Pageable pageable) {

		return bpUsiUserItemRepository.findByMyust(ust, pageable)
				.stream()
				.map(item -> BpUsiUserItemResponse.builder()
						.id(item.getId())
						.code(item.getCode())
						.username(item.getUsername())
						.firstname(item.getFirstname())
						.lastname(item.getLastname())
						.build())
				.collect(Collectors.toList());

	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.GET_ST_FROM_X)
	public BpUsiUserItemResponse getSTFromX(String xst) {
		UserAccountResponse userAccountResponse = cep100UserService.getByUsername(xst);

		return BpUsiUserItemResponse
				.builder()
				.code(userAccountResponse.getUsername())
				.firstname(userAccountResponse.getFirstName())
				.lastname(userAccountResponse.getLastName())
				.username(userAccountResponse.getUsername())
				.fullName(userAccountResponse.getFullName())
				.phone(userAccountResponse.getPhone())
				.build();
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setST")
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_ST
	)
	public BpUsiUserItemResponse createOrUpdateUsi(
			String code, String lastname, String firstname, String myust,
			String username, String fullname, String phone,
			String email) {
		BpUsiUserItem bpUsiUserItem = bpUsiUserItemRepository.findByCode(code)
				.orElseGet(() -> BpUsiUserItem.builder().code(code).build());

		bpUsiUserItem.setLastname(lastname);
		bpUsiUserItem.setFirstname(firstname);
		bpUsiUserItem.setMyust(myust);
		bpUsiUserItem.setUsername(username);
		bpUsiUserItem.setPhone(phone);
		bpUsiUserItem.setFullname(Objects.nonNull(fullname) ? fullname :
				String.join(" ", StringUtils.defaultIfBlank(lastname, "").trim(),
						StringUtils.defaultIfBlank(firstname, "").trim()));
		bpUsiUserItem.setEmail(email);

		bpUsiUserItem = bpUsiUserItemRepository.save(bpUsiUserItem);

		return BpUsiUserItemResponse
				.builder()
				.id(bpUsiUserItem.getId())
				.code(bpUsiUserItem.getCode())
				.username(bpUsiUserItem.getUsername())
				.lastname(bpUsiUserItem.getLastname())
				.firstname(bpUsiUserItem.getFirstname())
				.build();
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("findUSI")
	public BpUsiUserItemResponse findUSI(
			String lcetCode,
			String myust,
			String capCode,
			String chrtCode,
			String lcpCode,
			Collection<String> excludeUsi
	) {
		return bpUsiUserItemRepository.findFirstUSIByUSIDuty(
				lcetCode,
				myust,
				capCode,
				chrtCode,
				lcpCode,
				CollectionUtils.isNotEmpty(excludeUsi) ? excludeUsi : Collections.singletonList(""))
				.map(bpUsiUserItem -> BpUsiUserItemResponse.builder()
						.id(bpUsiUserItem.getId())
						.code(bpUsiUserItem.getCode())
						.username(bpUsiUserItem.getUsername())
						.firstname(bpUsiUserItem.getFirstname())
						.lastname(bpUsiUserItem.getLastname())
						.build())
				.orElse(null);
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("MigrateST-GG")
	public void migrateStGg(MigrationRequest request) {

		int pageIndex = 0;
		while (true) {

			// Get All Student
			Page<BpUsiUserItem> studentPage = bpUsiUserItemRepository.findByIdBetweenAndMyust(
					request.getFromId(), request.getToId(), "ST",
					PageRequest.of(pageIndex, request.getPageSize(), Sort.by("id")));
			List<BpUsiUserItem> students = studentPage.getContent();

			if (students.size() < request.getPageSize()) {
				break;
			} else {
				pageIndex++;
			}

			// For each ST
			students.forEach(st -> {
				try {
					bpUsiUserItemService.migrateStGg(st.getCode());
				} catch (Exception e) {
					log.error("Error when migrate ST-GG with st " + st.getCode(), e);
				}
			});

		}

	}

	@Override
	@WriteUnitTestLog
	public void migrateStGg(String stCode) {
		log.info("migrateStGg {}", stCode);
		// getST-GG
		BpGgGradegroupResponse gg = bpGGGradeGroupService.getST_GG(stCode);

		// setST-GG
		bpGgStService.setST_GG(stCode, gg.getCode());

	}

	@Override
	public List<AssignDoerResponse> findListDoer(String lcet, String ust, Timestamp time, String chrt) {
		CalendarPeriod cady = calendarPeriodService.getCAPByTime(time, CalendarPeriodTypeEnum.DAY.getCode());
		List<String> cassList = calendarPeriodService.findByMyGrandParentAndCapType(cady.getCode()
				, CalendarPeriodTypeEnum.SESSION.getCode()).stream().map(CalendarPeriod::getCode).collect(Collectors.toList());

		return bpUsiUserItemRepository.findListDoer(lcet, ust, cassList, chrt).stream()
				.map(a -> AssignDoerResponse.builder()
						.code(a.getCode())
						.username(a.getUsername())
						.myLcp(a.getMyLcp())
						.myCap(a.getMyCap())
						.myLcet(a.getMyLcet())
						.myChrt(a.getMyChrt())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void migrateTERole() {
		List<BpUsiUserItem> usiUserItemList = bpUsiUserItemRepository.findByMyust("TE");
		List<String> user = usiUserItemList.stream().map(BpUsiUserItem::getCode).collect(Collectors.toList());
		Map<String, String> userRole = getUserServiceProxy().getTeacherRoleByUserName(buildUserServiceUri(), user.toArray(new String[0])).getBody();

		usiUserItemList.forEach(u -> {
			if (Objects.equals(userRole.get(u.getUsername()), "STE")) u.setMyust("DTE");
			if (Objects.equals(userRole.get(u.getUsername()), "GTE")) u.setMyust("GTE");
			if (Objects.equals(userRole.get(u.getUsername()), "CTE")) u.setMyust("CTE");
		});

		bpUsiUserItemRepository.saveAll(usiUserItemList);
	}

	@Override
	public Page<BpUsiUserItem> findTE(String name, Pageable pageable) {
		return bpUsiUserItemRepository.findByNameAndCode(LIST_TEACHER_UST, name, pageable);
	}

	@Override
	public BpUsiUserItem save(BpUsiUserItem usi) {
		return bpUsiUserItemRepository.save(usi);
	}

	@Override
	public BpUsiUserItemResponse createEXTAccount(String pt, String usi) {
		if (pt.equals(ProductTypeEnum.OM.getName())) {
			BpUsiUserItem userItem = bpUsiUserItemRepository.findByCode(usi)
					.orElseThrow(() -> new NotFoundException("Coun't find usi by usiCode : " + usi));
			BpUsiUserItem usiCheck = bpUsiUserItemRepository.
					findFirstByMyustAndMyparent(UsiTypeEnum.EXT.getName(), usi).orElse(null);
			if (Objects.nonNull(usiCheck)) {
				return modelMapper.map(usiCheck, BpUsiUserItemResponse.class);
			}
			//create
			BpUsiUserItem extUserItem = bpUsiUserItemRepository
					.findFirstByMyustAndMyparentNull(UsiTypeEnum.EXT.getName()).orElse(null);
			if (Objects.nonNull(extUserItem)) {
				extUserItem.setMyparent(usi);
				save(extUserItem);
				return modelMapper.map(extUserItem, BpUsiUserItemResponse.class);
			}
			log.error("Coun't find any tienganh123 account for user : " + usi);
		}

		return null;
	}

	@Override
	public BpUsiUserItemResponse findByCodeVer2(String usiCode) {
		// need config
		String baseUrl = "https://www.tienganh123.com/cleverai";

		BpUsiUserItem userItem = bpUsiUserItemRepository.findByCode(usiCode)
				.orElse(null);

		if (Objects.isNull(userItem)) {
			return null;
		}
		
		BpUsiUserItem extUsi = bpUsiUserItemRepository.
				findFirstByMyustAndMyparent(UsiTypeEnum.EXT.getName(), usiCode).orElse(null);

		BpUsiUserItemResponse response = BpUsiUserItemResponse.builder()
				.id(userItem.getId())
				.code(userItem.getCode())
				.username(userItem.getCode())
				.fullName(userItem.getCode())
				.build();

		response.setLink(Objects.isNull(extUsi) ? null :
				baseUrl + "?username=" + extUsi.getUsername() + "&key=" + extUsi.getPassword());

		return response;
	}

	@Override
	public BpUsiUserItemResponse findEXTAccount(String pt, String usi) {
		if (pt.equals(ProductTypeEnum.OM.getName())) {
			BpUsiUserItem usiCheck = bpUsiUserItemRepository.
					findFirstByMyustAndMyparent(UsiTypeEnum.EXT.getName(), usi).orElse(null);
			if (Objects.nonNull(usiCheck)) {
				return BpUsiUserItemResponse.builder()
						.id(usiCheck.getId())
						.build();
			}
		}
		return null;
	}

	@Override
	public BpUsiUserItem findById(Integer id) {
		return bpUsiUserItemRepository.findById(id.longValue()).orElseThrow(
				() -> new NotFoundException("Not found usi by id :" + id)
		);
	}
}
