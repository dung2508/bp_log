package vn.edu.clevai.bplog.dto.sheet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CtiPCSheetDTO {
	private String timeWeek;
	private String currWeek;
	private String contentCode;
	private String contentTitle;
	private String giveOn;
	private String duration;
	private String ctiBL4QT;
	private String qPieceNum;
}
