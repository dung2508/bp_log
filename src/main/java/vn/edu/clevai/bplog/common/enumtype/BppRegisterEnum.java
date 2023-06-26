package vn.edu.clevai.bplog.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum BppRegisterEnum {
	GTE1_SETTING("Setting", "BPPRegister1-GTE", "BPPRegister1-Setting-GTE", 1, "GTE"),
	GTE2_REQUEST("Request", "BPPRegister2-GTE", "BPPRegister2-Request-GTE", 2, "GTE"),
	GTE3_CONFIRM("Confirm", "BPPRegister3-GTE", "BPPRegister3-Confirm-GTE", 3, "GTE"),
	GTE4_ALLOCATE("Allocate", "BPPRegister4-GTE", "BPPRegister4-Allocate-GTE", 4, "GTE"),
	GTE5_TRANSFORM("Transform", "BPPRegister5-GTE", "BPPRegister5-Transform-GTE", 5, "GTE"),
	DTE1_SETTING("Setting", "BPPRegister1-DTE", "BPPRegister1-Setting-DTE", 1, "DTE"),
	DTE2_REQUEST("Request", "BPPRegister2-DTE", "BPPRegister2-Request-DTE", 2, "DTE"),
	DTE3_CONFIRM("Confirm", "BPPRegister3-DTE", "BPPRegister3-Confirm-DTE", 3, "DTE"),
	DTE4_ALLOCATE("Allocate", "BPPRegister4-DTE", "BPPRegister4-Allocate-DTE", 4, "DTE"),
	DTE5_TRANSFORM("Transform", "BPPRegister5-DTE", "BPPRegister4-Transform-DTE", 5, "DTE"),
	CTE1_SETTING("Setting", "BPPRegister1-CTE", "", 1, "CTE"),
	CTE2_REQUEST("Request", "BPPRegister2-CTE", "", 2, "CTE"),
	CTE3_CONFIRM("Confirm", "BPPRegister3-CTE", "", 3,"CTE"),
	CTE4_ALLOCATE("Allocate", "BPPRegister4-CTE", "", 4, "CTE"),
	CTE5_TRANSFORM("Transform", "BPPRegister5-CTE", "", 5, "CTE"),
	SO1_SETTING("Setting", "BPPRegister1-SO", "", 1, "SO"),
	SO2_REQUEST("Request", "BPPRegister2-SO", "", 2, "SO"),
	SO3_CONFIRM("Confirm", "BPPRegister3-SO", "", 3, "SO"),
	SO4_ALLOCATE("Allocate", "BPPRegister4-SO", "", 4, "SO"),
	SO5_TRANSFORM("Transform", "BPPRegister5-SO", "",5, "SO"),
	TO1_SETTING("Setting", "BPPRegister1-TO", "", 1, "TO"),
	TO2_REQUEST("Request", "BPPRegister2-TO", "", 2, "TO"),
	TO3_CONFIRM("Confirm", "BPPRegister3-TO", "", 3, "TO"),
	TO4_ALLOCATE("Allocate", "BPPRegister4-TO", "",4, "TO"),
	TO5_TRANSFORM("Transform", "BPPRegister5-TO", "", 5, "TO"),
	DST1_SETTING("Setting", "BPPRegister1-DST", "", 1, "DST"),
	DST2_REQUEST("Request", "BPPRegister2-DST", "", 2, "DST"),
	DST3_CONFIRM("Confirm", "BPPRegister3-DST", "", 3, "DST"),
	DST4_ALLOCATE("Allocate", "BPPRegister4-DST", "", 4, "DST"),
	DST5_TRANSFORM("Transform", "BPPRegister5-DST", "", 5, "DST"),
	LTE1_SETTING("Setting", "BPPRegister1-LTE", "BPPRegister1-Setting-LTE", 1, "LTE"),
	LTE2_REQUEST("Request", "BPPRegister2-LTE", "BPPRegister2-Request-LTE", 2, "LTE"),
	LTE3_CONFIRM("Confirm", "BPPRegister3-LTE", "BPPRegister3-Confirm-LTE", 3, "LTE"),
	LTE4_ALLOCATE("Allocate", "BPPRegister4-LTE", "BPPRegister4-Allocate-LTE", 4, "LTE"),
	LTE5_TRANSFORM("Transform", "BPPRegister5-LTE", "BPPRegister4-Transform-LTE", 5, "LTE"),
	QO1_SETTING("Setting", "BPPRegister1-QO", "BPPRegister1-Setting-QO", 1, "QO"),
	QO2_REQUEST("Request", "BPPRegister2-QO", "BPPRegister2-Request-QO", 2, "QO"),
	QO3_CONFIRM("Confirm", "BPPRegister3-QO", "BPPRegister3-Confirm-QO", 3, "QO"),
	QO4_ALLOCATE("Allocate", "BPPRegister4-QO", "BPPRegister4-Allocate-QO", 4, "QO"),
	QO5_TRANSFORM("Transform", "BPPRegister5-QO", "BPPRegister4-Transform-QO", 5, "QO"),
	;
	private final String action;
	private final String name;
	private final String likeStatement;
	private final Integer step;
	private final String ust;

	public static BppRegisterEnum findByUstAndStep(String ust, Integer step) {
		return Stream.of(values())
				.filter(bppRegisterEnum -> bppRegisterEnum.getStep().equals(step))
				.filter(bppRegisterEnum -> bppRegisterEnum.getUst().equals(ust))
				.findFirst()
				.orElse(null);
	}
	
	public static Integer extractStep(String myppp) {
		String regex = "(.*)(BPPRegister)(\\d+)(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher match = p.matcher(myppp);
		if (match.matches()) {
			return Integer.parseInt(match.group(3));
		}
		return null;
	}
}
