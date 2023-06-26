package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class ContentRequest {
	private Timestamp collectTime;

	// CurrSessionParameters 1
	private Timestamp time;

	@NotNull
	private String grade;

	@NotNull
	private String subject;

	//// CurrSessionParameters 2
	// ProdType
	@NotNull
	private String prodType;

	// DFDL
	@NotNull
	private String dfdl;

	// ShiftType
	@NotNull
	private String shiftType;

	// SesionNo
	@NotNull
	private Integer sessionNo;

	// DFGE
	private String dfge;
}

