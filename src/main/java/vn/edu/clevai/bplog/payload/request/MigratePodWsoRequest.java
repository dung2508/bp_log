package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MigratePodWsoRequest {
	@JsonProperty("page_size")
	private Integer pageSize;

	@JsonProperty("from_id")
	private Long fromId;

	@JsonProperty("to_id")
	private Long toId;

}
