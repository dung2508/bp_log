package vn.edu.clevai.bplog.service.proxy.salesproxy;

import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;

public interface SaleService {
	PODResponse getPODFromX(Long xXdealid);
}
