package vn.edu.clevai.bplog.payload.request.bp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class BPScheduleUssRequest {
	private String sussCass;
	private Timestamp sussCassStartPeriod;
	private String sussLcp;
	private List<BPClagRequest> sussClagList;
	private List<BPClagRequest> sussClagUgeList;
	private List<BPClagRequest> sussClagBcList;
	private List<BPClagRequest> sussClagPmList;
	private List<String> sussTe;
	private List<String> sussTo;
	private List<String> sussCo;
	private List<String> sussSo;
	private String sussCti;
}
