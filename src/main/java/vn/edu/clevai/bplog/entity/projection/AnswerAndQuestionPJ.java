package vn.edu.clevai.bplog.entity.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public interface AnswerAndQuestionPJ {

	@JsonProperty("ulc_code")
	String getUlcCode();

	@JsonProperty("ulc_id")
	Long getUlcId();

	@JsonProperty("root_cti_id")
	Long getRootCtiId();

	@JsonProperty("root_cti_code")
	String getRootCtiCode();

	@JsonProperty("cui_code")
	String getCuiCode();

	@JsonProperty("cui_id")
	Long getCuiId();

	@JsonProperty("created_at")
	Timestamp getCreatedAt();

	String getMyUsiCui();

	String getMyUsiFullName();

	String getMyUst();

	String getMygg();

	Timestamp getQuestionCreatedAt();

	String getQuestionMyvalueset();

	Timestamp getAnswerCreatedAt();

	String getAnswerMyvalueset();

	@JsonProperty("is_expired")
	String getIsExpired();

}
