
package nextbeer.openApi

import groovy.util.slurpersupport.NodeChild
import spock.lang.Specification

class OpenApiFacadeSpec extends Specification {
    private openApiFacade = new OpenApiFacadeImpl("someKey", "someUrl")

    def "has location permission"() {
        given:
        NodeChild response = new XmlSlurper().parseText(xml)

        expect:
        hasPermission == openApiFacade.hasLocationPermission(response)

        where:
        hasPermission | xml
        true          | "<api><total>1</total><request><common><reqid>260020000000001009</reqid><request-time>2012-02-25 13:58:25.0 CET</request-time><status>accepted</status><charged>0</charged></common><permission><type>location</type><target>48602589752</target><granted-since>2012-02-25 13:58:42.0 CET</granted-since><valid-period>3600</valid-period></permission></request></api>"
        false         | "<api><total>1</total><request><common><reqid>260020000000001009</reqid><request-time>2012-02-25 13:58:25.0 CET</request-time><status>accepted</status><charged>0</charged></common><permission></permission></request></api>"
    }

    def "get location"() {
        given:
        String locationXml = "<api><request><common><reqid>260020000000001021</reqid><request-time>2012-02-25 14:19:53.760 CET</request-time><status>located</status><charged>10</charged></common><location><target>48602589752</target><permission>260020000000001009</permission><result><lat>52.27777862548828</lat><lon>21.053333282470703</lon><radius>1281</radius><level>100</level><location-time>2012-02-25 14:20:04.0 CET</location-time></result></location></request></api>"
        NodeChild response = new XmlSlurper().parseText(locationXml)

        when:
        Location location = openApiFacade.getLocation(response)

        then:
        location.latitude == "52.27777862548828"
        location.longitude == "21.053333282470703"
    }
}
