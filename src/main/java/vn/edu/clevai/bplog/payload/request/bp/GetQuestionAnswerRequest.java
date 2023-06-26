package vn.edu.clevai.bplog.payload.request.bp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.common.api.payload.request.BaseFilter;

import java.sql.Timestamp;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GetQuestionAnswerRequest extends BaseFilter {
	private String currentUsi;
	private String gg;
	private String status;
	private Boolean sla;
	private Boolean isExpired;
	private Timestamp fromTime;
	private Timestamp toTime;
	private Boolean filterByTime;

	private String lck;
	private String lcpLike;

}
