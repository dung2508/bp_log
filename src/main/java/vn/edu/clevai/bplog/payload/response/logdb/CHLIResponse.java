package vn.edu.clevai.bplog.payload.response.logdb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CHLIResponse {
	private Integer id;

	private String code;

	private String subCode;

	private String name;

	private String myChsi;

	private String myParentChlt;

	private String chltDo;

	private String doNot;

	private String correctExample;

	private String incorrectExample;

	private String scoreType1;

	private String score1;

	private String scoreType2;

	private String score2;

	private String description;

	private String myParentChli;

	private Timestamp createAt;

	private Timestamp updateAt;
}
