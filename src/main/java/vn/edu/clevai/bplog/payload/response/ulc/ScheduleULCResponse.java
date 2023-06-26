package vn.edu.clevai.bplog.payload.response.ulc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleULCResponse {
	private String code;
	private String lck;
	private String pt;
	private String gg;
	private String dfdl;
	private String dfge;
	private String dfcq;
	private String cady;
	private String wso;

	@JsonProperty("cash_start")
	private String cashStart;

	private String PRD;
}
