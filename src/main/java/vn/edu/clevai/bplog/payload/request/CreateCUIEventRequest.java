package vn.edu.clevai.bplog.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCUIEventRequest {
	private String lcetCode;
	private String cuiCode;
	private String usiCode;
}
