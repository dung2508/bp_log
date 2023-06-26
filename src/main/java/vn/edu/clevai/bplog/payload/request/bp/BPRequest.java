package vn.edu.clevai.bplog.payload.request.bp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BPRequest {
	private BPUlcRequest bpUlcRequest;
	private BPGetContentsRequest bpGetContentsRequest;
	private BPScheduleUssRequest bpScheduleUssRequest;
	private BPCuiRequest bpCuiRequest;
	private BPCuiEventRequest bpCuiEventRequest;
}
