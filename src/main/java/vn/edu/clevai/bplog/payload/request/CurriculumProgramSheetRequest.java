package vn.edu.clevai.bplog.payload.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class CurriculumProgramSheetRequest {
	private String crppCode;

	private String ggCode;
}
