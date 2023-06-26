package vn.edu.clevai.bplog.utils;

import java.lang.reflect.Field;

public class ReflectionUtil {

	public static <T> T getFieldVal(Object source, String name) throws NoSuchFieldException, IllegalAccessException {
		Field field = source.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return (T) field.get(source);

	}
}
