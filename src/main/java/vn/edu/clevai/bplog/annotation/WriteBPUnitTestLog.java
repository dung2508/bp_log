package vn.edu.clevai.bplog.annotation;

import vn.edu.clevai.bplog.common.enumtype.BPLogProcessEnum;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target({METHOD})
//@Retention(RUNTIME)
public @interface WriteBPUnitTestLog {
	BPLogProcessEnum value();
}
