package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_cti_contentitem")
@Getter
@Setter
@SuperBuilder
public class BpContentItem extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5311345292466653147L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "duration")
	private Integer duration;

	@Column(name = "myctt")
	private String myCtt;

	@Column(name = "myvalueset")
	private String myValueSet;

	@Column(name = "filebeginurl")
	private String fileBeginUrl;

	@Column(name = "filelocationurl")
	private String fileLocationUrl;

	@Column(name = "gdockey")
	private String gDocKey;

	@Column(name = "myparentcti")
	private String myParent;

	@Column(name = "qpiecenum")
	private String qPieceNum;

	@Column(name = "mylo")
	private String myLo;

	@Column(name = "mybl5qp")
	private String myBl5Qp;

	@Column(name = "zoomid")
	private String zoomId;

	@Column(name = "starturl")
	private String startUrl;

	@Column(name = "joinurl")
	private String joinUrl;

	@Column(name = "secretkey")
	private String secretKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "myparentcti", referencedColumnName = "code",
			nullable = false, insertable = false, updatable = false)
	private BpContentItem myParentCti;

	@OneToMany(mappedBy = "myParentCti", cascade = CascadeType.ALL)
	private Set<BpContentItem> subCti;

	private Boolean published;

}
