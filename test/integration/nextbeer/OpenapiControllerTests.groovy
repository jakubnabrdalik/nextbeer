package nextbeer

import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(OpenapiController)
class OpenapiControllerTests {
    @Test
    //BEWARE: this test is slow (waits for 10 seconds) because it tests quartz job schedulling
    void shouldSendSmsViaOpenApi() {
        //given
        params.api = new LinkedHashMap<String, String>()
        params.api."request[1].sender" = "789456123"
        params.api."request[1].text" = "5000"
        controller.checkPermissionIntervalInSeconds = 1

        //when
        controller.propose()
        sleep(1000 * 10)

        //then
        assert controller.openApiFacade.calls.last() == "sendSms"
    }
}
