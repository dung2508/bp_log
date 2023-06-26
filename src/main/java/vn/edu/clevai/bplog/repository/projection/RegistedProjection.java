package vn.edu.clevai.bplog.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistedProjection {
	private String dateInWeek;

	private Integer totalTeacher;
}
