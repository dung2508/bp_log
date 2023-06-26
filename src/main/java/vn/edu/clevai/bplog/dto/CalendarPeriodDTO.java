package vn.edu.clevai.bplog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarPeriodDTO {

	private Long id;

	private String code;

	private String description;

	private String myParent;

	private String capType;

	private Timestamp startTime;

	private Timestamp endTime;

	private Set<CalendarPeriodDTO>  subCap;

}
