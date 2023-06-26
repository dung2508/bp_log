package vn.edu.clevai.bplog.payload.response.teacher.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDetail {
	private Integer id;
	private String code;
	@JsonProperty("start_at")
	private String startAt;
}
