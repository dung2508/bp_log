package vn.edu.clevai.bplog.payload.response.cep100;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CEP100LearningScheduleClassResponse {
	private String name;

	private Integer dayOfWeek;
}
