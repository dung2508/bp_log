package vn.edu.clevai.bplog.common;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.dto.bp.ValueDto;

@Component
public class LocalValueSaving {
	private final ThreadLocal<ValueDto> local = new ThreadLocal<>();

	public ThreadLocal<ValueDto> getLocal() {
		return local;
	}

	public ValueDto getValueDto() {
		return getLocal().get();
	}

	public void setValueDto(ValueDto valueDto) {
		getLocal().set(valueDto);
	}

	public String getBppCode() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getBppCode() : null;
	}

	public String getBpsCode() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getBpsCode() : null;
	}

	public String getBpeCode() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getBpeCode() : null;
	}

	public String getPublishBpe() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getPublishBpe() : null;
	}

	public String getUnPublishBpe() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getUnPublishBpe() : null;
	}

	public String getCuiCode() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getCuiCode() : null;
	}

	public String getActualBpe() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getActualBpe() : null;
	}

	public String getPodCase() {
		ValueDto valueDto = getValueDto();
		return valueDto != null ? valueDto.getPodCase() : null;
	}

	public void setBppCode(String bppCode, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setBppCode(bppCode);
		setValueDto(valueDto);
	}

	public void setBpsCode(String bpsCode, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setBpsCode(bpsCode);
		setValueDto(valueDto);
	}

	public void setBpeCode(String bpeCode, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setBpeCode(bpeCode);
		setValueDto(valueDto);
	}

	public void setUnPublishBpe(String bpeCode, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setUnPublishBpe(bpeCode);
		setValueDto(valueDto);
	}

	public void setPublishBpe(String bpeCode, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setPublishBpe(bpeCode);
		setValueDto(valueDto);
	}

	public void setActualBpe(String bpeCode, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setActualBpe(bpeCode);
		setValueDto(valueDto);
	}

	public void setCuiCode(String cuiCode, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setCuiCode(cuiCode);
		setValueDto(valueDto);
	}

	public void setPodCase(String podCase, boolean newValue) {
		ValueDto valueDto = newValue ? ValueDto.builder().build() :
				ObjectUtils.defaultIfNull(getValueDto(), ValueDto.builder().build());
		valueDto.setPodCase(podCase);
		setValueDto(valueDto);
	}

	public void doClean() {
		this.local.set(null);
	}

}
