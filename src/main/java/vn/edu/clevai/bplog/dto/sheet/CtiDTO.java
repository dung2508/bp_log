package vn.edu.clevai.bplog.dto.sheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CtiDTO {
	private String timeWeek;

	private Integer currWeek;

	private String code;

	private String title;
	
	private Integer giveOn;

	private Integer duration;

	private String bl4QT;

	private Integer qPieceNum;
}
