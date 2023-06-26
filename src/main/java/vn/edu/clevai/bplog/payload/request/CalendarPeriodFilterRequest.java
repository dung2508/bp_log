package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class CalendarPeriodFilterRequest {
	private Timestamp time;

	private String inputCap;

	private String capNo;

	private String capType;
}
