package vn.edu.clevai.bplog.common;

import org.apache.commons.lang3.StringUtils;

import vn.edu.clevai.bplog.dto.email.ChsiCheckStepEmailDTO;

public class EmailContentDecorate {

	public static String buildEmailTitle(ChsiCheckStepEmailDTO dto) {
		StringBuilder builder = new StringBuilder(dto.getChstName());
		if(!StringUtils.isBlank(dto.getChrtCode())) {
			builder.append("-").append(dto.getChrtCode());
		}
		return builder.toString();
	}

	public static String buildEmailContent(ChsiCheckStepEmailDTO dto) {
		StringBuilder builder = new StringBuilder("CheckProcess: '");
		builder.append(dto.getChptName() + "'").append("<br>");
		builder.append("ULC: '").append(dto.getUlcCode()).append("' - TriggerUST: 'CHPT.").append(dto.getTriggerUst())
				.append("'").append("<br>");
		builder.append("CheckStep: '").append(dto.getChstName()).append(" -'").append(dto.getChrtCode()).append("'-'")
				.append(dto.getChriUsi()).append("'").append("<br>");
		builder.append("Link:").append(dto.getLink()).append(dto.getChliCode());
		return builder.toString();
	}
}
