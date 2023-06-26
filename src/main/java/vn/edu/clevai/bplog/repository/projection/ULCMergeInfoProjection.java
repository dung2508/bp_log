package vn.edu.clevai.bplog.repository.projection;

public interface ULCMergeInfoProjection {
	String getCode();

	String getMygg();

	String getMydfdl();

	String getMydfge();

	String getMylctpk();

	String getMylcp();

	String getMypt();

	//	@Value("#{target.ismain == 1}")
	Boolean getIsmain();
}
