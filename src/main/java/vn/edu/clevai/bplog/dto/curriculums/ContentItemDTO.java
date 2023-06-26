package vn.edu.clevai.bplog.dto.curriculums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentItemDTO {
	private String code;
	private String name;
	private String parentContentItem;
	private String contentItemType;
	private String valueSet;
	private String fileBeginUrl;
	private String fileLocationUrl;
	private String gdocKey;
}
