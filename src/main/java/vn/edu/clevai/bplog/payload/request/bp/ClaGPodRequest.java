package vn.edu.clevai.bplog.payload.request.bp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.clevai.bplog.entity.CalendarPeriod;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaGPodRequest {
	private String gg;
	private String dfdl;
	private String pt;
	private String ust;
	private String cashStart;

	// Time
	private String cady;
	private List<CalendarPeriod> cashList;
	private Timestamp startPeriod;
}
