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
public class TeachingProgramDTO {

	// TODO: 22/05/2023 convert to start timeweek, end timeweek
	private String timeWeek;
	private String currWeek;
	private String currShift;
	private String bl3QGroup;
	private String bL4QType;
	private String msnTE;
	private String s21Skill;
	private String s21Content;


}
