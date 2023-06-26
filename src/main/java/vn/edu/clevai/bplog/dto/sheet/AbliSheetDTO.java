package vn.edu.clevai.bplog.dto.sheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbliSheetDTO {
	private String abliCode;

	private String bl3Code;

	private String c1QType;

	private String c2QType;

	private String c1Bl4Code;

	private String c1Bl5Code;

	private String c2Bl4Code;

	private String c2Bl5Code;
}
