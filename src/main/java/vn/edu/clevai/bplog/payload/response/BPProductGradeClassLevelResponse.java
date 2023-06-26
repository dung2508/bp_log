package vn.edu.clevai.bplog.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.clevai.bplog.entity.BpDfdlDifficultygrade;
import vn.edu.clevai.bplog.entity.BpPTProductType;
import vn.edu.clevai.bplog.payload.response.teacher.detail.GradeDetail;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPProductGradeClassLevelResponse {

	@JsonProperty("products")
	private List<BpPTProductType> products;

	@JsonProperty("grades")
	private List<GradeDetail> grades;

	@JsonProperty("class_levels")
	private List<BpDfdlDifficultygrade> classLevels;

}
