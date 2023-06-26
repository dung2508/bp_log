package vn.edu.clevai.bplog.dto.sheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SSLSheetDTO {
	private String sslName;

	private String sslCode;

	private String dlC1Te;
	private String dlC1St;
	private String dlC2Te;
	private String dlC2St;

	private String dlgC1GEATe;
	private String dlgC1GEBTe;
	private String dlgC1GECTe;
	private String dlgC1GEDTe;
	private String dlgC2GEATe;
	private String dlgC2GEBTe;
	private String dlgC2GECTe;
	private String dlgC2GEDTe;

	private String gesC1GEATe;
	private String gesC1GEBTe;
	private String gesC1GECTe;
	private String gesC1GEDTe;
	private String gesC2GEATe;
	private String gesC2GEBTe;
	private String gesC2GECTe;
	private String gesC2GEDTe;

	private String gesC1GEASt;
	private String gesC1GEBSt;
	private String gesC1GECSt;
	private String gesC1GEDSt;
	private String gesC2GEASt;
	private String gesC2GEBSt;
	private String gesC2GECSt;
	private String gesC2GEDSt;
}
