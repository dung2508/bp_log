package vn.edu.clevai.bplog.repository.projection;

import java.sql.Timestamp;

public interface SessionOperatorAndCuiProjection {

	String getCuiCode();

	String getLcpCode();

	Timestamp getStartTime();

	String getGgCode();

	String getDfdlCode();

	String getDfgeCode();

	String getUsiCode();

	String getUsiFullname();

	String getUsiPhone();

}
