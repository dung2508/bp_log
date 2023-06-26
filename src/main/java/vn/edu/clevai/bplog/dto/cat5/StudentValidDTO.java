package vn.edu.clevai.bplog.dto.cat5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentValidDTO {
	private Long studentId;
	private String username;
	private Long podId;
	private String firstname;
	private String lastname;
	private String product;
}

