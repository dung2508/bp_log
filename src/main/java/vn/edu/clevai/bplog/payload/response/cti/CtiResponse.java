package vn.edu.clevai.bplog.payload.response.cti;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CtiResponse {
	private Integer id;

	private String code;

	private String name;

	private String myCtt;

	private String myLo;
}
