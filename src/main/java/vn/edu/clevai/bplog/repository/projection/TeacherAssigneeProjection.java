package vn.edu.clevai.bplog.repository.projection;

import java.sql.Date;

public interface TeacherAssigneeProjection {

	Date getDate();

	Long getUsiId();

	String getUsiCode();

	String getUsiFullname();

	Long getUsidId();

	Long getPtId();

	Long getGgId();

	Long getDfdlId();

	String getDfgeCode();

	String getPosition();

	Long getSumApproved();

	Long getSumMainAssigned();

	Long getSumBackupAssigned();

}
