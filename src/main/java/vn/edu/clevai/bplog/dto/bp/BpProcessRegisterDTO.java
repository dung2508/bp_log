package vn.edu.clevai.bplog.dto.bp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.clevai.bplog.common.enumtype.BppRegisterEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpProcessRegisterDTO {
	BppRegisterEnum step3BppEnum;
	BppRegisterEnum step4BppEnum;
	BppRegisterEnum step5BppEnum;
}
