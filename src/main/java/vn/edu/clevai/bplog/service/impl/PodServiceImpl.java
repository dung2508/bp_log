package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.annotation.UnitFunctionName;
import vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog;
import vn.edu.clevai.bplog.annotation.WriteUnitTestLog;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.BpWsoWeeklyscheduleoption;
import vn.edu.clevai.bplog.payload.request.MigrationRequest;
import vn.edu.clevai.bplog.repository.BpPTProductTypeRepository;
import vn.edu.clevai.bplog.repository.BpPodProductOfDealRepository;
import vn.edu.clevai.bplog.repository.BpUsiUserItemRepository;
import vn.edu.clevai.bplog.service.*;
import vn.edu.clevai.bplog.utils.Cep100TransformUtils;
import vn.edu.clevai.common.api.eureka.EurekaDiscoveryClientService;
import vn.edu.clevai.common.api.eureka.LookupStrategyEnum;
import vn.edu.clevai.common.api.exception.NotFoundException;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpClagClassgroupResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpDfdlDifficultgradeResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpPODResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;
import vn.edu.clevai.common.proxy.lms.proxy.LmsServiceProxy;
import vn.edu.clevai.common.proxy.sale.payload.response.DealAttributeResponse;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;
import vn.edu.clevai.common.proxy.sale.proxy.SaleServiceProxy;
import vn.edu.clevai.common.proxy.user.payload.response.UserAccountResponse;
import vn.edu.clevai.common.proxy.user.proxy.UserServiceProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PodServiceImpl implements PodService {

	@Autowired
	private BpPTProductTypeRepository productTypeRepository;

	@Autowired
	private BpPodProductOfDealRepository productOfDealRepository;

	@Autowired
	private LmsServiceProxy lmsServiceProxy;

	@Autowired
	private SaleServiceProxy saleServiceProxy;

	@Value(value = "${internal.apigate.services.lms.name}")
	private String lmsServiceName;

	@Value(value = "${internal.apigate.services.sale.name}")
	private String saleServiceName;

	@Autowired
	private UserServiceProxy userServiceProxy;

	@Value(value = "${internal.apigate.services.user.name}")
	private String userServiceName;

	@Autowired
	private BpUsiUserItemRepository userItemRepository;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private BpUsiUserItemService usiUserItemService;

	@Autowired
	private BpPodProductOfDealService bpPodProductOfDealService;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private BpWsoWeeklyscheduleoptionService bpWsoWeeklyscheduleoptionService;

	@Autowired
	private BpDfdlDifficultygradeService bpDfdlDifficultygradeService;

	@Autowired
	private BpPODDFDLService bpPODDFDLService;

	@Autowired
	@Lazy
	private BpClagClassgroupService bpClagClassgroupService;

	@Lazy
	@Autowired
	private PodService podService;

	@Override
	@WriteUnitTestLog
	public BpPTProductType getPTFromX(Long xptId) {
		String ptCode = Cep100TransformUtils.toPtCode(xptId);
		return productTypeRepository.findByCode(ptCode).orElse(null);
	}

	@Override
	@WriteUnitTestLog
	public BpPTProductType getPOD_PT(String podCode) {
		BpPodProductOfDeal productOfDeal = productOfDealRepository.findByCode(podCode).orElseThrow(
				() -> new NotFoundException("Couldn't find product of deal by pod_code : " + podCode)
		);
		Integer ptId = this.findXPT(productOfDeal.getXdeal().intValue());
		String ptCode = Cep100TransformUtils.toPtCode(ptId.longValue());
		return productTypeRepository.findByCode(ptCode).orElse(null);
	}

	@Override
	@WriteUnitTestLog
	public BpPodProductOfDeal setPOD_PT(String podCode, String ptCode) {
		BpPodProductOfDeal productOfDeal = productOfDealRepository.findByCode(podCode).orElseThrow(
				() -> new NotFoundException("Couldn't find product of deal by pod_code : " + podCode)
		);
		BpPTProductType productType = productTypeRepository.findByCode(ptCode).orElseThrow(
				() -> new NotFoundException("Couldn't find product type by pt_code : " + ptCode)
		);
		productOfDeal.setMypt(productType.getCode());

		return productOfDeal;
	}

	@Override
	@WriteUnitTestLog
	public Integer findXPT(Integer xdeal) {
		Long xptId = lmsServiceProxy.getProductTypeByDeal(
				EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient, lmsServiceName,
						LookupStrategyEnum.RANDOM), xdeal.longValue()).getBody();
		if (Objects.isNull(xptId)) {
			throw new NotFoundException("Couldn't find product type by deal : " + xdeal);
		}
		return xptId.intValue();
	}

	@Override
	@WriteUnitTestLog
	public PODResponse setPOD_ST(String podCode, String stCode) {
		BpPodProductOfDeal productOfDeal = productOfDealRepository.findByCode(podCode).orElseThrow(
				() -> new NotFoundException("Couldn't find product of deal by pod_code : " + podCode)
		);

		if (!userItemRepository.existsByCode(stCode)) {
			throw new NotFoundException("Couldn't find user item by st_code : " + stCode);
		}

		productOfDeal.setMyst(stCode);

		productOfDeal = productOfDealRepository.save(productOfDeal);

		return PODResponse
				.builder()
				.id(productOfDeal.getId())
				.code(productOfDeal.getCode())
				.myst(productOfDeal.getMyst())
				.myprd(productOfDeal.getMyprd())
				.mypt(productOfDeal.getMyst())
				.xdeal(productOfDeal.getXdeal())
				.fromDate(productOfDeal.getFromDate())
				.toDate(productOfDeal.getToDate())
				.description(productOfDeal.getDescription())
				.createdAt(productOfDeal.getCreatedAt())
				.updatedAt(productOfDeal.getUpdatedAt())
				.build();
	}

	@Override
	@WriteUnitTestLog
	public BpUsiUserItemResponse getPOD_ST(String podCode) {
		BpPodProductOfDeal productOfDeal = productOfDealRepository.findByCode(podCode).orElseThrow(
				() -> new NotFoundException("Couldn't find product of deal by pod_code : " + podCode)
		);
		String xst = this.findXST(productOfDeal.getXdeal()).toString();
		return usiUserItemService.getSTFromX(xst);
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.FIND_XST)
	public String findXST(Long xXdealid) {
		Long studentId = lmsServiceProxy.getLastStudentIdByDeal(
				EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient, lmsServiceName,
						LookupStrategyEnum.RANDOM), xXdealid).getBody();

		UserAccountResponse user = userServiceProxy.get(
				EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient, userServiceName,
						LookupStrategyEnum.RANDOM), studentId).getBody();

		if (Objects.isNull(user)) {
			throw new NotFoundException("Couldn't find user by user_id : " + studentId);
		}

		return user.getUsername();
	}

	@Override
	@WriteUnitTestLog
	@WriteBPUnitTestLog(BPLogProcessEnum.GET_ST_FROM_X)
	public BpUsiUserItemResponse getSTFromX(String xStudent_id) {
		BpUsiUserItemResponse usiUserItemResponse = usiUserItemService.getSTFromX(xStudent_id);
		if (Objects.isNull(usiUserItemResponse)) {
			throw new NotFoundException("Couldn't find user by xst : " + xStudent_id);
		}
		return usiUserItemResponse;
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("setPOD")
	@WriteBPUnitTestLog(
			BPLogProcessEnum.SET_POD
	)
	public BpPODResponse setPOD(
			String podPod_code,
			String podMypt,
			String podMyst,
			String myprd,
			java.sql.Date fromdate,
			java.sql.Date todate,
			Long xXdealid
	) {
		BpPodProductOfDeal pod = productOfDealRepository.findByCode(podPod_code).orElseGet(
				() -> BpPodProductOfDeal.builder().code(podPod_code).build());
		pod.setMyprd(myprd);
		pod.setMypt(podMypt);
		pod.setMyst(podMyst);

		if (Objects.nonNull(fromdate)) {
			pod.setFromDate(fromdate);

		}
		if (Objects.nonNull(todate)) {
			pod.setToDate(todate);

		}

		pod.setXdeal(xXdealid);
		pod = productOfDealRepository.save(pod);
		return mapper.map(pod, BpPODResponse.class);
	}

	@Override
	public void migratePod(Long dealIdLt) {
		List<DealAttributeResponse> deals = new ArrayList<>();
		int page = 0;
		long count = 10L;

		while (count != 0L) {
			page += 1;
			log.info("GET DEAL page " + page);
			List<DealAttributeResponse> contents = Objects.requireNonNull(saleServiceProxy.getDealInfo(
					EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient, saleServiceName,
							LookupStrategyEnum.RANDOM), page, 100, dealIdLt).getBody()).getContent();
			count = contents.size();

			deals.addAll(contents);
		}

		log.info("GET DEAL total " + deals.size());

		deals.forEach(d -> {
			try {
				Long studentId = lmsServiceProxy.getLastStudentIdByDeal(
						EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient, lmsServiceName,
								LookupStrategyEnum.RANDOM), d.getDealId()).getBody();
				System.out.println("xdt : " + studentId);
				UserAccountResponse st = userServiceProxy.get(
						EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient, userServiceName,
								LookupStrategyEnum.RANDOM), studentId).getBody();
				usiUserItemService.createOrUpdateUsi(st.getUsername(), st.getLastName(),
						st.getFirstName(), "ST", st.getUsername(), st.getFullName(), st.getPhone(), null);

				PODResponse pod = bpPodProductOfDealService.getPODFromX(d.getDealId());

				setPOD(pod.getCode(), pod.getMypt(), pod.getMyst(), pod.getMyprd(),
						pod.getFromDate(), pod.getToDate(), pod.getXdeal());
			} catch (Exception e) {
				log.error("Error when migrate migratePod " + d.getDealId(), e);
			}
		});
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("MigratePOD-DFDL")
	public void migratePodDfdl(MigrationRequest request) {

		int pageIndex = 0;
		while (true) {

			// Get All POD
			Page<BpPodProductOfDeal> podPage = productOfDealRepository.findByIdBetween(
					request.getFromId(), request.getToId(),
					PageRequest.of(pageIndex, request.getPageSize(), Sort.by("id")));
			List<BpPodProductOfDeal> pods = podPage.getContent();

			if (pods.size() < request.getPageSize()) {
				break;
			} else {
				pageIndex++;
			}

			// For each POD
			pods.forEach(pod -> {
				try {
					podService.migratePodDfdl(pod.getCode());
				} catch (Exception e) {
					log.error("Error when migrate POD-DFDL with st " + pod.getCode(), e);
				}
			});

		}

	}

	@Override
	public void migratePodDfdl(String podCode) {

		// getPOD_DFDL
		BpDfdlDifficultgradeResponse dfdl = bpDfdlDifficultygradeService.getPOD_DFDL(podCode);

		// setPOD_DFDL
		bpPODDFDLService.setPOD_DFDL(podCode, dfdl.getCode());

	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("MigratePOD-WSO")
	public void migratePodWso(Long fromId, Long toId, Integer size) {
		List<BpPodProductOfDeal> list;
		list = this.getPod(fromId, toId, size);
		list.forEach(pod -> {
			try {
				BpWsoWeeklyscheduleoption wso = bpWsoWeeklyscheduleoptionService.getPOD_WSO(pod.getCode());
				bpWsoWeeklyscheduleoptionService.setPOD_WSO(pod.getCode(), wso.getCode());
			} catch (Exception e) {
				log.error("Error when migrate POD-WSO with pod : " + pod.getCode(), e);
			}
		});
	}

	@WriteUnitTestLog
	@UnitFunctionName("getPOD")
	@Override
	public List<BpPodProductOfDeal> getPod(Long fromId, Long toId, Integer size) {
		List<BpPodProductOfDeal> list = new ArrayList<>();
		int p = 0;
		while (true) {
			List<BpPodProductOfDeal> contents = productOfDealRepository.findByIdBetween(
					fromId, toId, PageRequest.of(p, Objects.isNull(size) ? 1000 : size)).getContent();
			list.addAll(contents);
			if (contents.size() == 0L) break;
			p += 1;
		}
		return list;
	}

	@Override
	@WriteUnitTestLog
	@UnitFunctionName("MigratePOD-CLAGPERM")
	public void migratePodClagperm(MigrationRequest request) {

		int pageIndex = 0;
		while (true) {

			// Get All POD
			Page<BpPodProductOfDeal> podPage = productOfDealRepository.findByIdBetween(
					request.getFromId(), request.getToId(),
					PageRequest.of(pageIndex, request.getPageSize(), Sort.by("id")));
			List<BpPodProductOfDeal> pods = podPage.getContent();

			if (pods.size() < request.getPageSize()) {
				break;
			} else {
				pageIndex++;
			}

			// For each POD
			pods.forEach(pod -> {
				try {
					podService.migratePodClagperm(pod.getCode());
				} catch (Exception e) {
					log.error("Error when migrate POD-CLAGPERM with pod " + pod.getCode(), e);
				}
			});

		}

	}

	@Override
	@WriteUnitTestLog
	public void migratePodClagperm(String podCode) {

		// getPOD_CLAGPERM
		BpClagClassgroupResponse clagperm = bpClagClassgroupService.getPOD_CLAGPERM(podCode);

		// setPOD_CLAGPERM
		bpClagClassgroupService.setPOD_CLAGPERM(
				podCode,
				clagperm.getCode(),
				clagperm.getAssignedAt(),
				clagperm.getUnassignedAt(),
				clagperm.getClagtype(),
				null
		);

	}

}
