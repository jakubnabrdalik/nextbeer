import grails.util.Environment
import nextbeer.SmsAdvisor
import nextbeer.SmsJobPlanner
import nextbeer.aop.LoggingAspect
import nextbeer.aop.Pointcuts
import nextbeer.google.GooglePlacesFacade
import nextbeer.openApi.OpenApiFacadeImpl
import nextbeer.openApi.OpenApiFacadeMock

// Place your Spring DSL code here
beans = {
    Environment.executeForCurrentEnvironment {
        production {
            openApiFacade(OpenApiFacadeImpl, application.config.openapi.key, application.config.openapi.url) {}
        }
        development {
            openApiFacade(OpenApiFacadeMock, 4) {}
        }
    }

    googlePlacesFacade(GooglePlacesFacade, application.config.google.places.api.key) {}
    smsAdvisor(SmsAdvisor, openApiFacade, googlePlacesFacade) {}
    smsJobPlanner(SmsJobPlanner, openApiFacade, ref("quartzScheduler"), smsAdvisor) {}

    pointcuts(Pointcuts) {}
    debugAspect(LoggingAspect, "DebugOpenApiAdvice") {}
}
