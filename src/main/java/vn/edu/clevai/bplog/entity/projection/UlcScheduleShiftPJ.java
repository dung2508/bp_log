package vn.edu.clevai.bplog.entity.projection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import vn.edu.clevai.bplog.annotation.suport.DayOfWeekSerializer;

public interface UlcScheduleShiftPJ {
	String getCode();

	String getMyGg();

	String getMyDfdl();

	String getMyDfge();

	String getMyDfqc();

	String getMyPt();

	String getMyLck();

	String getCashStart();

	String getCady();

	@JsonSerialize(using = DayOfWeekSerializer.class)
	String getWso();

	String getMyprd();
}
