package vn.edu.clevai.bplog.dto.sheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurriculumSheetDTO {

	private String timeWeek;
	private String currWeek;
	private String currShift;
	private String qGroup;

	public LocalDate getStartDate() {
		return LocalDate.parse(timeWeek.trim().split(" - ")[0], DateTimeFormatter.ofPattern("dd/MM/yy"));
	}

	public LocalDate getEndDate() {
		return LocalDate.parse(timeWeek.trim().split(" - ")[1], DateTimeFormatter.ofPattern("dd/MM/yy"));
	}

	public Integer getOrdering() {
		return Integer.parseInt(StringUtils.substringAfterLast(currShift.trim(), " "));
	}

	public String getName() {
		return qGroup.trim();
	}

}
