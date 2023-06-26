package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TimeShiftRequest {
	private Integer timeShiftGroupId;

	private Integer timeShiftStartId;
}
