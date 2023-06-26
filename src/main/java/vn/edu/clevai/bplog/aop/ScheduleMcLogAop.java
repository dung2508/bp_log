package vn.edu.clevai.bplog.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.clevai.common.api.model.DebuggingDTO;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class ScheduleMcLogAop {
	@Autowired
	private ObjectMapper mapper;

	@Around(value = "@annotation(vn.edu.clevai.bplog.annotation.ScheduleMcLog)")
	public Object writeScheduleMcLog(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();

		String methodName = signature.getName();

		String[] parameterNames = signature.getParameterNames();
		Object[] parameterValues = joinPoint.getArgs();

		String startingMessage = "Started " + methodName;
		if (parameterNames.length > 0) {
			Map<String, Object> arguments = new HashMap<>();

			IntStream
					.range(0, parameterNames.length)
					.forEach(
							i -> {
								String n = parameterNames[i];
								Object v = parameterValues[i];

								if (
										Objects.isNull(v) || ClassUtils.isPrimitiveOrWrapper(v.getClass())
												|| v.getClass().equals(String.class)
								) {
									arguments.put(n, v);
								} else {
									try {
										Field f = v.getClass().getDeclaredField("code");

										f.setAccessible(true);

										arguments.put(n, f.get(v));
									} catch (Exception ignored) {
										arguments.put(n, v);
									}
								}
							}
					);

			startingMessage += " with parameters: " + mapper.writeValueAsString(arguments);
		}

		log.info(startingMessage);

		Object returnValue;
		try {
			returnValue = joinPoint.proceed();
		} catch (Exception e) {
			log.error("Error when process cause exception {}", DebuggingDTO.build(e));
			throw e;
		}

		return returnValue;
	}

//	@Around(value = "@annotation(vn.edu.clevai.bplog.annotation.WriteUnitTestLog)")
//	public Object writeUnitTestLog(ProceedingJoinPoint joinPoint) throws Throwable {
//		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//
//		String methodName = signature.getName();

//		if (joinPoint.getArgs().length == 1 && !ClassUtils.isPrimitiveOrWrapper(joinPoint.getArgs()[0].getClass())
//				&& joinPoint.getArgs()[0].getClass() != String.class) {
//			unitTestLog.setInputValue(
//					returnValueAsString(joinPoint.getArgs()[0].getClass().getSimpleName(), joinPoint.getArgs()[0]));
//		} else {
//			IntStream.range(0, joinPoint.getArgs().length).forEach(i -> {
//				String parameterName = signature.getParameterNames()[i];
//				try {
//					Field field = unitTestLog.getClass().getDeclaredField(parameterName);
//
//					field.setAccessible(true);
//					field.set(unitTestLog, String.valueOf(joinPoint.getArgs()[i]));
//				} catch (NoSuchFieldException | IllegalAccessException ignored) {
//				}
//			});
//		}
//
//		Object returnValue;
//		try {
//			returnValue = joinPoint.proceed();
//		} catch (Exception e) {
//			log.error("Error when process joinPoint {} cause exception {}", method, DebuggingDTO.build(e));
//			throw e;
//		}
//
//		unitTestLog.setCollectTime(DateUtils.now());
//
//		try {
//			unitTestLog.setReturnValue(returnValueAsString(signature.getReturnType().getSimpleName(), returnValue));
//
//			if (returnValue != null) {
//				Class<?> klass = returnValue.getClass();
//
//				if (ClassUtils.isPrimitiveOrWrapper(klass) || klass == String.class) {
//					unitTestLog.setOutputCode(String.valueOf(returnValue));
//				} else {
//					try {
//						Field field = klass.getDeclaredField("code");
//						field.setAccessible(true);
//
//						Object v = field.get(returnValue);
//
//						unitTestLog.setOutputCode(String.valueOf(v));
//					} catch (NoSuchFieldException ignored) {
//					}
//
//					for (Field field : klass.getDeclaredFields()) {
//						String outputFieldName = "output" + StringUtils.capitalize(field.getName());
//						field.setAccessible(true);
//						Object output = field.get(returnValue);
//
//						try {
//							Field outputField = unitTestLog.getClass().getDeclaredField(outputFieldName);
//							outputField.setAccessible(true);
//
//							outputField.set(unitTestLog, String.valueOf(output));
//						} catch (NoSuchFieldException ignored) {
//						}
//					}
//				}
//			}
//
//			unitTestLogRepository.save(unitTestLog);
//		} catch (Exception e) {
//			log.error("{}", e.getMessage(), e);
//		}
//
//		return returnValue;
//	}
}
