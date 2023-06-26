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
public class AnswerQuestionCTIDetail {
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("myusi")
	private String myusi;
	
	@JsonProperty("usi_fullname")
	private String usiFullName;
	
	@JsonProperty("myust")
	private String myust;
	
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("text")
	private String text;
	
	@JsonProperty("file_url")
	private String fileUrl;
	
	@JsonProperty("created_at")
	private Timestamp createdAt;
}
