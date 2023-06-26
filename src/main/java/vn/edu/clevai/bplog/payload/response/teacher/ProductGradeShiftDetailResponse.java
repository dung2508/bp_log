package vn.edu.clevai.bplog.payload.response.teacher;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.clevai.bplog.payload.response.teacher.detail.GradeDetail;
import vn.edu.clevai.bplog.payload.response.teacher.detail.ProductDetail;
import vn.edu.clevai.bplog.payload.response.teacher.detail.ShiftDetail;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductGradeShiftDetailResponse {
	@JsonProperty("products")
	private List<ProductDetail> products;
	
	@JsonProperty("grades")
	private List<GradeDetail> grades;
	
	@JsonProperty("time_slots")
	private List<ShiftDetail> shifts;
}
