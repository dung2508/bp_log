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
public class UsidRegisterSO5DTO {

	private String code;
	private String mybpe;
	private String myLcet;
	private String myUst;
	private String mypt;
	private String mycassstr;
	private String mywso;
	private String myCap;
	private String myUsi;
	private String myChrt;
	private String myLcp;
	private String myterm;
	private String myaccyear;
	private Boolean published;
}
