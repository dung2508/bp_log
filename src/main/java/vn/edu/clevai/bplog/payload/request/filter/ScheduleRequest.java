package vn.edu.clevai.bplog.payload.request.filter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.repository.BpDfdlDifficultygradeRepository;
import vn.edu.clevai.bplog.repository.BpGGGradeGroupRepository;
import vn.edu.clevai.common.api.payload.request.BaseFilter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleRequest extends BaseFilter {

	@NotEmpty
	private ArrayList<String> pt;
	private ArrayList<String> ggs;
	private ArrayList<String> dfdls;
	private String cady;

	@NotBlank
	@JsonAlias({"capCode", "cap-code", "cap", "cap_code"})
	private String capCode;
	private Boolean ggsIsNull;
	private Boolean dfdlsIsNull;

	private List<String> lcps;
	private String lcl;
	private String lck;

	public List<String> getGGWithDefault(BpGGGradeGroupRepository repo) {
		return !CollectionUtils.isEmpty(this.getGgs())
				? this.getGgs()
				: repo.findOnlyCode(true);
	}

	public List<String> getDffLWithDefault(BpDfdlDifficultygradeRepository repo) {
		return !CollectionUtils.isEmpty(this.getDfdls())
				? this.getDfdls()
				: repo.findOnlyCode(true);
	}
}
