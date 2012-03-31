package nextbeer.openApi

import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class OpenApiFacadeImplTest {
    OpenApiFacadeImpl openApiFacadeImpl
    String testNumber

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
        //openApiFacadeImpl.sendSms(testNumber, "Drink-bar%2C+Wsp%C3%B3lna+52%2F54%2C+Warsaw%2C+%2B48+22+629+26+25%0AAdmiral+Casino+Drink+Bar+Salon+gier%2C+Pu%C5%82awska+73%2F75%2C+Warsaw%2C+%2B48+22+845+50+10%0ADrink+Safe+Sp.+z+o.o.%2C+Bukowi%C5%84ska+22%2C+Warsaw%2C+%2B48+506+065+400%0AXL+Energy+Marketing+Sp.+z+o.o.%2C+W%C5%82adys%C5%82awa+Niegolewskiego+17%2C+Warsaw%2C+%2B48+22+839+75+99%0AMagic+Drinks+Sp.+z+o.o.%2C+Pandy+22%2C+Warsaw%2C+%2B48+22+836+41+03%0A%C5%BBywiec-Zdr%C3%B3j+S.A.%2C+Aleje+Jerozolimskie+146%2C+Warsaw%2C+%2B48+22+608+16+00%0APepsi-Cola+General+Bottlers+Sp.+z+o.o.%2C+Hrubieszowska+2%2C+Warsaw%2C+%2B48+22+366+20+00%0ASpin%2C+Warsaw+Uprising+Square+2%2C+Warsaw%2C+%2B48+22+582+97+31%0AGemza.+PHU.+Hurtownia+wody%2C+Post%C4%99pu+3%2C+Warsaw%2C+%2B48+22+843+22+61%0ANorton+sp.j.%2C+Krakowiak%C3%B3w+64%2C+Warsaw%2C+%2B48+22+886+17+04%0AHoop+Polska+Sp.+z+o.o.%2C+Jana+Olbrachta+94%2C+Warsaw%2C+%2B48+22+338+18+18%0AStara+Praga+Kawiarnia%2C+Targowa+18%2C+Warsaw%2C+%2B48+517+623+025%0AChocolate+Drinks+-+Antique+Shop%2C+Szpitalna+8%2C+Warsaw%2C+%2B48+22+827+29+16%0AYou%26Me+Bar%2C+%C5%BBurawia+6%2F12%2C+Warsaw%2C+%2B48+22+420+34+34%0APepper+Pub+%26+Restaurant%2C+Wilcza+35%2F41%2C+Warsaw%2C+%2B48+22+621+35+06%0ASketch%2C+Foksal+19%2C+Warschau%2C+%2B48+602+762+764")
        openApiFacadeImpl.sendSms(testNumber, "łódź ąę itd.")
        //you should get an sms
    }

    @Test
    @Ignore("This test will cost you money, perhaps it's better not to run it automatically")
    void shouldGetLocation() {
        Location location = openApiFacadeImpl.getLocation(testNumber)
        //you should get an sms
    }
}
