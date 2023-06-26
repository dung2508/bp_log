package vn.edu.clevai.bplog.payload.request.bp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQARequest {
	private MultipartFile file;
	private String questionText;
	private String claGCode;
	private String xclass;
	private Timestamp actualTimeFet;
	private String answerText;
	private String currentUsi;
	private String cuiCode;

}
