package vn.edu.clevai.bplog.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FindPodByClagRequest {
	List<String> clagCode;
	List<String> ust;
}
