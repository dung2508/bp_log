package vn.edu.clevai.bplog.dto.redis;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class BpeEventDTO implements Serializable {
	private static final long serialVersionUID = 3136666400053594693L;
	
	@ToString.Include
	private String code;

	@ToString.Include
	private String name;

	@ToString.Include
	private String bpetype;
	
	@ToString.Include
	private String mybps;
}
