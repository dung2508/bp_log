package vn.edu.clevai.bplog.payload.request.teacher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyTeacherRequest {
	private String cady;
	private String oldUsi;
	private String newUsi;
	private String clag;
}
