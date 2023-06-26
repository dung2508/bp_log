package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class CurriculumPeriodRequest {
	private Timestamp time;

	private String pt;

	private String lctShift;

	private String gg;

	private String dfdl;

	private String dfge;

	private String cudyNo;
}

