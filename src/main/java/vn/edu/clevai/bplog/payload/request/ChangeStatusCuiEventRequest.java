package vn.edu.clevai.bplog.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStatusCuiEventRequest {
	private String cuiEventCode;
	private Boolean publishStatus;
}
