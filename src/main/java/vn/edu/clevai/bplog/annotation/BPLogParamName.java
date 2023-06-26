package vn.edu.clevai.bplog.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD, ElementType.PARAMETER })
@Retention(RUNTIME)
public @interface BPLogParamName {
	String value();
}
