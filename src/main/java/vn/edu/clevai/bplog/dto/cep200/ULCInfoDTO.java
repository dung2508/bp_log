package vn.edu.clevai.bplog.dto.cep200;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ULCInfoDTO {

	private Long id;

	private String ulcCode;

	private String capCode;

	private String ggCode;

	private String dfdlCode;

	private String lctCode;

	private String lcpCode;

	private Timestamp startperiod;

	private Timestamp endperiod;

	private Boolean published;
}
