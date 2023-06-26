package vn.edu.clevai.bplog.payload.response.logdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CHSIResponse {

	private Integer id;

	@JsonProperty(value = "my_CHPI")
	private String myChpi;

	private String code;

	@JsonProperty(value = "check_sample")
	private Integer checkSample;

	@JsonProperty(value = "checker_type")
	private String checkerType;

	private String name;

	@JsonProperty(value = "my_CHRI")
	private String myChri;

	@JsonProperty(value = "my_CHST")
	private String myChst;

	private String description;

	@JsonProperty(value = "create_at")
	private Timestamp createAt;

	@JsonProperty(value = "update_at")
	private Timestamp updateAt;
}
