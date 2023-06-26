package vn.edu.clevai.bplog.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bp_usi_useritem")
@SuperBuilder
@Getter
@Setter
public class BpUsiUserItem extends BaseModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -706885895895708611L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code")
	private String code;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "password_salt")
	private String passwordSalt;

	@Column(name = "password_reminder_token")
	private String passwordReminderToken;

	@Column(name = "password_reminder_expire")
	private Timestamp passwordReminderExpire;

	@Column(name = "email_confirmation_token")
	private String emailConfirmationToken;

	@Column(name = "myust")
	private String myust;

	@Column(name = "myparent")
	private String myparent;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "fullname")
	private String fullname;

	@Column(name = "avatar")
	private String avatar;

	@Column(name = "birthday")
	private Date birthday;

	@Column(name = "job")
	private String job;

	@Column(name = "address")
	private String address;

	@Column(name = "email")
	private String email;

	@Column(name = "gender")
	private Integer gender;

	@Column(name = "phone")
	private String phone;

	@Column(name = "scheduleOM")
	private boolean scheduleOM;
}
