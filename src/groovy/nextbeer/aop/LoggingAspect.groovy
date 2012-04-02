
package nextbeer.aop

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.AfterThrowing
import groovyx.net.http.HttpResponseException

@Aspect
class LoggingAspect {
    private final Log log

    public LoggingAspect(String loggerName) {
        log = LogFactory.getLog(loggerName)
    }

    @Before("Pointcuts.allOpenApiMethodsExceptMetaClassCalls()")
    public void debug(JoinPoint joinPoint) {
        log.debug("Reporting a call: $joinPoint.staticPart; Args: " + joinPoint.getArgs().collect {it.toString()})
    }

    @AfterThrowing(pointcut = "Pointcuts.allOpenApiMethodsExceptMetaClassCalls()", throwing = "exception")
    public void debugtHttpCommunicationErrors(JoinPoint joinPoint, HttpResponseException exception) {
        log.debug("HttpResponseException detected; Status: $exception.response.status Response: $exception.response.data")
    }
}
