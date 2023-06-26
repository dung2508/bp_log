package vn.edu.clevai.bplog.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.annotation.BPLogParamName;
import vn.edu.clevai.bplog.common.BpProcessCommon;
import vn.edu.clevai.bplog.common.BpProcessCommon.VariableConfig;
import vn.edu.clevai.bplog.common.BpProcessMapping;
import vn.edu.clevai.bplog.repository.BPUnitTestLogRepository;
import vn.edu.clevai.common.api.model.DebuggingDTO;
import vn.edu.clevai.common.api.util.DateUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Aspect
@Component
public class WriteBPUnitTestLogAop {
	@Autowired
	private BPUnitTestLogRepository bpUnitTestLogRepository;

	@Autowired
	private BpProcessMapping processMapping;

	@Autowired
	private ObjectMapper mapper;

	@Around(value = "@annotation(vn.edu.clevai.bplog.annotation.WriteBPUnitTestLog)")
	public Object writeUnitTestLog(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();

		Object returnValue;
		try {
			returnValue = joinPoint.proceed();
		} catch (Exception e) {
			String method = String.format("%s::%s", signature.getDeclaringType().getSimpleName(), signature.getName());
			log.error("Error when process joinPoint {} cause exception {}", method, DebuggingDTO.build(e));
			throw e;
		}

//		WriteBPUnitTestLog parameterGroupNo = signature.getMethod().getDeclaredAnnotation(WriteBPUnitTestLog.class);
//		if (!Objects.isNull(parameterGroupNo)) {
//			BpProcessCommon process = processMapping.findByProcessEnumAndSignature(parameterGroupNo.value(), signature, joinPoint.getArgs());
//			if (!Objects.isNull(process)) {
//				BPUnitTestLog titleRow = BPUnitTestLog.builder().rowType("Title")
//						.myTimestamp(new Timestamp(System.currentTimeMillis())).funcName(signature.getName())
//						.processId(MDC.get("X-B3-TraceId")).processCode(process.getProcessCode())
//						.paramGroupNo(process.getGroupCode()).build();
//
//				BPUnitTestLog valueRow = BPUnitTestLog.builder().rowType("Value")
//						.myTimestamp(new Timestamp(System.currentTimeMillis())).funcName(signature.getName())
//						.processId(MDC.get("X-B3-TraceId")).processCode(process.getProcessCode())
//						.paramGroupNo(process.getGroupCode()).build();
//
//				try {
//					// Save input parameter
//					Map<VariableConfig, String> mapConfig = new HashMap<>();
//					mapConfig.putAll(argsToMapConfig(joinPoint.getArgs(), signature, process));
//					if (Objects.nonNull(returnValue) && !canTakeValue(returnValue)) {
//						mapConfig.putAll(objectToVConfig(returnValue, process));
//					}
//					for (Map.Entry<VariableConfig, String> entry : mapConfig.entrySet()) {
//						VariableConfig variable = entry.getKey();
//						String col = variable.getColumnName();
//						assignValue(col, variable.getVariable(), titleRow);
//						assignValue(col, entry.getValue(), valueRow);
//					}
//					bpUnitTestLogRepository.saveAll(Arrays.asList(titleRow, valueRow));
//				} catch (Exception e) {
//					log.error("Error when save BPUnitTestLog {}", e.getMessage(), e);
//				}
//			} else {
//				log.warn("Can not found config of group no {}", parameterGroupNo.value());
//			}
//
//		} else {
//			log.warn("Can not found config of group no");
//		}

		return returnValue;
	}

	private void assignValue(String fName, Object value, Object targetObj) throws Exception {
		List<Field> listField = FieldUtils.getAllFieldsList(targetObj.getClass());
		for (Field f : listField) {
			if (f.getName().equalsIgnoreCase(fName)) {
				f.setAccessible(true);
				f.set(targetObj, value);
			}
		}
	}

	private Map<VariableConfig, String> argsToMapConfig(Object[] args, MethodSignature signature,
														BpProcessCommon process) throws Exception {
		Map<VariableConfig, String> results = new HashMap<BpProcessCommon.VariableConfig, String>();
		for (int i = 0; i < args.length; i++) {
			Object objArg = args[i];
			if (Objects.nonNull(objArg)) {
				results.putAll(argsMap(args[i], signature, i, process));
			}
		}
		return results;
	}

	private Map<VariableConfig, String> argsMap(Object obj, MethodSignature signature, int i, BpProcessCommon process)
			throws Exception {
		Map<VariableConfig, String> results = new HashMap<BpProcessCommon.VariableConfig, String>();
		if (canTakeValue(obj)) {
			String param = getParam(signature.getMethod().getParameterAnnotations(), i);
			if (StringUtils.isBlank(param)) {
				param = signature.getParameterNames()[i];
			}
			VariableConfig vConfig = process.findByVariableName(param);
			if (Objects.nonNull(vConfig)) {
				results.put(vConfig, toStringValue(obj));
			} else {
				log.warn("Cant process because cant found mapping");
			}
		} else {
			results.putAll(objectToVConfig(obj, process));
		}

		return results;
	}

	private String getParam(Annotation[][] methodAnno, int index) {
		int i = 0;
		for (Annotation[] arrAno : methodAnno) {
			if (i == index) {
				for (Annotation pAnno : arrAno) {
					if (pAnno.annotationType().equals(BPLogParamName.class)) {
						BPLogParamName declare = (BPLogParamName) pAnno;
						return declare.value();
					}
				}
				return null;
			}
			i++;
		}
		return null;
	}

	private Map<VariableConfig, String> objectToVConfig(Object obj, BpProcessCommon process) throws Exception {
		Map<VariableConfig, String> results = new HashMap<BpProcessCommon.VariableConfig, String>();
		if (Objects.nonNull(obj)) {
			List<Field> fields = FieldUtils.getAllFieldsList(obj.getClass());
			for (Field f : fields) {
				f.setAccessible(true);
				Object value = f.get(obj);

				if (Objects.nonNull(value)) {
					VariableConfig vConfig = null;
					if (canTakeValue(value)) {
						BPLogParamName declare = f.getAnnotation(BPLogParamName.class);
						if (Objects.nonNull(declare)) {
							vConfig = process.findByVariableName(declare.value());
						} else {
							vConfig = process.findByVariableName(f.getName());
						}
						if (Objects.nonNull(vConfig)) {
							results.put(vConfig, toStringValue(value));
						}
					} else {
						results.putAll(objectToVConfig(value, process));
					}
				}
			}
		}
		return results;
	}

	private String toStringValue(Object obj) throws Exception {
		if (obj.getClass().equals(String.class)) {
			return (String) obj;
		} else if (ClassUtils.isPrimitiveOrWrapper(obj.getClass())) {
			return String.valueOf(obj);
		} else if (ClassUtils.isAssignable(obj.getClass(), Map.class)
				|| ClassUtils.isAssignable(obj.getClass(), Collection.class)) {
			return mapper.writeValueAsString(obj);
		} else if (ClassUtils.isAssignable(obj.getClass(), Date.class)) {
			if (obj instanceof java.sql.Date) {
				return DateUtils.format(new Date(((java.sql.Date) obj).getTime()), DateUtils.MEDIUM_PATTERN);
			} else if (obj instanceof Timestamp) {
				return DateUtils.format((Date) obj, DateUtils.MEDIUM_PATTERN);
			}
		}
		return null;
	}

	private boolean canTakeValue(Object obj) {
		return (ClassUtils.isPrimitiveOrWrapper(obj.getClass()) || obj.getClass().equals(String.class)
				|| ClassUtils.isAssignable(obj.getClass(), Map.class)
				|| ClassUtils.isAssignable(obj.getClass(), Collection.class)
				|| ClassUtils.isAssignable(obj.getClass(), Date.class));
	}
}
