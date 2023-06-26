package vn.edu.clevai.bplog.payload.request.cti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class CTIPCBl4QTRequest {
	private String bl4Qt;

	private Integer numberOfQuizs;

	Set<String> bl5_qps;
}

