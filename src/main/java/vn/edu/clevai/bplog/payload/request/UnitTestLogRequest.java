package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class UnitTestLogRequest {
	private Integer type;
	private String unitFunction;
	private Timestamp myTime;
	private Timestamp collectTime;
	private Timestamp time;
	private String academicYear;
	private String term;
	private String productType;
	private String gradeGroup;
	private String weekNo;
	private String shiftNo;
	private String dfdl;
	private String lct;
	private String sessionNo;
	private String dfge;
	private String sceneNo;
	private String crpp;
	private String crps;
	private String parentCupPeriod;
	private String cupPeriod;
	private String cupPeriodNumber;
	private String capPeriod;
	private Integer ordinarily;
}
