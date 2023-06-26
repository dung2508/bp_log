package vn.edu.clevai.bplog.dto.bp;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ValueDto {
	private String bppCode;
	private String bpsCode;
	private String bpeCode;
	private String ulcCode;
	private String actualBpe;
	private String publishBpe;
	private String unPublishBpe;
	private Map<String, String> mapUshBpp;
	private Map<String, String> mapCuiBps;
	private String cuiCode;
	private String podCase;
}
