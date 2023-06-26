package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bp_cti_contentitem")
public class ContentItem extends BaseModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	private String code;

	private String name;

	@Column(name = "myparentcti")
	private String parentContentItem;

	@Column(name = "myctt")
	private String contentItemType;

	@Column(name = "myvalueset")
	private String valueSet;

	@Column(name = "filebeginurl")
	private String fileBeginUrl;

	@Column(name = "filelocationurl")
	private String fileLocationUrl;

	@Column(name = "gdockey")
	private String gdocKey;
}
