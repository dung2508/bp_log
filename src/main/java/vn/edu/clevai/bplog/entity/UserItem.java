package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "bp_usi_useritem")
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserItem extends BaseModel {
	@Id
	private Integer id;

	private String code;

	private String username;

	private String password;

	@Column(name = "password_salt")
	private String passwordSalt;

	@Column(name = "password_reminder_token")
	private String passwordReminderToke;

	@Column(name = "email_confirmation_token")
	private String emailConfirmationToken;

	@Column(name = "myust")
	private String myUst;

	@Column(name = "myparent")
	private String myParent;

	private String firstname;

	private String lastname;

	private String fullname;

	private String avatar;

	private Date birthday;

	private String job;

	private String address;

	@Column(name = "district_id")
	private int districtId;

	private String phone;

	private String email;

	private String identity_number;

	@Column(name = "identity_issue_date")
	private Date identityIssueDate;

	@Column(name = "identity_issue_place")
	private String identitiyIssuePlace;

	@Column(name = "personal_tax_number")
	private String personalTaxNumber;
	private String notes;

	@Column(name = "displayname")
	private String displayName;
}
