package vn.edu.clevai.bplog.service;

import vn.edu.clevai.bplog.entity.BpPodProductOfDeal;
import vn.edu.clevai.bplog.entity.CalendarPeriod;
import vn.edu.clevai.common.proxy.sale.payload.response.PODResponse;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface BpPodProductOfDealService {
	BpPodProductOfDeal findByCode(String code);

	PODResponse getPODFromX(Long xdeal);

	Date getExpireDate(String podCode);

	List<BpPodProductOfDeal> findByClagsAndDate(List<String> sussClagList, Date date);

	PODResponse getPOD(String xst, Long xpt);

	List<BpPodProductOfDeal> findPodCodeByClag(List<String> clagCode, List<String> ust);

	List<BpPodProductOfDeal> findPodCodeByClag(List<String> clagCode, List<String> ust, Timestamp start);

	List<BpPodProductOfDeal> findPodByClagCadyAndUst(List<String> clagCodes, List<String> ust, CalendarPeriod cady);

	BpPodProductOfDeal findByUsi(String usi, String pt);

	BpPodProductOfDeal findFirstBy(
			String lcetCode,
			String myust,
			String capCode,
			String chrtCode,
			String lcpCode,
			Collection<String> excludeUsi,
			String gg,
			String dfdl,
			String dfge
	);

	List<BpPodProductOfDeal> findBy(
			String lcetCode,
			String myust,
			String capCode,
			String chrtCode,
			String lcpCode,
			Collection<String> excludeUsi,
			String gg,
			String dfdl,
			String dfge
	);

	BpPodProductOfDeal findByUsiAndDate(String s, String pt, long milliSeconds);


	List<String> getPtFromCadyAndUst(String cady, String ust);

	List<BpPodProductOfDeal> findByUsi(String usi, Timestamp time);

	List<BpPodProductOfDeal> findByListUsi(List<String> usis);

	List<BpPodProductOfDeal> findActivePodsByClagAndCap(String clag, String cap, List<String> usts);
}
