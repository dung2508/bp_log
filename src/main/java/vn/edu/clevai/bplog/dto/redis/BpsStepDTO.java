package vn.edu.clevai.bplog.dto.redis;

import java.io.Serializable;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class BpsStepDTO implements Serializable {
	private static final long serialVersionUID = -7784897468246817485L;
	
	@ToString.Include
	private String name;
	
	@ToString.Include
	private String code;
	
	@ToString.Include
	private String myprocess;

	private String bpstype;
}
