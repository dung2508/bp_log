package vn.edu.clevai.bplog.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import vn.edu.clevai.bplog.annotation.MakeBpProcess;
import vn.edu.clevai.bplog.common.enumtype.BppProcessTypeEnum;
import vn.edu.clevai.bplog.service.BpBppProcessService;

@Aspect
@Component
@RequiredArgsConstructor
public class CreateBpProcessAOP {

	private final BpBppProcessService bpBppProcessService;

	@Around(value = "@annotation(vn.edu.clevai.bplog.annotation.MakeBpProcess)")
	public Object makeProcess(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		MakeBpProcess makeBpProcess = signature.getMethod().getDeclaredAnnotation(MakeBpProcess.class);
		BppProcessTypeEnum process = makeBpProcess.process();
		String myParent = makeBpProcess.parentProcess();
		bpBppProcessService.createBppProcess(process, myParent);
		return joinPoint.proceed();
	}
}
