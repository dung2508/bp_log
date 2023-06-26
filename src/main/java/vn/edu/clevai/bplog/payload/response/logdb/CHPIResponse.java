package vn.edu.clevai.bplog.payload.response.logdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;


@Data
public class CHPIResponse {

	private Integer id;

	@JsonProperty(value = "CHPT_code")
	private String chptCode;

	private String name;

	@JsonProperty(value = "my_LCT")
	private String myLct;

	@JsonProperty(value = "my_CHPT_type")
	private String myChptType;

	@JsonProperty(value = "my_LCET")
	private String myLcet;

	@JsonProperty(value = "my_CUI_event")
	private String myCuiEvent;

	@JsonProperty(value = "my_trigger")
	private String myTrigger;

	@JsonProperty(value = "my_checker")
	private String myChecker;

	@JsonProperty(value = "my_CTI1")
	private String myCti1;

	@JsonProperty(value = "my_CTI2")
	private String myCti2;

	@JsonProperty(value = "my_CTI3")
	private String myCti3;

	private String description;

	@JsonProperty(value = "create_at")
	private Timestamp createAt;

	@JsonProperty(value = "update_at")
	private Timestamp updateAt;

}
