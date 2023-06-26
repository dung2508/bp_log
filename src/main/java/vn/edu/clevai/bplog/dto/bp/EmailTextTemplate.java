package vn.edu.clevai.bplog.dto.bp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.clevai.bplog.entity.logDb.BpChptCheckProcessTemp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTextTemplate {
	private String chsiCode;
	private String chstCode;
	private String chptCode;
	private BpChptCheckProcessTemp chpt;
}
