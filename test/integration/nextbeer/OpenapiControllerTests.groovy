package nextbeer

import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(OpenapiController)
class OpenapiControllerTests {
    @Test
    //BEWARE: this test is slow (waits for 10 seconds) because it tests quartz job schedulling
    void shouldFireTrigger() {
        //given
        params.from = "789456123"
        params.text = "5000"
        controller.checkPermissionIntervalInSeconds = 3

        //when
        controller.propose()
        sleep(1000 * 10)

        //then
        println controller.openApiFacade.calls
        assert controller.openApiFacade.calls.last() == "sendSms"
    }
}
