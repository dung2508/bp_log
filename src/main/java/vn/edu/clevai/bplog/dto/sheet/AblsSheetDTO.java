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
public class AblsSheetDTO {
	private String shiftName;

	private String abliCode;
	private String abliLink;

	private String coqCode;
	private String coqLink;

	private String ctiPcCode;
	private String ctiPcLink;
}
