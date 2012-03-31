import grails.plugin.quartz2.QuartzFactoryBean
import grails.util.GrailsUtil
import nextbeer.SmsAdvisor
import nextbeer.SmsJobPlanner
import nextbeer.google.GooglePlacesFacade
import nextbeer.openApi.OpenApiFacadeImpl
import nextbeer.openApi.OpenApiFacadeMock
import grails.plugin.quartz2.QuartzFactoryBean
import nextbeer.aop.LoggingAspect
import nextbeer.aop.Pointcuts

// Place your Spring DSL code here
beans = {
    if (GrailsUtil.environment == "test") {
        openApiFacade(OpenApiFacadeMock, 4) {}
    } else {
        openApiFacade(OpenApiFacadeImpl, application.config.openapi.key, application.config.openapi.url) {}
    }

    googlePlacesFacade(GooglePlacesFacade, application.config.google.places.api.key) {}
    smsAdvisor(SmsAdvisor, openApiFacade, googlePlacesFacade) {}
    smsJobPlanner(SmsJobPlanner, openApiFacade, ref("quartzScheduler"), smsAdvisor) {}

    pointcuts(Pointcuts) {}
    debugAspect(LoggingAspect, "DebugOpenApiAdvice") {}
}
