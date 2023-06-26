package vn.edu.clevai.bplog.payload.response.logdb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpChptCheckProcessTempResponse {

	private Integer id;

	private String chptCode;

	private String name;

	private String myLct;

	private String myChptType;

	private String myLcEg;

	private String myLcEt;

	private String myLctFilter;

	private String triggerUserType;

	private String checkerUserType;

	private Timestamp createdAt;

	private Timestamp updatedAt;

}
