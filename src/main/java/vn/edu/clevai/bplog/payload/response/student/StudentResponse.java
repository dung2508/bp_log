package vn.edu.clevai.bplog.payload.response.student;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("myst")
	private String myst;

	@JsonProperty("myclag")
	private String myclag;

	@JsonProperty("mypt")
	private String mypt;

	private String fullname;
}
