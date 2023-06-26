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
public class StudentLearningPackageResponse {
	@JsonProperty("index")
	private Integer ulcNo;
	private Long id;
	private String name;
	private String code;
	private Long trainingTypeid;
}
