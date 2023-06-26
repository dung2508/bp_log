package vn.edu.clevai.bplog.dto.sheet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnRampProductCRPPDTO {

	private String subjectName;
	private String gradeName;
	private String lessonName;
	private String shiftName;
	private String videoLink;
	private Set<String> bl4c1;
	private Set<String> bl4c2;
	private String sessionsC1;
	private String sessionsC2;
}
