package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@SuperBuilder
@Table(name = "bp_video_dilive_config")
public class BpVideoDiliveConfig extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String code;

	private String name;

	private String mypt;

	private String mygg;

	private String mydfdl;

	@Column(name = "start_url")
	private String startUrl;

	@Column(name = "secret_key")
	private String secretKey;

	@Column(name = "join_url")
	private String joinUrl;
}
