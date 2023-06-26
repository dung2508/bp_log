package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class CurriculumProgramPackageRequest {
	private String ayCode;

	private Timestamp time;

	private String ptCode;
}
