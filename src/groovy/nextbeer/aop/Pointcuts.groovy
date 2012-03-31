
package nextbeer.aop

import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.annotation.Aspect

@Aspect
class Pointcuts {
    @Pointcut("execution(* nextbeer.openApi..*.*(..)) && !execution(* nextbeer.openApi..*.getMetaClass(..))")
    public void allOpenApiMethods() {}

    @Pointcut("!execution(* *..*.getMetaClass(..))")
    public void noMetaClassMethods() {}

    @Pointcut("allOpenApiMethods() && noMetaClassMethods()")
    public void allOpenApiMethodsExceptMetaClassCalls() {}

}
