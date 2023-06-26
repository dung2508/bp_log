package vn.edu.clevai.bplog.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChsiCheckStepEmailDTO {
	private String chptName;
	private String chstName;
	private String chrtCode;
	private String chliCode;
	private String triggerUst;
	private String ulcCode;
	private String chriUsi;
	private String link;
}
