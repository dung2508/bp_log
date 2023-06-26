package vn.edu.clevai.bplog.entity.logDb;

import lombok.*;
import lombok.experimental.SuperBuilder;
import vn.edu.clevai.bplog.entity.BaseModel;
import vn.edu.clevai.bplog.entity.BpUsiUserItem;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bp_cui_content_user_ulc_instance")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class BpCuiContentUserUlc extends BaseModel implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 4630622048726801890L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "code", unique = true)
	@EqualsAndHashCode.Include
	private String code;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "myulc", referencedColumnName = "code", nullable = false)
	private BpUniqueLearningComponent myUlc;

	@Column(name = "myulc", insertable = false, updatable = false)
	private String myUlcCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "myusi", referencedColumnName = "code", nullable = false)
	private BpUsiUserItem myUsi;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mycti", referencedColumnName = "code")
	private BpContentItem myCti;

	@Column(name = "mycti", insertable = false, updatable = false)
	private String myCtiCode;
	private String description;

	@Column(name = "mypodp")
	private String myPodp;

	private Boolean published;

	@Column(name = "myulc", insertable = false, updatable = false)
	private String myulc;

	@Column(name = "myusi", insertable = false, updatable = false)
	private String myusi;

	@Column(name = "myusi", insertable = false, updatable = false)
	private String myUsiCode;

	@Column(name = "mybps")
	private String mybps;

	@Column(name = "publishbps")
	private String publishbps;

	@Column(name = "unpublishbps")
	private String unpublishbps;

}
