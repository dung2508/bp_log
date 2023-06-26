package vn.edu.clevai.bplog.common;

import lombok.*;
import org.springframework.util.CollectionUtils;
import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpProcessCommon {
	private BPLogProcessEnum process;
	private String processCode;
	private String groupCode;
	private List<VariableConfig> listConfig;

	@Data
	@Builder
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	public static class VariableConfig {
		@EqualsAndHashCode.Include
		private String variable;

		@EqualsAndHashCode.Include
		private String columnName;
	}


	public VariableConfig findByVariableName(String variable) {
		if (CollectionUtils.isEmpty(listConfig)) {
			return null;
		}
		return listConfig.stream().filter(k -> k.getVariable().equals(variable)).findAny().orElse(null);
	}
}
