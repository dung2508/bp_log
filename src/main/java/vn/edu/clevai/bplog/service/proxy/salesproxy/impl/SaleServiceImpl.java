package vn.edu.clevai.bplog.service.proxy.salesproxy.impl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import vn.edu.clevai.bplog.service.proxy.salesproxy.SaleService;
import vn.edu.clevai.common.api.eureka.EurekaDiscoveryClientService;
import vn.edu.clevai.common.api.eureka.LookupStrategyEnum;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;
import vn.edu.clevai.common.proxy.sale.proxy.SaleServiceProxy;

@Service
public class SaleServiceImpl implements SaleService {

	@Value("${internal.apigate.services.sale.name}")
	private String saleServiceName;
	private final SaleServiceProxy saleServiceProxy;
	private final DiscoveryClient discoveryClient;

	public SaleServiceImpl(DiscoveryClient discoveryClient,
						   SaleServiceProxy saleServiceProxy) {
		this.discoveryClient = discoveryClient;
		this.saleServiceProxy = saleServiceProxy;
	}

	@Override
	public PODResponse getPODFromX(Long xXdealid) {
		try {
			return saleServiceProxy.getPODFromX(buildSaleServiceUri(), xXdealid).getBody();
		} catch (Exception e) {
			throw new RuntimeException("Sale service is down: " + e.getMessage());
		}
	}

	public URI buildSaleServiceUri() throws Exception {
		return EurekaDiscoveryClientService.getUriOfEurekaService(discoveryClient,
				saleServiceName, LookupStrategyEnum.RANDOM);
	}
}
