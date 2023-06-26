package vn.edu.clevai.bplog.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignVideosRequest {

	@JsonIgnore
	private String usiCode;

	@JsonProperty("assign_videos")
	private List<AssignVideoRequest> assignVideos;

	@JsonProperty("bet_time")
	private Timestamp betTime;

	@JsonProperty("fet_time")
	private Timestamp fetTime;

}