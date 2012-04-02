
package nextbeer

import grails.plugin.spock.ControllerSpec

class OpenapiControllerSpec extends ControllerSpec {
    //BEWARE: this test is slow (waits for 10 seconds) because it tests quartz job schedulling
    def "should send sms via Open Api"() {
        given:
        mockLogging(OpenapiController, true)
        controller.params.api = ["request[1].sender": "789456123", "request[1].text": "5000"]
        controller.checkPermissionIntervalInSeconds = 1

        when:
        controller.propose()
        sleep(1000 * 10)

        then:
        controller.openApiFacade.calls.last() == "sendSms"
    }
}
