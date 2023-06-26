package vn.edu.clevai.bplog.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetClagUlcRequest {
	private String clagCode;
	private String ulcCode;
}
