package vn.edu.clevai.bplog.payload.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
	private Integer id;
	private String code;
	private String name;
}
