package vn.edu.clevai.bplog.annotation;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target({METHOD})
//@Retention(RUNTIME)
public @interface WriteUnitTestLog {
}
