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
public class CrppDTO {
	private String timeWeek;
	
	private Integer currWeek;
	
	private String currShift;
	
	private String bl3QGroup;
	
	private String bl4;
	
	private String missionTest;
	
	private String s21;
	
	private String s21Content;
	
	private String ctiPcLink;
	
}
