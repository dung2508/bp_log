package vn.edu.clevai.bplog.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GteRequiredProjection {
	private String fromDay;

	private String mydfge;

	private Integer totalTeacher;
	
	private Integer totalStudent;

}
