package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MapTEToCLagConfigEnum {
	CTE_BC("BC", 3),
	CTE_PM("PM", 4),
	CTE_TP10("TP10", 4),

	SO_GE("SO-GE", 15),
	TO_GE("TO-GE", 15);
	private String name;
	private int number;

	public static Map<String, Integer> getConfig() {
		Map<String, Integer> result = new HashMap<>();
		result.put(CTE_BC.name, CTE_BC.getNumber());
		result.put(CTE_PM.name, CTE_PM.getNumber());
		result.put(CTE_TP10.name, CTE_TP10.getNumber());
		return result;
	}
}
