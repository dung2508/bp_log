package vn.edu.clevai.bplog.payload.response.teacher.tocap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponse {
	private Long id;

	@JsonProperty("code")
	private String code;

	@JsonProperty("student_id")
	private Long studentId;

	@JsonProperty("email")
	private String email;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	@JsonProperty("full_name")
	private String fullName;

	@JsonProperty("display_name")
	private String displayName;

	@JsonProperty("username")
	private String username;

	@JsonProperty("district")
	private String district;

	@JsonProperty("school")
	private String school;

	@JsonProperty("school_year")
	private String schoolYear;

	@JsonProperty("city")
	private String city;

	@JsonProperty("grade")
	private String grade;

	@JsonProperty("birthday")
	private Date birthday;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("address")
	private String address;

	@JsonProperty("user_account_type")
	private String userAccountType;

	@JsonProperty("created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Timestamp createdAt;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Timestamp updatedAt;
}
