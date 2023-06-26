package vn.edu.clevai.bplog.dto.cep200;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CEP200WsoDTO {
	private Integer id;

	private String code;

	private String name;

	private Boolean monday;

	private Boolean tuesday;

	private Boolean wednesday;

	private Boolean thursday;

	private Boolean friday;

	private Boolean saturday;

	private Boolean sunday;
}
