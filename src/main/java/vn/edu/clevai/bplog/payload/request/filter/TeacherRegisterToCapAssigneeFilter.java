package vn.edu.clevai.bplog.payload.request.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRegisterToCapAssigneeFilter {

	private Long userAccountTypeId;

	private Long productId;

	private Long gradeId;

	private Long subjectId;

	private Long classLevelId;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate startDate;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate endDate;

	private List<Long> teacherIds;

//	private BigDecimal fromAverageRating;

//	private BigDecimal toAverageRating;

//	private BigDecimal fromCanceledRate;

//	private BigDecimal toCanceledRate;

	private Long fromTotalScheduleRegister;

	private Long toTotalScheduleRegister;

	private Long fromTotalArrangedIsMain;

	private Long toTotalArrangedIsMain;

	private Long fromTotalArrangedIsBackup;

	private Long toTotalArrangedIsBackup;

//	private List<Long> teacherRanks;

}