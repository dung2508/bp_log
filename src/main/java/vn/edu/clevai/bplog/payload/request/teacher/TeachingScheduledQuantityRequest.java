package vn.edu.clevai.bplog.payload.request.teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeachingScheduledQuantityRequest {

	private Integer productId;

	private Integer gradeId;

	private Integer classLevelId;

	private String mybpp;

	private String myust;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

}