package vn.edu.clevai.bplog.dto.cep200;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClagDTO {
	private Long id;
	private String code;
	private String name;

	@JsonProperty("pt_id")
	private Integer ptId;
	@JsonProperty("pt_code")
	private String ptCode;
	@JsonProperty("pt_name")
	private String ptName;

	@JsonProperty("gg_id")
	private Integer ggId;
	@JsonProperty("gg_code")
	private String ggCode;
	@JsonProperty("gg_name")
	private String ggName;

	@JsonProperty("dfdl_id")
	private Integer dfdlId;
	@JsonProperty("dfdl_code")
	private String dfdlCode;
	@JsonProperty("dfdl_name")
	private String dfdlName;

	@JsonProperty("wso_id")
	private Integer wsoId;
	@JsonProperty("pt_id")
	private String wsoCode;
	@JsonProperty("pt_id")
	private String wsoName;

	@JsonProperty("dfge_id")
	private Integer dfgeId;

	@JsonProperty("dfge_code")
	private String dfgeCode;

	@JsonProperty("dfge_name")
	private String dfgeName;

	@JsonProperty("clag_type")
	private String clagType;

	@JsonProperty("max_total_student")
	private Integer maxTotalStudent;
}
