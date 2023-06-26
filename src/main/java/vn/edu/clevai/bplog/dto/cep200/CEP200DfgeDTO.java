package vn.edu.clevai.bplog.dto.cep200;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CEP200DfgeDTO {
	private String podCode;

	private String usiCode;

	private String dfgeCode;
	
	private String dfgeName;
}
