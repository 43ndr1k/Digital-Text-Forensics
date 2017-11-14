package de.uni_leipzig.digital_text_forensics.logging;

import de.uni_leipzig.digital_text_forensics.model.LoggingDocument;
import de.uni_leipzig.digital_text_forensics.model.Query;
import de.uni_leipzig.digital_text_forensics.service.LoggingDocService;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Aspect
@Component
@Order(1)
public class Logging {
	private static final Logger LOGGER = LoggerFactory.getLogger(Logging.class);

	@Autowired
	LoggingDocService loggingDocService;

	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) && execution(* *(..))")
	public Object logExecutionTimeOfRequests(ProceedingJoinPoint thisJointPoint) throws Throwable {
		long start = System.currentTimeMillis();
		Object ret = thisJointPoint.proceed();

		Long durationInMilliseconds = (System.currentTimeMillis() - start);

		LOGGER.info("Call to '{}' with arguments {} took {} ms for user with client identification '{}'",
				thisJointPoint.getSignature().toString(), thisJointPoint.getArgs(),
				durationInMilliseconds, RequestContextHolder.currentRequestAttributes().getSessionId());

		if (ret instanceof ModelAndView) {
			((ModelAndView) ret).getModel().put("durationInMilliseconds", durationInMilliseconds);
		}

		return ret;
	}

	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)"
			+ "&& execution(* de.uni_leipzig.digital_text_forensics.controller.RedirectController..*(..))")
	public Object logRedirects(ProceedingJoinPoint thisJointPoint) throws Throwable {
		Object ret = thisJointPoint.proceed();

		if (!(ret instanceof RedirectView)) {
			throw new RuntimeException(
					"This aspect is expected to be woven around redirect methods which return instances of RedirectView.");
		}

		LOGGER.info(
				"A user identified by '{}' is redirected due to a call to '{}' with arguments {} to the url '{}'. The user comes from '{}'.",
				RequestContextHolder.currentRequestAttributes().getSessionId(),
				thisJointPoint.getSignature().toString(), thisJointPoint.getArgs(),
				((RedirectView) ret).getUrl(),
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
						.getHeader("referer"));

		loggingDocService.updateDocCount(mapArgsToLoggingDoc(thisJointPoint.getArgs()));

		return ret;
	}

	private LoggingDocument mapArgsToLoggingDoc(Object[] args) {

		List<Query> queryList = new LinkedList<>();
		queryList.add(new Query(args[1].toString()));
		return new LoggingDocument(new Long(args[0].toString()), args[2].toString(), queryList);

	}

}
