package vn.edu.clevai.bplog.dto.cep200;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClagInputDTO {
	
	private String clagType;
	
	private CEP200WsoDTO wsoDTO;

	private CEP200GradeGroupDTO ggDto;

	private CEP200DfdlDTO dfdlDto;
	
	private CEP200PTDTO ptDto;
	
	private CEP200DfgeDTO dfgeDTO;
	
	private Integer classCodeIndex;
}
