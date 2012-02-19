package nextbeer.openApi

import org.junit.Test

class OpenApiResponseValidatorTest {
    String failingXml = "<api><request><common><reqid>260020000000001018</reqid><request-time>2012-02-25 14:06:16.623 CET</request-time><status>failed</status><charged>-1</charged></common><location><target>48602589752</target><permission>260020000000001009</permission><result/></location></request></api>"
    String passingXml = "<api><request><common><reqid>260020000000001018</reqid><request-time>2012-02-25 14:06:16.623 CET</request-time><status>success</status><charged>-1</charged></common><location><target>48602589752</target><permission>260020000000001009</permission><result/></location></request></api>"
    
    @Test(expected = RuntimeException)
    public void shouldThrowExceptionIfContainsFails() {
        //given
        def xml = new XmlSlurper().parseText(failingXml)

        //when
        OpenApiResponseValidator.verifyXmlContainsNoFails(xml)

        //then exception is thrown
    }

    @Test
    public void shouldNotThrowExceptionIfDoesntContainsFails() {
        //given
        def xml = new XmlSlurper().parseText(passingXml)

        //when
        OpenApiResponseValidator.verifyXmlContainsNoFails(xml)

        //no exception is thrown
    }
}
