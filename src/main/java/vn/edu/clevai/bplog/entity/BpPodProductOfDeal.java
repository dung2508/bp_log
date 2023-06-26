package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_pod_productofdeal")
@SuperBuilder
@Getter
@Setter
public class BpPodProductOfDeal extends BaseModel {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "myst")
	private String myst;

	@Column(name = "mycbod")
	private String mycbod;

	@Column(name = "mypt")
	private String mypt;

	@Column(name = "myprd")
	private String myprd;

	@Column(name = "description")
	private String description;

	@Column(name = "xdeal")
	private Long xdeal;

	@Column(name = "fromdate")
	private Date fromDate;

	@Column(name = "todate")
	private Date toDate;

	@ManyToOne
	@JoinColumn(name = "myst", referencedColumnName = "code", insertable = false, updatable = false)
	private BpUsiUserItem usi;

}
