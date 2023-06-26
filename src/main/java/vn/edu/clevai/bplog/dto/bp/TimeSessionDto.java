package vn.edu.clevai.bplog.dto.bp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSessionDto {
	private String myusi;
	private Long start;
	private String end;
}
