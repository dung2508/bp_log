package vn.edu.clevai.bplog.dto.bp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PtGgDfdlDTO {

	private String pt;
	private Set<GG> ggs;


	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GG {

		@JsonProperty("pt_code")
		private String ptCode;
		private String code;
		private Set<DFDL> dfdls;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class DFDL {
		@JsonProperty("gg_code")
		private String ggCode;

		@JsonProperty("code")
		private String code;
	}

}
