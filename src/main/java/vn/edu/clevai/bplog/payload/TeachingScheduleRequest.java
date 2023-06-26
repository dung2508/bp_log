package vn.edu.clevai.bplog.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeachingScheduleRequest {
	List<TeachingScheduleRequest> teachingScheduleRequestList;
}
