package nextbeer.openApi

import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
class OpenApiFacadeImplManualTest {
    OpenApiFacadeImpl openApiFacadeImpl
    String testNumber

    OpenApiFacadeImplManualTest() {
    }

    @Before
    void createOpenApiFacadeWithApiKey() {
        Properties properties = new Properties()
        properties.load(new FileReader(new File("grails-app/conf/external-config.properties")))
        String openApiKey = properties.get("openapi.key")
        String openApiUrl = properties.get("openapi.url")
        testNumber = properties.get("openapi.testNumber")
        openApiFacadeImpl = new OpenApiFacadeImpl(openApiKey, openApiUrl)
    }

    @Test
    @Ignore("This test will cost you money, perhaps it's better not to run it automatically")
    void shouldAskIfHasPermissionToGetLocation() {
        boolean hasPermission = openApiFacadeImpl.hasPermissionToGetLocation(testNumber)
        //the result will vary depending on whether you have answered the get permission sms or not
    }

    @Test
    @Ignore("This test will cost you money, perhaps it's better not to run it automatically")
    void shouldAskForPermissionToGetLocation() {
        openApiFacadeImpl.askForPermissionToGetLocation(testNumber)
        //now wait for the sms
    }

    @Test
    @Ignore("This test will cost you money, perhaps it's better not to run it automatically")
    void shouldSendSms() {
        openApiFacadeImpl.sendSms(testNumber, "Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25 Pepper Pub & Restaurant, Wspólna 52/54, Warsaw, +48 22 629 26 25")
        //you should get an sms
    }

    @Test
    @Ignore("This test will cost you money, perhaps it's better not to run it automatically")
    void shouldGetLocation() {
        Location location = openApiFacadeImpl.getLocation(testNumber)
        //you should get an sms
    }
}
