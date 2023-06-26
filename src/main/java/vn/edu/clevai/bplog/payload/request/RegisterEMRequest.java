package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterEMRequest {

	private String usi;
	private String cady;
	private String gg;
	private String dfdl;
	private String lcet;
	private String lct;
	private String chrt;

}
