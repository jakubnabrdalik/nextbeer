
package nextbeer.openApi

import spock.lang.Specification

class OpenApiResponseValidatorSpec extends Specification {
    static String failingXml = "<api><request><common><reqid>260020000000001018</reqid><request-time>2012-02-25 14:06:16.623 CET</request-time><status>failed</status><charged>-1</charged></common><location><target>48602589752</target><permission>260020000000001009</permission><result/></location></request></api>"
    static String passingXml = "<api><request><common><reqid>260020000000001018</reqid><request-time>2012-02-25 14:06:16.623 CET</request-time><status>success</status><charged>-1</charged></common><location><target>48602589752</target><permission>260020000000001009</permission><result/></location></request></api>"

    def "throw exception if xml contains fails"() {
        given:
        def xml = new XmlSlurper().parseText(failingXml)

        when:
        OpenApiResponseValidator.verifyXmlContainsNoFails(xml)

        then:
        thrown(RuntimeException)
    }


    def "don't throw exception if xml doesn't contains fails"() {
        given:
        def xml = new XmlSlurper().parseText(passingXml)

        when:
        OpenApiResponseValidator.verifyXmlContainsNoFails(xml)

        then:
        notThrown(RuntimeException)
    }
}
