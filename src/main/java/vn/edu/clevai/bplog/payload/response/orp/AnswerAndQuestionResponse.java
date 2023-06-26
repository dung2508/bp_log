package vn.edu.clevai.bplog.payload.response.orp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerAndQuestionResponse {

	@JsonProperty("ulc_code")
	private String ulcCode;

	@JsonProperty("ulc_id")
	private Long ulcId;

	@JsonProperty("root_cti_id")
	private Long rootCtiId;

	@JsonProperty("root_cti_code")
	private String rootCtiCode;

	@JsonProperty("cui_code")
	private String cuiCode;

	@JsonProperty("question")
	private AnswerQuestionCTIDetail question;

	@JsonProperty("answer")
	private AnswerQuestionCTIDetail answer;

	@JsonProperty("created_at")
	private Timestamp createdAt;

	@JsonProperty("mygg")
	private String mygg;

	@JsonProperty("is_expired")
	private String isExpired;

	public Boolean getIsExpired() {
		if ("0".equals(isExpired)) return false;
		if ("1".equals(isExpired)) return true;
		return null;
	}
}
