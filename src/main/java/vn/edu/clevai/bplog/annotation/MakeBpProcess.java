package vn.edu.clevai.bplog.annotation;

import vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
public @interface MakeBpProcess {
	BppProcessTypeEnum process();
	String parentProcess();

}
