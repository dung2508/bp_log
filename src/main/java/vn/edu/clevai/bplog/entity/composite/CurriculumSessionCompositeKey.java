package vn.edu.clevai.bplog.entity.composite;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurriculumSessionCompositeKey {
	@Column(name = "curriculum_shift_code")
	private String curriculumShiftCode;

	@Column(name = "dfdl")
	private String dfdl;

	@Column(name = "lct_shift_type")
	private String lctShiftType;

	@Column(name = "session_no")
	private String sessionNo;

	@Column(name = "dfge")
	private String dfge;
}
