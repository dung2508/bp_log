package vn.edu.clevai.bplog.repository.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CLAGDetailInfoProjection {

	Integer getId();

	String getCode();

	String getName();

	String getMypt();

	String getMygg();

	String getMydfdl();

	String getMywso();

	String getMydfge();

	String getClagtype();

	@Value("#{target.active == 1}")
	Boolean getActive();

	Integer getMaxtotalstudents();

	String getDescription();

	String getXclass();

	String getXsessiongroup();

	String getXcash();

	Long getTotalActiveStudents();

	String getTeUsername();

	String getTeFullName();

	String getTeAvatar();
}
