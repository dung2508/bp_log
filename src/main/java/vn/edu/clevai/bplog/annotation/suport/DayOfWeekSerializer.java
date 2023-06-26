package vn.edu.clevai.bplog.annotation.suport;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.Getter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by hanhhv
 * User: hanhhv
 * Date: 27/05/2023
 * Time: 12:12 PM
 * From the result of dayOfWeek function in mysql -> parse to the corresponding thing
 */
public class DayOfWeekSerializer extends JsonSerializer<String> {

	@Override
	public void serialize(String dayOfWeekStr, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		String result = dayOfWeekStr != null ? DAY_OF_WEEK_TEMPLATE.previewVal(dayOfWeekStr) : "";
		jsonGenerator.writeString(result);
	}


	@Getter
	public static enum DAY_OF_WEEK_TEMPLATE {
		MONDAY("2", "MONDAY"),
		TUESDAY("3", "TUESDAY"),
		WEDNESDAY("4", "WEDNESDAY"),
		THURSDAY("5", "THURSDAY"),
		FRIDAY("6", "FRIDAY"),
		SATURDAY("7", "SATURDAY"),
		SUNDAY("1", "SUNDAY"),
		;

		private final String inMysql;
		private final String beautyVal;

		DAY_OF_WEEK_TEMPLATE(String inMysql, String beautyVal) {
			this.inMysql = inMysql;
			this.beautyVal = beautyVal;
		}

		public static String previewVal(String val) {
			return Arrays.stream(DAY_OF_WEEK_TEMPLATE.values()).filter(fil -> fil.getInMysql().equals(val))
					.map(DAY_OF_WEEK_TEMPLATE::getBeautyVal).findFirst().orElse(null);
		}
	}
}
