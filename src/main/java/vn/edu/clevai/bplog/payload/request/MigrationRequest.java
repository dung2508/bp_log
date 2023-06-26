package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MigrationRequest {

	@JsonProperty("from_id")
	private Long fromId;

	@JsonProperty("to_id")
	private Long toId;

	@JsonProperty("page_size")
	private Integer pageSize;

}