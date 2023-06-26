package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CTIHRGRequest {
	List<CTIAQRRequest> ctiAqrs;
}

