package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CTIULCEnum {
	DLC("DLC", "Dilive Coach"),
	DLG("DLG", "Dilive Get"),
	DL("DL", "Dilive"),
	HW("HW", "Homework"),
	RC("RC", "Race"),
	DSC("DSC", "DSC"),
	DQS("DQS", "DQS");

	private String code;
	private String name;
}
