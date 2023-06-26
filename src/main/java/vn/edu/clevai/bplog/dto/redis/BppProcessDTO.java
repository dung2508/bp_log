package vn.edu.clevai.bplog.dto.redis;

import java.io.Serializable;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class BppProcessDTO implements Serializable {
	private static final long serialVersionUID = 8412249042784507031L;
	
	private String name;

	private String code;

	private String myparent;

	private String bpptype;
}
