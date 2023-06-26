package vn.edu.clevai.bplog.repository.projection;

import java.sql.Timestamp;

public interface CheckProcessTempProjection {
	Integer getId();

	String getCode();

	String getName();

	String getMyLct();

	String getMychpttype();

	String getMylceg();

	String getMylcet();

	String getMylctfilter();

	String getTriggerusertype();

	String getCheckerusertype();

	Timestamp getCreateAt();

	Timestamp getUpdateAt();

}
