package vn.edu.clevai.bplog.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DteRequiredProjection {
	private String fromDay;

	private Integer totalTeacher;
}
