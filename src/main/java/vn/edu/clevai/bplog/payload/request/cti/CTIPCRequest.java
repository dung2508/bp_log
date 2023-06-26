package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CTIPCRequest {
	String code;

	String name;

	Integer duration;

	List<CTIBl4QtRequest> bl4Qts;
}

