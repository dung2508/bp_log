package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CTTEnum {
	SSL("CTI_SSL", "Session slide"),
	SSST("CTI_SSST", "Session slide teacher"),
	SSTE("CTI_SSTE", "Session slide student"),
	BL3QG("CTI_BL3QG", "Book lo3"),
	BL4QT("CTI_BL4QT", "Book lo4"),
	BL5QP("CTI_BL5QP", "Book lo5"),
	VIDEO("CTI_VDL", "Video"),
	ULC("CTI_ULC", "ULC"),
	RECORDED_LINK("CTI_RVL", "Recorded Link"),
	PERIOD_CHALLENGE("CTI_PCL", "Period Challenge"),
	CTI_QTS("CTI_QTS", "CTI SUBMIT QUESTION"),
	CTI_AWS("CTI_AWS", "CTI SUBMIT ANSWER"),
	;
	private String code;
	private String name;
}
