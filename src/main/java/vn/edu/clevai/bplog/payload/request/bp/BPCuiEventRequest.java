package vn.edu.clevai.bplog.payload.request.bp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class BPCuiEventRequest {
	private String cuieMyusi;
	private String cuieCuiecode;
	private String cuieMylcet;
	private Timestamp cuiePlantime;
	@JsonProperty("cuie_cui_code")
	private String cuieCuicode;
	private String cuieMylcp;
	private Timestamp cuieActualtimeBet;
	private Timestamp cuieActualtimeFet;
	private Boolean cuiePublished;
}
