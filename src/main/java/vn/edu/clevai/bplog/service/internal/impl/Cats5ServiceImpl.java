package vn.edu.clevai.bplog.service.internal.impl;

import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.internal.Cats5Service;
import vn.edu.clevai.common.proxy.BaseProxyService;

import java.util.Date;

@Service
public class Cats5ServiceImpl extends BaseProxyService implements Cats5Service {
	@Override
	public Date getExpireDate(Long podId) {
		return getCat5ServiceProxy().getExpireDate(buildWarehouseFinanceServiceUri(), podId).getBody();
	}
}
