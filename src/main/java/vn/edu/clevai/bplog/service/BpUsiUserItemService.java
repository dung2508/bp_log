package vn.edu.clevai.bplog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;
import vn.edu.clevai.bplog.payload.request.MigrationRequest;
import vn.edu.clevai.common.proxy.bplog.payload.response.AssignDoerResponse;
import vn.edu.clevai.common.proxy.bplog.payload.response.BpUsiUserItemResponse;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface BpUsiUserItemService {
	BpUsiUserItem findByCode(String usiCode);

	BpUsiUserItem findByUsername(String username);

	List<BpUsiUserItemResponse> getUSI(String ust, Pageable pageable);

	BpUsiUserItemResponse getSTFromX(String xST);

	BpUsiUserItemResponse createOrUpdateUsi
			(String code, String lastname, String firstname, String myust, String username, String fullname, String phone, String email);

	BpUsiUserItemResponse findUSI(
			String lcetCode,
			String myust,
			String capCode,
			String chrtCode,
			String lcpCode,
			Collection<String> excludeUsi
	);

	void migrateStGg(MigrationRequest request);

	void migrateStGg(String stCode);

	List<AssignDoerResponse> findListDoer(String lcet, String ust, Timestamp time, String chrt);

	void migrateTERole();

	Page<BpUsiUserItem> findTE(String name, Pageable pageable);

	BpUsiUserItem save(BpUsiUserItem usi);

	BpUsiUserItemResponse createEXTAccount(String pt, String usi);

	BpUsiUserItemResponse findByCodeVer2(String usi);

	BpUsiUserItemResponse findEXTAccount(String pt, String usi);

	BpUsiUserItem findById(Integer id);
}
